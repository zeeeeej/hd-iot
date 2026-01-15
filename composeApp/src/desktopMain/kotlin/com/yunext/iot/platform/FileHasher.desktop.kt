package com.yunext.iot.platform

// desktopMain/kotlin/FileHasher.kt
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileInputStream
import java.io.BufferedInputStream
import java.security.MessageDigest
import java.security.DigestInputStream
import kotlin.math.roundToInt

/**
 * 桌面端文件哈希计算器实现
 */
class DesktopFileHasher : FileHasher {

    override suspend fun calculateMD5(filePath: String): HashResult = withContext(Dispatchers.IO) {
        return@withContext calculateHash(filePath, "MD5")
    }

    override fun calculateMD5WithProgress(filePath: String): Flow<Pair<HashProgress, HashResult?>> = flow {
        val file = File(filePath)
        if (!file.exists()) {
            emit(Pair(HashProgress(0, 0, 0f, 0.0), null))
            return@flow
        }

        val totalBytes = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val digest = MessageDigest.getInstance("MD5")
        var bytesProcessed = 0L
        var lastUpdateTime = System.currentTimeMillis()
        var lastBytesProcessed = 0L

        FileInputStream(file).use { fis ->
            BufferedInputStream(fis).use { bis ->
                var bytesRead: Int
                while (bis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                    bytesProcessed += bytesRead

                    val currentTime = System.currentTimeMillis()
                    val timeDiff = currentTime - lastUpdateTime

                    if (timeDiff >= 100) { // 每100ms更新一次进度
                        val bytesDiff = bytesProcessed - lastBytesProcessed
                        val speed = if (timeDiff > 0) {
                            (bytesDiff / timeDiff.toDouble()) * 1000 / 1024 // KB/s
                        } else 0.0

                        emit(
                            Pair(
                                HashProgress(
                                    bytesProcessed = bytesProcessed,
                                    totalBytes = totalBytes,
                                    percentage = (bytesProcessed.toFloat() / totalBytes * 100),
                                    currentSpeed = speed
                                ),
                                null
                            )
                        )

                        lastUpdateTime = currentTime
                        lastBytesProcessed = bytesProcessed
                    }
                }
            }
        }

        val hash = digest.digest().joinToString("") { "%02x".format(it) }
        val result = HashResult(
            hash = hash,
            algorithm = "MD5",
            fileSize = totalBytes,
            timeCost = System.currentTimeMillis() - lastUpdateTime,
            success = true
        )

        emit(Pair(
            HashProgress(
                bytesProcessed = totalBytes,
                totalBytes = totalBytes,
                percentage = 100f,
                currentSpeed = 0.0
            ),
            result
        ))
    }.flowOn(Dispatchers.IO)

    override suspend fun calculateSHA1(filePath: String): HashResult = withContext(Dispatchers.IO) {
        return@withContext calculateHash(filePath, "SHA-1")
    }

    override suspend fun calculateSHA256(filePath: String): HashResult = withContext(Dispatchers.IO) {
        return@withContext calculateHash(filePath, "SHA-256")
    }

    override suspend fun batchCalculate(files: List<String>, algorithm: String): List<HashResult> {
        return coroutineScope {
            files.map { filePath ->
                async(Dispatchers.IO) {
                    calculateHash(filePath, algorithm)
                }
            }.awaitAll()
        }
    }

