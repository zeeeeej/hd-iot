package com.yunext.iot.ui.compoent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Stable
sealed interface ToastData {
    data object Nan : ToastData
    data class Show(val msg: String) : ToastData
}

@Composable
internal fun Toast(
    modifier: Modifier = Modifier,
    msg: String,
    content: (@Composable (ToastData) -> Unit)? = null,
    black: Boolean = false,
    onDismiss: () -> Unit
) {
    var toast: ToastData by remember { mutableStateOf(ToastData.Nan) }
    LaunchedEffect(msg) {
//        Napier.d {
//            "Toast:$msg"
//        }
        if (msg.isNotEmpty()) {
            toast = ToastData.Show(msg)
            delay(2000)
            toast = ToastData.Nan
        } else {
            toast = ToastData.Nan
        }
        onDismiss()
    }
    AnimatedVisibility(
        toast is ToastData.Show, modifier = modifier
    ) {
        content?.invoke(toast) ?: ToastInternal(
            Modifier.wrapContentSize(), toast, black
        )
    }
}

@Composable
private fun ToastInternal(modifier: Modifier = Modifier, toastData: ToastData, black: Boolean) {
    if (toastData is ToastData.Show) {
        Box(
            modifier = modifier

                .clip(RoundedCornerShape(16.dp)).shadow(4.dp)
                .background(if (black) Color.Black else Color.White).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = toastData.msg,
                modifier = Modifier.wrapContentSize(),
                style = TextStyle.Default.copy(
                    color = Color.Red, fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

