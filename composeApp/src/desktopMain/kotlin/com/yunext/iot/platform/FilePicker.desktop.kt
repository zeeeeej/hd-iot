package com.yunext.iot.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual class FileInfo actual constructor(actual val path: String) {
    private val file = File(path)

    actual val name: String get() = file.name
    actual val extension: String get() = file.extension
    actual val size: Long? get() = if (file.exists()) file.length() else null
    actual val lastModified: Long? get() = if (file.exists()) file.lastModified() else null
    actual val isDirectory: Boolean get() = file.isDirectory

    actual fun exists(): Boolean = file.exists()
    actual fun readText(): String = file.readText()
    actual fun readBytes(): ByteArray = file.readBytes()
}

/**
 * 桌面端文件选择器实现
 */
class DesktopFilePicker : FilePicker {

    override suspend fun pickFile(config: FilePickerConfig): FileSelectionResult {
        return withContext(Dispatchers.Swing) {
            val fileChooser = createFileChooser(config)

            val result = if (config.isDirectoryChooser) {
                fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION
            } else {
                if (config.multipleSelection) {
                    fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION
                } else {
                    fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION
                }
            }

            if (result) {
                val selectedFiles = if (config.multipleSelection) {
                    fileChooser.selectedFiles.toList()
                } else {
                    listOf(fileChooser.selectedFile)
                }

                FileSelectionResult(
                    files = selectedFiles.map { FileInfo(it.absolutePath) },
                    success = true
                )
            } else {
                FileSelectionResult(
                    files = emptyList(),
                    success = false,
                    errorMessage = "用户取消选择"
                )
            }
        }
    }

    override fun pickFileFlow(config: FilePickerConfig): Flow<FileSelectionResult> = callbackFlow {
        withContext(Dispatchers.Swing) {
            val fileChooser = createFileChooser(config)

            val listener = object : java.awt.event.ActionListener {
                override fun actionPerformed(e: java.awt.event.ActionEvent) {
                    when (e.actionCommand) {
                        JFileChooser.APPROVE_SELECTION -> {
                            val selectedFiles = if (config.multipleSelection) {
                                fileChooser.selectedFiles.toList()
                            } else {
                                listOf(fileChooser.selectedFile)
                            }

                            trySend(
                                FileSelectionResult(
                                    files = selectedFiles.map { FileInfo(it.absolutePath) },
                                    success = true
                                )
                            )
                        }
                        JFileChooser.CANCEL_SELECTION -> {
                            trySend(
                                FileSelectionResult(
                                    files = emptyList(),
                                    success = false,
                                    errorMessage = "用户取消选择"
                                )
                            )
                        }
                    }
                }
            }

            fileChooser.addActionListener(listener)

            // 显示对话框（非阻塞）
            Thread {
                if (config.isDirectoryChooser) {
                    fileChooser.showOpenDialog(null)
                } else {
                    fileChooser.showOpenDialog(null)
                }
            }.start()

            awaitClose {
                fileChooser.removeActionListener(listener)
            }
        }
    }

    override suspend fun saveFile(
        config: FilePickerConfig,
        suggestedFileName: String?
    ): FileSelectionResult {
        return withContext(Dispatchers.Swing) {
            val fileChooser = createFileChooser(config)
            fileChooser.dialogType = JFileChooser.SAVE_DIALOG

            suggestedFileName?.let {
                fileChooser.selectedFile = File(it)
            }

            val result = fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION

            if (result) {
                val selectedFile = fileChooser.selectedFile
                FileSelectionResult(
                    files = listOf(FileInfo(selectedFile.absolutePath)),
                    success = true
                )
            } else {
                FileSelectionResult(
                    files = emptyList(),
                    success = false,
                    errorMessage = "用户取消保存"
                )
            }
        }
    }

    private fun createFileChooser(config: FilePickerConfig): JFileChooser {
        return JFileChooser().apply {
            dialogTitle = config.title

            config.initialDirectory?.let {
                currentDirectory = File(it)
            }

            fileSelectionMode = when {
                config.isDirectoryChooser -> JFileChooser.DIRECTORIES_ONLY
                else -> JFileChooser.FILES_ONLY
            }

            isMultiSelectionEnabled = config.multipleSelection

            // 添加文件过滤器
            config.filters.forEach { filter ->
                addChoosableFileFilter(
                    FileNameExtensionFilter(
                        filter.description,
                        *filter.extensions.toTypedArray()
                    )
                )
            }
        }
    }
}

actual object FilePickerFactory {
    actual fun create(): FilePicker = DesktopFilePicker()
}