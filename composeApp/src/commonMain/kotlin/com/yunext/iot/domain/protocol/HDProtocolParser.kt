package com.yunext.iot.domain.protocol

import com.yunext.iot.domain.HDResult
import com.yunext.iot.domain.fail
import com.yunext.iot.domain.protocol.HDUtils.toByteArray
import com.yunext.iot.domain.protocol.HDUtils.toInt
import com.yunext.iot.domain.success

sealed interface ProtocolParser<Data> {
    suspend fun encode(data: Data): HDResult<ByteArray>
    suspend fun decode(payload: ByteArray): HDResult<Data>
}

@Deprecated("use HDRequest/HDResponse")
interface HostHeartbeatProtocolParser : ProtocolParser<UByte> {
    override suspend fun encode(data: UByte): HDResult<ByteArray>
    override suspend fun decode(payload: ByteArray): HDResult<UByte>

    companion object Companion : HostHeartbeatProtocolParser {
        override suspend fun encode(data: UByte): HDResult<ByteArray> {
            return try {
                byteArrayOf(data.toByte()).success()
            } catch (e: Throwable) {
                e.fail()
            }
        }

        override suspend fun decode(payload: ByteArray): HDResult<UByte> {
            return try {
                if (payload.size == 1) {
                    payload[0].toUByte().success()
                } else throw IllegalStateException("HostHeartbeatProtocolParser::decode payload.size error")
            } catch (e: Throwable) {
                e.fail()
            }
        }
    }
}

@Deprecated("use HDRequest/HDResponse")
interface SlaveHeartbeatProtocolParser : ProtocolParser<UByte> {
    override suspend fun encode(data: UByte): HDResult<ByteArray>
    override suspend fun decode(payload: ByteArray): HDResult<UByte>

    companion object Companion : HostHeartbeatProtocolParser {
        override suspend fun encode(data: UByte): HDResult<ByteArray> {
            return try {
                byteArrayOf(data.toByte()).success()
            } catch (e: Throwable) {
                e.fail()
            }
        }

        override suspend fun decode(payload: ByteArray): HDResult<UByte> {
            return try {
                if (payload.size == 1) {
                    payload[0].toUByte().success()
                } else throw IllegalStateException("payload.size error")
            } catch (e: Throwable) {
                e.fail()
            }
        }
    }
}

@Deprecated("use HDRequest/HDResponse")
interface HostPicInfoProtocolParser : ProtocolParser<Unit> {
    override suspend fun encode(data: Unit): HDResult<ByteArray>
    override suspend fun decode(payload: ByteArray): HDResult<Unit>

    companion object Companion : HostPicInfoProtocolParser {
        override suspend fun encode(data: Unit): HDResult<ByteArray> {
            return byteArrayOf().success()
        }

        override suspend fun decode(payload: ByteArray): HDResult<Unit> {
            return try {
                if (payload.isNotEmpty()) Unit.success() else throw IllegalStateException("HostPicInfoProtocolParser::decode payload.size error")
            } catch (e: Throwable) {
                e.fail()
            }
        }

    }
}

@Deprecated("use HDRequest/HDResponse")
interface SlavePicInfoProtocolParser : ProtocolParser<List<PicInfo>> {
    override suspend fun encode(data: List<PicInfo>): HDResult<ByteArray>
    override suspend fun decode(payload: ByteArray): HDResult<List<PicInfo>>

