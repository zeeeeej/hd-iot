package com.yunext.iot.ui.workspace

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yunext.iot.ui.compoent.randomBG

/**
 * Menu-温控器模拟器
 */
@Composable
fun MasterScreen(modifier: Modifier){
    Box(modifier = Modifier.randomBG()) {
        Text("MasterScreen")
    }
}