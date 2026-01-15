package com.yunext.iot.domain.project

sealed interface HDProject{
    val name:String
    val key:String
    val secretKey: ByteArray

    data object KSF:HDProject{
        override val name: String
            get() = "康师傅"
        override val key: String
            get() = "hd-ksf-0001"
        override val secretKey: ByteArray
            get() = byteArrayOf(0x01,0x02,0x03)

    }

    data object JML:HDProject{
        override val name: String
            get() = "今麦郎"
        override val key: String
            get() = "hd-jml-0002"
        override val secretKey: ByteArray
            get() = byteArrayOf(0x01,0x02,0x03)

    }

    data object XW:HDProject{
        override val name: String
            get() = "玄武"
        override val key: String
            get() = "hd-xw-0003"
        override val secretKey: ByteArray
            get() = byteArrayOf(0x01,0x02,0x03)

    }

    data object AI:HDProject{
        override val name: String
            get() = "AI语音"
        override val key: String
            get() = "hd-ai-0003"
        override val secretKey: ByteArray
            get() = byteArrayOf(0x01,0x02,0x03)

    }

}