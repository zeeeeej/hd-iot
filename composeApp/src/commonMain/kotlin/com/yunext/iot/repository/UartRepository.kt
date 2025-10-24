package com.yunext.iot.repository

import com.yunext.iot.datasource.UartDatasource
import com.yunext.iot.domain.Com
import com.yunext.iot.domain.ComDomain
import com.yunext.iot.domain.ComException
import com.yunext.iot.domain.ComInfo
import com.yunext.iot.domain.ComInfoDomain
import com.yunext.iot.domain.ComStatus
import com.yunext.iot.domain.HDErrorCode
import com.yunext.iot.domain.opened
import kotlinx.coroutines.flow.MutableStateFlow

interface UartRepository : ComInfoDomain, ComDomain

class UartRepositoryImpl(private val uartDatasource: UartDatasource) : UartRepository {
    private val _comMap: MutableStateFlow<Map<Com, ComInfo>> = MutableStateFlow(mapOf())

    override suspend fun listComInfo(): List<ComInfo> {
        return _comMap.value.values.toList()
    }

    override suspend fun findComInfo(com: Com): ComInfo? {
        return _comMap.value[com]
    }

    override suspend fun deleteAllComInfo() {
        closeAll()
        _comMap.value = emptyMap()
    }

    override suspend fun deleteComInfo(com: Com): List<ComInfo> {
        close(com)
        val find = _comMap.value[com] ?: return _comMap.value.values.toList()
        val newMap = _comMap.value - find.com
        _comMap.value = newMap
        return _comMap.value.values.toList()
    }

    override suspend fun addComInfo(com: Com): List<ComInfo> {
        val newMap = _comMap.value + (com to ComInfo(
            com = com,
            status = ComStatus.DISCONNECTED,
            rate = DEFAULT_RATE
        ))
        _comMap.value = newMap
        return _comMap.value.values.toList()
    }

    override suspend fun editComInfo(info: ComInfo): List<ComInfo> {
        val find = _comMap.value[info.com] ?: return _comMap.value.values.toList()
        val newMap = _comMap.value + (find.com to info)
        _comMap.value = newMap
        return _comMap.value.values.toList()
    }

    override suspend fun list(): List<Com> {
        return uartDatasource.list()
    }

    override suspend fun open(com: String, rate: Int): Boolean {
        println("UartRepositoryImpl::open")
        printMap()
        val map = _comMap.value.toMutableMap()
        val find: ComInfo = _comMap.value[com] ?: return false
        // 打开串口
        val handle = uartDatasource.open(find.com, find.rate)
        println("open handle = $handle")
        val state = find.copy(status = ComStatus.CONNECTED(handle))
        map[com] = state
        _comMap.value = map
        printMap()
        println("open handle = ${state.opened} ${state.status}")
        return state.opened
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
                if (!v.opened) {
                    print("<$com>已经关闭\n")
                    break
                }
                // 关闭串口
                when (v.status) {
                    is ComStatus.CONNECTED -> {
                        val result = uartDatasource.close(v.status.handle)
                        map[k] = v.copy(status = ComStatus.DISCONNECTED)
                        _comMap.value = map
                    }

                    ComStatus.DETACH -> {

                    }

                    ComStatus.DISCONNECTED -> {

                    }
                }

                break
            }
        }
        printMap()
    }

    override suspend fun status(com: Com): ComStatus? {
        val find: ComInfo = _comMap.value[com] ?: return null
        return find.status
    }

    override suspend fun closeAll() {
        val map = _comMap.value.toMutableMap()
        for ((k, v) in map) {

            when (v.status) {
                is ComStatus.CONNECTED -> {
                    uartDatasource.close(v.status.handle)
                    map[k] = v.copy(status = ComStatus.DISCONNECTED)
                }

                ComStatus.DETACH -> {

                }

                ComStatus.DISCONNECTED -> {

                }
            }


        }
        _comMap.value = map
    }

    override suspend fun write(com: Com, data: ByteArray): Int {
        println("UartRepositoryImpl::write [$com]${data.toHexString()}")
        printMap()
        val find: ComInfo = _comMap.value[com] ?: return 0
        when (find.status) {
            is ComStatus.CONNECTED -> {
                return uartDatasource.write(find.status.handle, data)
            }

            ComStatus.DETACH -> {
                throw ComException("write fail when com is detach", code = HDErrorCode.COM_DETACH)
            }

            ComStatus.DISCONNECTED -> {
                throw ComException(
                    "write fail com is disconnected",
                    code = HDErrorCode.COM_DISCONNECTED
                )
            }
        }

    }

    override suspend fun read(com: Com): ByteArray {
        println("UartRepositoryImpl::read $com")
        printMap()
        val find: ComInfo = _comMap.value[com] ?: return byteArrayOf()
        when (find.status) {
            is ComStatus.CONNECTED -> {
                return uartDatasource.read(find.status.handle, MAX)
            }

            ComStatus.DETACH -> {
                throw ComException("read fail when com is detach", code = HDErrorCode.COM_DETACH)
            }

            ComStatus.DISCONNECTED -> {
                throw ComException(
                    "read fail com is disconnected",
                    code = HDErrorCode.COM_DISCONNECTED
                )
            }
        }

    }

    companion object {
        private const val MAX = 600 * 1024
        private const val DEFAULT_RATE = 460800
    }

}