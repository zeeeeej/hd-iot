//package com.yunext.iot.utils
//
//import com.fazecast.jSerialComm.SerialPort
//import java.io.FileOutputStream
//import java.nio.ByteBuffer
//import java.util.concurrent.TimeUnit
//
//// 配置参数
//private const val SERIAL_PORT_NAME = "/dev/cu.wchusbserial11410"
//private const val BAUD_RATE = 460800
//private const val TARGET_DATA_SIZE = 248991 // 目标接收字节数
//private const val MAX_READ_BUF = 32 // 单次最大读取字节数
//private const val RECEIVE_TIMEOUT_SEC = 10 // 总接收超时时间
//private const val SAVE_FILE_PATH = "rs485.jpg"
//
//// 要发送的16进制命令（转字节数组）
//private val SEND_CMD = byteArrayOf(
//    0xaa.toByte(), 0x5a.toByte(), 0xee.toByte(), 0x19.toByte(), 0x09.toByte(),
//    0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte(),
//    0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x94.toByte(), 0xcc.toByte(),
//    0x03.toByte(), 0x00.toByte(), 0x7d.toByte(), 0x7f.toByte()
//)
//
//// aa 5a ee 19 95 cc 03 00 00 ff d8 ff e0 00 10 4a 46 49 46 00 01 02 00 00 01 00 01 00 00 ff db 00 43 ...
//// 6d 8d d9 ff 0f 4c e1 cf 00 ff
//fun test_rs485() {
//    // 1. 打开并配置串口
//    val serialPort = SerialPort.getCommPort(SERIAL_PORT_NAME).apply {
//        // 核心串口配置
//        baudRate = BAUD_RATE
//        numDataBits = 8
//        numStopBits = SerialPort.ONE_STOP_BIT
//        parity = SerialPort.NO_PARITY
//        //flowControl = SerialPort.FLOW_CONTROL_DISABLED
//
//        // 防丢包配置：扩大接收缓冲区（jSerialComm自动适配系统）
////        setComPortTimeouts(
////            SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
////            100, // 单次读取超时100ms（非阻塞）
////            0
////        )
//        setComPortTimeouts(
//            SerialPort.TIMEOUT_READ_BLOCKING,
//            100, // 单次读取超时100ms（非阻塞）
//            0
//        )
//
//        // 打开串口
//        if (!openPort()) {
//            println("串口打开失败：$SERIAL_PORT_NAME")
//            return
//        }
//        println("串口打开成功：$SERIAL_PORT_NAME (波特率：$BAUD_RATE)")
//
//        // 清空缓冲区
////        clearRxBuffer()/
////        clearRxBuffer();
//    }
//
//    try {
//        // 2. 发送命令
//        val sendBytes = serialPort.writeBytes(SEND_CMD, SEND_CMD.size.toLong())
//        if (sendBytes.toLong() != SEND_CMD.size.toLong()) {
//            println("命令发送失败！发送字节数：$sendBytes，期望：${SEND_CMD.size}")
//            return
//        }
//        println("命令发送成功，共发送 ${SEND_CMD.size} 字节")
//
//        // 3. 高速接收数据（带超时）
//        val receiveBuffer = ByteBuffer.allocate(2 * 1024 * 1024) // 2M缓冲区
//        val startTime = System.currentTimeMillis()
//        var totalRead = 0
//
//        println("开始接收数据...")
//        while (totalRead < TARGET_DATA_SIZE) {
//            // 检查是否超时
//            val elapsedTime = System.currentTimeMillis() - startTime
//            if (elapsedTime > RECEIVE_TIMEOUT_SEC * 1000L) {
//                println("接收超时！已读：$totalRead 字节，期望：$TARGET_DATA_SIZE 字节")
//                break
//            }
//
//            // 计算单次读取长度（最多32字节，不超过剩余需要的字节）
//            val readLen = minOf(MAX_READ_BUF, TARGET_DATA_SIZE - totalRead)
//            val tempBuf = ByteArray(readLen)
//
//            // 非阻塞读取（利用jSerialComm的半阻塞超时）
//            val readBytes = serialPort.readBytes(tempBuf, readLen.toLong())
//            if (readBytes > 0) {
//                receiveBuffer.put(tempBuf, 0, readBytes.toInt())
//                totalRead += readBytes.toInt()
//            }
//        }
//
//        println("接收完成！已读：$totalRead 字节，缺失：${TARGET_DATA_SIZE - totalRead} 字节")
//
//        // 4. 处理并保存数据（去前9后2字节）
//        val rawData = receiveBuffer.array().copyOf(totalRead) // 截取实际接收的字节
//        if (rawData.size > 11) { // 至少需要9+2=11字节才有可处理的数据
//            val processedData = rawData.copyOfRange(9, rawData.size - 2)
//            FileOutputStream(SAVE_FILE_PATH).use { output ->
//                output.write(processedData)
//            }
//            println("处理后的数据已保存到：$SAVE_FILE_PATH（长度：${processedData.size} 字节）")
//
//            // 5. 打印前20字节和最后10字节（16进制）
//            println("\n=== 原始数据预览 ===")
//            // 打印前20字节
//            val firstBytes = rawData.copyOfRange(0, minOf(20, rawData.size))
//            println("前${firstBytes.size}字节：")
//            printHexBytes(firstBytes)
//
//            // 打印最后10字节
//            if (rawData.size >= 10) {
//                val lastBytes = rawData.copyOfRange(rawData.size - 10, rawData.size)
//                println("最后10字节：")
//                printHexBytes(lastBytes)
//            }
//        } else {
//            println("数据长度不足，无法处理（仅${rawData.size}字节）")
//        }
//
//    } catch (e: Exception) {
//        println("执行异常：${e.message}")
//        e.printStackTrace()
//    } finally {
//        // 关闭串口
//        if (serialPort.isOpen) {
//            serialPort.closePort()
//            println("串口已关闭")
//        }
//    }
//}
//
///**
// * 打印字节数组的16进制格式
// */
//private fun printHexBytes(bytes: ByteArray) {
//    val hexStr = bytes.joinToString(" ") { String.format("%02x", it) }
//    println(hexStr)
//}

