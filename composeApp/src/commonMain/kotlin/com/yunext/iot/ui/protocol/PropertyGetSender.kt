package com.yunext.iot.ui.protocol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.isEmpty

@Composable
fun PropertyGetSender(
    modifier: Modifier,
    address: UByte,
    output: HDProtocolFrame,
    onSend: (ByteArray) -> Unit,
    propertyList: List<PropertyVo>
) {
    var property: PropertyVo? by remember {
        mutableStateOf(null)
    }


    val frame by remember(property, address) {
        derivedStateOf {
            val p = property
            if (p == null) {
                HDProtocolFrame.EMPTY
            } else
                HDProtocolFrame(
                    address = address,
                    cmd = HDProtocolCmd.PropertyGet.key,
                    byteArrayOf(p.id.key.toByte())
                )
        }

    }

    Row(modifier = modifier) {

        Column(modifier = Modifier.weight(1f)) {
            Text("[输入]")
            CommonSender(modifier = Modifier, frame = frame, onSend = {
                onSend.invoke(it)
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("属性ID：")
                        Text("0x${property?.id?.key?.toHexString() ?: "-"}")
                    }
                }

            }
            PropertyDisplay(modifier = Modifier.fillMaxWidth().height(160.dp), list = propertyList, onGet = {
                property = it
            })
        }

        Column(modifier = Modifier.weight(1f)) {
            Text("[输出]")

            if (output.isEmpty) {

            } else {
                Text(
                    "属性ID：0x${
                        if (output.isEmpty) {
                            "-"
                        } else output.payload.byteArray[0].toHexString()
                    }"
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "结果：0x${
                        if (output.isEmpty) {
                            "-"
                        } else output.payload.byteArray[1].toHexString()
                    }"
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "属性ID值：${
                        if (output.isEmpty) {
                            ("-")
                        } else {
                            (output.payload.byteArray.run {
                                this.slice(2..<this.size).toByteArray().toHexString()
                            })
                        }
                    }"
                )
                HDProtocolFrameDisplay(modifier = Modifier, frame = output)
            }

        }
    }
}
