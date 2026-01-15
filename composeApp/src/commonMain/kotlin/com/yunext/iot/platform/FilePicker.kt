package com.yunext.iot.platform

import com.yunext.iot.ui.protocol.FileVo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * 文件选择结果
 */
data class FileSelectionResult(
    val files: List<FileInfo>,
    val success: Boolean,
    val errorMessage: String? = null
)

/**
 * 文件信息
 */
expect class FileInfo(path: String) {
    val path: String
    val name: String
    val extension: String
    val size: Long?
    val lastModified: Long?
    val isDirectory: Boolean
    fun exists(): Boolean
    fun readText(): String
    fun readBytes(): ByteArray
}

/**
 * 文件选择器配置
 */
data class FilePickerConfig(
    val title: String = "选择文件",
    val initialDirectory: String? = "/Users/xiangpengle/Downloads",
    val multipleSelection: Boolean = false,
    val filters: List<FileFilter> = emptyList(),
    val isDirectoryChooser: Boolean = false
)

/**
 * 文件过滤器
 */
data class FileFilter(
    val description: String,
    val extensions: List<String>
)

/**
 * 文件选择器接口
 */
interface FilePicker {
    /**
     * 选择文件
     */
    suspend fun pickFile(config: FilePickerConfig = FilePickerConfig()): FileSelectionResult

    /**
     * 选择文件（Flow版本）
     */
    fun pickFileFlow(config: FilePickerConfig = FilePickerConfig()): Flow<FileSelectionResult>

    /**
     * 保存文件
     */
    suspend fun saveFile(
        config: FilePickerConfig = FilePickerConfig(),
        suggestedFileName: String? = null
    ): FileSelectionResult
}

/**
 * 平台文件选择器工厂
 */
expect object FilePickerFactory {
    fun create(): FilePicker
}
private val defaultFilePicker by lazy(mode= LazyThreadSafetyMode.PUBLICATION) {
    FilePickerFactory.create()
}
suspend fun selectAppFileBytes(fileVo: FileVo): ByteArray {
    return withContext(Dispatchers.IO){
        fileVo.file.readBytes()
    }
}

suspend fun selectAppFile(signed: Boolean = true): FileVo? {


    // 选择单个文件
    suspend fun selectSingleImage(): FileInfo? {
        val result = defaultFilePicker.pickFile(
            FilePickerConfig(
                title = "选择APP固件(${if (signed) "加密" else "不加密"})",
                filters = listOf(
//                    FileFilter("图片文件", listOf("jpg", "jpeg", "png", "gif")),
                    FileFilter("HDApp", listOf("*"))
                )
            )
        )

        if (result.success) {
            result.files.forEach { file ->
                println("选择的文件: ${file.name}")
                println("文件大小: ${file.size} bytes")
                println("文件路径: ${file.path}")
            }
            return if (result.files.isEmpty()) null else result.files[0]
        } else {
            return null
        }
    }

    val fileInfo = selectSingleImage() ?: return null
    val fileHasher = FileHasherFactory.create()
    val hashResult = fileHasher.calculateMD5(fileInfo.path)
    return if (hashResult.success) {
        return FileVo(
            fileSize = fileInfo.size?.toInt() ?: 0,
            path = fileInfo.path,
            fileMd5 = hashResult.hash.hexToByteArray(), file = fileInfo
        )
    } else null

}

suspend fun selectSystemFile(): FileVo? {


    // 选择单个文件
    suspend fun selectSingleImage(): FileInfo? {
        val result = defaultFilePicker.pickFile(
            FilePickerConfig(
                title = "选择系统固件",
                filters = listOf(
//                    FileFilter("图片文件", listOf("jpg", "jpeg", "png", "gif")),
                    FileFilter("系统固件", listOf("tar"))
                )
            )
        )

        if (result.success) {
            result.files.forEach { file ->
                println("选择的文件: ${file.name}")
                println("文件大小: ${file.size} bytes")
                println("文件路径: ${file.path}")
            }
            return if (result.files.isEmpty()) null else result.files[0]
        } else {
            return null
        }
    }

    val fileInfo = selectSingleImage() ?: return null
    val fileHasher = FileHasherFactory.create()
    val hashResult = fileHasher.calculateMD5(fileInfo.path)
    return if (hashResult.success) {
        return FileVo(
            fileSize = fileInfo.size?.toInt() ?: 0,
            path = fileInfo.path,
            fileMd5 = hashResult.hash.hexToByteArray(), file = fileInfo
        )
    } else null

}