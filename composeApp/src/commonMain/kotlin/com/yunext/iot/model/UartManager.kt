package com.yunext.iot.model

data class Com(
    val path: String
)

 interface UartManager {
    fun open(path: String, rate: Int): Boolean
    fun close(path: String): Boolean
    fun list(): List<Com>
}

expect class UartManagerImpl():UartManager
