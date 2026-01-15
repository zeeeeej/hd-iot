package com.yunext.iot.domain.uart

open class HDException(msg: String, val code: Int = -1, cause: Throwable? = null) :
    Throwable(message = msg, cause = cause)

class UartException(msg: String, code: Int = -1, cause: Throwable? = null) :
    HDException(msg = msg, cause = cause, code = code)


object HDErrorCode{
    const val UART_DETACH            = 0xC001
    const val UART_DISCONNECTED      = 0xC002
}