    companion object Companion : SlavePicInfoProtocolParser {
        override suspend fun encode(data: List<PicInfo>): HDResult<ByteArray> {
            return try {
                data.toByteArray().success()
            } catch (e: Throwable) {
                e.fail()
            }
        }

        override suspend fun decode(payload: ByteArray): HDResult<List<PicInfo>> {
            return try {
                payload.toPicInfo().success()
            } catch (e: Throwable) {
                e.fail()
            }
        }

        // 将PicInfo列表序列化为ByteArray
        private fun List<PicInfo>.toByteArray(): ByteArray {
            val totalSize = 1 + size * 27 // 1 byte for list size + 27 bytes per PicInfo
            val buffer = ByteArray(totalSize)

            // 写入列表长度 (1 byte)
            buffer[0] = size.toByte()

            for ((index, picInfo) in this.withIndex()) {
                val offset = 1 + index * 27

                // 写入 id (1 byte)
                buffer[offset] = picInfo.id.toByte()

                // 写入 triggerType (1 byte)
                buffer[offset + 1] = picInfo.triggerType.value.toByte()

                // 写入 angel (1 byte)
                buffer[offset + 2] = picInfo.angel

                // 写入 timestamp (4 bytes, 小端)
                buffer[offset + 3] = (picInfo.timestamp and 0xFF).toByte()
                buffer[offset + 4] = ((picInfo.timestamp ushr 8) and 0xFF).toByte()
                buffer[offset + 5] = ((picInfo.timestamp ushr 16) and 0xFF).toByte()
                buffer[offset + 6] = ((picInfo.timestamp ushr 24) and 0xFF).toByte()

                // 写入 length (4 bytes, 小端)
                buffer[offset + 7] = (picInfo.length and 0xFF).toByte()
                buffer[offset + 8] = ((picInfo.length ushr 8) and 0xFF).toByte()
                buffer[offset + 9] = ((picInfo.length ushr 16) and 0xFF).toByte()
                buffer[offset + 10] = ((picInfo.length ushr 24) and 0xFF).toByte()

                // 写入 md5 (16 bytes)
                val md5Bytes = picInfo.md5.hexStringToByteArray()
                if (md5Bytes.size != 16) {
                    throw IllegalArgumentException("MD5 must be 32 characters (16 bytes)")
                }

                // 手动复制数组
                for (i in md5Bytes.indices) {
                    buffer[offset + 11 + i] = md5Bytes[i]
                }
            }

            return buffer
        }

        // 从ByteArray反序列化为PicInfo列表
        private fun ByteArray.toPicInfo(): List<PicInfo> {
            if (isEmpty()) {
                return emptyList()
            }

            // 读取列表长度 (1 byte)
            val listSize = this[0].toInt() and 0xFF

            // 验证数据长度
            val expectedSize = 1 + listSize * 27
            if (size != expectedSize) {
                throw IllegalArgumentException(
                    "Invalid byte array size. Expected: $expectedSize, Actual: $size"
                )
            }

            val result = mutableListOf<PicInfo>()

            for (i in 0 until listSize) {
                val offset = 1 + i * 27

                // 读取 id (1 byte)
                val id = this[offset].toUByte()

                // 读取 triggerType (1 byte)
                val triggerType = TriggerType.fromValue(this[offset + 1].toUByte())

                // 读取 angel (1 byte)
                val angel = this[offset + 2]

                // 读取 timestamp (4 bytes, 小端)
                val timestamp = (this[offset + 3].toInt() and 0xFF) or
                        ((this[offset + 4].toInt() and 0xFF) shl 8) or
                        ((this[offset + 5].toInt() and 0xFF) shl 16) or
                        ((this[offset + 6].toInt() and 0xFF) shl 24)

                // 读取 length (4 bytes, 小端)
                val length = (this[offset + 7].toInt() and 0xFF) or
                        ((this[offset + 8].toInt() and 0xFF) shl 8) or
                        ((this[offset + 9].toInt() and 0xFF) shl 16) or
                        ((this[offset + 10].toInt() and 0xFF) shl 24)

                // 读取 md5 (16 bytes)
                val md5Bytes = ByteArray(16)
                for (j in 0 until 16) {
                    md5Bytes[j] = this[offset + 11 + j]
                }
                val md5 = md5Bytes.toHexString()

                result.add(PicInfo(id, triggerType, angel, timestamp, length, md5))
            }

            return result
        }

        // 扩展函数：将16进制字符串转换为ByteArray
        private fun String.hexStringToByteArray(): ByteArray {
            require(length % 2 == 0) { "Hex string must have even length" }

            return ByteArray(length / 2).also { result ->
                for (i in result.indices) {
                    val high = this[i * 2].digitToInt(16) shl 4
                    val low = this[i * 2 + 1].digitToInt(16)
                    result[i] = (high or low).toByte()
                }
            }
        }

        // 扩展函数：将ByteArray转换为16进制字符串
        private fun ByteArray.toHexString(): String {
            val hexChars = CharArray(size * 2)
            val hexDigits = "0123456789abcdef"

            for (i in indices) {
                val v = this[i].toInt() and 0xFF
                hexChars[i * 2] = hexDigits[v ushr 4]
                hexChars[i * 2 + 1] = hexDigits[v and 0x0F]
            }

            return String(hexChars)
        }

//        private fun PicInfo.toByteArray(): ByteArray {
//            val buffer = ByteArray(27) // 1 + 1 + 1 + 4 + 4 + 16 = 27 bytes
//
//            // id: 1 byte
//            buffer[0] = id.toByte()
//
//            // triggerType: 1 byte
//            buffer[1] = triggerType.value.toByte()
//
//            // angel: 1 byte (有符号)
//            buffer[2] = angel
//
//            // timestamp: 4 bytes 小端
//            buffer[3] = (timestamp and 0xFF).toByte()
//            buffer[4] = ((timestamp shr 8) and 0xFF).toByte()
//            buffer[5] = ((timestamp shr 16) and 0xFF).toByte()
//            buffer[6] = ((timestamp shr 24) and 0xFF).toByte()
//
//            // length: 4 bytes 小端
//            buffer[7] = (length and 0xFF).toByte()
//            buffer[8] = ((length shr 8) and 0xFF).toByte()
//            buffer[9] = ((length shr 16) and 0xFF).toByte()
//            buffer[10] = ((length shr 24) and 0xFF).toByte()
//
//            // md5: 16 bytes (将32个字符的hex字符串转换为16字节)
//            if (md5.length != 32) {
//                throw IllegalArgumentException("MD5字符串必须是32个字符")
//            }
//
//            for (i in 0 until 16) {
//                val hexByte = md5.substring(i * 2, i * 2 + 2)
//                buffer[11 + i] = hexByte.toInt(16).toByte()
//            }
//
//            return buffer
//        }
//
//        private fun ByteArray.toPicInfo(): PicInfo {
//            require(size >= 27) { "字节数组长度至少需要27字节，当前为$size" }
//
//            return PicInfo(
//                id = this[0].toUByte(),
//                triggerType = this[1].toUByte().let { triggerValue ->
//                    TriggerType.entries.find { it.value == triggerValue }
//                        ?: throw IllegalArgumentException(
//                            "未知的TriggerType值: 0x${
//                                triggerValue.toString(
//                                    16
//                                )
//                            }"
//                        )
//                },
//                angel = this[2],
//                timestamp = getLittleEndianInt(3),
//                length = getLittleEndianInt(7),
//                md5 = this.copyOfRange(11, 27).toHexString()
//            )
//        }
//
//        // 辅助扩展函数：从小端字节序解析Int
//        private fun ByteArray.getLittleEndianInt(startIndex: Int): Int {
//            require(startIndex + 4 <= size) { "起始索引超出范围" }
//            return (this[startIndex].toInt() and 0xFF) or
//                    ((this[startIndex + 1].toInt() and 0xFF) shl 8) or
//                    ((this[startIndex + 2].toInt() and 0xFF) shl 16) or
//                    ((this[startIndex + 3].toInt() and 0xFF) shl 24)
//        }
//
//        // 辅助扩展函数：字节数组转hex字符串
//        private fun ByteArray.toHexString(): String {
//            return toHexString()
//        }

    }


}

