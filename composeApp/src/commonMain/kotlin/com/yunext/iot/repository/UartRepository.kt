package com.yunext.iot.repository

import com.yunext.iot.datasource.UartDatasource
import com.yunext.iot.domain.protocol.display
import com.yunext.iot.domain.uart.Uart
import com.yunext.iot.domain.uart.UartDomain
import com.yunext.iot.domain.uart.UartException
import com.yunext.iot.domain.uart.UartInfo
import com.yunext.iot.domain.uart.UartInfoDomain
import com.yunext.iot.domain.uart.UartLogcat
import com.yunext.iot.domain.uart.UartLogcatDomain
import com.yunext.iot.domain.uart.UartStatus
import com.yunext.iot.domain.uart.HDErrorCode
import com.yunext.iot.domain.uart.InputLogcat
import com.yunext.iot.domain.uart.NormalLogcat
import com.yunext.iot.domain.uart.OutputLogcat
import com.yunext.iot.domain.uart.opened
import com.yunext.kotlin.kmp.common.util.currentTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface UartRepository : UartInfoDomain, UartDomain, UartLogcatDomain

class UartRepositoryImpl(private val uartDatasource: UartDatasource) : UartRepository {

    private val _logcat: MutableStateFlow<List<UartLogcat>> = MutableStateFlow(emptyList())
    private val _comMap: MutableStateFlow<Map<Uart, UartInfo>> = MutableStateFlow(mapOf())

    override val logcat: StateFlow<List<UartLogcat>> = _logcat.asStateFlow()
    override suspend fun clearLogcat(com: Uart) {
        val latest = _logcat.value.filter {
            it.com != com
        }
        _logcat.value = latest
    }

    override suspend fun clearAllLogcat() {
        _logcat.value = emptyList()
    }

    override suspend fun listInfo(): List<UartInfo> {
        return _comMap.value.values.toList()
    }

    override suspend fun find(com: Uart): UartInfo? {
        return _comMap.value[com]
    }

    private fun addLogcat(logcat: UartLogcat) {
        val find: UartInfo = _comMap.value[logcat.com] ?: return
        val latest = _logcat.value
        _logcat.value = listOf(logcat) + latest
    }

    override suspend fun deleteAll() {
        closeAll()
        _comMap.value = emptyMap()
        addLogcat(NormalLogcat(com = "", timestamps = currentTime(), "deleteAllComInfo"))
    }

    override suspend fun delete(com: Uart): List<UartInfo> {
        close(com)
        val find = _comMap.value[com] ?: return _comMap.value.values.toList()
        val newMap = _comMap.value - find.com
        _comMap.value = newMap
        addLogcat(NormalLogcat(com = com, timestamps = currentTime(), "deleteComInfo:$com"))
        return _comMap.value.values.toList()
    }

    override suspend fun add(com: Uart): List<UartInfo> {
        val newMap = _comMap.value + (com to UartInfo(
            com = com,
            status = UartStatus.DISCONNECTED,
            rate = DEFAULT_RATE
        ))
        _comMap.value = newMap
        addLogcat(NormalLogcat(com = com, timestamps = currentTime(), "addComInfo:$com"))
        return _comMap.value.values.toList()
    }

    override suspend fun edit(info: UartInfo): List<UartInfo> {
        val find = _comMap.value[info.com] ?: return _comMap.value.values.toList()
        val newMap = _comMap.value + (find.com to info)
        _comMap.value = newMap
        addLogcat(NormalLogcat(com = info.com, timestamps = currentTime(), "editComInfo:$info"))
        return _comMap.value.values.toList()
    }

    override suspend fun list(): List<Uart> {
        return uartDatasource.list()
    }

    override suspend fun open(com: String, rate: Int): Boolean {
        println("UartRepositoryImpl::open")
        printMap()
        val map = _comMap.value.toMutableMap()
        val find: UartInfo = _comMap.value[com] ?: return false
        // 打开串口
        val handle = uartDatasource.open(find.com, find.rate)
        println("open handle = $handle")
        val state = find.copy(status = UartStatus.CONNECTED(handle))
        map[com] = state
        _comMap.value = map
        printMap()
        println("open handle = ${state.opened} ${state.status}")
        addLogcat(
            NormalLogcat(
                com = com,
                timestamps = currentTime(),
                "open $com ${find.rate} status = $state"
            )
        )
        return state.opened
    }

    private fun printMap() {
        println("printMap-----")
        for ((k, v) in _comMap.value) {
            println("$k => $v")
        }
        println("printMap--------------")
    }

    override suspend fun close(com: Uart) {
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
                    is UartStatus.CONNECTED -> {
                        val result = uartDatasource.close(v.status.handle)
                        map[k] = v.copy(status = UartStatus.DISCONNECTED)
                        _comMap.value = map
                    }

                    UartStatus.DETACH -> {

                    }

                    UartStatus.DISCONNECTED -> {

                    }
                }

