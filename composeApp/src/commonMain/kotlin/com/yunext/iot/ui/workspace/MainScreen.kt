package com.yunext.iot.ui.workspace

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import color
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.uart.ByteBlock
import com.yunext.iot.ui.uart.ByteData
import com.yunext.iot.ui.uart.ByteDisplayMode
import com.yunext.iot.ui.uart.ComInfoVO
import com.yunext.iot.ui.uart.UartInfoItemV2
import com.yunext.iot.ui.uart.UartInfoListSplit
import randomZhongGuoSe

/**
 * Menu-主页
 */
@Composable
fun MainScreen(
    modifier: Modifier,
    comInfoList: List<ComInfoVO>,
    sendEffect: Effect<String, ByteArray>,
    receiveData: String, onSend: (ComInfoVO, String) -> Unit,
    onConnect: (ComInfoVO) -> Unit,
    onDisconnect: (ComInfoVO) -> Unit,
    onEditRate: (ComInfoVO) -> Unit,
) {
    var currentComInfo: ComInfoVO? by remember {
        mutableStateOf(null)
    }

    var selectedComInfoDialog: Boolean by remember {
        mutableStateOf(false)
    }

    var byteDisplayMode: ByteDisplayMode by remember {
        mutableStateOf(ByteDisplayMode.Hex)
    }

    LaunchedEffect(comInfoList) {
        if (comInfoList.isEmpty()) {
            currentComInfo = null
            return@LaunchedEffect
        }
        val cur = currentComInfo
        if (cur != null) {
            val find = comInfoList.singleOrNull() {
                it.path == cur.path
            }
            if (find == null) {
                currentComInfo = null
            } else {
                currentComInfo = find
            }
        } else {
            currentComInfo = comInfoList[0]
        }
    }

    Box(modifier = Modifier/*.randomBG()*/) {
        Column() {

            AnimatedVisibility(selectedComInfoDialog) {
                UartInfoListSplit(
                    modifier = Modifier,
                    list = comInfoList,
                    info = currentComInfo,
                    onSelected = {
                        currentComInfo = it
                        selectedComInfoDialog = false
                    }
                )
            }

            Box(
                modifier
                    //.randomBG()
                    .padding(12.dp).fillMaxWidth()
            ) {
                CurrentComInfo(
                    modifier = Modifier,
                    current = currentComInfo,
                    onConnect = onConnect,
                    onDisconnect = onDisconnect,
                    onSelectList = {
                        if (comInfoList.isEmpty()) return@CurrentComInfo
                        selectedComInfoDialog = !selectedComInfoDialog
                    }, onEditRate = onEditRate
                )
            }

            CurrentByteDisplayMode(
                modifier = Modifier.padding(horizontal = 12.dp),
                mode = byteDisplayMode
            ) {
                byteDisplayMode = it
            }
            Box(
                modifier.weight(1f)
                    //.randomBG()
                    .padding(12.dp).fillMaxWidth()
            ) {
                Sender(modifier = Modifier.fillMaxSize(), onSend = {
                    val info = currentComInfo ?: return@Sender
                    onSend(info, it)
                }, mode = byteDisplayMode, sendEffect = sendEffect)
            }

            Box(
                modifier.weight(1f)
                    //.randomBG()
                    .padding(12.dp).fillMaxWidth()
            ) {
                Receiver(
                    modifier = Modifier.fillMaxSize(),
                    data = receiveData,
                    mode = byteDisplayMode
                )
            }

        }
    }
}

@Composable
private fun CurrentByteDisplayMode(
    modifier: Modifier,
    mode: ByteDisplayMode,
    onChanged: (ByteDisplayMode) -> Unit
) {

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            "显示模式：",
            style = MaterialTheme.typography.bodySmall,
            color = randomZhongGuoSe().color,
            fontWeight = FontWeight.Normal,
        )
        Text(
            when (mode) {
                ByteDisplayMode.Asic -> "ASCII"
                ByteDisplayMode.Hex -> "HEX"
                ByteDisplayMode.Oct -> "OCT"
            },
            style = MaterialTheme.typography.bodySmall,
            color = randomZhongGuoSe().color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onChanged(
                    when (mode) {
                        ByteDisplayMode.Asic -> ByteDisplayMode.Hex
                        ByteDisplayMode.Hex -> ByteDisplayMode.Oct
                        ByteDisplayMode.Oct -> ByteDisplayMode.Asic
                    }
                )
            }
        )
    }

}

@Composable
private fun SendEffect(modifier: Modifier, sendEffect: Effect<String, ByteArray>) {
    AnimatedContent(
        modifier = modifier,
        targetState = when (sendEffect) {
            Effect.Completed -> ("发送完毕")
            is Effect.Fail<*> -> ("发送失败!${sendEffect.output.message}")
            Effect.Idle -> {
                ""
            }

            is Effect.Progress<*, *> -> ("发送中 ... ... ${sendEffect.progress}")
            is Effect.Success<*, *> -> ("发送成功！")
        }
    ) {
        Text(
            it,
            style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.End),
            color = randomZhongGuoSe().color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CurrentComInfo(
    modifier: Modifier = Modifier, current: ComInfoVO?,
    onConnect: (ComInfoVO) -> Unit,
    onDisconnect: (ComInfoVO) -> Unit,
    onSelectList: () -> Unit,
    onEditRate: (ComInfoVO) -> Unit
) {
    Box(modifier = modifier.fillMaxWidth().clickable { onSelectList() }) {
        if (current != null) {
            UartInfoItemV2(
                modifier = Modifier,
                info = current,
                onConnect = {
                    onConnect(current)
                },
                onDisconnect = {
                    onDisconnect(current)
                }, onEditRate = {
                    onEditRate(current.copy(rate = it))
                }
            )
        } else {
            Text("请选择串口")
        }
    }
}

@Composable
private fun Sender(
    modifier: Modifier = Modifier,
    mode: ByteDisplayMode,
    sendEffect: Effect<String, ByteArray>,
    onSend: (String) -> Unit
) {
    var data by remember { mutableStateOf("AA5aeec201000000a06ead") }
    val list: List<ByteData> by remember(data, mode) {
        derivedStateOf {
            try {
                data.hexToByteArray().mapIndexed() { index, data ->
                    ByteData(index, data, mode)
                }
            } catch (e: Throwable) {
                listOf()
            }
        }
    }
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("[发送]")
            SendEffect(sendEffect = sendEffect, modifier = Modifier.weight(1f))
        }

        TextField(
            value = data,
            onValueChange = { v ->
                try {
                    data = v
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
//            keyboardActions = KeyboardActions.Default.onSend,
            trailingIcon = {
                Text("发送", modifier = Modifier.clickable {
                    onSend(data)
                })
            }
        )


        ByteBlock(
            modifier = Modifier.wrapContentSize(),
            list = list
        )
    }
}

@Composable
private fun Receiver(
    modifier: Modifier = Modifier, data: String,
    mode: ByteDisplayMode,
) {

    val list: List<ByteData> by remember(data, mode) {
        derivedStateOf {
            try {
                data.hexToByteArray().mapIndexed() { index, data ->
                    ByteData(index, data, mode = mode)
                }
            } catch (e: Throwable) {
                listOf()
            }
        }
    }
    Column(modifier = modifier) {
        Text("[接受]")
        Text(data)
        ByteBlock(
            modifier = Modifier.wrapContentSize(),
            list = list,
        )
    }
}