package com.yunext.iot.domain.uart

interface UartDomain {


    /**
     * 查询本次串口
     */
    suspend fun list(): List<Uart>

    /**
     * 打开串口
     */
    suspend fun open(com: Uart, rate: Int): Boolean

    /**
     * 关闭串口
     */
    suspend fun close(com: Uart)

    /**
     * 查询串口状态
     */
    suspend fun status(com: Uart): UartStatus?

    /**
     * 关闭所有串口
     */
    suspend fun closeAll()

    /**
     * 写
     */
    suspend fun writeDelay(com: Uart, data: ByteArray,delay:Int = 10000): Int
    suspend fun write(com: Uart, data: ByteArray): Int

    /**
     * 读
     */
    suspend fun read(com: Uart,timeout:Int ): ByteArray
    suspend fun readExpect(com: Uart,expect:Int,timeout:Int ): ByteArray

}