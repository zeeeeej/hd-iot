package com.yunext.iot.ui.uart

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import color
import com.yunext.iot.ui.vm.ComInfoVo
import randomZhongGuoSe

data class ComVO(val path: String)

@Composable
fun UartCom(
    modifier: Modifier,
    list: List<ComVO>,
    com: ComInfoVo,
    onSelected: (ComVO) -> Unit,
    onRefresh: () -> Unit,
    onSwitch: () -> Unit,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("端口列表：", modifier = Modifier)
            Text(
                text = when (com) {
                    is ComInfoVo.Selected -> com.com.path
                    ComInfoVo.UnSelected -> "请选择"
                },
                modifier = Modifier.weight(1f),
                style = LocalTextStyle.current.copy(
                    color = when (com) {
                        is ComInfoVo.Selected -> if (com.state) Color.Green else Color.Black
                        ComInfoVo.UnSelected -> Color.Gray
                    },
                    fontWeight = FontWeight.Bold
                )
            )

            Button(onClick = {
                onSwitch()
            }) {
                Text("打开", modifier = Modifier)
            }

            Button(onClick = {
                onRefresh()
            }) {
                Text("刷新", modifier = Modifier)
            }
        }

        UartList(
            modifier = Modifier, list = list, com = when (com) {
                is ComInfoVo.Selected -> com.com
                ComInfoVo.UnSelected -> null
            }, onSelected = onSelected
        )
    }

}

@Composable
private fun UartList(
    modifier: Modifier,
    list: List<ComVO>,
    com: ComVO?,
    onSelected: (ComVO) -> Unit,
) {
    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items(items = list, { it }) { item ->
            UartItem(modifier = Modifier.clickable {
                onSelected(item)
            }, item, { item == com })
        }
    }
}

@Composable
private fun UartItem(modifier: Modifier, com: ComVO, areSelected: () -> Boolean) {
    Text(
        modifier = modifier
            .border(color =randomZhongGuoSe().color, width = 1.dp)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .fillMaxWidth(),
        text = com.path,
        color = if (areSelected()) Color.Red else Color.Gray
    )
}