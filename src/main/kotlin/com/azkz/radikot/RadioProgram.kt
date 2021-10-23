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
     * 放送局ID<br>
     * [http://radiko.jp/v3/station/region/full.xml](http://radiko.jp/v3/station/region/full.xml)に書いてあります。
     */
    val stationId: String

) {

}