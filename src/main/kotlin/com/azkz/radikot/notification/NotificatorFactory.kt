package com.azkz.radikot.notification

import com.azkz.radikot.RadikotProperties

/**
 * 通知インスタンスを作成するオブジェクト
 */
object NotificatorFactory {

    fun create(): Notificator? {
        // Slackのプロパティ値が設定されている場合
        if (RadikotProperties.SLACK_TOKEN != null && RadikotProperties.SLACK_CHANNEL != null) {
            return SlackNotificator()
        }

        return null
    }

}

