package com.yunext.iot.model

import com.yunext.iot.domain.uart.Uart
import com.yunext.iot.jni.NativeFunctions

actual class UartManagerImpl : UartManager {
    private val nativeFunctions = NativeFunctions
    override fun open(path: String, rate: Int): Long {
        return nativeFunctions.uartOpen(path, rate)
    }

    override fun close(handle: Long): Boolean {
        nativeFunctions.uartClose(handle)
        return true
    }


    override fun list(): List<Uart> {
        return nativeFunctions.uartList().map { it }
    }

    override fun write(handle: Long, data: ByteArray): Int {
        return nativeFunctions.uartWrite(handle, data)
    }

    override fun writeDelay(handle: Long, data: ByteArray,delay:Int): Int {
        return nativeFunctions.uartWriteDelay(handle, data,delay)
    }

    override fun read(handle: Long, max: Int,timeout:Int): ByteArray {
        return nativeFunctions.uartRead(handle, max)?: byteArrayOf()
    }

    override fun readExpect(handle: Long, expect: Int, timeout: Int): ByteArray {
        return nativeFunctions.uartReading(handle, expect,timeout)?: byteArrayOf()
    }
}