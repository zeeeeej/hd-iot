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
            val lib = "libhd_camera_uart_jni.dylib"
            println("lib = ${lib}")
            //   try {
            //                // 方法1：从绝对路径加载
            //                System.load("/full/path/to/libhd_camera_uart_jni.dylib")
            //
            //
            //                // 方法2：从相对路径加载
            //                val libPath =
            //                    System.getProperty("user.dir") + "/lib/darwin-x64/libhd_camera_uart_jni.dylib"
            //                System.load(libPath)
            //            } catch (e: UnsatisfiedLinkError) {
            //                System.err.println("加载 JNI 库失败: " + e.message)
            //            }


            val os = System.getProperty("os.name").lowercase()
            val arch = System.getProperty("os.arch").lowercase()

            val platform = when {
                os.contains("win") -> "windows"
                os.contains("nix") || os.contains("nux") -> "linux"
                os.contains("mac") -> "darwin"
                else -> throw UnsupportedOperationException("Unsupported OS: $os")
            }
            println("platform  = $platform")
            val architecture = when {
                arch.contains("64") -> "x64"
                arch == "x86" -> "x86"
                arch.contains("arm") -> "arm64"
                else -> throw UnsupportedOperationException("Unsupported architecture: $arch")
            }
            println("architecture  = $architecture")
            val root = "/Users/xiangpengle/Documents/job/manhattan/code/hd-iot/composeApp/src/desktopMain/kotlin/lib"
            val libraryPath = "${root}/$platform-$architecture/$lib"
            println("libraryPath  = $libraryPath")

            try {
                System.load(libraryPath)

//                println("111111")
//                NativeFunctions().uartList()
//                println("2222222")
//                NativeFunctions.uartReading(0, 0,1)
                println("加载JNI成功！" )
            } catch (e: UnsatisfiedLinkError) {
                e.printStackTrace()
                println("（1）加载JNI失败！$e" )
                // 如果 System.loadLibrary 失败，尝试从资源加载
                loadLibraryFromResources(libraryPath)
            }
        }

        @Deprecated("error")
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