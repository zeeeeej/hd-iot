package com.yunext.iot.ui.vm

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import color
import com.yunext.iot.domain.HDResult
import com.yunext.iot.domain.generateHDClipboard
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.HDRequest
import com.yunext.iot.domain.protocol.HDResponse
import com.yunext.iot.domain.protocol.arePicPull
import com.yunext.iot.domain.protocol.display
import com.yunext.iot.domain.protocol.toProtocolFrame
import com.yunext.iot.domain.uart.InputLogcat
import com.yunext.iot.domain.uart.NormalLogcat
import com.yunext.iot.domain.uart.OutputLogcat
import com.yunext.iot.domain.uart.Uart
import com.yunext.iot.domain.uart.UartException
import com.yunext.iot.domain.uart.UartInfo
import com.yunext.iot.domain.uart.UartStatus
import com.yunext.iot.repository.UartRepository
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.compoent.effectCompleted
import com.yunext.iot.ui.compoent.effectIdle
import com.yunext.iot.ui.compoent.effectProgress
import com.yunext.iot.ui.protocol.ActionInputData
import com.yunext.iot.ui.protocol.ActionOutputData
import com.yunext.iot.ui.protocol.ProtocolVO
import com.yunext.iot.ui.uart.ComInfoVO
import com.yunext.iot.ui.uart.ComVO
import com.yunext.iot.ui.uart.LogcatHistory
import com.yunext.iot.ui.uart.display
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class Snapshot(
    private val uartRepo: UartRepository
) {
    private val _comListFlow: MutableStateFlow<List<ComVO>> = MutableStateFlow(emptyList())
    private val _comInfoListFlow: MutableStateFlow<List<ComInfoVO>> = MutableStateFlow(emptyList())
    private val _logcatListFlow: MutableStateFlow<List<LogcatHistory>> =
        MutableStateFlow(emptyList())
    private val _receiverDataFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val _globalToastEffect: MutableStateFlow<Effect<String, Unit>> =
        MutableStateFlow(effectIdle())
    private val _sendUartDataEffectFlow: MutableStateFlow<Effect<Uart, ByteArray>> =
        MutableStateFlow(effectIdle())


    private val _sendCmdEffectFlow: MutableStateFlow<Effect<ActionInputData, ActionOutputData>> =
        MutableStateFlow(effectIdle())

    val comListFlow = _comListFlow.asStateFlow()
    val comInfoListFlow = _comInfoListFlow.asStateFlow()
    val logcatListFlow = _logcatListFlow.asStateFlow()
    val receiverDataFlow = _receiverDataFlow.asStateFlow()
    val globalToastEffect = _globalToastEffect.asStateFlow()
    val sendUartDataEffectFlow = _sendUartDataEffectFlow.asStateFlow()
    val sendCmdEffectFlow = _sendCmdEffectFlow.asStateFlow()

    private var clearToastJob: Job? = null

    fun clearToast() {
        clearToastJob?.cancel()
        _globalToastEffect.value = effectCompleted()
    }


    fun init(viewModelScope: CoroutineScope) {
        uartRepo.logcat.onEach { logcat ->
            _logcatListFlow.value = logcat.map {
                val log = when (it) {
                    is InputLogcat -> "<${it.com}>[收到]${it.data.display()}（${it.data.size}）"
                    is NormalLogcat -> "<${it.com}>${it.msg}"
                    is OutputLogcat -> {
                        "<${it.com}>[发送]${it.data.display()}（${it.data.size}）"
                    }
                }
                LogcatHistory(
                    log, it.timestamps,
                    textColor = when (it) {
                        is InputLogcat -> ZhongGuoSe.橄榄绿.color
                        is NormalLogcat -> ZhongGuoSe.牵牛花蓝.color
                        is OutputLogcat -> ZhongGuoSe.银灰.color
                    },
                    textStyle = when (it) {
                        is InputLogcat -> TextStyle.Default.copy(fontWeight = FontWeight.Bold)
                        is NormalLogcat -> TextStyle.Default.copy(fontWeight = FontWeight.Light)
                        is OutputLogcat -> TextStyle.Default.copy(fontWeight = FontWeight.Bold)
                    },
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun CoroutineScope.toast(msg: String) {
        if (msg.isBlank()) return
        clearToastJob?.cancel()
        clearToastJob = this.launch {
            _globalToastEffect.value = effectIdle()
            _globalToastEffect.value = Effect.Progress(msg, 0)
            delay(2000)
            _globalToastEffect.value = effectCompleted()
        }
    }

    fun CoroutineScope.refreshCom() {
        println("$TAG ::refreshCom")
        this.launch {
            try {
                _comListFlow.value = uartRepo.list().map {
                    ComVO(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun CoroutineScope.appOTA(
        actionId: String,
        comInfo: ComInfoVO?,
        protocol: ProtocolVO,
        data: ByteArray
    ) {
        println("$TAG ::appOTA ${data.size}")
        if (comInfo == null) {
            toast("请打开串口")
            return
        }

        sendInternal(comInfo) {
            val inputFrame = HDProtocolFrame.EMPTY
            val req =  HDRequest.Empty
            val inputEffect = ActionInputData(
                comInfo.path,
                protocol,
                data,
                actionId = actionId,
                frame = inputFrame ?: HDProtocolFrame.EMPTY,
                req = req,

                )
            when (comInfo.status) {
                is UartStatus.CONNECTED -> {
                    println("write 开始...")
                    _sendCmdEffectFlow.value = effectProgress(inputEffect, "write...")
                    val result =
                        withContext(Dispatchers.IO) {
                            uartRepo.writeDelay(comInfo.path, data = data)
                        }

                    println("write 结束！$result")
                    delay(10000)

                    if (result > 0) {
                        println("读结果...")
                        _sendCmdEffectFlow.value =
                            Effect.Progress(inputEffect, "read ...")
                        println("----------->")
                        val bytes = withContext(Dispatchers.IO) {

                                uartRepo.read(
                                    comInfo.path,
                                    3 * 1000
                                )
                        }
                        println("<-----------")
                        _receiverDataFlow.value = bytes.toHexString()
//                        delay(1000)
                        _sendCmdEffectFlow.value =
                            Effect.Success(
                                input = inputEffect,
                                output = ActionOutputData(
                                    comInfo.path,
                                    protocol,
                                    bytes,
                                    actionId = actionId,
                                    frame = bytes.toProtocolFrame() ?: HDProtocolFrame.EMPTY
                                )
                            )
                    } else {
                        toast("发送失败")
                        _sendCmdEffectFlow.value =
                            Effect.Fail(inputEffect, UartException("发送失败"))
                    }
                }

                UartStatus.DETACH -> {
                    toast("请检查串口")
                    _sendCmdEffectFlow.value =
                        Effect.Fail(inputEffect, UartException("ComStatus.DETACH请检查串口"))
                }

                UartStatus.DISCONNECTED -> {
                    toast("请打开串口")
                    _sendCmdEffectFlow.value =
                        Effect.Fail(inputEffect, UartException("ComStatus.DISCONNECTED请打开串口"))
                }
            }
        }
    }

    fun CoroutineScope.send(
        actionId: String,
        comInfo: ComInfoVO?,
        protocol: ProtocolVO,
        data: ByteArray
    ) {
        println("$TAG ::send ${data.size}")
        if (comInfo == null) {
            toast("请打开串口")
            return
        }

        sendInternal(comInfo) {
            val inputFrame = data.toProtocolFrame()
            val req = if (protocol.cmdReal.arePicPull) {
                HDRequest.PicPull.decode(
                    inputFrame?.payload?.byteArray
                        ?: throw IllegalStateException("frame is null")
                ).run {
                    when (this) {
                        is HDResult.Fail -> throw this.throwable
                        is HDResult.Success<HDRequest.PicPull> -> this.data
                    }
                }
            } else HDRequest.Empty
            val inputEffect = ActionInputData(
                comInfo.path,
                protocol,
                data,
                actionId = actionId,
                frame = inputFrame ?: HDProtocolFrame.EMPTY,
                req = req,

                )
            when (comInfo.status) {
                is UartStatus.CONNECTED -> {
                    _sendCmdEffectFlow.value = effectProgress(inputEffect, "write...")
                    val result =
                        withContext(Dispatchers.IO) {
                            uartRepo.write(comInfo.path, data = data)
                        }


                    if (result > 0) {
                        _sendCmdEffectFlow.value =
                            Effect.Progress(inputEffect, "read ...")
                        println("----------->")
                        val bytes = withContext(Dispatchers.IO) {
                            if (protocol.cmdReal.arePicPull) {
                                uartRepo.readExpect(
                                    comInfo.path,
                                    (req as HDRequest.PicPull).readLen + 11,
                                    20 * 1000
                                )

                            } else {
                                uartRepo.read(
                                    comInfo.path,
                                    3 * 1000
                                )
                            }
                        }
                        println("<-----------")
                        _receiverDataFlow.value = bytes.toHexString()
//                        delay(1000)
                        _sendCmdEffectFlow.value =
                            Effect.Success(
                                input = inputEffect,
                                output = ActionOutputData(
                                    comInfo.path,
                                    protocol,
                                    bytes,
                                    actionId = actionId,
                                    frame = bytes.toProtocolFrame() ?: HDProtocolFrame.EMPTY
                                )
                            )
                    } else {
                        toast("发送失败")
                        _sendCmdEffectFlow.value =
                            Effect.Fail(inputEffect, UartException("发送失败"))
                    }
                }

                UartStatus.DETACH -> {
                    toast("请检查串口")
                    _sendCmdEffectFlow.value =
                        Effect.Fail(inputEffect, UartException("ComStatus.DETACH请检查串口"))
                }

                UartStatus.DISCONNECTED -> {
                    toast("请打开串口")
                    _sendCmdEffectFlow.value =
                        Effect.Fail(inputEffect, UartException("ComStatus.DISCONNECTED请打开串口"))
                }
            }
        }
    }

    fun CoroutineScope.send(comInfo: ComInfoVO, data: String) {
        sendInternal(comInfo) {

            when (comInfo.status) {
                is UartStatus.CONNECTED -> {
                    _sendUartDataEffectFlow.value =
                        Effect.Progress(comInfo.path, "write ...")
                    val result =
                        withContext(Dispatchers.IO) {
                            uartRepo.write(comInfo.path, data = data.hexToByteArray())
                        }


                    if (result > 0) {
                        _sendUartDataEffectFlow.value =
                            Effect.Progress(comInfo.path, "read ...")
                        val bytes = withContext(Dispatchers.IO) {
                            uartRepo.read(comInfo.path, 3 * 1000)
                        }
                        _receiverDataFlow.value = bytes.toHexString()
                        _sendUartDataEffectFlow.value =
                            Effect.Success(comInfo.path, bytes)
                    } else {
                        toast("发送失败")
                        _sendUartDataEffectFlow.value =
                            Effect.Fail(comInfo.path, UartException("发送失败"))
                    }
                }

                UartStatus.DETACH -> {
                    toast("请检查串口")
                    _sendUartDataEffectFlow.value =
                        Effect.Fail(comInfo.path, UartException("ComStatus.DETACH请检查串口"))
                }

                UartStatus.DISCONNECTED -> {
                    toast("请打开串口")
                }
            }


        }
    }

    private fun CoroutineScope.sendInternal(comInfo: ComInfoVO, block: suspend () -> Unit) {
        println("$TAG ::sendInternal")
        this.launch {
            try {
                _sendUartDataEffectFlow.value = effectIdle()
                block()

            } catch (e: Throwable) {
                if (e !is CancellationException) {
                    toast("发送失败$e")
                    e.printStackTrace()
                    _sendUartDataEffectFlow.value =
                        Effect.Fail(comInfo.path, e)
                }
            } finally {
                _sendUartDataEffectFlow.value = effectCompleted()
            }
        }
    }

    fun CoroutineScope.listComInfo() {
        println("$TAG ::listComInfo")
        this.launch {
            try {
                _comInfoListFlow.value = uartRepo.listInfo().map {
                    ComInfoVO(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun CoroutineScope.addComInfo(com: String) {
        println("$TAG ::addComInfo")
        this.launch {
            try {
                val list = uartRepo.add(com)
                _comInfoListFlow.value = list.map {
                    ComInfoVO(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun CoroutineScope.disconnectComInfo(com: String) {
        this.launch {
            try {
                uartRepo.close(com)
            } catch (e: Throwable) {
                toast("关闭[${com}]失败 ${e.message}")
            } finally {
                listComInfo()
            }
        }
    }

    fun CoroutineScope.connectComInfo(com: String) {
        this.launch {
            try {
                val opened = uartRepo.open(com, 0)
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

    fun CoroutineScope.deleteComInfo(info: ComInfoVO) {
        println("$TAG ::deleteComInfo")
        this.launch {
            try {
                val list = uartRepo.delete(info.path)
                _comInfoListFlow.value = list.map {
                    ComInfoVO(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun CoroutineScope.editComRate(info: ComInfoVO) {
        println("$TAG ::editComRate")
        this.launch {
            try {
                val list = uartRepo.edit(
                    UartInfo(
                        com = info.path, rate = info.rate, status = info.status
                    )
                )
                _comInfoListFlow.value = list.map {
                    ComInfoVO(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun CoroutineScope.clearLogcat() {
        this.launch {
            withContext(Dispatchers.IO) {
                uartRepo.clearAllLogcat()
            }
        }
    }

    private val clipboard = generateHDClipboard()
    fun CoroutineScope.shareLogcat(it: List<LogcatHistory>) {
        clipboard.copy(it.joinToString("\n") {
            it.display
        })
        toast("复制成功！")
    }

    private companion object {
        private const val TAG = "Snapshot"
    }

}