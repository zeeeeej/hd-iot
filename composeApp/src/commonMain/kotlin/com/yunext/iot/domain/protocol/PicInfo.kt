package com.yunext.iot.domain.protocol

enum class TriggerType(val value: UByte) {
    Gyroscope(0x00.toUByte()), Initiative(0x01.toUByte());
    companion object {
        fun fromValue(value: UByte): TriggerType {
            return entries.firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Invalid TriggerType value: $value")
        }
    }
}

data class PicInfo(
    val id: UByte, // 1byte
    val triggerType: TriggerType, // 1byte
    val angel: Byte, // 1byte
    val timestamp: Int, // 4byte 小端
    val length: Int, // 4 byte 小端
    val md5: String, // 16 byte
)

