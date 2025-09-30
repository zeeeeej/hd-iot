package com.yunext.iot.model

import NativeFunctions

actual class UartManagerImpl : UartManager {
    override fun open(path: String, rate: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun close(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun list(): List<Com> {
        return NativeFunctions.uartList().map { Com(it) }
    }
}