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

sealed interface PicInfosDisplayAction {
    data object Look : PicInfosDisplayAction
    data object Delete : PicInfosDisplayAction
}

@Composable
fun PicInfosDisplay(
    modifier: Modifier,
    list: List<PicInfo>,
    sel: PicInfo? = null,
    onSel:(PicInfo)->Unit = {}
) {
    Column(modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
        Text("图片信息（${list.size}）")
        LazyColumn {
            item { PicInfoTitle(modifier = Modifier.fillMaxWidth()) }
            items(list, { it }) {
                PicInfoItem(
                    modifier = Modifier.fillMaxWidth().border(
                        if (sel?.id == it.id) {
                            1.dp
                        } else 1.dp, color = if (sel?.id == it.id) {
                            Color.Red
                        } else Color.Transparent
                    ), it, onAction = { action ->
                        when (action) {
                            PicInfosDisplayAction.Delete -> {

                            }

                            PicInfosDisplayAction.Look -> {
                                onSel(it)
                            }
                        }
                    })
            }
        }
    }

}

private val WIDTH = 72.dp

@Composable
private fun PicInfoTitle(
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
                "触发方式",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH)) {
            Text(
                "拍照角度",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH)) {
            Text(
                "拍照时间",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH + 20.dp)) {
            Text(
                "图片大小",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier) {
            Text(
                "图片MD5",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PicInfoItem(
    modifier: Modifier,
    picInfo: PicInfo,
    onAction: (PicInfosDisplayAction) -> Unit
) {
    Row(modifier.clickable { onAction(PicInfosDisplayAction.Look) }, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(WIDTH / 2)) {
            Text(
                "0x${picInfo.id.toHexString()}",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH)) {
            Text(
                when (picInfo.triggerType) {
                    TriggerType.Gyroscope -> "陀螺仪触发"
                    TriggerType.Initiative -> "主动触发"
                }, style = TextStyle.Default.copy(fontSize = 11.sp), maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH)) {
            Text(
                "${picInfo.angel}",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH)) {
            Text(
                "${picInfo.timestamp}(秒)",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier.width(WIDTH + 20.dp)) {
            Text(
                "${picInfo.length}(bytes)",
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
        Box(Modifier) {
            Text(
                picInfo.md5,
                style = TextStyle.Default.copy(fontSize = 11.sp),
                maxLines = 1
            )
        }
    }
}