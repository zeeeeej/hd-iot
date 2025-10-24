package com.yunext.iot.model

import NativeFunctions
import com.yunext.iot.domain.Com

actual class UartManagerImpl : UartManager {
    override fun open(path: String, rate: Int): Long {
        return NativeFunctions.uartOpen(path, rate)
    }

    override fun close(handle: Long): Boolean {
        NativeFunctions.uartClose(handle)
        return true
    }


    override fun list(): List<Com> {
        return NativeFunctions.uartList().map { it }
    }

    override fun write(handle: Long, data: ByteArray): Int {
        return NativeFunctions.uartWrite(handle, data)
    }

    override fun read(handle: Long, max: Int): ByteArray {
        return NativeFunctions.uartRead(handle, max)?: byteArrayOf()
    }
}