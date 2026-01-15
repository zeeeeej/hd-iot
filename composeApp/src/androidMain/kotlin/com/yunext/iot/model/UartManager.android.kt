package com.yunext.iot.model

import com.yunext.iot.domain.uart.Uart

actual class UartManagerImpl : UartManager {
    override fun open(path: String, rate: Int): Long {
        TODO("Not yet implemented")
    }

    override fun close(handle: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun list(): List<Uart> {
        TODO("Not yet implemented")
    }

    override fun write(handle: Long, data: ByteArray): Int {
        TODO("Not yet implemented")
    }

    override fun writeDelay(handle: Long, data: ByteArray, delay: Int):Int {
        TODO("Not yet implemented")
    }

    override fun read(handle: Long, max: Int,timeout:Int): ByteArray {
        TODO("Not yet implemented")
    }

    override fun readExpect(handle: Long, expect: Int, timeout: Int): ByteArray {
        TODO("Not yet implemented")
    }
}