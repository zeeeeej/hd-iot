package com.yunext.iot.ui.work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.yunext.iot.ui.compoent.randomBG

@Composable
fun MainScreen(modifier: Modifier,receiveData:String ,onSend: (String) -> Unit) {
    Box(modifier = Modifier.randomBG()) {
        Column() {

            Box(modifier.weight(1f).randomBG().padding(12.dp).fillMaxWidth()) {
                Sender(modifier = Modifier.fillMaxSize(), onSend = {
                    onSend(it)
                })
            }

            Box(modifier.weight(1f).randomBG().padding(12.dp).fillMaxWidth()) {
                Receiver(modifier = Modifier.fillMaxSize(),data = receiveData )
            }

        }
    }
}

@Composable
private fun Sender(modifier: Modifier = Modifier, onSend: (String) -> Unit) {
    var data by remember { mutableStateOf("AA5aeec201000000a06ead") }
    Column(modifier = modifier) {
        Text("[发送]")
        TextField(
            value = data,
            onValueChange = { v ->
                try {
                    data = v
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
//            keyboardActions = KeyboardActions.Default.onSend,
            trailingIcon = {
                Text("发送",modifier = Modifier.clickable {
                    onSend(data)
                })
            }
        )
    }
}

@Composable
private fun Receiver(modifier: Modifier = Modifier, data: String) {
    Column(modifier = Modifier) {
        Text("[接受]")
        Text(data)
    }
}