package com.yunext.iot.ui.compoent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import color
import randomZhongGuoSe

internal val DEFAULT_DP = 12.dp

fun Modifier.randomBG(padding: Dp = DEFAULT_DP): Modifier {
    return this.composed {

        this
            .padding(padding)
            .clip(RoundedCornerShape(DEFAULT_DP))
//            .drawBehind {
//                drawRoundRect(color = randomZhongGuoSe().color.copy(alpha = .5f))
//            }
            .background(randomZhongGuoSe().color.copy(alpha = .5f))
            .padding(DEFAULT_DP)
    }
}