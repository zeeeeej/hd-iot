package com.yunext.iot.domain

typealias Com = String

sealed interface ComStatus {
    /* 未检测到 */
    data object DETACH : ComStatus

    /* 离线 */
    data object DISCONNECTED : ComStatus

    /* 在线 */
    data class CONNECTED(val handle: Long) : ComStatus
}

data class ComInfo(val com: Com, val status: ComStatus) {

}

val ComInfo.opened: Boolean
    get() = when (status) {
        is ComStatus.CONNECTED -> true
        ComStatus.DETACH -> false
        ComStatus.DISCONNECTED -> false
    }


interface ComInfoDomain{
    /**
     * 查询本次串口
     */
    suspend fun listComInfo(): List<ComInfo>

    /**
     * 查询串口
     */
    suspend fun findComInfo(com:Com): ComInfo?

    /**
     * 关闭所有串口
     */
    suspend fun deleteAllComInfo()

    /**
     * 删除串口
     */
    suspend fun deleteComInfo(com: Com):List<ComInfo>

    /**
     * 添加串口
     */
    suspend fun addComInfo(com: Com):List<ComInfo>

}

interface ComDomain{
    /**
     * 查询本次串口
     */
    suspend fun list(): List<Com>

    /**
     * 打开串口
     */
    suspend fun open(com: Com, rate: Int): Boolean

    /**
     * 关闭串口
     */
    suspend fun close(com: Com)

    /**
     * 查询串口状态
     */
    suspend fun status(com: Com): ComStatus?

    /**
     * 关闭所有串口
     */
    suspend fun closeAll()

    /**
     * 写
     */
    suspend fun write(com: Com, data: ByteArray): Int

    /**
     * 读
     */
    suspend fun read(com: Com): ByteArray
}