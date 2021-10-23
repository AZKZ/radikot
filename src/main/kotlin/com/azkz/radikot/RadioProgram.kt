package com.azkz.radikot

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

}