package com.yunext.iot.jni

import java.io.File

object NativeFunctions {


                init {
                        // 确保库只加载一次
                        loadNativeLibraryOnce()
                }

                @Volatile
                private var libraryLoaded = false

                private fun loadNativeLibraryOnce() {
                        if (libraryLoaded) return

                        synchronized(this) {
                                if (libraryLoaded) return

                                try {
                                        println("=== 开始加载JNI库 ===")

                                        // 获取系统信息
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

                                        println("平台: $platform-$architecture")

                                        // 构建库路径
                                        val libName = "libhd_camera_uart_jni.dylib"
                                        val root = "/Users/xiangpengle/Documents/job/manhattan/code/hd-iot/composeApp/src/desktopMain/kotlin/lib"
                                        val libraryPath = "$root/$platform-$architecture/$libName"

                                        println("库路径: $libraryPath")

                                        val libFile = File(libraryPath)
                                        if (!libFile.exists()) {
                                                throw RuntimeException("库文件不存在: $libraryPath")
                                        }

                                        println("库文件大小: ${libFile.length()} 字节")
                                        println("库文件最后修改: ${libFile.lastModified()}")

                                        // 方法1：使用绝对路径加载
                                        System.load(libraryPath)
                                        println("✓ 库加载成功")

                                        // 测试简单的JNI函数
                                        testSimpleFunctions()

                                        libraryLoaded = true

                                } catch (e: UnsatisfiedLinkError) {
                                        println("✗ 库链接错误: ${e.message}")
                                        e.printStackTrace()
                                        diagnoseLibraryLoadError()
                                } catch (e: Exception) {
                                        println("✗ 加载库时发生异常: ${e.message}")
                                        e.printStackTrace()
                                }
                        }
                }

                private fun testSimpleFunctions() {
                        println("=== 测试基础JNI函数 ===")

                        // 测试1: addNumbers
                        try {
                                val result = addNumbers(5, 3)
                                println("✓ addNumbers(5, 3) = $result")
                        } catch (e: UnsatisfiedLinkError) {
                                println("✗ addNumbers 链接失败: ${e.message}")
                                throw e
                        } catch (e: Exception) {
                                println("⚠ addNumbers 运行时错误: ${e.message}")
                        }

                        // 测试2: getMessage
                        try {
                                val msg = getMessage()
                                println("✓ getMessage() = $msg")
                        } catch (e: UnsatisfiedLinkError) {
                                println("✗ getMessage 链接失败: ${e.message}")
                        } catch (e: Exception) {
                                println("⚠ getMessage 运行时错误: ${e.message}")
                        }

                        println("=== 基础测试完成 ===")
                }

                private fun diagnoseLibraryLoadError() {
                        println("=== 库加载诊断 ===")

                        val libPaths = listOf(
                                "/Users/xiangpengle/Documents/job/manhattan/code/hd_camera_clion/hd_camera_jni/build/bin/libhd_camera_uart_jni.dylib",
                                "/Users/xiangpengle/Documents/job/manhattan/code/hd-iot/composeApp/src/desktopMain/kotlin/lib/darwin-x64/libhd_camera_uart_jni.dylib"
                        )

                        for (path in libPaths) {
                                val file = File(path)
                                println("\n检查: $path")
                                println("  存在: ${file.exists()}")
                                if (file.exists()) {
                                        println("  大小: ${file.length()} 字节")
                                        println("  可读: ${file.canRead()}")

                                        // 尝试用System.load加载
                                        try {
                                                System.load(path)
                                                println("  ✓ 可以加载")

                                                // 测试函数
                                                try {
                                                        val testResult = addNumbers(1, 1)
                                                        println("  ✓ 函数调用成功: 1 + 1 = $testResult")
                                                } catch (e: Exception) {
                                                        println("  ⚠ 函数调用失败: ${e.message}")
                                                }

                                        } catch (e: UnsatisfiedLinkError) {
                                                println("  ✗ 加载失败: ${e.message}")
                                        }
                                }
                        }
                }

                @JvmStatic
                external fun addNumbers(a: Int, b: Int): Int

                @JvmStatic
                external fun getMessage(): String

                @JvmStatic
                external fun uartVersion(type: Int): String

                @JvmStatic
                external fun uartList(): Array<String>

                @JvmStatic
                external fun uartOpen(com: String, rate: Int): Long

                @JvmStatic
                external fun uartClose(handle: Long)

                @JvmStatic
                external fun uartWrite(handle: Long, data: ByteArray): Int

                @JvmStatic
                external fun uartWriteDelay(handle: Long, data: ByteArray,delay:Int): Int

                @JvmStatic
                external fun uartRead(handle: Long, maxLength: Int): ByteArray?

                @JvmStatic
                external fun uartReading(handle: Long, expect: Int, timeout: Int): ByteArray?

                // 添加一个安全调用的方法
                fun safeUartList(): Array<String>? {
                        return try {
                                uartList()
                        } catch (e: UnsatisfiedLinkError) {
                                println("uartList 链接错误: ${e.message}")
                                null
                        } catch (e: Exception) {
                                println("uartList 运行时错误: ${e.message}")
                                null
                        }
                }
}