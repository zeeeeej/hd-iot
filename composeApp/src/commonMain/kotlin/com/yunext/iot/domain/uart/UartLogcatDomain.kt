package com.yunext.iot.domain.uart

import kotlinx.coroutines.flow.StateFlow

interface UartLogcatDomain {
    val logcat: StateFlow<List<UartLogcat>>

    suspend fun clearLogcat(com: Uart)

    suspend fun clearAllLogcat()
}