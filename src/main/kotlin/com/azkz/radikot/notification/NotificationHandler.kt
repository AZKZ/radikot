package com.azkz.radikot.notification

import mu.KotlinLogging
import java.time.LocalDateTime

/**
 * 通知ハンドラー<br>
 * [notificator]がnullの場合はどの処理も行いません。
 */
class NotificationHandler(private val notificator: Notificator?) {
    /**
     * ロガー
     */
    private val logger = KotlinLogging.logger {}

    fun onStart() {
        val result = notificator?.notification(Message("【開始】${LocalDateTime.now()}", "")) ?: return
        logger.info { "====== 開始通知送信 ${boolean2OKNG(result)} ======" }
    }

    fun onNormalFinish() {
        val result = notificator?.notification(Message("【正常終了】${LocalDateTime.now()}", "")) ?: return
        logger.info { "====== 正常終了通知送信 ${boolean2OKNG(result)} ======" }
    }

    fun onAbnormalFinish(throwable: Throwable) {
        val result =
            notificator?.notification(Message("【異常終了】${LocalDateTime.now()}", throwable.stackTraceToString())) ?: return
        logger.info { "====== 異常終了通知送信 ${boolean2OKNG(result)} ======" }
    }

    /**
     * [Boolean]をOK/NGに変換する。
     * @param result 結果
     * @return [result]がtrueの場合は「OK」、それ以外の場合は「NG」
     */
    private fun boolean2OKNG(result: Boolean): String {
        return if (result) "OK" else "NG"
    }

}