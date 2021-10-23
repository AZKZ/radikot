package com.azkz.radikot

import io.ktor.utils.io.charsets.*
import java.io.File
import java.util.*

/**
 * radikotの各種プロパティ値<br>
 * JVM(-D)オプションで「propertyfiledir」の指定がある場合はそのディレクトリ、指定が無い場合は実行時ディレクトリの「radikot.properties」を読み込みます。
 */
object RadikotProperties {
    private val properties = Properties()

    init {
        // プロパティファイルのディレクトリ
        // JVM(-D)オプションで「propertyfiledir」の指定がある場合はそのディレクトリ、指定が無い場合は実行時ディレクトリを使用する。
        val propertyFileDir = System.getProperty("propertyfiledir") ?: System.getProperty("user.dir")!!

        // プロパティファイルを読み込む
        val propertyFile = File(propertyFileDir, "radikot.properties")
        this.properties.load(propertyFile.reader(Charsets.UTF_8))
    }

    /**
     * 番組一覧CSVのファイルパス
     */
    val PROGRAM_LIST_CSV_FILE_PATH: String = this.properties["program.list.csv.file.path"].toString()

    /**
     * ダウンロード先のディレクトリパス
     */
    val DOWNLOAD_LOCATION_DIR_PATH: String = this.properties["download.location.dir.path"].toString()

    /**
     * radikoのログインメールアドレス
     */
    val RADIKO_LOGIN_MAIL: String = this.properties["radiko.login.mail"].toString()

    /**
     * radikoのログインパスワード
     */
    val RADIKO_LOGIN_PASSWORD: String = this.properties["radiko.login.password"].toString()

}