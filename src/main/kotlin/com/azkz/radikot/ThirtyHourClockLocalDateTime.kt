package com.azkz.radikot

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 30時間制で表現された日時
 */
class ThirtyHourClockLocalDateTime private constructor(
    val date: LocalDate,
    val hour: Int,
    val minute: Int,
    val second: Int
) {

    companion object {
        /**
         * 日付、時分秒文字列から[]ThirtyHourClockLocalDateTime]のインスタンスを取得します。
         *
         * @param date 日付
         * @param hhmmss 時分秒(30時間制) 「:」で区切られてても良い
         * @return 30時間制で表現された日時
         */
        fun of(date: LocalDate, hhmmss: String): ThirtyHourClockLocalDateTime {
            // 「:」を取り除く
            val replaced = hhmmss.replace(":", "")

            // 桁数が不正な場合
            if (replaced.length != 6) {
                throw IllegalArgumentException("時分秒の桁数が不正です。hhmmssの形式で指定してください。")
            }

            // 文字列から時/分/秒を切り出す
            val hour = replaced.subSequence(0, 2).toString().toInt()
            val minute = replaced.subSequence(2, 4).toString().toInt()
            val second = replaced.subSequence(4, 6).toString().toInt()

            return ThirtyHourClockLocalDateTime(date, hour, minute, second)
        }
    }

    /**
     * 24時間制に変換された日時<br>
     * [hour]が24時以降の場合は翌日の0時以降として変換した日時、それ以外の場合はそのままの日時が設定されます。
     */
    private val convertedTo24HoursClock: LocalDateTime =
        if (hour >= 24) {
            LocalDateTime.of(date.plusDays(1), LocalTime.of(hour - 24, minute, second))
        } else {
            LocalDateTime.of(date, LocalTime.of(hour, minute, second))
        }

    /**
     * フォーマットされた24時間制の日時文字列を返します。<br>
     * 例えば、このインスタンスが「2020-12-31 24:59:58」の日時で、フォーマッターが「yyyyMMddHHmmss」の場合、"20210101005958"を返します。
     *
     * @param formatter 日時フォーマッター
     * @return フォーマットされた24時間制の日時文字列
     */
    fun formatted24HoursClockDatetime(formatter: DateTimeFormatter): String {
        return this.convertedTo24HoursClock.format(formatter)
    }


}