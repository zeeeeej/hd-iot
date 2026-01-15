package com.yunext.iot.domain.uart

sealed interface UartLogcat {
    val com: Uart
    val timestamps: Long
}

data class NormalLogcat(
    override val com: Uart,
    override val timestamps: Long,
    val msg: String
) : UartLogcat

data class InputLogcat(
    override val com: Uart,
    override val timestamps: Long,
    val data: ByteArray
) : UartLogcat {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InputLogcat

        if (timestamps != other.timestamps) return false
        if (com != other.com) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamps.hashCode()
        result = 31 * result + com.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

data class OutputLogcat(
    override val com: Uart,
    override val timestamps: Long,
    val data: ByteArray
) : UartLogcat {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutputLogcat

        if (timestamps != other.timestamps) return false
        if (com != other.com) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamps.hashCode()
        result = 31 * result + com.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}