package com.yunext.iot

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.yunext.iot.di.KoinInit

import com.yunext.iot.jni.NativeLibrary
import kotlinx.coroutines.delay
import kotlin.random.Random

fun main() = application {

    KoinInit.init { }

    val rememberWindowState = rememberWindowState(size = DpSize(1280.dp,720.dp))
    Window(
        onCloseRequest = ::exitApplication,
        title = "海大摄像头v1.0.0-alpha001",
        resizable = false,
        state =  rememberWindowState

    ) {
       LaunchedEffect(Unit){
           delay(100)
           NativeLibrary()
       }
        App()
        var txt by remember {
            mutableStateOf("")
        }
        Button (onClick = {

//            NativeFunctions.init()

            // 自动加载 JNI 库
//            val result = lib.addNumbersJVM(5, 3)
//            println("Native addition result: $result")
//
//            val message = lib.getMessageJVM()
//            println("Native message: $message")
            try {
                // 加载 JNI 库
                //DebugJniLoader.ensureLoaded()

                // 调用 JNI 方法
                val result = NativeFunctions.addNumbers(10, Random.nextInt(100))
                println("JNI result: $result")
                txt = "$result"
                val message = NativeFunctions.getMessage()
                println("JNI message: $message")
                val version = NativeFunctions.uartVersion(1)
                txt = "$result + $message v:$version"
            } catch (e: Exception) {
                txt =  e.message?:"error"
                println("Error: ${e.message}")
                e.printStackTrace()
            }
        }){
            Text("test jni $txt")
        }
    }
}