package com.yunext.iot.domain.protocol

import com.yunext.iot.domain.project.HDProject

data class ProtocolInfo(
    val cmd: HDProtocolCmd, val project: HDProject
)