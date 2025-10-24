package com.yunext.iot.ui.uart

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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
import randomZhongGuoSe

data class ComVO(val path: String)

@Composable
fun UartComSpit(modifier: Modifier = Modifier, list: List<ComVO>, onSelect: (ComVO) -> Unit) {
    UartList(
        modifier = modifier, list = list, null, onSelected = onSelect
    )
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
            .border(color = randomZhongGuoSe().color, width = 1.dp)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .fillMaxWidth(),
        text = com.path,
        color = if (areSelected()) Color.Red else Color.Gray
    )
}