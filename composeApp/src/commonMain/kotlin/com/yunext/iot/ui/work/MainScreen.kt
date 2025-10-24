package com.yunext.iot.ui.work

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import color
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.compoent.randomBG
import com.yunext.iot.ui.uart.ComInfoVO
import com.yunext.iot.ui.uart.UartInfoItemV2
import com.yunext.iot.ui.uart.UartInfoListSplit
import randomZhongGuoSe

@Composable
fun MainScreen(
    modifier: Modifier,
    comInfoList: List<ComInfoVO>,
    sendEffect: Effect<String, ByteArray>,
    receiveData: String, onSend: (ComInfoVO, String) -> Unit,
    onConnect: (ComInfoVO) -> Unit,
    onDisconnect: (ComInfoVO) -> Unit,
) {
    var currentComInfo: ComInfoVO? by remember {
        mutableStateOf(null)
    }

    var selectedComInfoDialog: Boolean by remember {
        mutableStateOf(false)
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
                        selectedComInfoDialog = true
                    })
            }
            AnimatedContent(
                when (sendEffect) {
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
                    style = MaterialTheme.typography.bodySmall,
                    color = randomZhongGuoSe().color,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(modifier.weight(1f).randomBG().padding(12.dp).fillMaxWidth()) {
                Sender(modifier = Modifier.fillMaxSize(), onSend = {
                    val info = currentComInfo ?: return@Sender
                    onSend(info, it)
                })
            }

            Box(modifier.weight(1f).randomBG().padding(12.dp).fillMaxWidth()) {
                Receiver(modifier = Modifier.fillMaxSize(), data = receiveData)
            }

        }
    }
}

@Composable
private fun CurrentComInfo(
    modifier: Modifier = Modifier, current: ComInfoVO?,
    onConnect: (ComInfoVO) -> Unit,
    onDisconnect: (ComInfoVO) -> Unit,
    onSelectList: () -> Unit
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
                }
            )
        } else {
            Text("请选择串口")
        }
    }
}

@Composable
private fun Sender(modifier: Modifier = Modifier, onSend: (String) -> Unit) {
    var data by remember { mutableStateOf("AA5aeec201000000a06ead") }
    Column(modifier = modifier) {
        Text("[发送]")
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
    }
}

@Composable
private fun Receiver(modifier: Modifier = Modifier, data: String) {
    Column(modifier = Modifier) {
        Text("[接受]")
        Text(data)
    }
}