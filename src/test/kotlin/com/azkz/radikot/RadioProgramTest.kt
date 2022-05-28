package com.azkz.radikot

import kotlinx.datetime.LocalDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RadioProgramTest {
    @Nested
    class jsonSerialisation {

        @Test
        fun `encode時の出力の確認`() {
            val radioProgram = RadioProgram(
                "Name",
                ThirtyHourClockLocalDateTime.of(LocalDate(2020, 12, 31), "24:59:58"),
                ThirtyHourClockLocalDateTime.of(LocalDate(2020, 12, 31), "24:59:59"),
                "TBS",
            )

            print(Json.encodeToString(radioProgram))
        }

        @Test
        fun `decodeの確認`() {
            val textJson =
                "{\"name\":\"Name\",\"startDateTime\":{\"date\":\"2020-12-31\",\"hour\":24,\"minute\":59,\"second\":58},\"endDateTime\":{\"date\":\"2020-12-31\",\"hour\":24,\"minute\":59,\"second\":59},\"stationId\":\"TBS\"}"

            val radioProgram = Json.decodeFromString<RadioProgram>(textJson)
            print(radioProgram)
        }

    }
}