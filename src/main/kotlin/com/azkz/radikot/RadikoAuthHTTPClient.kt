package com.azkz.radikot

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.util.*

/**
 * radiko認証用HTTPクライアント
 */
class RadikoAuthHTTPClient {

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
            requestTimeoutMillis = 5000
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
     * HTTPクライアントをクローズします。
     */
    fun close() {
        return this.client.close()
    }

}