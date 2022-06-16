package com.azkz.radikot

import kotlinx.datetime.*
import java.io.File

/**
 * 番組一覧CSVファイル
 */
class ProgramListFile(csvFile: File) {

    companion object {
        private const val INDEX_ENABLED_FLAG = 0;
        private const val INDEX_DAY_OF_WEEK = 1;
        private const val INDEX_PROGRAM_NAME = 2;
        private const val INDEX_STATION_ID = 3;
        private const val INDEX_START_TIME = 4;
        private const val INDEX_END_TIME = 5;
        private val DAY_OF_WEEK_JPN_TO_ENUM: Map<String, DayOfWeek> = mapOf(
            "日" to DayOfWeek.SUNDAY,
            "月" to DayOfWeek.MONDAY,
            "火" to DayOfWeek.TUESDAY,
            "水" to DayOfWeek.WEDNESDAY,
            "木" to DayOfWeek.THURSDAY,
            "金" to DayOfWeek.FRIDAY,
            "土" to DayOfWeek.SATURDAY,
        )
    }

    /**
     * ラジオ番組リスト
     */
    val radioPrograms: List<RadioProgram>

    init {
        // ==============================================================
        // システム日付前日までの1週間の曜日と日付の対応表を作る
        // ==============================================================
        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val yesterday = LocalDate(currentDateTime.year,currentDateTime.month,currentDateTime.dayOfMonth).minus(1,DateTimeUnit.DAY)
        val targetDates: Map<DayOfWeek, LocalDate> = mapOf(
            yesterday.dayOfWeek to yesterday,
            yesterday.minus(DatePeriod(0,0,1)).dayOfWeek to yesterday.minus(DatePeriod(0,0,1)),
            yesterday.minus(DatePeriod(0,0,2)).dayOfWeek to yesterday.minus(DatePeriod(0,0,2)),
            yesterday.minus(DatePeriod(0,0,3)).dayOfWeek to yesterday.minus(DatePeriod(0,0,3)),
            yesterday.minus(DatePeriod(0,0,4)).dayOfWeek to yesterday.minus(DatePeriod(0,0,4)),
            yesterday.minus(DatePeriod(0,0,5)).dayOfWeek to yesterday.minus(DatePeriod(0,0,5)),
            yesterday.minus(DatePeriod(0,0,6)).dayOfWeek to yesterday.minus(DatePeriod(0,0,6)),
        )


        // ==============================================================
        // CSVファイルを1行ずつ処理して番組リストに追加する。
        // ==============================================================

        // CSVファイルの全行
        val lines = csvFile.readLines()
        // 一時的に使用する対象番組のリスト フィールドに設定する際にはimmutableにする。
        val programsMutable: MutableList<RadioProgram> = mutableListOf()

        // 1行目はヘッダーなので、2行目から処理する
        for (i in 1 until lines.size) {
            // カンマ区切りにする
            val values = lines[i].split(",")

            // 有効フラグが1以外の場合はスキップする
            if (! "1".equals(values[INDEX_ENABLED_FLAG])) continue

            // 曜日の日本語からDayOfWeek Enumを取得する
            val dayOfWeek = DAY_OF_WEEK_JPN_TO_ENUM[values[INDEX_DAY_OF_WEEK]]

            // 番組をリストに追加する
            programsMutable += RadioProgram(
                name = values[INDEX_PROGRAM_NAME],
                startDateTime = ThirtyHourClockLocalDateTime.of(targetDates[dayOfWeek]!!, values[INDEX_START_TIME]),
                endDateTime = ThirtyHourClockLocalDateTime.of(targetDates[dayOfWeek]!!, values[INDEX_END_TIME]),
                stationId = values[INDEX_STATION_ID]!!
            )
        }

        // immutable化してフィールドに設定する
        this.radioPrograms = programsMutable.toList()
    }
}