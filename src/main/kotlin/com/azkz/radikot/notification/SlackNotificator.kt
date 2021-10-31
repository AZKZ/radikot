package com.azkz.radikot.notification

import com.azkz.radikot.RadikotProperties
import com.slack.api.Slack

/**
 * Slackの通知を行うクラス
 */
class SlackNotificator : Notificator {

    val slack = Slack.getInstance()
    val token = RadikotProperties.SLACK_TOKEN

    /**
     * 通知を行う<br>
     * [RadikotProperties.SLACK_CHANNEL]のチャンネルに対して以下の形式で通知します。<br>
     * <pre>
     *     @channel
     *     ${message.subject}
     *     ${message.body}
     * </pre>
     *
     * @return 通知を行えた場合はtrue、それ以外の場合はfalse
     */
    override fun notification(message: Message): Boolean {
        val response = slack.methods(token).chatPostMessage {
            it
                .channel(RadikotProperties.SLACK_CHANNEL)
                .text(
                    """
                <!channel>
                ${message.subject}
                ${message.body}
            """.trimIndent()
                )
        }

        return response.isOk
    }
}