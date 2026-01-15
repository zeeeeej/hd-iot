package com.yunext.iot.ui.workspace

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yunext.iot.ui.compoent.randomBG

/**
 * Menu-调试
 */
@Composable
fun DebugScreen(modifier: Modifier){
    Box(modifier = Modifier.randomBG()){
        Text("DebugScreen")
    }
}