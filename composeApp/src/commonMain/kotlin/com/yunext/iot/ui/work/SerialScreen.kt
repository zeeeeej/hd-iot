package com.yunext.iot.ui.work

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yunext.iot.ui.compoent.randomBG

@Composable
fun SerialScreen(modifier: Modifier){
    Box(modifier = androidx.compose.ui.Modifier.randomBG()) {
        Text("SerialScreen")
    }
}