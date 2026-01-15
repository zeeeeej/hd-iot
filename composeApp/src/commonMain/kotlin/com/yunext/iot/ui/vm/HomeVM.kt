package com.yunext.iot.ui.vm

import ZhongGuoSe
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import color
import com.yunext.iot.domain.uart.Uart
import com.yunext.iot.domain.uart.UartException
import com.yunext.iot.domain.uart.UartInfo
import com.yunext.iot.domain.uart.UartStatus
import com.yunext.iot.domain.uart.InputLogcat
import com.yunext.iot.domain.uart.NormalLogcat
import com.yunext.iot.domain.uart.OutputLogcat
import com.yunext.iot.domain.generateHDClipboard
import com.yunext.iot.domain.project.HDProject
import com.yunext.iot.repository.UartRepository
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.compoent.effectCompleted
import com.yunext.iot.ui.compoent.effectIdle
import com.yunext.iot.ui.protocol.ProtocolVO
import com.yunext.iot.ui.uart.ComInfoVO
import com.yunext.iot.ui.uart.ComVO
import com.yunext.iot.ui.uart.LogcatHistory
import com.yunext.iot.ui.uart.display
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeState(
    val comList: List<ComVO> = emptyList(),
    val comInfoList: List<ComInfoVO> = emptyList(),
    val receiveData: String = "",
    val sendUartDataEffect: Effect<Uart, ByteArray> = effectIdle(),
    val globalToastEffect: Effect<String, Unit> = effectIdle(),
    val logcatHistory: List<LogcatHistory> = emptyList()
) {
    companion object {
        val EMPTY = HomeState()
    }
}

class HomeVM(private val snap:Snapshot) : ViewModel() {

    private val comListFlow: StateFlow<List<ComVO>> = snap.comListFlow
    private val comInfoListFlow: StateFlow<List<ComInfoVO>> = snap.comInfoListFlow
    private val logcatListFlow: StateFlow<List<LogcatHistory>> =
        snap.logcatListFlow
    private val receiverDataFlow: StateFlow<String> = snap.receiverDataFlow
    private val globalToastEffect: StateFlow<Effect<String, Unit>> =
        snap.globalToastEffect
    private val sendUartDataEffectFlow:StateFlow<Effect<Uart, ByteArray>> =
       snap.sendUartDataEffectFlow

    private companion object {
        private const val TAG = "HomeVM"
    }

    @Suppress("UNCHECKED_CAST")
    val state: Flow<HomeState> = combine(
        comListFlow,
        comInfoListFlow,
        receiverDataFlow,
        globalToastEffect,
        sendUartDataEffectFlow,
        logcatListFlow
    ) { data ->
        val comList = data[0] as List<ComVO>
        val comInfoList = data[1] as List<ComInfoVO>
        val receiveData = data[2] as String
        val globalToast = data[3] as Effect<String, Unit>
        val sendUartDataEffect = data[4] as Effect<Uart, ByteArray>
        val logcat = data[5] as List<LogcatHistory>
        HomeState(
            comList,
            comInfoList = comInfoList,
            receiveData,
            globalToastEffect = globalToast,
            sendUartDataEffect = sendUartDataEffect,
            logcatHistory = logcat
        )
    }

    init {
       snap.init(viewModelScope)
    }





    fun refreshCom() {
        println("$TAG ::refreshCom")
        snap.run {
            viewModelScope.refreshCom()
        }

    }

    fun send(     actionId: String,comInfo: ComInfoVO?,protocol: ProtocolVO, data: ByteArray) {
        snap.run {
            viewModelScope.send(actionId,comInfo,protocol,data)
        }
    }

    fun sendAppOTA(     actionId: String,comInfo: ComInfoVO?,protocol: ProtocolVO, data: ByteArray) {
        snap.run {
            viewModelScope.appOTA(actionId,comInfo,protocol,data)
        }
    }

    fun send(comInfo: ComInfoVO, data: String) {
        snap.run {
            viewModelScope.   send(comInfo,data)
        }

    }



    fun listComInfo() {
        snap.run {
            viewModelScope.listComInfo()
        }
    }

    fun addComInfo(com: String) {
        snap.run {
            viewModelScope.addComInfo(com)
        }
    }

    fun disconnectComInfo(com: String) {
        snap.run {
            viewModelScope.disconnectComInfo(com)
        }
    }

    fun connectComInfo(com: String) {
        snap.run {
            viewModelScope.connectComInfo(com)
        }
    }

    fun deleteComInfo(info: ComInfoVO) {
        snap.run {
            viewModelScope.deleteComInfo(info)
        }
    }

    fun editComRate(info: ComInfoVO) {
        snap.run {
            viewModelScope.editComRate(info)
        }

    }

    fun clearLogcat() {
        snap.run {
            viewModelScope.clearLogcat()
        }

    }

    fun clearToast() {
        snap.run { clearToast()
        }

    }

    fun shareLogcat(it: List<LogcatHistory>) {
        snap.run {
            viewModelScope.shareLogcat(it)
        }
    }

    fun clearAllLogcat() {
        snap.run {
            viewModelScope.clearLogcat()
        }
    }
}