package com.yunext.iot.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.iot.domain.Com
import com.yunext.iot.domain.ComException
import com.yunext.iot.domain.ComStatus
import com.yunext.iot.repository.UartRepository
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.compoent.effectCompleted
import com.yunext.iot.ui.compoent.effectIdle
import com.yunext.iot.ui.uart.ComInfoVO
import com.yunext.iot.ui.uart.ComVO
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeState(
    val comList: List<ComVO> = emptyList(),
    val comInfoList: List<ComInfoVO> = emptyList(),
    val receiveData: String = "",
    val sendUartDataEffect: Effect<Com, ByteArray> = effectIdle(),
    val globalToastEffect: Effect<String, Unit> = effectIdle(),
) {
    companion object {
        val EMPTY = HomeState()
    }
}

class HomeVM(private val uartRepo: UartRepository) : ViewModel() {

    private val comListFlow: MutableStateFlow<List<ComVO>> = MutableStateFlow(emptyList())
    private val comInfoListFlow: MutableStateFlow<List<ComInfoVO>> = MutableStateFlow(emptyList())
    private val receiverDataFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val globalToastEffect: MutableStateFlow<Effect<String, Unit>> =
        MutableStateFlow(effectIdle())
    private val sendUartDataEffectFlow: MutableStateFlow<Effect<Com, ByteArray>> =
        MutableStateFlow(effectIdle())

    private companion object {
        private const val TAG = "HomeVM"
    }

    val state: Flow<HomeState> = combine(
        comListFlow,
        comInfoListFlow,
        receiverDataFlow,
        globalToastEffect,
        sendUartDataEffectFlow,
    ) { comList, comInfoList, receiveData, globalToast, sendUartDataEffect ->
        HomeState(
            comList,
            comInfoList = comInfoList,
            receiveData,
            globalToastEffect = globalToast,
            sendUartDataEffect = sendUartDataEffect
        )
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

    fun send(comInfo: ComInfoVO, data: String) {
        sendInternal(comInfo) {

            when (comInfo.status) {
                is ComStatus.CONNECTED -> {
                    sendUartDataEffectFlow.value =
                        Effect.Progress(comInfo.path, "write ...")
                    val result =
                        withContext(Dispatchers.IO) {
                            uartRepo.write(comInfo.path, data = data.hexToByteArray())
                        }

                    if (result > 0) {
                        sendUartDataEffectFlow.value =
                            Effect.Progress(comInfo.path, "read ...")
                        val bytes = withContext(Dispatchers.IO) {
                            uartRepo.read(comInfo.path)
                        }
                        receiverDataFlow.value = bytes.toHexString()
                        sendUartDataEffectFlow.value =
                            Effect.Success(comInfo.path, bytes)
                    } else {
                        toast("发送失败")
                        sendUartDataEffectFlow.value =
                            Effect.Fail(comInfo.path, ComException("发送失败"))
                    }
                }

                ComStatus.DETACH -> {
                    toast("请检查串口")
                    sendUartDataEffectFlow.value =
                        Effect.Fail(comInfo.path, ComException("ComStatus.DETACH请检查串口"))
                }

                ComStatus.DISCONNECTED -> {
                    toast("请打开串口")
                }
            }


        }
    }

    private fun sendInternal(comInfo: ComInfoVO, block: suspend () -> Unit) {
        println("$TAG ::send")
        viewModelScope.launch {
            try {
                sendUartDataEffectFlow.value = effectIdle()
                block()

            } catch (e: Throwable) {
                if (e !is CancellationException) {
                    toast("发送失败$e")
                    sendUartDataEffectFlow.value =
                        Effect.Fail(comInfo.path, e)
                }
            } finally {
                sendUartDataEffectFlow.value = effectCompleted()
            }
        }
    }

    fun listComInfo() {
        println("$TAG ::listComInfo")
        viewModelScope.launch {
            try {
                comInfoListFlow.value = uartRepo.listComInfo().map {
                    ComInfoVO(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addComInfo(com: String) {
        println("$TAG ::addComInfo")
        viewModelScope.launch {
            try {
                val list = uartRepo.addComInfo(com)
                comInfoListFlow.value = list.map {
                    ComInfoVO(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disconnectComInfo(com: String) {
        viewModelScope.launch {
            try {
                uartRepo.close(com)
            } catch (e: Throwable) {
                toast("关闭[${com}]失败 ${e.message}")
            } finally {
                listComInfo()
            }
        }
    }

    fun connectComInfo(com: String) {
        viewModelScope.launch {
            try {
                val opened = uartRepo.open(com, 460800)
                println("open ${com} result:$opened")
                if (opened) {
                    // debugWrite(comVO.com)
                    toast("打开[${com}]成功")
                } else {
                    toast("打开[${com}]失败")
                }

            } catch (e: Throwable) {
                toast("打开[${com}]失败 ${e.message}")
            } finally {
                listComInfo()
            }
        }
    }

    fun deleteComInfo(info: ComInfoVO) {
        println("$TAG ::deleteComInfo")
        viewModelScope.launch {
            try {
                val list = uartRepo.deleteComInfo(info.path)
                comInfoListFlow.value = list.map {
                    ComInfoVO(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}