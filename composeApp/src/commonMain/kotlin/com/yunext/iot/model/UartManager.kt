package com.yunext.iot.model

import com.yunext.iot.domain.uart.Uart

interface UartManager {
    fun open(path: String, rate: Int): Long
    fun close(handle: Long): Boolean
    fun list(): List<Uart>
    fun write(handle: Long, data: ByteArray): Int
    fun writeDelay(handle: Long, data: ByteArray,delay:Int):Int
    fun read(handle: Long, max: Int,timeout:Int): ByteArray
    fun readExpect(handle: Long, expect: Int,timeout:Int): ByteArray
}

expect class UartManagerImpl() : UartManager
