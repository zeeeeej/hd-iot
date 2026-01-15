package com.yunext.iot.platform

/**
 * 文件信息
 */
actual class FileInfo actual constructor(path: String) {
    actual val path: String
        get() = TODO("Not yet implemented")
    actual val name: String
        get() = TODO("Not yet implemented")
    actual val extension: String
        get() = TODO("Not yet implemented")
    actual val size: Long?
        get() = TODO("Not yet implemented")
    actual val lastModified: Long?
        get() = TODO("Not yet implemented")
    actual val isDirectory: Boolean
        get() = TODO("Not yet implemented")

    actual fun exists(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun readText(): String {
        TODO("Not yet implemented")
    }

    actual fun readBytes(): ByteArray {
        TODO("Not yet implemented")
    }
}

/**
 * 平台文件选择器工厂
 */
actual object FilePickerFactory {
    actual fun create(): FilePicker {
        TODO("Not yet implemented")
    }
}