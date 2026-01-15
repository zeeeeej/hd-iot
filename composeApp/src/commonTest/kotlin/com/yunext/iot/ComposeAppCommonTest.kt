package com.yunext.iot

import com.yunext.iot.domain.protocol.HDUtils
import com.yunext.iot.domain.protocol.toProtocolFrame
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeAppCommonTest {

    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun test_toProtocolFrame(){
        println("test_toProtocolFrame")
        val data = "aa 5a ee 01 01 00 00 00 05 f1 38"
        println(data)
        val byteArray = HDUtils.run { data.hexToByteArray() }
        println(byteArray)
        val protocolFrame = byteArray.toProtocolFrame()
        println(protocolFrame)

    }
}