                break
            }
        }
        printMap()
        addLogcat(NormalLogcat(com = com, timestamps = currentTime(), "close $com"))
    }

    override suspend fun status(com: Uart): UartStatus? {
        val find: UartInfo = _comMap.value[com] ?: return null
        return find.status
    }

    override suspend fun closeAll() {
        val map = _comMap.value.toMutableMap()
        for ((k, v) in map) {

            when (v.status) {
                is UartStatus.CONNECTED -> {
                    uartDatasource.close(v.status.handle)
                    map[k] = v.copy(status = UartStatus.DISCONNECTED)
                }

                UartStatus.DETACH -> {

                }

                UartStatus.DISCONNECTED -> {

                }
            }


        }
        addLogcat(NormalLogcat(com = "", timestamps = currentTime(), "closeAll"))
        _comMap.value = map
    }

    override suspend fun writeDelay(com: Uart, data: ByteArray,delay:Int): Int {
        println("UartRepositoryImpl::write [$com]${data.display()}")
        printMap()
        val find: UartInfo = _comMap.value[com] ?: return 0

        when (find.status) {
            is UartStatus.CONNECTED -> {
                val write = uartDatasource.writeDelay(find.status.handle, data,delay)
                // addLogcat(NormalLogcat(com = com, timestamps = currentTime(), "write $write"))
                addLogcat(OutputLogcat(com = com, timestamps = currentTime(), data))
                return write
            }

            UartStatus.DETACH -> {
                addLogcat(
                    NormalLogcat(
                        com = com,
                        timestamps = currentTime(),
                        "write ComStatus.DETACH ${HDErrorCode.UART_DETACH}"
                    )
                )
                throw UartException("write fail when com is detach", code = HDErrorCode.UART_DETACH)
            }

            UartStatus.DISCONNECTED -> {
                addLogcat(
                    NormalLogcat(
                        com = com,
                        timestamps = currentTime(),
                        "write ComStatus.DISCONNECTED ${HDErrorCode.UART_DISCONNECTED}"
                    )
                )
                throw UartException(
                    "write fail com is disconnected",
                    code = HDErrorCode.UART_DISCONNECTED
                )
            }
        }
    }
    override suspend fun write(com: Uart, data: ByteArray): Int {
        println("UartRepositoryImpl::write [$com]${data.display()}")
        printMap()
        val find: UartInfo = _comMap.value[com] ?: return 0

        when (find.status) {
            is UartStatus.CONNECTED -> {
                val write = uartDatasource.write(find.status.handle, data)
                // addLogcat(NormalLogcat(com = com, timestamps = currentTime(), "write $write"))
                addLogcat(OutputLogcat(com = com, timestamps = currentTime(), data))
                return write
            }

            UartStatus.DETACH -> {
                addLogcat(
                    NormalLogcat(
                        com = com,
                        timestamps = currentTime(),
                        "write ComStatus.DETACH ${HDErrorCode.UART_DETACH}"
                    )
                )
                throw UartException("write fail when com is detach", code = HDErrorCode.UART_DETACH)
            }

            UartStatus.DISCONNECTED -> {
                addLogcat(
                    NormalLogcat(
                        com = com,
                        timestamps = currentTime(),
                        "write ComStatus.DISCONNECTED ${HDErrorCode.UART_DISCONNECTED}"
                    )
                )
                throw UartException(
                    "write fail com is disconnected",
                    code = HDErrorCode.UART_DISCONNECTED
                )
            }
        }


    }

    override suspend fun read(com: Uart,timeout:Int): ByteArray {
        println("UartRepositoryImpl::read $com")
        printMap()
        val find: UartInfo = _comMap.value[com] ?: return byteArrayOf()
        when (find.status) {
            is UartStatus.CONNECTED -> {
                val data = uartDatasource.read(find.status.handle, MAX,timeout)
                if (data.isNotEmpty()) {
                    addLogcat(InputLogcat(com = com, timestamps = currentTime(), data))
                }
                return data
            }

            UartStatus.DETACH -> {
                addLogcat(
                    NormalLogcat(
                        com = com,
                        timestamps = currentTime(),
                        "read ComStatus.DETACH ${HDErrorCode.UART_DETACH}"
                    )
                )
                throw UartException("read fail when com is detach", code = HDErrorCode.UART_DETACH)
            }

            UartStatus.DISCONNECTED -> {
                addLogcat(
                    NormalLogcat(
                        com = com,
                        timestamps = currentTime(),
                        "read ComStatus.DISCONNECTED ${HDErrorCode.UART_DISCONNECTED}"
                    )
                )

                throw UartException(
                    "read fail com is disconnected",
                    code = HDErrorCode.UART_DISCONNECTED
                )
            }
        }
    }

    override suspend fun readExpect(com: Uart, expect: Int, timeout: Int): ByteArray {
        println("UartRepositoryImpl::readExpect $com")
        printMap()
        val find: UartInfo = _comMap.value[com] ?: return byteArrayOf()
        when (find.status) {
            is UartStatus.CONNECTED -> {
                val data = uartDatasource.readExpect(find.status.handle, expect,timeout)
                if (data.isNotEmpty()) {
                    addLogcat(InputLogcat(com = com, timestamps = currentTime(), data))
                }
                return data
            }

            UartStatus.DETACH -> {
                addLogcat(
                    NormalLogcat(
                        com = com,
                        timestamps = currentTime(),
                        "read ComStatus.DETACH ${HDErrorCode.UART_DETACH}"
                    )
                )
                throw UartException("read fail when com is detach", code = HDErrorCode.UART_DETACH)
            }

            UartStatus.DISCONNECTED -> {
                addLogcat(
                    NormalLogcat(
                        com = com,
                        timestamps = currentTime(),
                        "read ComStatus.DISCONNECTED ${HDErrorCode.UART_DISCONNECTED}"
                    )
                )

                throw UartException(
                    "read fail com is disconnected",
                    code = HDErrorCode.UART_DISCONNECTED
                )
            }
        }
    }

    companion object {
        private const val MAX = 600 * 1024
        private const val DEFAULT_RATE = 460800
    }

}