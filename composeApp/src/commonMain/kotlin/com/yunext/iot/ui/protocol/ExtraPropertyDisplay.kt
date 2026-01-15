package com.yunext.iot.ui.protocol

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.iot.domain.protocol.PicInfo
import com.yunext.iot.domain.protocol.TriggerType

sealed interface ExtraPropertyDisplayAction {
    data object Get : ExtraPropertyDisplayAction
    data object Set : ExtraPropertyDisplayAction
}

@Composable
fun ExtraPropertyDisplay(
    modifier: Modifier,
    list: List<ExtraPropertyVo>,
    sel: ExtraPropertyVo? = null,
    onGet: (ExtraPropertyVo) -> Unit = {}
) {
    Column(modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
        Text("额外属性列表（${list.size}）")
        LazyColumn {
            item { PropertyTitle(modifier = Modifier.fillMaxWidth()) }
            items(list, { it }) {
                PropertyItem(
                    modifier = Modifier.fillMaxWidth().border(
                        if (sel?.id == it.id) {
                            1.dp
                        } else 1.dp, color = if (sel?.id == it.id) {
                            Color.Red
                        } else Color.Transparent
                    ), it, onAction = { a->
                        onGet(it)
                    })
            }
        }
    }

}

private val WIDTH = 72.dp

@Composable
private fun PropertyTitle(
    modifier: Modifier,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(WIDTH / 2)) {
            Text(
                "ID",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH)) {
            Text(
                "长度",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH)) {
            Text(
                "数据类型",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH)) {
            Text(
                "读写",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }

        Box(Modifier.width(WIDTH)) {
            Text(
                "名称",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }

        Box(Modifier) {
            Text(
                "值说明",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }

    }
}

@Composable
private fun PropertyItem(
    modifier: Modifier,
    item: ExtraPropertyVo,
    onAction: (ExtraPropertyDisplayAction) -> Unit
) {
    Row(
        modifier.clickable { onAction(ExtraPropertyDisplayAction.Get) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(WIDTH / 2)) {
                Text(
                    "0x${item.id.key.toHexString()}",
                    style = TextStyle.Default.copy(fontSize = 11.sp),
                    maxLines = 1
                )
            }
            Box(Modifier.width(WIDTH)) {
                Text(
                    "${item.id.len}",
                    style = TextStyle.Default.copy(fontSize = 11.sp),
                    maxLines = 1
                )
            }
            Box(Modifier.width(WIDTH)) {
                Text(
                    item.id.type,
                    style = TextStyle.Default.copy(fontSize = 11.sp),
                    maxLines = 1
                )
            }
            Box(Modifier.width(WIDTH)) {
                Text(
                    item.id.rw,
                    style = TextStyle.Default.copy(fontSize = 11.sp),
                    maxLines = 1
                )
            }

            Box(Modifier.width(WIDTH)) {
                Text(
                    item.id.title,
                    style = TextStyle.Default.copy(fontSize = 11.sp),
                    maxLines = 1
                )
            }

            Box(Modifier) {
                Text(
                    item.id.desc,
                    style = TextStyle.Default.copy(fontSize = 11.sp),
                    maxLines = 1
                )
            }

        }
    }
}