@Deprecated("use HDRequest/HDResponse")
interface HostPicSnapProtocolParser : ProtocolParser<Unit> {
    override suspend fun encode(data: Unit): HDResult<ByteArray>
    override suspend fun decode(payload: ByteArray): HDResult<Unit>

    companion object Companion : HostPicSnapProtocolParser {
        override suspend fun encode(data: Unit): HDResult<ByteArray> {
            return byteArrayOf().success()
        }

        override suspend fun decode(payload: ByteArray): HDResult<Unit> {
            return try {
                if (payload.isNotEmpty()) Unit.success() else throw IllegalStateException("HostPicSnapProtocolParser::decode payload.size error")
            } catch (e: Throwable) {
                e.fail()
            }
        }

    }
}

sealed interface HDRequest {
    data object Empty : HDRequest

    data object PicSnap : HDRequest, ProtocolParser<Unit> {
        override suspend fun encode(data: Unit): HDResult<ByteArray> {
            return byteArrayOf().success()
        }

        override suspend fun decode(payload: ByteArray): HDResult<Unit> {
            return try {
                if (payload.isNotEmpty()) Unit.success() else throw IllegalStateException("payload.size error")
            } catch (e: Throwable) {
                e.fail()
            }
        }
    }

    data class ExtraPropertyGet(val id: UByte) : HDRequest {
        companion object : ProtocolParser<ExtraPropertyGet> {


            override suspend fun encode(data: ExtraPropertyGet): HDResult<ByteArray> {
                return try {
                    byteArrayOf(data.id.toByte()).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<ExtraPropertyGet> {
                return try {
                    if (payload.size != 1) throw IllegalStateException("ExtraPropertyGet::decode payload size error ${payload.size}")
                    ExtraPropertyGet(
                        payload[0].toUByte(),
                    ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }
        }
    }

    data class PicPull(val picId: UByte, val offset: Int, val readLen: Int) : HDRequest {
        companion object : ProtocolParser<PicPull> {
            override suspend fun encode(data: PicPull): HDResult<ByteArray> {
                return try {
                    (byteArrayOf(data.picId.toByte()) + data.offset.toByteArray()
                            + data.readLen.toByteArray()
                            ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<PicPull> {
                return try {
                    if (payload.size != 9) throw IllegalStateException("PicPull::decode payload size error ${payload.size}")
                    PicPull(
                        payload[0].toUByte(),
                        payload.toInt(1), payload.toInt(5)
                    ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }


        }
    }

    data class PicDelete(val picId: UByte) : HDRequest {
        companion object : ProtocolParser<PicDelete> {
            override suspend fun encode(data: PicDelete): HDResult<ByteArray> {
                return try {
                    byteArrayOf(data.picId.toByte()).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<PicDelete> {
                return try {
                    if (payload.size != 1) throw IllegalStateException("PicDelete::decode payload size error ${payload.size}")
                    PicDelete(
                        payload[0].toUByte(),
                    ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }


        }
    }

    data class AppOTA(val fileSize: Int, val md5: ByteArray) : HDRequest {
        companion object : ProtocolParser<AppOTA> {
            override suspend fun encode(data: AppOTA): HDResult<ByteArray> {
                return try {
                    (HDUtils.run { data.fileSize.toByteArray() } + data.md5
                            ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<AppOTA> {
                return try {
                    if (payload.size != 4 + 16) throw IllegalStateException("AppOTA::decode payload size error ${payload.size}")
                    AppOTA(
                        payload.toInt(0),
                        payload.copyOfRange(4, payload.size - 1)
                    ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }


        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AppOTA

            if (fileSize != other.fileSize) return false
            if (!md5.contentEquals(other.md5)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = fileSize
            result = 31 * result + md5.contentHashCode()
            return result
        }
    }

    data class SystemOTA(val fileSize: Int, val md5: ByteArray) : HDRequest {
        companion object : ProtocolParser<SystemOTA> {
            override suspend fun encode(data: SystemOTA): HDResult<ByteArray> {
                return try {
                    (HDUtils.run { data.fileSize.toByteArray() } + data.md5
                            ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<SystemOTA> {
                return try {
                    if (payload.size != 4 + 16) throw IllegalStateException("AppOTA::decode payload size error ${payload.size}")
                    SystemOTA(
                        payload.toInt(0),
                        payload.copyOfRange(4, payload.size - 1)
                    ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }


        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AppOTA

            if (fileSize != other.fileSize) return false
            if (!md5.contentEquals(other.md5)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = fileSize
            result = 31 * result + md5.contentHashCode()
            return result
        }
    }
}

sealed interface HDResponse {
    data class PicSnap(val result: UByte, val picId: UByte) : HDResponse {
        companion object : ProtocolParser<PicSnap> {
            override suspend fun encode(data: PicSnap): HDResult<ByteArray> {
                return try {
                    byteArrayOf(data.result.toByte(), data.picId.toByte()).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<PicSnap> {
                return try {
                    if (payload.size != 2) throw IllegalStateException("HDResponse.PicSnap::decode payload size error ${payload.size}")
                    PicSnap(payload[0].toUByte(), payload[1].toUByte()).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

        }
    }

    class PicPull(val result: UByte, val pic: ByteArray) : HDResponse {
        companion object : ProtocolParser<PicPull> {
            override suspend fun encode(data: PicPull): HDResult<ByteArray> {
                return try {
                    (byteArrayOf(data.result.toByte()) + data.pic).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<PicPull> {
                return try {
                    println("PicPull::decode ${payload.size}")
                    if (payload.isEmpty()) throw IllegalStateException("PicPull::decode payload size error ${payload.size}")
                    if (payload.size == 1) {
                        PicPull(
                            payload[0].toUByte(),
                            byteArrayOf()
                        ).success()
                    } else {
                        PicPull(
                            payload[0].toUByte(),
                            payload.copyOfRange(1, payload.size - 1)
                        ).success()
                    }

                } catch (e: Exception) {
                    e.fail()
                }
            }

        }
    }

    data class PicDelete(val result: UByte) : HDRequest {
        companion object : ProtocolParser<PicDelete> {
            override suspend fun encode(data: PicDelete): HDResult<ByteArray> {
                return try {
                    byteArrayOf(data.result.toByte()).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<PicDelete> {
                return try {
                    if (payload.size != 1) throw IllegalStateException("PicDelete::decode payload size error ${payload.size}")
                    PicDelete(
                        payload[0].toUByte(),
                    ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }


        }
    }

    data class ExtraPropertyGet(val id: UByte, val result: UByte, val value: ByteArray) :
        HDRequest {
        companion object : ProtocolParser<ExtraPropertyGet> {
            override suspend fun encode(data: ExtraPropertyGet): HDResult<ByteArray> {
                return try {
                    (byteArrayOf(data.id.toByte(), data.result.toByte()) + data.value).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<ExtraPropertyGet> {
                return try {
                    if (payload.size < 2) throw IllegalStateException("ExtraPropertyGet::decode payload size error ${payload.size}")
                    ExtraPropertyGet(
                        payload[0].toUByte(),
                        payload[1].toUByte(),
                        payload.copyOfRange(2, payload.size - 1)
                    ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ExtraPropertyGet

            if (id != other.id) return false
            if (result != other.result) return false
            if (!value.contentEquals(other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            var result1 = id.hashCode()
            result1 = 31 * result1 + result.hashCode()
            result1 = 31 * result1 + value.contentHashCode()
            return result1
        }
    }

    data class AppOTANotice(val result: UByte, val offset: Int) : HDRequest {
        companion object Companion : ProtocolParser<AppOTANotice> {
            override suspend fun encode(data: AppOTANotice): HDResult<ByteArray> {
                return try {
                    (byteArrayOf(data.result.toByte()) + HDUtils.run { data.offset.toByteArray() }
                            ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<AppOTANotice> {
                return try {
                    if (payload.size != 5) throw IllegalStateException("AppOTA::decode payload size error ${payload.size}")
                    AppOTANotice(
                        payload[0].toUByte(),
                        payload.toInt(1)
                    ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }
        }
    }

    data class SystemOTANotice(val result: UByte, val offset: Int) : HDRequest {
        companion object Companion : ProtocolParser<SystemOTANotice> {
            override suspend fun encode(data: SystemOTANotice): HDResult<ByteArray> {
                return try {
                    (byteArrayOf(data.result.toByte()) + HDUtils.run { data.offset.toByteArray() }
                            ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }

            override suspend fun decode(payload: ByteArray): HDResult<SystemOTANotice> {
                return try {
                    if (payload.size != 5) throw IllegalStateException("SystemOTANotice::decode payload size error ${payload.size}")
                    SystemOTANotice(
                        payload[0].toUByte(),
                        payload.toInt(1)
                    ).success()
                } catch (e: Exception) {
                    e.fail()
                }
            }
        }
    }

}