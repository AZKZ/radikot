package com.azkz.radikot

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

internal class ThirtyHourClockLocalDateTimeTest {

    @Nested
    class of() {
        @Test
        fun `コロン有りの場合`() {
            // 入力値
            val date = LocalDate.of(2020, 12, 31)
            val hhmmss = "24:59:58"

            // テストメソッド呼び出し
            val actual = ThirtyHourClockLocalDateTime.of(date, hhmmss)

            // 確認
            assertEquals(date, actual.date) // 日付
            assertEquals(24, actual.hour) // 時
            assertEquals(59, actual.minute) // 分
            assertEquals(58, actual.second) // 秒
        }

        @Test
        fun `コロン無しの場合`() {
            // 入力値
            val date = LocalDate.of(2020, 12, 31)
            val hhmmss = "245958"

            // テストメソッド呼び出し
            val actual = ThirtyHourClockLocalDateTime.of(date, hhmmss)

            // 確認
            assertEquals(date, actual.date) // 日付
            assertEquals(24, actual.hour) // 時
            assertEquals(59, actual.minute) // 分
            assertEquals(58, actual.second) // 秒
        }

        @Test
        fun `時分秒の桁数が超過している場合`() {
            // 入力値
            val date = LocalDate.of(2020, 12, 31)
            val hhmmss = "2459581"

            // テストメソッド呼び出し
            // 確認
            assertThrows<IllegalArgumentException> { ThirtyHourClockLocalDateTime.of(date, hhmmss) }
        }

        @Test
        fun `時分秒の桁数が不足している場合`() {
            // 入力値
            val date = LocalDate.of(2020, 12, 31)
            val hhmmss = "24595"

            // テストメソッド呼び出し
            // 確認
            assertThrows<IllegalArgumentException> { ThirtyHourClockLocalDateTime.of(date, hhmmss) }
        }

        @Test
        fun `時分秒に数字以外の文字がある場合`() {
            // 入力値
            val date = LocalDate.of(2020, 12, 31)
            val hhmmss = "24595A"

            // テストメソッド呼び出し
            // 確認
            assertThrows<NumberFormatException> { ThirtyHourClockLocalDateTime.of(date, hhmmss) }
        }
    }

    @Nested
    class formatted24HoursClockDatetime() {
        @Test
        fun `24時以降の日時が24時間制に変換されてフォーマットされていることの確認`() {
            // 入力値
            val date = LocalDate.of(2020, 12, 31)
            val hhmmss = "24:59:58"
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

            // 期待値
            val expected = "20210101005958"

            // テストメソッド呼び出し
            val actual = ThirtyHourClockLocalDateTime.of(date, hhmmss).formatted24HoursClockDatetime(formatter)

            // 確認
            assertEquals(expected, actual)
        }

        @Test
        fun `24時未満の日時がそのままフォーマットされていることの確認`() {
            // 入力値
            val date = LocalDate.of(2020, 12, 31)
            val hhmmss = "23:59:58"
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

            // 期待値
            val expected = "20201231235958"

            // テストメソッド呼び出し
            val actual = ThirtyHourClockLocalDateTime.of(date, hhmmss).formatted24HoursClockDatetime(formatter)

            // 確認
            assertEquals(expected, actual)
        }

    }
}