// 2、
//import com.fazecast.jSerialComm.SerialPort
//import java.io.FileOutputStream
//import java.nio.ByteBuffer
//
//// 配置参数
//private const val SERIAL_PORT_NAME = "/dev/cu.wchusbserial11410"
//private const val BAUD_RATE = 460800
//private const val TARGET_DATA_SIZE = 248991 // 目标接收字节数
//private const val MAX_READ_BUF = 1024 // 单次最大读取字节数（硬件限制）
//private const val RECEIVE_TIMEOUT_SEC = 10 // 总接收超时时间
//private const val SAVE_FILE_PATH = "rs485.jpg"
//
//// 要发送的16进制命令（转字节数组）
//private val SEND_CMD = byteArrayOf(
//    0xaa.toByte(), 0x5a.toByte(), 0xee.toByte(), 0x19.toByte(), 0x09.toByte(),
//    0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte(),
//    0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x94.toByte(), 0xcc.toByte(),
//    0x03.toByte(), 0x00.toByte(), 0x7d.toByte(), 0x7f.toByte()
//)
//
//fun test_rs485() {
//    // 1. 查找并打开串口
//    val serialPort = SerialPort.getCommPort(SERIAL_PORT_NAME)
//    // 核心串口参数配置
//    serialPort.baudRate = BAUD_RATE
//    serialPort.numDataBits = 8
//    serialPort.numStopBits = SerialPort.ONE_STOP_BIT
//    serialPort.parity = SerialPort.NO_PARITY
////    serialPort.flowControl = SerialPort.FLOW_CONTROL_DISABLED
//
//    // ========== 关键修改：配置纯阻塞读取模式 ==========
//    // TIMEOUT_READ_BLOCKING：阻塞直到读取到指定字节数或超时
//    // 单次读取超时设为总超时时间（ms），确保单次读取不会提前中断
//    serialPort.setComPortTimeouts(
//        SerialPort.TIMEOUT_READ_BLOCKING,
//        RECEIVE_TIMEOUT_SEC * 1000, // 阻塞读取超时（ms）= 总超时时间
//        0                           // 写入超时（0=无超时）
//    )
//
//    // 打开串口
//    if (!serialPort.openPort()) {
//        println("❌ 串口打开失败：$SERIAL_PORT_NAME")
//        println("   请检查串口地址是否正确，或是否有其他程序占用串口")
//        return
//    }
//    println("✅ 串口打开成功：$SERIAL_PORT_NAME（波特率：$BAUD_RATE，阻塞模式）")
//
//    try {
//        // 2. 清空串口缓冲区
////        serialPort.clearRxBuffer() // 清空接收缓冲区
////        serialPort.clearTxBuffer() // 清空发送缓冲区
//        println("✅ 串口缓冲区已清空")
//
//        // 3. 发送命令（阻塞发送，确保命令完整发出）
//        val sendBytes = serialPort.writeBytes(SEND_CMD, SEND_CMD.size.toLong())
//        if (sendBytes.toLong() != SEND_CMD.size.toLong()) {
//            println("❌ 命令发送失败！发送字节数：$sendBytes，期望：${SEND_CMD.size}")
//            return
//        }
//        println("✅ 命令发送成功，共发送 ${SEND_CMD.size} 字节")
//
//        // 4. 阻塞模式高速接收数据（带总超时控制）
//        val receiveBuffer = ByteBuffer.allocate(2 * 1024 * 1024) // 2M接收缓冲区
//        val startTime = System.currentTimeMillis()
//        var totalRead = 0
//
//        println("📥 开始阻塞接收数据...")
//        while (totalRead < TARGET_DATA_SIZE) {
//            // 检查总超时（避免无限阻塞）
//            val elapsedTime = System.currentTimeMillis() - startTime
//            if (elapsedTime > RECEIVE_TIMEOUT_SEC * 1000L) {
//                println("⚠️ 接收超时！已读：$totalRead 字节，期望：$TARGET_DATA_SIZE 字节")
//                break
//            }
//
//            // 计算单次读取长度（不超过硬件限制32字节，且不超过剩余需要的字节）
//            val readLen = minOf(MAX_READ_BUF, TARGET_DATA_SIZE - totalRead)
//            val tempBuf = ByteArray(readLen)
//
//            // ========== 阻塞读取：直到读满readLen字节或触发超时 ==========
//            // 阻塞模式下，readBytes会一直等，直到读取到readLen字节 或 超过设置的超时时间
//            val readBytes = serialPort.readBytes(tempBuf, readLen.toLong())
//
//            if (readBytes <= 0) {
//                // 读取超时/无数据：退出循环（避免空等）
//                println("⚠️ 单次阻塞读取超时/无数据，已读字节：$readBytes")
//                break
//            }
//
//            // 成功读取数据，写入缓冲区
//            receiveBuffer.put(tempBuf, 0, readBytes.toInt())
//            totalRead += readBytes.toInt()
//
//            // 可选：打印读取进度（调试用）
//            // println("📌 已读：$totalRead/$TARGET_DATA_SIZE 字节")
//        }
//
//        println("📤 接收完成！已读：$totalRead 字节，缺失：${TARGET_DATA_SIZE - totalRead} 字节")
//
//        // 5. 处理数据（去掉前9字节、最后2字节）并保存
//        val rawData = receiveBuffer.array().copyOf(totalRead) // 截取实际接收的字节
//        if (rawData.size > 11) { // 至少需要9+2=11字节才有可处理的数据
//            val processedData = rawData.copyOfRange(9, rawData.size - 2)
//            // 保存到文件（use函数自动关闭流，避免资源泄漏）
//            FileOutputStream(SAVE_FILE_PATH).use { output ->
//                output.write(processedData)
//            }
//            println("✅ 处理后的数据已保存到：$SAVE_FILE_PATH（长度：${processedData.size} 字节）")
//
//            // 6. 打印前20字节和最后10字节（16进制格式）
//            println("\n=== 原始数据预览 ===")
//            // 打印前20字节
//            val firstBytesCount = minOf(20, rawData.size)
//            val firstBytes = rawData.copyOfRange(0, firstBytesCount)
//            println("前$firstBytesCount 字节：")
//            printHexBytes(firstBytes)
//
//            // 打印最后10字节
//            if (rawData.size >= 10) {
//                val lastBytes = rawData.copyOfRange(rawData.size - 10, rawData.size)
//                println("最后10字节：")
//                printHexBytes(lastBytes)
//            }
//        } else {
//            println("❌ 数据长度不足，无法处理（仅${rawData.size}字节，需至少11字节）")
//        }
//
//    } catch (e: Exception) {
//        println("❌ 执行异常：${e.message}")
//        e.printStackTrace()
//    } finally {
//        // 7. 关闭串口（无论是否异常，都确保关闭）
//        if (serialPort.isOpen) {
//            serialPort.closePort()
//            println("\n✅ 串口已关闭")
//        }
//    }
//}
//
///**
// * 辅助函数：以16进制格式打印字节数组
// */
//private fun printHexBytes(bytes: ByteArray) {
//    val hexStr = bytes.joinToString(" ") { String.format("%02x", it) }
//    println(hexStr)
//}

