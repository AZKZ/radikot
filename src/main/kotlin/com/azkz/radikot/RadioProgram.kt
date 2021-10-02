package com.azkz.radikot

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 録画対象の
 */
class RadioProgram(
    /**
     * 番組名
     */
    val name: String,

    /**
     * 開始日時
     */
    val startDateTime: LocalDateTime,

    /**
     * 終了日時
     */
    val endDateTime: LocalDateTime,

    /**
     * 放送局
     */
    val station: Station

) {


    suspend fun download(authToken: String): File {
        val client = HttpClient(CIO)
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        // AACファイル
        val aacFile = File("/Users/kazuya/Desktop/${name}_${startDateTime.format(dateFormatter)}.aac")

        val playlistResponse: HttpResponse =
            client.get(
                "https://radiko.jp/v2/api/ts/playlist.m3u8" +
                        "?station_id=${station.id}" +
                        "&l=15" +
                        "&ft=${startDateTime.format(dateTimeFormatter)}" +
                        "&to=${endDateTime.format(dateTimeFormatter)}"
            ) {
                headers {
                    append("X-Radiko-AuthToken", authToken)
                }
            }

        // httpsから始まる行がチャンクを取得するURLなので抜き取る
        val chunklistUrl: String = playlistResponse.readText().lines().find { line -> line.startsWith("https://") }!!

        // チャンクの一覧を取得する
        val chunklistResponse: HttpResponse = client.get(chunklistUrl) {
            headers {
                append("X-Radiko-AuthToken", authToken)
            }
        }

        for (line in chunklistResponse.readText().lines()) {
            // URLの場合はAACファイルなので取得する
            if (line.startsWith("https://")) {
                println("load aac:${line}")
                val aacResponse: HttpResponse = client.get(line) {
                    headers {
                        append("X-Radiko-AuthToken", authToken)
                    }
                }
                // ファイルに書き込む
                aacFile.appendBytes(aacResponse.readBytes())
            }
        }
        return aacFile

    }
}