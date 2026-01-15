package com.yunext.iot.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yunext.iot.ui.uart.HistoriesInfo
import com.yunext.iot.ui.uart.LogcatHistory

/**
 * 日志部分
 */
@Composable
fun LogcatSpace(
    modifier: Modifier = Modifier, list: List<LogcatHistory>,
    onClear: () -> Unit,
    onShare: (List<LogcatHistory>) -> Unit,

) {
    HistoriesInfo(modifier = modifier, list = list, onClose = {}, onShare = onShare)
}