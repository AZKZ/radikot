package com.azkz.radikot

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * 完了済み一覧
 */
interface CompletedList {

    /**
     * 番組を完了済みリストに登録する
     */
    fun add(radioProgram: RadioProgram)

    /**
     * この番組が完了済みかどうかを確認する
     */
    fun isCompleted(radioProgram: RadioProgram):Boolean

}

/**
 * 完了済み一覧のファイル
 */
class CompletedListFile(private val file: File):CompletedList {

    private val completedRadioPrograms : MutableSet<RadioProgram> = mutableSetOf()

    init {
        // 1行ずつ読み込んで、setに追加する
        val inputStream = file.inputStream()
        inputStream.bufferedReader().forEachLine {
            completedRadioPrograms.add(Json.decodeFromString<RadioProgram>(it))
        }
    }


    /**
     * 番組を完了済みリストに登録する
     */
    override fun add(radioProgram: RadioProgram) {
        // ファイルに書き込む
        file.appendText(Json.encodeToString(radioProgram))
        file.appendText(System.lineSeparator())

        // フィールドのsetに追加する
        completedRadioPrograms.add(radioProgram)
    }

    /**
     * この番組が完了済みかどうかを確認する
     */
    override fun isCompleted(radioProgram: RadioProgram): Boolean {
        return completedRadioPrograms.contains(radioProgram)
    }
}