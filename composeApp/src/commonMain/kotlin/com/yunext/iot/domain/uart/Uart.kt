package com.yunext.iot.domain.uart

typealias Uart = String

sealed interface UartStatus {
    /* 未检测到 */
    data object DETACH : UartStatus

    /* 离线 */
    data object DISCONNECTED : UartStatus

    /* 在线 */
    data class CONNECTED(val handle: Long) : UartStatus
}

data class UartInfo(val com: Uart, val rate: Int, val status: UartStatus)

val UartInfo.opened: Boolean
    get() = when (status) {
        is UartStatus.CONNECTED -> true
        UartStatus.DETACH -> false
        UartStatus.DISCONNECTED -> false
    }

