package com.yunext.iot.ui.protocol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunext.iot.domain.HDResult
import com.yunext.iot.domain.project.HDProject
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.HDRequest
import com.yunext.iot.domain.protocol.HDResponse
import com.yunext.iot.domain.protocol.HostHeartbeatProtocolParser
import com.yunext.iot.domain.protocol.PicInfo
import com.yunext.iot.domain.protocol.SlavePicInfoProtocolParser
import com.yunext.iot.domain.protocol.isEmpty
import com.yunext.iot.domain.protocol.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PicSnapSender(
    modifier: Modifier,
    cmd: HDProtocolCmd,
    address: UByte,
    output: HDProtocolFrame,
    onSend: (ByteArray) -> Unit,
) {

    var inputFrame: HDProtocolFrame by remember {
        mutableStateOf(HDProtocolFrame.EMPTY)
    }

    LaunchedEffect(address) {
        launch {
            val todo = withContext(Dispatchers.IO) {
                val result = HDRequest.PicSnap.encode(
                    Unit
                )

                when (result) {
                    is HDResult.Fail -> {
                        HDProtocolFrame.EMPTY
                    }

                    is HDResult.Success<ByteArray> -> {
                        HDProtocolFrame(
                            address = address,
                            cmd =  cmd.key, result.data
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
                    //
                }

            }


        }

//        Text("${frame}")

        var snapResponse: HDResponse.PicSnap? by remember {
            mutableStateOf(null)
        }


        LaunchedEffect(output) {
            launch {
                val resp = withContext(Dispatchers.IO) {
                    val result = HDResponse.PicSnap.decode(output.payload.byteArray)
                    when (result) {
                        is HDResult.Fail -> null
                        is HDResult.Success<HDResponse.PicSnap> -> result.data
                    }
                }
                snapResponse = resp
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("[输出]")
            if (snapResponse != null) {
                Text("result : 0x${snapResponse?.result?.toHexString()}")
                Text("picId : 0x${snapResponse?.picId?.toHexString()}")
            }

            if (!output.isEmpty) {
                HDProtocolFrameDisplay(modifier = Modifier, frame = output)
            }
        }
    }

}


