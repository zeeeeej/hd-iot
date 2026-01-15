package com.yunext.iot.ui.uart

import ZhongGuoSe
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import color
import com.yunext.iot.ui.compoent.randomBG
import randomZhongGuoSe

sealed interface ByteDisplayMode {
    data object Hex : ByteDisplayMode
    data object Oct : ByteDisplayMode
    data object Asic : ByteDisplayMode
}

data class ByteData(val index: Int, val byte: Byte, val mode: ByteDisplayMode = ByteDisplayMode.Hex)
fun ByteArray.toByteData() = this.mapIndexed { index, byte ->
    ByteData(index,byte)
}

@Composable
fun ByteBlock(modifier: Modifier, list: List<ByteData>) {
    Column(modifier = modifier.wrapContentSize()) {
        if (list.isEmpty()) {

        } else {
//            ByteList(modifier = Modifier, list = list)
            ByteGrid(modifier = Modifier, list = list)

        }
    }
}

@Composable
private fun ByteList(modifier: Modifier, list: List<ByteData>) {
    LazyRow(
        modifier = modifier.background(ZhongGuoSe.月白.color),
//        horizontalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.Start,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(list, { it.toString() }) { byte ->
            ByteItem(modifier = Modifier, byte)
        }
    }
}

@Composable
private fun ByteGrid(modifier: Modifier, list: List<ByteData>) {
    LazyVerticalGrid(
        modifier = modifier.background(ZhongGuoSe.月白.color),
        columns = GridCells.FixedSize(48.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(list, { it.toString() }) { byte ->
            ByteItem(modifier = Modifier, byte)
        }
    }
}


@Composable
private fun ByteItem(modifier: Modifier, byte: ByteData) {
    var mode: ByteDisplayMode by remember(byte.mode) { mutableStateOf(byte.mode) }
    Box(
        modifier = modifier
            .wrapContentSize()
            .height(48.dp)
            .padding(4.dp)
            .aspectRatio(1f).border(
                width = 1.dp,
                color = randomZhongGuoSe().color,//Color.Black,
                shape = RoundedCornerShape(2.dp),
            ).clickable {
                mode = when (mode) {
                    ByteDisplayMode.Asic -> ByteDisplayMode.Hex
                    ByteDisplayMode.Hex -> ByteDisplayMode.Oct
                    ByteDisplayMode.Oct -> ByteDisplayMode.Asic
                }
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            when (mode) {
                ByteDisplayMode.Asic -> "${byte.byte.toInt().toChar()}"
                ByteDisplayMode.Hex -> byte.byte.toHexString()
                ByteDisplayMode.Oct -> "${byte.byte.toInt()}"
            },
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center,
                color = Color.Red
            ),
            modifier = Modifier.wrapContentSize()
        )
    }
}
