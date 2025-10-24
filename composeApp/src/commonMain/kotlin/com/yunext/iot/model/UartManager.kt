package com.yunext.iot.model

//data class Com(
//    val path: String,
//    val handle: Long
//)

typealias Com = String

interface UartManager {
    fun open(path: String, rate: Int): Long
    fun close(handle: Long): Boolean
    fun list(): List<Com>
    fun write(handle: Long, data: ByteArray): Int
    fun read(handle: Long, max: Int): ByteArray
}

expect class UartManagerImpl() : UartManager
