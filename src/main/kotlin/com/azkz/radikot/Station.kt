package com.azkz.radikot

/**
 * 放送局一覧
 */
enum class Station(val id: String) {
    ＨＢＣラジオ("HBC"),
    AIR_G_FM北海道("AIR_G"),
    ＳＴＶラジオ("STV"),
    FM_NORTH_WAVE("NORTHWAVE"),
    ＲＡＢ青森放送("RAB"),
    エフエム青森("AFB"),
    IBCラジオ("IBC"),
    エフエム岩手("FMI"),
    TBCラジオ("TBC"),
    Date_fm_エフエム仙台("DATEFM"),
    ＡＢＳ秋田放送("ABS"),
    エフエム秋田("AFM"),
    YBC山形放送("YBC"),
    Rhythm_Station_エフエム山形("RFM"),
    RFCラジオ福島("RFC"),
    ふくしまFM("FMF"),
    NHKラジオ第1_札幌("JOIK"),
    NHKラジオ第1_仙台("JOHK"),
    TBSラジオ("TBS"),
    文化放送("QRR"),
    ニッポン放送("LFR"),
    InterFM897("INT"),
    TOKYO_FM("FMT"),
    J_WAVE("FMJ"),
    ラジオ日本("JORF"),
    bayfm78("BAYFM78"),
    NACK5("NACK5"),
    ＦＭヨコハマ("YFM"),
    LuckyFM_茨城放送("IBS"),
    CRT栃木放送("CRT"),
    RadioBerry("RADIOBERRY"),
    FM_GUNMA("FMGUNMA"),
    ＢＳＮラジオ("BSN"),
    FM_NIIGATA("FMNIIGATA"),
    ＫＮＢラジオ("KNB"),
    ＦＭとやま("FMTOYAMA"),
    MROラジオ("MRO"),
    エフエム石川("HELLOFIVE"),
    FBCラジオ("FBC"),
    FM福井("FMFUKUI"),
    ＹＢＳラジオ("YBS"),
    FM_FUJI("FM_FUJI"),
    SBCラジオ("SBC"),
    ＦＭ長野("FMN"),
    NHKラジオ第1_東京("JOAK"),
    NHKラジオ第1_名古屋("JOCK"),
    CBCラジオ("CBC"),
    東海ラジオ("TOKAIRADIO"),
    ぎふチャン("GBS"),
    ZIP_FM("ZIP_FM"),
    FM_AICHI("FMAICHI"),
    ＦＭ_ＧＩＦＵ("FMGIFU"),
    SBSラジオ("SBS"),
    K_MIX_SHIZUOKA("K_MIX"),
    レディオキューブ_ＦＭ三重("FMMIE"),
    ABCラジオ("ABC"),
    MBSラジオ("MBS"),
    OBCラジオ大阪("OBC"),
    FM_COCOLO("CCL"),
    FM802("802"),
    FM大阪("FMO"),
    Kiss_FM_KOBE("KISSFMKOBE"),
    ラジオ関西("CRK"),
    e_radio_FM滋賀("E_RADIO"),
    KBS京都ラジオ("KBS"),
    α_STATION_FM京都("ALPHA_STATION"),
    wbs和歌山放送("WBS"),
    NHKラジオ第1_大阪("JOBK"),
    BSSラジオ("BSS"),
    エフエム山陰("FM_SANIN"),
    ＲＳＫラジオ("RSK"),
    ＦＭ岡山("FM_OKAYAMA"),
    RCCラジオ("RCC"),
    広島FM("HFM"),
    ＫＲＹ山口放送("KRY"),
    エフエム山口("FMY"),
    ＪＲＴ四国放送("JRT"),
    FM徳島("FM807"),
    RNC西日本放送("RNC"),
    エフエム香川("FMKAGAWA"),
    RNB南海放送("RNB"),
    FM愛媛("JOEU_FM"),
    RKC高知放送("RKC"),
    エフエム高知("HI_SIX"),
    NHKラジオ第1_広島("JOFK"),
    NHKラジオ第1_松山("JOZK"),
    RKBラジオ("RKB"),
    KBCラジオ("KBC"),
    LOVE_FM("LOVEFM"),
    CROSS_FM("CROSSFM"),
    FM_FUKUOKA("FMFUKUOKA"),
    エフエム佐賀("FMS"),
    NBCラジオ("NBC"),
    FM長崎("FMNAGASAKI"),
    RKKラジオ("RKK"),
    FMKエフエム熊本("FMK"),
    OBSラジオ("OBS"),
    エフエム大分("FM_OITA"),
    宮崎放送("MRT"),
    エフエム宮崎("JOYFM"),
    ＭＢＣラジオ("MBC"),
    μＦＭ("MYUFM"),
    RBCiラジオ("RBC"),
    ラジオ沖縄("ROK"),
    FM沖縄("FM_OKINAWA"),
    NHKラジオ第1_福岡("JOLK"),
    ラジオNIKKEI第1("RN1"),
    ラジオNIKKEI第2("RN2"),
    放送大学("HOUSOU_DAIGAKU"),
    NHK_FM_東京("JOAK_FM");

    companion object {
        /**
         * 放送局IDをキーとしたEnumのMap
         */
        private val ID_TO_ENUM: Map<String, Station> = Station.values().map { station -> station.id to station }.toMap()

        /**
         * IDからEnumを取得する
         */
        fun enumById(id: String): Station? {
            return ID_TO_ENUM[id]
        }
    }


}