// 3、
import com.fazecast.jSerialComm.SerialPort
import java.io.FileOutputStream
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

// 配置参数
private const val SERIAL_PORT_NAME = "/dev/cu.wchusbserial1410"
private const val BAUD_RATE = 460800
private const val TARGET_DATA_SIZE = 422506 // 目标接收字节数
private const val MAX_READ_BUF = 32 // 单次最大读取字节数（硬件限制）
private const val RECEIVE_TIMEOUT_SEC = 10 // 总接收超时时间
private const val SAVE_FILE_PATH = "rs485.jpg"
private const val BUFFER_CAPACITY = 1024 * 64 // 缓冲区容量（64K，适配高速接收）

// 要发送的16进制命令（转字节数组）
private val SEND_CMD = byteArrayOf(
    0xaa.toByte(), 0x5a.toByte(), 0xee.toByte(), 0x19.toByte(), 0x09.toByte(),
    0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte(),
    0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x94.toByte(), 0xcc.toByte(),
    0x03.toByte(), 0x00.toByte(), 0x7d.toByte(), 0x7f.toByte()
)

fun test_rs485() {
    println("test_rs485==")
    // 1. 初始化线程安全缓冲区（ArrayBlockingQueue是线程安全的阻塞队列）
    val dataBuffer = ArrayBlockingQueue<Byte>(BUFFER_CAPACITY)
    // 标记是否停止接收线程
    val stopReceiveThread = AtomicBoolean(false)
    // 用于等待接收完成/超时的Latch
    val receiveLatch = CountDownLatch(1)

    // 2. 打开并配置串口
    val serialPort = SerialPort.getCommPort(SERIAL_PORT_NAME).apply {
        baudRate = BAUD_RATE
        numDataBits = 8
        numStopBits = SerialPort.ONE_STOP_BIT
        parity = SerialPort.NO_PARITY
        // flowControl = SerialPort.FLOW_CONTROL_DISABLED

        // 配置纯阻塞读取模式
        setComPortTimeouts(
            SerialPort.TIMEOUT_READ_BLOCKING,
            1000, // 单次读取超时1秒（避免接收线程卡死）
            0
        )

        if (!openPort()) {
            println("❌ 串口打开失败：$SERIAL_PORT_NAME")
            return
        }
//        clearRxBuffer()
//        clearTxBuffer()
        println("✅ 串口打开成功：$SERIAL_PORT_NAME（波特率：$BAUD_RATE）")
    }

    try {
        val cmd = "aa5aee190900000001000000005f720600cdc8".hexToByteArray(
            HexFormat.Default
        )
        // 3. 发送命令
        val sendBytes = serialPort.writeBytes(cmd, cmd.size.toLong())
        if (sendBytes.toLong() != cmd.size.toLong()) {
            println("❌ 命令发送失败！发送字节数：$sendBytes，期望：${cmd.size}")
            return
        }
        println("✅ 命令发送成功，共发送 ${cmd.size} 字节")

        // 4. 启动接收线程（专门负责读取串口数据写入缓冲区）
        val receiveThread = Thread({
            try {
                println("📥 接收线程启动，开始阻塞读取串口数据...")
                val tempBuf = ByteArray(MAX_READ_BUF) // 单次读取缓冲区

                while (!stopReceiveThread.get()) {
                    // 阻塞读取串口数据（最多32字节）
                    val readBytes = serialPort.readBytes(tempBuf, MAX_READ_BUF.toLong())

                    if (readBytes <= 0) {
                        // 单次读取超时/无数据，继续循环（避免线程退出）
                        continue
                    }

                    // 将读取到的字节写入线程安全缓冲区
                    for (i in 0 until readBytes.toInt()) {
                        // 缓冲区满时阻塞，直到处理线程取走数据（避免丢包）
                        dataBuffer.put(tempBuf[i])
                    }
                }
            } catch (e: InterruptedException) {
                println("⚠️ 接收线程被中断：${e.message}")
            } catch (e: Exception) {
                println("❌ 接收线程异常：${e.message}")
                e.printStackTrace()
            } finally {
                receiveLatch.countDown() // 通知处理线程接收完成
                println("📤 接收线程退出")
            }
        }, "Serial-Receive-Thread")
        receiveThread.start()

        // 5. 启动处理线程（从缓冲区读取数据并处理）
        val processThread = Thread({
            try {
                println("🔧 处理线程启动，开始从缓冲区读取数据...")
                val resultBuffer = ByteArray(TARGET_DATA_SIZE) // 最终拼接缓冲区
                var totalRead = 0
                val startTime = System.currentTimeMillis()

                while (totalRead < TARGET_DATA_SIZE) {
                    // 检查总超时
                    val elapsedTime = System.currentTimeMillis() - startTime
                    if (elapsedTime > RECEIVE_TIMEOUT_SEC * 1000L) {
                        println("⚠️ 处理线程超时！已读：$totalRead 字节，期望：$TARGET_DATA_SIZE 字节")
                        break
                    }

                    // 从缓冲区取字节（阻塞50ms，避免空等）
                    val byte = dataBuffer.poll(50, TimeUnit.MILLISECONDS)
                    if (byte == null) {
                        continue // 缓冲区无数据，继续循环
                    }

                    // 将字节写入结果缓冲区
                    resultBuffer[totalRead] = byte
                    totalRead++
                }

                // 6. 处理并保存数据
                println("📤 数据接收完成！已读：$totalRead 字节，缺失：${TARGET_DATA_SIZE - totalRead} 字节")
                val rawData = resultBuffer.copyOf(totalRead) // 截取实际接收的字节

                if (rawData.size > 11) {
                    val processedData = rawData.copyOfRange(9, rawData.size - 2)
                    // 保存到文件
                    FileOutputStream(SAVE_FILE_PATH).use { output ->
                        output.write(processedData)
                    }
                    println("✅ 处理后的数据已保存到：$SAVE_FILE_PATH（长度：${processedData.size} 字节）")

                    // 打印首尾数据
                    println("\n=== 原始数据预览 ===")
                    val firstBytesCount = minOf(20, rawData.size)
                    val firstBytes = rawData.copyOfRange(0, firstBytesCount)
                    println("前$firstBytesCount 字节：")
                    printHexBytes(firstBytes)

                    if (rawData.size >= 10) {
                        val lastBytes = rawData.copyOfRange(rawData.size - 10, rawData.size)
                        println("最后10字节：")
                        printHexBytes(lastBytes)
                    }
                } else {
                    println("❌ 数据长度不足，无法处理（仅${rawData.size}字节，需至少11字节）")
                }

            } catch (e: InterruptedException) {
                println("⚠️ 处理线程被中断：${e.message}")
            } catch (e: Exception) {
                println("❌ 处理线程异常：${e.message}")
                e.printStackTrace()
            } finally {
                // 停止接收线程
                stopReceiveThread.set(true)
                receiveThread.interrupt()
                receiveLatch.await(1, TimeUnit.SECONDS) // 等待接收线程退出
                println("🔧 处理线程退出")
            }
        }, "Serial-Process-Thread")
        processThread.start()

        // 等待处理线程完成
        processThread.join(RECEIVE_TIMEOUT_SEC * 1000L + 1000)
        if (processThread.isAlive) {
            println("⚠️ 处理线程超时未完成，强制停止")
            processThread.interrupt()
            stopReceiveThread.set(true)
            receiveThread.interrupt()
        }

    } catch (e: Exception) {
        println("❌ 主线程异常：${e.message}")
        e.printStackTrace()
    } finally {
        // 7. 关闭串口
        if (serialPort.isOpen) {
            serialPort.closePort()
            println("\n✅ 串口已关闭")
        }
    }
}

/**
 * 辅助函数：以16进制格式打印字节数组
 */
private fun printHexBytes(bytes: ByteArray) {
    val hexStr = bytes.joinToString(" ") { String.format("%02x", it) }
    println(hexStr)
}