package com.yunext.iot.model

import com.yunext.iot.domain.Com

interface UartManager {
    fun open(path: String, rate: Int): Long
    fun close(handle: Long): Boolean
    fun list(): List<Com>
    fun write(handle: Long, data: ByteArray): Int
    fun read(handle: Long, max: Int): ByteArray
}

expect class UartManagerImpl() : UartManager
