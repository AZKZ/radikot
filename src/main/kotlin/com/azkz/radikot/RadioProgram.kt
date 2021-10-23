package com.azkz.radikot

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.format.DateTimeFormatter
import kotlin.io.path.notExists

/**
 * ラジオ番組
 */
class RadioProgram(
    /**
     * 番組名
     */
    val name: String,

    /**
     * 開始日時
     */
    val startDateTime: ThirtyHourClockLocalDateTime,

    /**
     * 終了日時
     */
    val endDateTime: ThirtyHourClockLocalDateTime,

    /**
     * 放送局
     */
    val station: Station

) {

    /**
     * このラジオ番組をダウンロードします。<br>
     * ダウンロードされたファイルは[RadikotProperties.DOWNLOAD_LOCATION_DIR_PATH]内の番組名ディレクトリに保存されます。<br>
     * 実行時に同名ファイルがある場合は削除して、新たにファイルを作成します。<br>
     * 何らかの理由で途中でダウンロードが中断された場合は、その時点までダウンロードされた内容でファイルは残ります。<br>
     *
     * @param authToken 認証されたトークン
     * @return ダウンロードされたファイル
     */
    suspend fun download(authToken: String): File {
        // ダウンロード用のHTTPクライアント
        // 認証用と分けているのは将来的に並行処理にしたいから
        val client = HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
            install(HttpTimeout) {
                // タイムアウト設定
                requestTimeoutMillis = 5000
            }
        }

        // ダウンロード先のディレクトリ + 番組名のディレクトリが無い場合は作成する
        val downloadLocation = Path.of(RadikotProperties.DOWNLOAD_LOCATION_DIR_PATH, name)
        if (downloadLocation.notExists()) {
            Files.createDirectories(downloadLocation)
        }

        // ダウンロードする番組の音声を書き込むAACファイル
        // ファイル名の例:[YYYY-MM-DD放送分]誰かさんのオールナイトニッポン
        val aacFile = File(downloadLocation.toFile(), "[${startDateTime.date}放送分]${name}.aac")

        // 既存ファイルがある場合は削除する
        if (aacFile.exists()) {
            aacFile.delete()
        }

        // ストリーミングのプレイリスト情報をHTTP通信で取得する
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val playlistResponse: HttpResponse =
            client.get(
                "https://radiko.jp/v2/api/ts/playlist.m3u8" +
                        "?station_id=${station.id}" +
                        "&l=15" +
                        "&ft=${startDateTime.formatted24HoursClockDatetime(dateTimeFormatter)}" +
                        "&to=${endDateTime.formatted24HoursClockDatetime(dateTimeFormatter)}"
            ) {
                headers {
                    append("X-Radiko-AuthToken", authToken)
                }
            }

        // httpsから始まる行がチャンクリスト(AACファイルの一覧)を取得するURLなので抜き取る
        val chunklistUrl: String = playlistResponse.readText().lines().find { line -> line.startsWith("https://") }!!

        // チャンクリストをHTTP通信で取得する
        val chunklistResponse: HttpResponse = client.get(chunklistUrl) {
            headers {
                append("X-Radiko-AuthToken", authToken)
            }
        }

        // チャンクリストの行数分繰り返す
        for (line in chunklistResponse.readText().lines()) {
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

        client.close()

        return aacFile
    }
}