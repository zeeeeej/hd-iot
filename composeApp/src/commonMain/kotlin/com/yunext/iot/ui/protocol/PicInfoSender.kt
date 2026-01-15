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
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.PicInfo
import com.yunext.iot.domain.protocol.SlavePicInfoProtocolParser
import com.yunext.iot.domain.protocol.isEmpty
import com.yunext.iot.domain.protocol.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PicInfoSender(
    modifier: Modifier,
    cmd: HDProtocolCmd,
    address: UByte,
    output: HDProtocolFrame,
    onSend: (ByteArray) -> Unit,
    onParser:(List<PicInfo>)->Unit
) {

    val frame by remember( address) {
        derivedStateOf {
            HDProtocolFrame(
                address = address,
                cmd = cmd.key,
                byteArrayOf()
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
                    //
                }

            }


        }

//        Text("${frame}")

        var picInfos:List<PicInfo> by remember {
            mutableStateOf(emptyList())
        }


        LaunchedEffect(output){
            launch {
                val list = withContext(Dispatchers.IO){
                    val result = SlavePicInfoProtocolParser.decode(output.payload.byteArray)
                    when(result){
                        is HDResult.Fail -> emptyList<PicInfo>()
                        is HDResult.Success<List<PicInfo>> ->result.data
                    }
                }
                picInfos = list
                onParser(picInfos)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("[输出]")
            PicInfosDisplay(modifier = Modifier.fillMaxWidth().height(100.dp),picInfos)

            if (!output.isEmpty) {
                HDProtocolFrameDisplay(modifier = Modifier, frame = output)
            }
        }
    }

}


