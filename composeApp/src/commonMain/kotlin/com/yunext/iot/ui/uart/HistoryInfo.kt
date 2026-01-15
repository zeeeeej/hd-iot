package com.yunext.iot.ui.uart

import ZhongGuoSe
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import com.yunext.kotlin.kmp.common.util.datetimeFormat

import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import randomZhongGuoSe

private object HistoryDefaults {
    val Style_1 = TextStyle.Default.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
    val Style_2 = TextStyle.Default.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.Monospace
    )
    val Style_3 = TextStyle.Default.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Light,
        fontFamily = FontFamily.Monospace
    )
    val Style_TYPE = TextStyle.Default.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace
    )
}


data class LogcatHistory(
    val log: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val index: Long = INDEX++,
    val textColor: Color = Color.Black,
    val textStyle: TextStyle = TextStyle.Default
) {
    companion object Companion {
        private var INDEX = 0L
    }
}

private enum class ScrollStatus {
    Top, Bottom, Center, NaN;
}

val LogcatHistory.display: String
    get() = "${datetimeFormat { this@display.timestamp.toStr() }} ${this.log}"

@Composable
internal fun HistoriesInfo(
    modifier: Modifier = Modifier,
    list: List<LogcatHistory>,
    onClose: () -> Unit,
    onShare: (List<LogcatHistory>) -> Unit
) {
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var auto by remember { mutableStateOf(true) }
    val moreInfo by remember {
        derivedStateOf {
            state.firstVisibleItemIndex != 0
        }
    }


//    LaunchedEffect(state.firstVisibleItemScrollOffset,state.firstVisibleItemIndex){
//        Napier.d(tag = "HistoriesInfo") {
//            """
//             firstVisibleItemIndex           :   ${state.firstVisibleItemIndex}
//             firstVisibleItemScrollOffset    :   ${state.firstVisibleItemScrollOffset}
//             canScrollBackward               :   ${state.canScrollBackward}
//             canScrollForward                :   ${state.canScrollForward}
//            """.trimIndent()
//        }
//
//    }

//    val scrollStatus by remember(  state.canScrollBackward,state.canScrollForward ) {
//        derivedStateOf {
//            when {
//                state.canScrollBackward && !state.canScrollForward -> ScrollStatus.Bottom
//                !state.canScrollBackward && state.canScrollForward -> ScrollStatus.Top
//                !state.canScrollBackward && !state.canScrollForward -> ScrollStatus.NaN
//                else -> ScrollStatus.Center
//            }
//        }
//    }

//    val isBottom by remember {
//        derivedStateOf {
//            state.canScrollForward
//        }
//    }

    var suoxie by remember { mutableStateOf(true) }
    var showIn by remember { mutableStateOf(true) }
    var showOut by remember { mutableStateOf(true) }
    var showOpt by remember { mutableStateOf(true) }
    var filterList by remember(list) { mutableStateOf(list) }
    LaunchedEffect(filterList) {
        if (auto) {
            state.scrollToItem(0)
        }
    }
    Box(modifier = modifier) {
        Column {
//            Spacer(
//                Modifier
//                    .padding(10.dp)
//                    .width(100.dp).height(4.dp)
//                    .clip(RoundedCornerShape(2.dp))
//                    .background(Color.Black).align(Alignment.CenterHorizontally)
//
//            )

            key("top") {

                Row(
                    Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "日志：(${filterList.size}条${
                            if (moreInfo) {
                                "..."
                            } else ""
                        })", modifier = Modifier.weight(1f)
                    )

//                    Text(
//                        "In",
//                        style = HistoryDefaults.Style_TYPE.copy(
//                            color = if (showIn) ZhongGuoSe.余烬红.color else ZhongGuoSe.垩灰.color,
//                            textAlign = TextAlign.Center
//                        ),
//                        modifier = Modifier.clickable {
//                            showIn = !showIn
//                        }.widthIn(min = 24.dp)
//                    )
//                    Text(
//                        "Out",
//                        style = HistoryDefaults.Style_TYPE.copy(
//                            color = if (showOut) ZhongGuoSe.余烬红.color else ZhongGuoSe.垩灰.color,
//                            textAlign = TextAlign.Center
//                        ),
//                        modifier = Modifier.clickable {
//                            showOut = !showOut
//                        }.widthIn(min = 24.dp)
//                    )
//                    Text(
//                        "Opt",
//                        style = HistoryDefaults.Style_TYPE.copy(
//                            color = if (showOpt) ZhongGuoSe.余烬红.color else ZhongGuoSe.垩灰.color,
//                            textAlign = TextAlign.Center
//                        ),
//                        modifier = Modifier.clickable {
//                            showOpt = !showOpt
//                        }.widthIn(min = 24.dp)
//                    )
                }

            }
            Box(Modifier.weight(1f)) {

                LazyColumn(Modifier.fillMaxSize(), state) {

                    itemsIndexed(
                        filterList,
                        { _, it -> "${it.timestamp}${it.log}${it.index}" }) { index, it ->
                        HistoryItem(
                            modifier = Modifier.background(ZhongGuoSe.乳白.color.copy(if (index % 2 == 0) 1f else .5f)),
                            history = it, suoxie = { suoxie }, onSuoXie = {
                                suoxie = !suoxie
                            })
                    }
                }

                Text(
                    if (auto) "自动滚动中..." else "打开自动滚动",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(中国色.墨紫.color)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable {
                            auto = !auto
                        },
                    style = TextStyle.Default.copy(
                        color = if (auto) 中国色.余烬红.color else 中国色.月灰.color,
                        fontSize = 11.sp,
                        fontWeight = if (auto) FontWeight.Light else FontWeight.Bold
                    )
                )


            }

            key("bottom") {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(randomZhongGuoSe().color)
                )
            }

        }

