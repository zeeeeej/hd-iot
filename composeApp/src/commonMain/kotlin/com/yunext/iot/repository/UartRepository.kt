package com.yunext.iot.repository

import com.yunext.iot.datasource.UartDatasource
import com.yunext.iot.model.Com
import kotlinx.coroutines.flow.MutableStateFlow

interface UartRepository {
    suspend fun list(): List<Com>
    suspend fun open(path: String, rate: Int)
    suspend fun close(path: String)
    suspend fun closeAll()
    suspend fun clear()
}

class UartRepositoryImpl(private val uartDatasource: UartDatasource) : UartRepository {
    private val _comMap: MutableStateFlow<Map<Com, Boolean>> = MutableStateFlow(mapOf())

    override suspend fun list(): List<Com> {
        return uartDatasource.list()
    }

    override suspend fun open(path: String, rate: Int) {
        val map = _comMap.value.toMutableMap()
        for ((k, v) in map) {
            if (k.path == path) {
                if (v) {
                    print("<$path>已经开启\n")
                    break
                }
                // 打开串口
                val result = uartDatasource.open(path, rate)
                map[k] = result
                _comMap.value = map
                break
            }
        }

    }

    override suspend fun close(path: String) {
        val map = _comMap.value.toMutableMap()
        for ((k, v) in map) {
            if (k.path == path) {
                if (!v) {
                    print("<$path>已经关闭\n")
                    break
                }
                // 关闭串口
                val result = uartDatasource.close(path)
                map[k] = false
                _comMap.value = map
                break
            }
        }
    }

    override suspend fun closeAll() {
        val map = _comMap.value.toMutableMap()
        for ((k, v) in map) {
            uartDatasource.close(k.path)
            map[k] = false
        }
        _comMap.value = map
    }

    override suspend fun clear() {
        closeAll()
        _comMap.value = emptyMap()
    }

}