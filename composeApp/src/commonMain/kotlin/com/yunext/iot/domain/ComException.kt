package com.yunext.iot.domain

open class HDException(msg: String, val code: Int = -1, cause: Throwable? = null) :
    Throwable(message = msg, cause = cause)

class ComException(msg: String, code: Int = -1, cause: Throwable? = null) :
    HDException(msg = msg, cause = cause, code = code)


object HDErrorCode{
    const val COM_DETACH            = 0xC001
    const val COM_DISCONNECTED      = 0xC002
}
