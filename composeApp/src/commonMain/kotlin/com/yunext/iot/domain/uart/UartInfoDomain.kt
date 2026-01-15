package com.yunext.iot.domain.uart
interface UartInfoDomain {
    /**
     * 查询本次串口
     */
    suspend fun listInfo(): List<UartInfo>

    /**
     * 查询串口
     */
    suspend fun find(com: Uart): UartInfo?

    /**
     * 关闭所有串口
     */
    suspend fun deleteAll()

    /**
     * 删除串口
     */
    suspend fun delete(com: Uart): List<UartInfo>

    /**
     * 添加串口
     */
    suspend fun add(com: Uart): List<UartInfo>

    /**
     * 添加串口
     */
    suspend fun edit(info: UartInfo): List<UartInfo>

}