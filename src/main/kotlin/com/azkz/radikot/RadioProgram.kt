package com.azkz.radikot

import kotlinx.serialization.Serializable

/**
 * ラジオ番組
 */
@Serializable
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
     * 放送局ID<br>
     * [http://radiko.jp/v3/station/region/full.xml](http://radiko.jp/v3/station/region/full.xml)に書いてあります。
     */
    val stationId: String

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RadioProgram) return false

        if (name != other.name) return false
        if (startDateTime != other.startDateTime) return false
        if (endDateTime != other.endDateTime) return false
        if (stationId != other.stationId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + startDateTime.hashCode()
        result = 31 * result + endDateTime.hashCode()
        result = 31 * result + stationId.hashCode()
        return result
    }

}