//        FloatingActionButton(
//            onClick = {
//                onClose()
//            }, modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(16.dp)
//                .size(24.dp), contentColor = ZhongGuoSe.向日葵黄.color
//        ) {
//            Image(Icons.TwoTone.Close, null)
//        }


        Row(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            FloatingActionButton(
                onClick = {
                    onShare(filterList)
                }, modifier = Modifier
                    .size(24.dp), containerColor =
                    ZhongGuoSe.向日葵黄.color
            ) {
                Image(Icons.Sharp.Share, null)
            }
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        state.scrollToItem(filterList.size)
                    }
                }, modifier = Modifier.size(24.dp),
                containerColor =
                    ZhongGuoSe.向日葵黄.color
            ) {
                Image(Icons.TwoTone.KeyboardArrowDown, null, modifier = Modifier)
            }

            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        state.scrollToItem(0)
                    }
                },
                modifier = Modifier
                    .size(24.dp),
                containerColor =
                    ZhongGuoSe.向日葵黄.color

            ) {
                Image(Icons.TwoTone.KeyboardArrowUp, null, modifier = Modifier)
            }
        }

//        Text(
//            "scrollStatus=$scrollStatus", style = HistoryDefaults.Style_TYPE
//        )


    }

}

@Composable
private fun HistoryItem(
    modifier: Modifier = Modifier,
    history: LogcatHistory,
    suoxie: () -> Boolean,
    onSuoXie: () -> Unit
) {
//    var suoxie by remember { mutableStateOf(true) }

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            datetimeFormat {
                if (suoxie()) {
                    history.timestamp.toStr("HH:mm:ss[ SSS]")
                } else
                    history.timestamp.toStr()
            },
            style = HistoryDefaults.Style_1.copy(
                color = history.textColor,
                fontWeight = history.textStyle.fontWeight
            ),
            modifier = Modifier.clickable {
//            suoxie = !suoxie
                onSuoXie()
            })
        Text(
            "${history.log}", style = HistoryDefaults.Style_3.copy(
                color = history.textColor, fontWeight = history.textStyle.fontWeight
            )
        )
        //Text(history.type, style = HistoryDefaults.Style_TYPE.copy(color = ZhongGuoSe.大红.color))
//        Text(
//            "${history.tag ?: ""}${history.message}", style = HistoryDefaults.Style_3.copy(
//                color = when (history) {
//                    is IBleIn -> ZhongGuoSe.古铜绿.color
//                    is IBleOpt -> ZhongGuoSe.天蓝.color
//                    is IBleOut -> ZhongGuoSe.月季红.color
//                }
//            )
//        )

    }
}