    override suspend fun verifyHash(filePath: String, expectedHash: String, algorithm: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = calculateHash(filePath, algorithm)
                result.hash.equals(expectedHash, ignoreCase = true)
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * 计算文件哈希（核心方法）
     */
    private fun calculateHash(filePath: String, algorithm: String): HashResult {
        val startTime = System.currentTimeMillis()
        val file = File(filePath)

        if (!file.exists()) {
            return HashResult(
                hash = "",
                algorithm = algorithm,
                fileSize = 0,
                timeCost = 0,
                success = false,
                errorMessage = "文件不存在: $filePath"
            )
        }

        if (!file.isFile) {
            return HashResult(
                hash = "",
                algorithm = algorithm,
                fileSize = 0,
                timeCost = 0,
                success = false,
                errorMessage = "不是文件: $filePath"
            )
        }

        val fileSize = file.length()

        try {
            val digest = MessageDigest.getInstance(algorithm)
            FileInputStream(file).use { fis ->
                BufferedInputStream(fis).use { bis ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytesRead: Int
                    while (bis.read(buffer).also { bytesRead = it } != -1) {
                        digest.update(buffer, 0, bytesRead)
                    }
                }
            }

            val hash = digest.digest().joinToString("") { "%02x".format(it) }
            val timeCost = System.currentTimeMillis() - startTime

            return HashResult(
                hash = hash,
                algorithm = algorithm,
                fileSize = fileSize,
                timeCost = timeCost,
                success = true
            )

        } catch (e: Exception) {
            return HashResult(
                hash = "",
                algorithm = algorithm,
                fileSize = fileSize,
                timeCost = System.currentTimeMillis() - startTime,
                success = false,
                errorMessage = "计算哈希时出错: ${e.message}"
            )
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8192
    }
}

actual object FileHasherFactory {
    actual fun create(): FileHasher = DesktopFileHasher()
}

// // desktopMain/kotlin/NioFileHasher.kt
//import kotlinx.coroutines.*
//import kotlinx.coroutines.flow.*
//import java.nio.file.*
//import java.nio.file.attribute.BasicFileAttributes
//import java.security.MessageDigest
//import kotlin.io.path.*
//import java.util.HexFormat
//
///**
// * 使用 Java NIO 的文件哈希计算器（性能更好）
// */
//class NioFileHasher : FileHasher {
//
//    override suspend fun calculateMD5(filePath: String): HashResult = withContext(Dispatchers.IO) {
//        return@withContext calculateHashNio(Paths.get(filePath), "MD5")
//    }
//
//    override fun calculateMD5WithProgress(filePath: String): Flow<Pair<HashProgress, HashResult?>> {
//        return callbackFlow {
//            val path = Paths.get(filePath)
//            if (!path.exists()) {
//                send(Pair(HashProgress(0, 0, 0f, 0.0), null))
//                close()
//                return@callbackFlow
//            }
//
//            val fileSize = path.fileSize()
//            val buffer = ByteArray(8192)
//            val digest = MessageDigest.getInstance("MD5")
//            var bytesProcessed = 0L
//            var lastUpdateTime = System.currentTimeMillis()
//            var lastBytesProcessed = 0L
//
//            path.inputStream().use { inputStream ->
//                var bytesRead: Int
//                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                    digest.update(buffer, 0, bytesRead)
//                    bytesProcessed += bytesRead
//
//                    val currentTime = System.currentTimeMillis()
//                    val timeDiff = currentTime - lastUpdateTime
//
//                    if (timeDiff >= 100) {
//                        val bytesDiff = bytesProcessed - lastBytesProcessed
//                        val speed = if (timeDiff > 0) {
//                            bytesDiff / timeDiff.toDouble() * 1000 / 1024
//                        } else 0.0
//
//                        trySend(
//                            Pair(
//                                HashProgress(
//                                    bytesProcessed = bytesProcessed,
//                                    totalBytes = fileSize,
//                                    percentage = bytesProcessed.toFloat() / fileSize * 100,
//                                    currentSpeed = speed
//                                ),
//                                null
//                            )
//                        )
//
//                        lastUpdateTime = currentTime
//                        lastBytesProcessed = bytesProcessed
//                    }
//                }
//            }
//
//            val hash = HexFormat.of().formatHex(digest.digest())
//            val result = HashResult(
//                hash = hash,
//                algorithm = "MD5",
//                fileSize = fileSize,
//                timeCost = System.currentTimeMillis() - lastUpdateTime,
//                success = true
//            )
//
//            send(Pair(
//                HashProgress(
//                    bytesProcessed = fileSize,
//                    totalBytes = fileSize,
//                    percentage = 100f,
//                    currentSpeed = 0.0
//                ),
//                result
//            ))
//
//            close()
//        }.flowOn(Dispatchers.IO)
//    }
//
//    override suspend fun calculateSHA1(filePath: String): HashResult = withContext(Dispatchers.IO) {
//        return@withContext calculateHashNio(Paths.get(filePath), "SHA-1")
//    }
//
//    override suspend fun calculateSHA256(filePath: String): HashResult = withContext(Dispatchers.IO) {
//        return@withContext calculateHashNio(Paths.get(filePath), "SHA-256")
//    }
//
//    override suspend fun batchCalculate(files: List<String>, algorithm: String): List<HashResult> {
//        return coroutineScope {
//            files.map { filePath ->
//                async(Dispatchers.IO) {
//                    calculateHashNio(Paths.get(filePath), algorithm)
//                }
//            }.awaitAll()
//        }
//    }
//
//    override suspend fun verifyHash(filePath: String, expectedHash: String, algorithm: String): Boolean {
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = calculateHashNio(Paths.get(filePath), algorithm)
//                result.hash.equals(expectedHash, ignoreCase = true)
//            } catch (e: Exception) {
//                false
//            }
//        }
//    }
//
//    /**
//     * 使用 NIO 计算文件哈希
//     */
//    private fun calculateHashNio(path: Path, algorithm: String): HashResult {
//        val startTime = System.currentTimeMillis()
//
//        if (!path.exists()) {
//            return HashResult(
//                hash = "",
//                algorithm = algorithm,
//                fileSize = 0,
//                timeCost = 0,
//                success = false,
//                errorMessage = "文件不存在: $path"
//            )
//        }
//
//        if (!path.isRegularFile()) {
//            return HashResult(
//                hash = "",
//                algorithm = algorithm,
//                fileSize = 0,
//                timeCost = 0,
//                success = false,
//                errorMessage = "不是普通文件: $path"
//            )
//        }
//
//        val fileSize = path.fileSize()
//
//        try {
//            val digest = MessageDigest.getInstance(algorithm)
//            path.inputStream().use { inputStream ->
//                val buffer = ByteArray(8192)
//                var bytesRead: Int
//                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                    digest.update(buffer, 0, bytesRead)
//                }
//            }
//
//            val hash = HexFormat.of().formatHex(digest.digest())
//            val timeCost = System.currentTimeMillis() - startTime
//
//            return HashResult(
//                hash = hash,
//                algorithm = algorithm,
//                fileSize = fileSize,
//                timeCost = timeCost,
//                success = true
//            )
//
//        } catch (e: Exception) {
//            return HashResult(
//                hash = "",
//                algorithm = algorithm,
//                fileSize = fileSize,
//                timeCost = System.currentTimeMillis() - startTime,
//                success = false,
//                errorMessage = "计算哈希时出错: ${e.message}"
//            )
//        }
//    }
//
//    /**
//     * 计算目录下所有文件的哈希
//     */
//    suspend fun calculateDirectoryHash(
//        directoryPath: String,
//        algorithm: String = "MD5",
//        recursive: Boolean = true
//    ): Map<String, HashResult> = withContext(Dispatchers.IO) {
//        val result = mutableMapOf<String, HashResult>()
//        val dir = Paths.get(directoryPath)
//
//        if (!dir.exists() || !dir.isDirectory()) {
//            return@withContext result
//        }
//
//        val visitor = object : SimpleFileVisitor<Path>() {
//            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
//                if (file.isRegularFile()) {
//                    val hashResult = calculateHashNio(file, algorithm)
//                    result[file.toString()] = hashResult
//                }
//                return FileVisitResult.CONTINUE
//            }
//
//            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
//                return if (recursive || dir == Paths.get(directoryPath)) {
//                    FileVisitResult.CONTINUE
//                } else {
//                    FileVisitResult.SKIP_SUBTREE
//                }
//            }
//        }
//
//        Files.walkFileTree(dir, visitor)
//        result
//    }
//}

// // 使用示例
//class FileHashExample {
//    private val fileHasher = FileHasherFactory.create()
//
//    suspend fun demonstrateFileHashing() {
//        // 示例1: 计算单个文件的 MD5
//        val filePath = "/path/to/your/file.txt"
//
//        println("正在计算文件哈希...")
//
//        // 方法1: 简单计算
//        val result1 = fileHasher.calculateMD5(filePath)
//        if (result1.success) {
//            println("MD5: ${result1.hash}")
//            println("文件大小: ${result1.fileSize} bytes")
//            println("计算时间: ${result1.timeCost}ms")
//        }
//
//        // 方法2: 带进度计算
//        fileHasher.calculateMD5WithProgress(filePath).collect { (progress, result) ->
//            if (result != null) {
//                println("计算完成! MD5: ${result.hash}")
//            } else {
//                println("进度: ${"%.2f".format(progress.percentage)}% | " +
//                       "速度: ${"%.2f".format(progress.currentSpeed)} KB/s")
//            }
//        }
//
//        // 方法3: 计算其他哈希算法
//        val sha1Result = fileHasher.calculateSHA1(filePath)
//        val sha256Result = fileHasher.calculateSHA256(filePath)
//
//        println("SHA-1: ${sha1Result.hash}")
//        println("SHA-256: ${sha256Result.hash}")
//
//        // 方法4: 批量计算
//        val files = listOf("/path/file1.txt", "/path/file2.txt", "/path/file3.txt")
//        val batchResults = fileHasher.batchCalculate(files, "MD5")
//
//        batchResults.forEachIndexed { index, result ->
//            println("文件 ${index + 1}: ${result.hash}")
//        }
//
//        // 方法5: 验证哈希
//        val expectedHash = "d41d8cd98f00b204e9800998ecf8427e"
//        val isValid = fileHasher.verifyHash(filePath, expectedHash, "MD5")
//        println("哈希验证: ${if (isValid) "通过" else "失败"}")
//
//        // 方法6: 使用扩展函数
//        val file = File(filePath)
//        val md5ByExtension = file.calculateMD5()
//        println("使用扩展函数计算的 MD5: $md5ByExtension")
//
//        // 方法7: 计算字符串哈希
//        val text = "Hello, World!"
//        val textMD5 = text.calculateMD5()
//        val textSHA256 = text.calculateSHA256()
//        println("文本 MD5: $textMD5")
//        println("文本 SHA-256: $textSHA256")
//    }
//
//    /**
//     * 比较两个文件是否相同
//     */
//    suspend fun compareFiles(filePath1: String, filePath2: String): Boolean {
//        val hash1 = fileHasher.calculateMD5(filePath1)
//        val hash2 = fileHasher.calculateMD5(filePath2)
//
//        if (!hash1.success || !hash2.success) {
//            return false
//        }
//
//        return hash1.hash == hash2.hash
//    }
//
//    /**
//     * 查找重复文件
//     */
//    suspend fun findDuplicateFiles(directory: String): Map<String, List<String>> {
//        val files = File(directory).walk()
//            .filter { it.isFile }
//            .toList()
//
//        val fileHashes = fileHasher.batchCalculate(files.map { it.path }, "MD5")
//
//        val hashMap = mutableMapOf<String, MutableList<String>>()
//        fileHashes.forEachIndexed { index, result ->
//            if (result.success) {
//                val fileList = hashMap.getOrPut(result.hash) { mutableListOf() }
//                fileList.add(files[index].path)
//            }
//        }
//
//        return hashMap.filter { it.value.size > 1 }
//    }
//}
//
//// 主程序示例
//suspend fun main() {
//    val example = FileHashExample()
//
//    // 使用文件选择器选择文件
//    val filePicker = FilePickerFactory.create()
//    val result = filePicker.pickFile(
//        FilePickerConfig(
//            title = "选择要计算哈希的文件",
//            multipleSelection = false
//        )
//    )
//
//    if (result.success && result.files.isNotEmpty()) {
//        val selectedFile = result.files[0]
//        println("选择的文件: ${selectedFile.name}")
//
//        example.demonstrateFileHashing()
//    } else {
//        println("没有选择文件")
//    }
//}