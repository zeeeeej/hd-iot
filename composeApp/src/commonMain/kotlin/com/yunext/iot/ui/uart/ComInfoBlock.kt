package com.yunext.iot.ui.uart

import ZhongGuoSe
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import color
import com.yunext.iot.domain.ComInfo
import com.yunext.iot.domain.ComStatus
import randomZhongGuoSe

data class ComInfoVO(val path: String, val rate: Int, val status: ComStatus)

fun ComInfoVO(comInfo: ComInfo): ComInfoVO {
    return ComInfoVO(path = comInfo.com, rate = comInfo.rate, status = comInfo.status)
}

@Composable
fun ComInfoBlock(
    modifier: Modifier,
    list: List<ComInfoVO>,

    onAdd: () -> Unit,
    onList: () -> Unit,
    onDelete: (ComInfoVO) -> Unit,
    onConnect: (ComInfoVO) -> Unit,
    onDisconnect: (ComInfoVO) -> Unit,
) {

    var info: ComInfoVO? by remember { mutableStateOf(null) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .drawBehind {
                drawRect(randomZhongGuoSe().color.copy(alpha = .1f))
            }) {
        // 列表
        Box(modifier = Modifier.weight(1f)) {
            UartInfoList(
                modifier = Modifier.align(Alignment.BottomCenter),
                list = list,
                info = info,
                onSelected = {
                    info = it
                },
                onDelete = onDelete,
                onConnect = onConnect,
                onDisconnect = onDisconnect, onList = onList
            )
        }
        Spacer(Modifier.height(12.dp))
        // 添加按钮
        Box {
            AddButton(modifier = Modifier, onAdd = onAdd)
        }
    }

}

@Composable
private fun AddButton(modifier: Modifier, onAdd: () -> Unit) {
    Text(
        "添加串口",
        modifier = modifier

            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = randomZhongGuoSe().color,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onAdd()
            }
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 12.dp),
        style = TextStyle.Default.copy(
            textAlign = TextAlign.Center,
            color = randomZhongGuoSe().color,
            fontWeight = FontWeight.Bold
        )
    )
}


@Composable
private fun UartInfoList(
    modifier: Modifier,
    list: List<ComInfoVO>,
    info: ComInfoVO?,
    onSelected: (ComInfoVO) -> Unit,
    onList: () -> Unit,
    onDelete: (ComInfoVO) -> Unit,
    onConnect: (ComInfoVO) -> Unit,
    onDisconnect: (ComInfoVO) -> Unit,
) {
    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        item {
            Text(
                modifier = Modifier
                    .border(color = randomZhongGuoSe().color, width = 1.dp)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .wrapContentWidth().clickable {
                        onList()
                    },
                text = "${list.size}个", style = MaterialTheme.typography.bodyLarge,
            )
        }
        items(items = list, { it }) { item ->
            UartInfoItem(modifier = Modifier.clickable {
                onSelected(item)
            }, item, { item == info }, onDelete = {
                onDelete(item)
            }, onConnect = {
                onConnect(item)
            }, onDisconnect = {
                onDisconnect(item)
            })
        }
    }
}


@Composable
internal fun UartInfoListSplit(
    modifier: Modifier,
    list: List<ComInfoVO>,
    info: ComInfoVO?,
    onSelected: (ComInfoVO) -> Unit,

    ) {
    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items(items = list, { it }) { item ->
            UartInfoItem(modifier = Modifier.clickable {
                onSelected(item)
            }, item, { item == info }, onDelete = {

            }, onConnect = {

            }, onDisconnect = {

            }, simple = true)
        }
    }
}

@Composable
private fun UartInfoItem(
    modifier: Modifier, info: ComInfoVO, areSelected: () -> Boolean,
    onDelete: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit, simple: Boolean = false
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {

        Text(
            modifier = Modifier
                .border(color = randomZhongGuoSe().color, width = 1.dp)
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .wrapContentWidth(),
            text = "[${
                when (info.status) {
                    is ComStatus.CONNECTED -> "已连接"
                    ComStatus.DETACH -> "未找到"
                    ComStatus.DISCONNECTED -> "未连接"
                }
            }]", style = MaterialTheme.typography.bodySmall,
            color = if (areSelected()) Color.Red else Color.Gray
        )

        Text(
            modifier = Modifier
                .border(color = randomZhongGuoSe().color, width = 1.dp)
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .weight(1f),
            text = info.path,
            style = MaterialTheme.typography.bodySmall,
            color = if (areSelected()) Color.Red else Color.Gray
        )
        Text(
            modifier = Modifier
                .border(color = randomZhongGuoSe().color, width = 1.dp)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            text = info.rate.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = ZhongGuoSe.金叶黄.color
        )
        if (!simple) {
            Spacer(Modifier.width(4.dp))
            Text(
                "删除",
                Modifier.clickable { onDelete() },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            )
            Spacer(Modifier.width(4.dp))

            when (info.status) {
                is ComStatus.CONNECTED -> {
                    Text(
                        "关闭串口",
                        Modifier.clickable { onDisconnect() },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }

                ComStatus.DETACH -> {}
                ComStatus.DISCONNECTED -> {
                    Text(
                        "打开串口",
                        Modifier.clickable { onConnect() },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
        }

    }
}


@Composable
internal fun UartInfoItemV2(
    modifier: Modifier, info: ComInfoVO,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onEditRate: (Int) -> Unit,
) {

    var editRate: Boolean by remember {
        mutableStateOf(false)
    }

    var currentRate: Int by remember {
        mutableStateOf(info.rate)
    }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {

        Text(
            modifier = Modifier
                .border(color = randomZhongGuoSe().color, width = 1.dp)
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .wrapContentWidth(),
            text = "[${
                when (info.status) {
                    is ComStatus.CONNECTED -> "已连接"
                    ComStatus.DETACH -> "未找到"
                    ComStatus.DISCONNECTED -> "未连接"
                }
            }]", style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Text(
            modifier = Modifier
                .border(color = randomZhongGuoSe().color, width = 1.dp)
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .weight(1f),
            text = info.path,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(Modifier.width(4.dp))
        if (editRate) {
            TextField(
                value = if (currentRate == 0) "" else currentRate.toString(),
                onValueChange = { v ->
                    try {
                        currentRate = v.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        currentRate = 0
                    }
                },
                modifier = Modifier.wrapContentSize(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
//            keyboardActions = KeyboardActions.Default.onSend,
                trailingIcon = {
                    Text("设置波特率", modifier = Modifier.clickable {
                        onEditRate(currentRate)
                        editRate = false
                    })
                }
            )
        } else {
            Text(
                modifier = Modifier
                    .border(color = randomZhongGuoSe().color, width = 1.dp)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .clickable(info.status == ComStatus.DISCONNECTED) { editRate = true },
                text = info.rate.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ),
                color = ZhongGuoSe.金叶黄.color
            )
        }


        when (info.status) {
            is ComStatus.CONNECTED -> {
                Text(
                    "关闭串口",
                    Modifier.clickable { onDisconnect() },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }

            ComStatus.DETACH -> {}
            ComStatus.DISCONNECTED -> {
                Text(
                    "打开串口",
                    Modifier.clickable { onConnect() },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}
