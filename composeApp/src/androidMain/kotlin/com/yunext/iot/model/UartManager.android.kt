package com.yunext.iot.model

actual class UartManagerImpl : UartManager {
    override fun open(path: String, rate: Int): Long {
        TODO("Not yet implemented")
    }

    override fun close(handle: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun list(): List<Com> {
        TODO("Not yet implemented")
    }

    override fun write(handle: Long, data: ByteArray): Int {
        TODO("Not yet implemented")
    }

    override fun read(handle: Long, max: Int): ByteArray {
        TODO("Not yet implemented")
    }
}