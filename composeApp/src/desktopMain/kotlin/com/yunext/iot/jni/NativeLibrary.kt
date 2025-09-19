package com.yunext.iot.jni

import java.io.File

class NativeLibrary {
    init {
        // 加载 JNI 库
        loadNativeLibrary()
    }

//    private external fun nativeMethod(): String
//
//    private external fun addNumbers(a: Int, b: Int): Int
//
//    private external fun getMessage(): String
//
//    fun addNumbersJVM(a: Int, b: Int): Int {
//        return addNumbers(a,b)
//    }
//
//    fun getMessageJVM(): String{
//        return getMessage()
//    }

    companion object {
        private fun loadNativeLibrary() {
            val os = System.getProperty("os.name").lowercase()
            val arch = System.getProperty("os.arch").lowercase()

            val platform = when {
                os.contains("win") -> "windows"
                os.contains("nix") || os.contains("nux") -> "linux"
                os.contains("mac") -> "darwin"
                else -> throw UnsupportedOperationException("Unsupported OS: $os")
            }

            val architecture = when {
                arch.contains("64") -> "x64"
                arch == "x86" -> "x86"
                arch.contains("arm") -> "arm64"
                else -> throw UnsupportedOperationException("Unsupported architecture: $arch")
            }

            val libraryPath = "/jni/$platform-$architecture/native-library"
            try {
                System.loadLibrary("native-library")
            } catch (e: UnsatisfiedLinkError) {
                // 如果 System.loadLibrary 失败，尝试从资源加载
                loadLibraryFromResources(libraryPath)
            }
        }

        private fun loadLibraryFromResources(path: String) {
            val resource = NativeLibrary::class.java.getResource("$path.dll")
                ?: NativeLibrary::class.java.getResource("$path.so")
                ?: throw RuntimeException("Native library not found: $path")

            val tempFile = File.createTempFile("native", ".lib")
            tempFile.deleteOnExit()

            resource.openStream().use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            System.load(tempFile.absolutePath)
        }
    }
}