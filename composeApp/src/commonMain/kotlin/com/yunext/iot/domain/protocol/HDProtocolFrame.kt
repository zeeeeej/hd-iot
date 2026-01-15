package com.yunext.iot.domain.protocol

import androidx.compose.runtime.Stable
import java.lang.IllegalStateException

@Stable
data class HDProtocolFrame internal constructor(
    val head: HDProtocolFrameType,
    val address: HDProtocolFrameType,
    val cmd: HDProtocolFrameType,
    val length: HDProtocolFrameType,
    val payload: HDProtocolFrameType,
    val crc: HDProtocolFrameType
){
    override fun toString(): String {
        if (this.isEmpty)return "HDProtocolFrame::Empty"
        return """
            |-----------------------------
            |[帧头]${head.byteArray.toHexString()}
            |[地址]${address.byteArray.toHexString()}
            |[命令]${cmd.byteArray.toHexString()}
            |[长度]${length.byteArray.toHexString()}
            |[负载]${payload.byteArray.display()}
            |[校验]${crc.byteArray.toHexString()}
            |-----------------------------
        """.trimMargin()
    }

    companion object{
        val EMPTY = HDProtocolFrame(address = 0.toUByte(),cmd= 0.toUByte(), byteArrayOf())
    }
}

val HDProtocolFrame.isEmpty:Boolean
    get() = this == HDProtocolFrame.EMPTY

fun HDProtocolFrame(address: UByte, cmd: UByte, payload: ByteArray): HDProtocolFrame {
    val headType = HDProtocolFrameType.Head
    val addressType = HDProtocolFrameType.Address(address)
    val cmdType = HDProtocolFrameType.Cmd(cmd)
    val lengthType = HDProtocolFrameType.DataLength(payload.size)
    val dataType = HDProtocolFrameType.Payload(payload)
    val crcType = HDUtils.run {
        val todo = headType + addressType + cmdType + lengthType + dataType
        todo.calculateCRC()
    }.let { (c1, c2) ->
        HDProtocolFrameType.Crc(c1, c2)
    }

    return HDProtocolFrame(
        head = headType,
        cmd = cmdType,
        address = addressType,
        length = lengthType,
        payload = dataType,
        crc = crcType
    )
}


private const val HDProtocolFrameHead02: Byte = 0x5a
private const val HDProtocolFrameHead01: Byte = 0xaa.toByte()
private const val HDProtocolBroadcastAddress = 0xff

internal operator fun HDProtocolFrameType.plus(other: HDProtocolFrameType): ByteArray {
    return this.byteArray + other.byteArray
}

internal operator fun HDProtocolFrameType.plus(other: ByteArray): ByteArray {
    return this.byteArray + other
}

internal operator fun ByteArray.plus(other: HDProtocolFrameType): ByteArray {
    return this + other.byteArray
}


sealed interface HDProtocolFrameType {
    val byteArray: ByteArray

    data object Head : HDProtocolFrameType {
        override val byteArray: ByteArray
            get() = byteArrayOf(HDProtocolFrameHead01, HDProtocolFrameHead02)
    }

    /**
     * [address] 0~0xff
     */
    data class Address(val address: UByte) : HDProtocolFrameType {
        override val byteArray: ByteArray
            get() = byteArrayOf(address.toByte())
    }

    data class Cmd(val cmd: UByte) : HDProtocolFrameType {
        override val byteArray: ByteArray
            get() = byteArrayOf(cmd.toByte())
    }

    data class DataLength(val length: Int) : HDProtocolFrameType {
        override val byteArray: ByteArray
            get() = HDUtils.run {
                length.toByteArray()
            }
    }

    data class Payload(override val byteArray: ByteArray) : HDProtocolFrameType {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Payload

            return byteArray.contentEquals(other.byteArray)
        }

        override fun hashCode(): Int {
            return byteArray.contentHashCode()
        }

    }

    data class Crc(val crc1: Byte, val crc2: Byte) : HDProtocolFrameType {
        override val byteArray: ByteArray
            get() = byteArrayOf(crc1, crc2)
    }
}

internal fun HDProtocolFrame.toByteArray(): ByteArray {
    return head + address + cmd + length + payload + crc
}

internal fun ByteArray.toProtocolFrame(): HDProtocolFrame? {
    return try {
        // 最小长度检查：head(2) + address(1) + cmd(1) + length(4) + crc(2) = 8字节
        if (size < 10) return null

        var index = 0

        // 1. 解析帧头
        if (this[index] != HDProtocolFrameHead01 || this[index + 1] != HDProtocolFrameHead02) {
            return null
        }
        val head = HDProtocolFrameType.Head
        index += 2

        // 2. 解析地址
        val address = HDProtocolFrameType.Address(this[index].toUByte())
        index += 1

        // 3. 解析命令
        val cmd = HDProtocolFrameType.Cmd(this[index].toUByte())
        index += 1

        // 4. 解析数据长度（假设使用小端序，根据HDUtils实现调整）
        val dataLength = HDUtils.run {
            // 假设HDUtils有对应的fromByteArray方法
            // 如果没有，需要根据实际情况解析2字节的长度
            // 这里假设是2字节小端序的Int
            val lengthBytes = copyOfRange(index, index + 4)
            HDUtils.run {
                lengthBytes.toInt()
            }

        }
        val length = HDProtocolFrameType.DataLength(dataLength)
        index += 4

        // 5. 检查数据部分长度是否足够
        if (index + dataLength  > size) {
            return null
        }

        // 6. 解析数据
        val dataBytes = if (dataLength > 0) {
            copyOfRange(index, index + dataLength)
        } else {
            byteArrayOf()
        }
        val data = HDProtocolFrameType.Payload(dataBytes)
        index += dataLength

        // 7. 解析CRC
        val crc1 = this[index]
        val crc2 = this[index + 1]
        val crc = HDProtocolFrameType.Crc(crc1, crc2)

        // 8. 验证CRC（如果需要）
        // 这里可以添加CRC校验逻辑
        val checkCrc = HDUtils.run {
            val todo = head + address + cmd + length + data
            todo.calculateCRC()
        }.let { (c1, c2) ->
            crc1 == c1 && crc2 == c2
        }
        if (checkCrc) {
            HDProtocolFrame(head, address, cmd, length, data, crc)
        } else throw IllegalStateException("crc error.")
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

