package com.yunext.iot.ui.protocol

import ZhongGuoSe
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.HDProtocolFrameType
import com.yunext.iot.domain.protocol.HDUtils.toInt
import com.yunext.iot.domain.protocol.isEmpty
import com.yunext.iot.ui.uart.ByteData
import com.yunext.iot.ui.uart.ByteDisplayMode
import com.yunext.iot.ui.uart.toByteData

@Composable
fun HDProtocolFrameDisplay(
    modifier: Modifier,
    frame: HDProtocolFrame
) {
    if (frame.isEmpty){
        Text("【空】")
    }else{
        Column(modifier = modifier.background(ZhongGuoSe.粉绿.color.copy(.1f))) {
            HDProtocolFrameDisplayItem(Modifier.fillMaxWidth(), frame.head)
            HDProtocolFrameDisplayItem(Modifier.fillMaxWidth(), frame.address)
            HDProtocolFrameDisplayItem(Modifier.fillMaxWidth(), frame.cmd)
            HDProtocolFrameDisplayItem(Modifier.fillMaxWidth(), frame.length)
            HDProtocolFrameDisplayItem(Modifier.fillMaxWidth(), frame.payload)
            HDProtocolFrameDisplayItem(Modifier.fillMaxWidth(), frame.crc)
        }
    }

}

@Composable
private fun HDProtocolFrameDisplayItem(
    modifier: Modifier,
    type: HDProtocolFrameType
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {

        Box {
            Text(
                modifier = Modifier,
                text = when (type) {
                    is HDProtocolFrameType.Address -> "[地址] "
                    is HDProtocolFrameType.Cmd -> "[命令] "
                    is HDProtocolFrameType.Crc -> "[校验] "
                    is HDProtocolFrameType.DataLength -> "[长度] "
                    HDProtocolFrameType.Head -> "[帧头] "
                    is HDProtocolFrameType.Payload -> "[数据] "
                }, style = TextStyle.Default.copy(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                modifier = Modifier.align(Alignment.TopEnd),
                text = "${type.byteArray.size}", style = TextStyle.Default.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraLight
                ), color = Color.Red
            )
        }

        LazyRow {
            items(type.byteArray.toByteData(), { it }) {
                ByteItem(
                    modifier = Modifier, byte = it, color = when (type) {
                        is HDProtocolFrameType.Address -> ZhongGuoSe.金莲花橙.color
                        is HDProtocolFrameType.Cmd -> ZhongGuoSe.洋葱紫.color
                        is HDProtocolFrameType.Crc -> ZhongGuoSe.姜红.color
                        is HDProtocolFrameType.DataLength -> ZhongGuoSe.晴山蓝.color
                        HDProtocolFrameType.Head -> ZhongGuoSe.金叶黄.color
                        is HDProtocolFrameType.Payload -> ZhongGuoSe.宝石绿.color
                    }
                )
            }
        }
        Text(
            when (type) {
                is HDProtocolFrameType.Address -> type.address.toString()
                is HDProtocolFrameType.Cmd -> null
                is HDProtocolFrameType.Crc -> null
                is HDProtocolFrameType.DataLength -> type.byteArray.toInt().toString()
                HDProtocolFrameType.Head -> null
                is HDProtocolFrameType.Payload -> null
            }?.let { "<$it>" } ?: "", color = Color.Red, fontSize = 11.sp
        )
    }
}


@Composable
private fun ByteItem(modifier: Modifier, byte: ByteData, color: Color, edit: Boolean = false) {
    var mode: ByteDisplayMode by remember(byte.mode) { mutableStateOf(byte.mode) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .wrapContentSize()
//                .height(48.dp)
//                .width(48.dp)
                .padding(1.dp)
//                .aspectRatio(1f)
                .border(
                    width = 1.dp,
                    color = color,//Color.Black,
                    shape = RoundedCornerShape(2.dp),
                ).clickable {
                    mode = when (mode) {
                        ByteDisplayMode.Asic -> ByteDisplayMode.Hex
                        ByteDisplayMode.Hex -> ByteDisplayMode.Oct
                        ByteDisplayMode.Oct -> ByteDisplayMode.Asic
                    }
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                when (mode) {
                    ByteDisplayMode.Asic -> "${byte.byte.toInt().toChar()}"
                    ByteDisplayMode.Hex -> byte.byte.toHexString()
                    ByteDisplayMode.Oct -> "${byte.byte.toInt()}"
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    color = if (edit) Color.Red else Color.Black
                ),
                modifier = Modifier.wrapContentSize()
            )
        }
    }

}

@Composable
private fun ByteItemV2(
    modifier: Modifier,
    text: String,
    color: Color,
    edit: Boolean = false,
    onClick: () -> Unit
) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .wrapContentSize()
//                .height(48.dp)
//                .width(48.dp)
                .padding(1.dp)
//                .aspectRatio(1f)
                .border(
                    width = 1.dp,
                    color = color,//Color.Black,
                    shape = RoundedCornerShape(2.dp),
                ).clickable {

                    onClick()
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    color = if (edit) Color.Red else Color.Black
                ),
                modifier = Modifier.wrapContentSize()
            )
        }
    }

}



