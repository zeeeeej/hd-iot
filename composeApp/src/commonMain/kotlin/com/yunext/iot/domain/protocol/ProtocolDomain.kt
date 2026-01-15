package com.yunext.iot.domain.protocol

import com.yunext.iot.domain.project.HDProject

interface ProtocolDomain {
    suspend fun listProtocol(project: HDProject): List<ProtocolInfo>
}

