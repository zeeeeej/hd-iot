package com.yunext.iot.platform

import kotlinx.coroutines.flow.Flow

/**
 * 哈希计算结果
 */
data class HashResult(
    val hash: String,
    val algorithm: String,
    val fileSize: Long,
    val timeCost: Long, // 毫秒
    val success: Boolean,
    val errorMessage: String? = null
)

/**
 * 哈希计算进度
 */
data class HashProgress(
    val bytesProcessed: Long,
    val totalBytes: Long,
    val percentage: Float,
    val currentSpeed: Double // KB/s
)

/**
 * 文件哈希计算器接口
 */
interface FileHasher {
    /**
     * 计算文件的 MD5 哈希值
     */
    suspend fun calculateMD5(filePath: String): HashResult

    /**
     * 计算文件的 MD5 哈希值（带进度）
     */
    fun calculateMD5WithProgress(filePath: String): Flow<Pair<HashProgress, HashResult?>>

    /**
     * 计算文件的 SHA-1 哈希值
     */
    suspend fun calculateSHA1(filePath: String): HashResult

    /**
     * 计算文件的 SHA-256 哈希值
     */
    suspend fun calculateSHA256(filePath: String): HashResult

    /**
     * 批量计算文件哈希
     */
    suspend fun batchCalculate(files: List<String>, algorithm: String = "MD5"): List<HashResult>

    /**
     * 验证文件哈希
     */
    suspend fun verifyHash(filePath: String, expectedHash: String, algorithm: String = "MD5"): Boolean
}

/**
 * 平台哈希计算器工厂
 */
expect object FileHasherFactory {
    fun create(): FileHasher
}