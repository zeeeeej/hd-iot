package com.yunext.iot.ui.protocol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yunext.iot.domain.HDResult
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.HostHeartbeatProtocolParser
import com.yunext.iot.domain.protocol.SlaveHeartbeatProtocolParser
import com.yunext.iot.domain.protocol.isEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HeartBeatSender(
    modifier: Modifier,
    address: UByte,
    ack: UByte,
    output: HDProtocolFrame,
    onSend: (ByteArray) -> Unit,
) {
    var ackInternal: UByte by rememberSaveable() {
        mutableStateOf(ack)
    }
    var inputFrame: HDProtocolFrame by remember {
        mutableStateOf(HDProtocolFrame.EMPTY)
    }
//    val frame by remember(ackInternal, address) {
//        derivedStateOf {
//            HDProtocolFrame(
//                address = address,
//                cmd = HDProtocolCmd.HeartBeat.key,
//                //byteArrayOf(ackInternal.toByte())
//                HDProtocol.ps[HDProtocolCmd.HeartBeat]?.host?.encode(
//                    HDProtocolBiz.HeartbeatAck(
//                        ackInternal
//                    )
//                )?.byteArray ?: byteArrayOf()
//
//            )
//        }
//    }
    LaunchedEffect(ackInternal, address) {
        launch {
            val todo = withContext(Dispatchers.IO) {
                val result = HostHeartbeatProtocolParser.encode(
                    ackInternal
                )

                when (result) {
                    is HDResult.Fail -> {
                        HDProtocolFrame.EMPTY
                    }

                    is HDResult.Success<ByteArray> -> {
                        HDProtocolFrame(
                            address = address,
                            cmd = HDProtocolCmd.HeartBeat.key, result.data
                        )
                    }
                }
            }

            inputFrame = todo
        }
    }

    Row(modifier = modifier) {

        Column(modifier = Modifier.weight(1f)) {
            Text("[输入]")
            CommonSender(modifier = Modifier, frame = inputFrame, onSend = {
                onSend.invoke(it)
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(" - ", Modifier.clickable {
                        ackInternal--
                    })

                    Text("ack:$ackInternal")

                    Text(" + ", Modifier.clickable {
                        ackInternal++
                    })
                }

            }


        }

//        Text("${frame}")

        var outputAck: UByte? by remember {
            mutableStateOf(null)
        }

        LaunchedEffect(output) {
            val resp = withContext(Dispatchers.IO) {
                val result = SlaveHeartbeatProtocolParser.decode(output.payload.byteArray)
                when(result){
                    is HDResult.Fail -> null
                    is HDResult.Success<UByte> -> result.data
                }
            }
            outputAck = resp
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("[输出]")
            if (outputAck == null) {
                Text("ack:-")
            } else {
                Text("ack:${outputAck}")
            }

            if (!output.isEmpty) {
                HDProtocolFrameDisplay(modifier = Modifier, frame = output)
            }
        }
    }
}
