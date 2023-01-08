package com.azkz.radikot

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.notExists

/**
 * radikotのHTTPクライアント
 */
class RadikotHTTPClient {

    /**
     * HTTPクライアント
     */
    private val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            // タイムアウト設定
            requestTimeoutMillis = 30000
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    /**
     * 認証されたトークン
     */
    var authToken: String = "Unauthenticated"

    companion object {
        // 認証に必要なキー
        // http://radiko.jp/apps/js/playerCommon.js の中に記載されてる
        const val AUTH_KEY = "bcd151073c03b352e1ef2fd66c32209da9ca0afa"
    }

    /**
     * Radikoの認証を行います。
     * 2段階の認証に成功したら、[authToken]に認証されたトークンを設定します。
     *
     */
    suspend fun authenticate() {
        // ==============================================================
        // ログイン処理(プロパティでradikoのメールアドレスとパスワードが設定されている場合のみ処理する)
        // ==============================================================
        if (RadikotProperties.RADIKO_LOGIN_MAIL != null && RadikotProperties.RADIKO_LOGIN_PASSWORD != null) {
            try {
                val loginResponse: HttpResponse = client.submitForm(
                    url = "https://radiko.jp/ap/member/webapi/member/login",
                    formParameters = Parameters.build {
                        append("mail", RadikotProperties.RADIKO_LOGIN_MAIL)
                        append("pass", RadikotProperties.RADIKO_LOGIN_PASSWORD)
                    }
                )
            } catch (e: RedirectResponseException) {
                // HTTPステータスコード3xxはRedirectResponseExceptionがthrowされるが
                // それが認証されたということなので意図した通りの挙動ということ
            }
        }

        // ==============================================================
        // 第1認証(第2認証をするためのトークンなどを取得する)
        // ==============================================================
        val auth1Response: HttpResponse = client.get("https://radiko.jp/v2/api/auth1") {
            headers {
                append("X-Radiko-App", "pc_html5")
                append("X-Radiko-App-Version", "0.0.1")
                append("X-Radiko-User", "dummy_user")
                append("X-Radiko-Device", "pc")
            }
        }
        val token = auth1Response.headers["X-Radiko-AuthToken"]!!
        val keyLength = auth1Response.headers["X-Radiko-KeyLength"]!!.toInt()
        val keyOffset = auth1Response.headers["X-Radiko-KeyOffset"]!!.toInt()

        // ==============================================================
        // 第2認証(第1認証の情報を使って認証キーを加工する。それを使ってトークンを認証させる。)
        // ==============================================================

        //  第1認証の取得値を使って認証キーの一部を切り出す
        val partialKeyBeforeEncoded = AUTH_KEY.substring(keyOffset, keyOffset + keyLength)
        // それをBASE64でエンコードする
        val partialKey = Base64.getEncoder().encodeToString(partialKeyBeforeEncoded.toByteArray())

        val auth2Response: HttpResponse = client.get("https://radiko.jp/v2/api/auth2") {
            headers {
                append("X-Radiko-AuthToken", token)
                append("X-Radiko-KeyLength", keyLength.toString())
                append("X-Radiko-KeyOffset", keyOffset.toString())
                append("X-Radiko-PartialKey", partialKey)
            }
        }

        // 認証されたトークンをインスタンス変数に設定する
        this.authToken = token
    }

    /**
     * 指定したラジオ番組をダウンロードします。<br>
     * ダウンロードされたファイルは[RadikotProperties.DOWNLOAD_LOCATION_DIR_PATH]内の番組名ディレクトリに保存されます。<br>
     * 実行時に同名ファイルがある場合は削除して、新たにファイルを作成します。<br>
     * 何らかの理由で途中でダウンロードが中断された場合は、その時点までダウンロードされた内容でファイルは残ります。<br>
     *
     * @param radioProgram ダウンロード対象のラジオ番組
     * @return ダウンロードされたファイル
     */
    suspend fun download(radioProgram: RadioProgram): File {
        // ダウンロード先のディレクトリ + 番組名のディレクトリが無い場合は作成する
        val downloadLocation = Path.of(RadikotProperties.DOWNLOAD_LOCATION_DIR_PATH, radioProgram.name)
        if (downloadLocation.notExists()) {
            Files.createDirectories(downloadLocation)
        }

        // ダウンロードする番組の音声を書き込むAACファイル
        // ファイル名の例:[YYYY-MM-DD放送分]誰かさんのオールナイトニッポン
        val aacFile = File(downloadLocation.toFile(), "[${radioProgram.startDateTime.date}放送分]${radioProgram.name}.aac")

        // 既存ファイルがある場合は削除する
        if (aacFile.exists()) {
            aacFile.delete()
        }

        // ストリーミングのプレイリスト情報をHTTP通信で取得する
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val playlistResponse: HttpResponse =
            client.get(
                "https://radiko.jp/v2/api/ts/playlist.m3u8" +
                        "?station_id=${radioProgram.stationId}" +
                        "&l=15" +
                        "&ft=${radioProgram.startDateTime.formatted24HoursClockDatetime(dateTimeFormatter)}" +
                        "&to=${radioProgram.endDateTime.formatted24HoursClockDatetime(dateTimeFormatter)}"
            ) {
                headers {
                    append("X-Radiko-AuthToken", authToken)
                }
            }

        // httpsから始まる行がチャンクリスト(AACファイルの一覧)を取得するURLなので抜き取る
        val chunklistUrl: String = playlistResponse.bodyAsText().lines().find { line -> line.startsWith("https://") }!!

        // チャンクリストをHTTP通信で取得する
        val chunklistResponse: HttpResponse = client.get(chunklistUrl) {
            headers {
                append("X-Radiko-AuthToken", authToken)
            }
        }

        // チャンクリストの行数分繰り返す
        for (line in chunklistResponse.bodyAsText().lines()) {
            // 「https://」から始まる場合はAACファイルのURLなのでHTTP通信で取得する
            if (line.startsWith("https://")) {
                val aacResponse: HttpResponse = client.get(line) {
                    headers {
                        append("X-Radiko-AuthToken", authToken)
                    }
                }
                // レスポンスの内容をファイルに追記する
                aacFile.appendBytes(aacResponse.readBytes())
            }
        }

        return aacFile
    }

    /**
     * ログアウトする
     */
    suspend fun logout() {
        val logoutResponse: HttpResponse = client.get("https://radiko.jp/ap/member/webapi/member/logout")
    }

    /**
     * HTTPクライアントをクローズします。
     */
    fun close() {
        return this.client.close()
    }

}
