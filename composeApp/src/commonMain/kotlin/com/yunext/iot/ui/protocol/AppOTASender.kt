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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yunext.iot.domain.HDResult
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.HDRequest
import com.yunext.iot.domain.protocol.HDResponse
import com.yunext.iot.domain.protocol.display
import com.yunext.iot.domain.protocol.isEmpty
import com.yunext.iot.platform.FileInfo
import com.yunext.iot.platform.selectAppFile
import com.yunext.iot.platform.selectAppFileBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FileVo(val path: String, val fileSize: Int, val fileMd5: ByteArray,val file: FileInfo
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileVo

        if (fileSize != other.fileSize) return false
        if (path != other.path) return false
        if (!fileMd5.contentEquals(other.fileMd5)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileSize
        result = 31 * result + path.hashCode()
        result = 31 * result + fileMd5.contentHashCode()
        return result
    }
}

@Composable
fun AppOTASender(
    modifier: Modifier,
    address: UByte,
    output: HDProtocolFrame,
    onSend: (ByteArray) -> Unit,
    onSendAppFile: (ByteArray) -> Unit,
) {

    var fileVO: FileVo? by remember {
        mutableStateOf(null)
    }

    val coroutineScope = rememberCoroutineScope()

    var inputFrame: HDProtocolFrame by remember {
        mutableStateOf(HDProtocolFrame.EMPTY)
    }

    LaunchedEffect(address, fileVO) {
        launch {
            val todo = withContext(Dispatchers.IO) {
                val tmp = fileVO ?: return@withContext HDProtocolFrame.EMPTY
                val result = HDRequest.AppOTA.encode(
                    HDRequest.AppOTA(tmp.fileSize, tmp.fileMd5)
                )
                when (result) {
                    is HDResult.Fail -> {
                        HDProtocolFrame.EMPTY
                    }

                    is HDResult.Success<ByteArray> -> {
                        HDProtocolFrame(
                            address = address,
                            cmd =
                                HDProtocolCmd.AppOTANotice.key, result.data
                        )
                    }
                }
            }
            inputFrame = todo
        }
    }
//    FilePickerFactory.create();
//    val picker:FilePicker by remember { mutableStateOf(    FilePickerFactory.create()) }
    Row(modifier = modifier) {

        Column(modifier = Modifier.weight(1f)) {
            Text("[输入]")

            Text("选择文件",Modifier.clickable {
                coroutineScope.launch {
                    val file = selectAppFile()?:return@launch
                    fileVO = file
                }
            })

            CommonSender(modifier = Modifier, frame = inputFrame, onSend = {
                onSend.invoke(it)
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column() {
                        Text("path:0x${fileVO?.path ?: "-"}")
                        Text("size:0x${fileVO?.fileSize?.toHexString() ?: "-"} (${fileVO?.fileSize ?: "-"})")
                        Text("md5:${fileVO?.fileMd5?.toHexString() ?: "-"}")
                    }
                }

            }


        }

//        Text("${frame}")

        var snapResponse: HDResponse.AppOTANotice? by remember {
            mutableStateOf(null)
        }


        LaunchedEffect(output) {
            launch {
                try {
                    val resp = withContext(Dispatchers.IO) {
                        val result = HDResponse.AppOTANotice.decode(output.payload.byteArray)
                        when (result) {
                            is HDResult.Fail -> null
                            is HDResult.Success<HDResponse.AppOTANotice> -> result.data
                        }
                    }
                    snapResponse = resp
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("[输出]")
            if (snapResponse != null) {
                Text("result : 0x${snapResponse?.result?.toHexString()}")
                Text("offset : 0x${snapResponse?.offset ?: "-"}")
            }

            if (!output.isEmpty) {
                HDProtocolFrameDisplay(modifier = Modifier, frame = output)
                var otaFileSize by remember {
                    mutableStateOf(0)
                }
                Text("升级文件${if (otaFileSize>0)"$otaFileSize bytes sending ..." else ""}", modifier = Modifier.clickable {
                    coroutineScope.launch {
                        val f = fileVO?:return@launch
                        val bytes = selectAppFileBytes(f)
                        otaFileSize = bytes.size
                        onSendAppFile(bytes)
                    }
                })
            }
        }
    }

}


