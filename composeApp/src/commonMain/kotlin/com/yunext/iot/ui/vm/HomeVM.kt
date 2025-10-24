package com.yunext.iot.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.iot.repository.UartRepository
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.compoent.effectCompleted
import com.yunext.iot.ui.compoent.effectIdle
import com.yunext.iot.ui.compoent.effectProgress
import com.yunext.iot.ui.uart.ComVO
import com.yunext.kotlin.kmp.common.util.currentTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface ComInfoVo {
    data class Selected(val com: ComVO, val state: Boolean = false) : ComInfoVo
    data object UnSelected : ComInfoVo
}


data class HomeState(
    val comList: List<ComVO> = emptyList(),
    val com: ComInfoVo = ComInfoVo.UnSelected,
    val receiveData: String = "",
    val globalToastEffect: Effect<String, Unit> = effectIdle(),
) {
    companion object {
        val EMPTY = HomeState()
    }
}

class HomeVM(private val uartRepo: UartRepository) : ViewModel() {

    private val comListFlow: MutableStateFlow<List<ComVO>> = MutableStateFlow(emptyList())
    private val comFlow: MutableStateFlow<ComInfoVo> = MutableStateFlow(ComInfoVo.UnSelected)
    private val receiverDataFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val globalToastEffect: MutableStateFlow<Effect<String, Unit>> = MutableStateFlow(effectIdle())

    private companion object{
        private const val TAG = "HomeVM"
    }

    val state: Flow<HomeState> = combine(
        comListFlow,
        comFlow,
        receiverDataFlow,
        globalToastEffect,
    ) { comList, com, receiveData, globalToast ->
        HomeState(comList, com, receiveData, globalToast)
    }

    private var clearToastJob: Job? = null

    fun clearToast() {
        clearToastJob?.cancel()
        globalToastEffect.value = effectCompleted()
    }

    private fun toast(msg: String) {
        if (msg.isBlank()) return
        clearToastJob?.cancel()
        clearToastJob = viewModelScope.launch {
            globalToastEffect.value = effectIdle()
            globalToastEffect.value = Effect.Progress(msg, 0)
            delay(2000)
            globalToastEffect.value = effectCompleted()
        }
    }

    fun refreshCom() {
        println("$TAG ::refreshCom")
        viewModelScope.launch {
            try {
                comListFlow.value = uartRepo.list().map {
                    ComVO(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectCom(com: ComVO) {
        println("$TAG ::selectCom")
        this.comFlow.value = ComInfoVo.Selected(com)
    }

//    private fun debugWrite(com: ComVO) {
//        viewModelScope.launch {
//            // 写
//            launch {
//                while (isActive) {
//                    delay(3000)
//                    println("=====================================")
//                    val data = "AA5aeec201000000a06ead".hexToByteArray()
//                    println("[发送]${data.toHexString()}")
////                val result = uartRepo.write(com.path, Random.Default.nextBytes(10))
//                    val result = uartRepo.write(com.path, data)
//                    println("write result : $result")
//
//                    if (result > 0) {
//                        // 读
//                        val bytes = uartRepo.read(com.path)
//                        println("[接受]${bytes.toHexString()}")
//                        // aa 5a ee c2 04 00 00 00 a0 00 00 1e a9 6c
//                        receiverDataFlow.value = bytes.toHexString()
//                    }
//                }
//            }
//
//        }
//    }

    fun switchCom() {
        println("$TAG ::switchCom")
        val comVO = comFlow.value
        viewModelScope.launch {
            when (comVO) {
                is ComInfoVo.Selected -> {
                    if (!comVO.state) {
                        println("open ${comVO.com.path}")
                        val opened = uartRepo.open(comVO.com.path, 460800)
                        println("open ${comVO.com.path} result:$opened")
                        if (opened) {
                            // debugWrite(comVO.com)
                            toast("打开[${comVO.com.path}]成功")
                        } else {
                            toast("打开[${comVO.com.path}]失败")
                        }
                    } else {
                        uartRepo.close(comVO.com.path)
                    }
                }

                ComInfoVo.UnSelected -> {
                    toast("请选择串口")
                }
            }
        }
    }

    fun send(data: String) {
        println("$TAG ::send")
        viewModelScope.launch {
            when (val comInfo = comFlow.value) {
                is ComInfoVo.Selected -> {

                    val result = uartRepo.write(comInfo.com.path, data = data.hexToByteArray())
                    if (result > 0) {
                        // 读
                        val bytes = uartRepo.read(comInfo.com.path)
                        println("[接受]${bytes.toHexString()}")
                        // aa 5a ee c2 04 00 00 00 a0 00 00 1e a9 6c
                        receiverDataFlow.value = bytes.toHexString()
                    } else {
                        toast("发送失败")
                    }
                }

                ComInfoVo.UnSelected -> {
                    toast("请选择串口")
                }
            }
        }
    }
}