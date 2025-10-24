package com.yunext.iot.repository

import com.yunext.iot.datasource.UartDatasource
import com.yunext.iot.model.Com
import kotlinx.coroutines.flow.MutableStateFlow

interface UartRepository {
    suspend fun list(): List<Com>
    suspend fun open(com: Com, rate: Int): Boolean
    suspend fun close(com: Com)
    suspend fun status(com: Com): Boolean
    suspend fun closeAll()
    suspend fun clear()
    suspend fun write(com: Com, data: ByteArray): Int
    suspend fun read(com: Com): ByteArray
}

data class UartState(val com: Com, val handle: Long = -1)

val UartState.open: Boolean
    get() = handle > 0L

class UartRepositoryImpl(private val uartDatasource: UartDatasource) : UartRepository {
    private val _comMap: MutableStateFlow<Map<Com, UartState>> = MutableStateFlow(mapOf())

    override suspend fun list(): List<Com> {
        return uartDatasource.list()
    }

    override suspend fun open(com: String, rate: Int): Boolean {
        println("UartRepositoryImpl::open")
        printMap()
        val map = _comMap.value.toMutableMap()
        for ((k, v) in map) {
            if (k == com) {
                if (v.open) {
                    print("<$com>已经开启\n")
                    break
                }

                break
            }
        }
        // 打开串口
        val handle = uartDatasource.open(com, rate)
        println("open handle = $handle")
        val state =  UartState(com, handle)
        map[com] =state
        _comMap.value = map
        printMap()
        println("open handle = ${state.open} ${state.handle}")
        return state.open
    }

    private fun printMap() {
        println("printMap-----")
        for ((k, v) in _comMap.value) {
            println("$k => $v")
        }
        println("printMap--------------")
    }

    override suspend fun close(com: Com) {
        println("UartRepositoryImpl::close")
        printMap()
        val map = _comMap.value.toMutableMap()
        for ((k, v) in map) {
            if (k == com) {
                if (!v.open) {
                    print("<$com>已经关闭\n")
                    break
                }
                // 关闭串口
                val result = uartDatasource.close(v.handle)
                map[k] = UartState("",-1)
                _comMap.value = map
                break
            }
        }
        printMap()
    }

    override suspend fun status(com: Com): Boolean {
        val find: UartState = _comMap.value[com] ?: return false
        return find.open
    }

    override suspend fun closeAll() {
        val map = _comMap.value.toMutableMap()
        for ((k, v) in map) {
            uartDatasource.close(v.handle)
            map[k] = UartState("", -1)
        }
        _comMap.value = map
    }

    override suspend fun clear() {
        closeAll()
        _comMap.value = emptyMap()
    }

    override suspend fun write(com: Com, data: ByteArray): Int {
        println("UartRepositoryImpl::write [$com]${data.toHexString()}")
        printMap()
        val find: UartState = _comMap.value[com] ?: return 0
        return uartDatasource.write(find.handle, data)
    }

    override suspend fun read(com: Com): ByteArray {
        println("UartRepositoryImpl::read $com")
        printMap()
        val find: UartState = _comMap.value[com] ?: return byteArrayOf()
        return uartDatasource.read(find.handle, MAX)
    }

    companion object {
        private const val MAX = 600 * 1024
    }

}