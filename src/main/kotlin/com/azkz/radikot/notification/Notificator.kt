package com.azkz.radikot.notification

/**
 * 通知をする役割
 */
interface Notificator {

    /**
     * 通知を行う
     * @return 通知を行えた場合はtrue、それ以外の場合はfalse
     */
    fun notification(message: Message): Boolean
}