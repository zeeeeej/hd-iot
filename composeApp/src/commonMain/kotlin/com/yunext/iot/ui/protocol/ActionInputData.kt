package com.yunext.iot.ui.protocol

import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.HDRequest
import com.yunext.iot.domain.uart.Uart

class ActionInputData(
    val com: Uart,
    val protocol: ProtocolVO,
    val value: ByteArray,
    val frame:HDProtocolFrame,
    val actionId: String,
    val req:HDRequest
)

class ActionOutputData(
    val com: Uart,
    val protocol: ProtocolVO,
    val value: ByteArray,
    val frame:HDProtocolFrame,
    val actionId: String
)