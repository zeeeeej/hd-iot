package com.yunext.iot.ui.protocol

import ZhongGuoSe
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import color
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.HDProtocolFrameType
import com.yunext.iot.domain.protocol.isEmpty
import com.yunext.iot.domain.protocol.toByteArray
import com.yunext.iot.ui.uart.ByteData
import com.yunext.iot.ui.uart.ByteDisplayMode

val HDProtocolFrameType.color: Color
    get() {
        return when (this) {
            is HDProtocolFrameType.Address -> ZhongGuoSe.明绿.color
            is HDProtocolFrameType.Cmd -> ZhongGuoSe.金叶黄.color
            is HDProtocolFrameType.Crc -> ZhongGuoSe.云山蓝.color
            is HDProtocolFrameType.DataLength -> ZhongGuoSe.丁香淡紫.color
            HDProtocolFrameType.Head -> ZhongGuoSe.品红.color
            is HDProtocolFrameType.Payload -> ZhongGuoSe.金莲花橙.color
        }
    }

@Composable
fun CommonSender(
    modifier: Modifier,
    frame: HDProtocolFrame,
    onSend: (ByteArray) -> Unit,
    content: @Composable HDProtocolFrame.() -> Unit
) {
    Column(modifier) {
        content(frame)

        HDProtocolFrameDisplay(modifier = Modifier, frame = frame)
        if (!frame.isEmpty){
            Box(Modifier.fillMaxWidth().clickable() {
                onSend(frame.toByteArray())
            }, contentAlignment = Alignment.Center) {
                Text("[发送]", modifier = Modifier)
            }
        }

    }

}
