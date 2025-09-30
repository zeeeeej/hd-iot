package com.yunext.iot.model

data class Com(
    val path: String
)

interface UartManager {
    fun open(path: String, rate: Int): Boolean
    fun close(path: String): Boolean
    fun list(): List<Com>
}

class UartManagerImpl : UartManager {



    override fun open(path: String, rate: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun close(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun list(): List<Com> {
        TODO("Not yet implemented")
    }


}