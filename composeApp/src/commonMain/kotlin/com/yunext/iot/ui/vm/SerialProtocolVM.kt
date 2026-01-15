package com.yunext.iot.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.iot.domain.project.HDProject
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.HDRequest
import com.yunext.iot.domain.protocol.HDResponse
import com.yunext.iot.domain.protocol.arePicPull
import com.yunext.iot.domain.uart.Uart
import com.yunext.iot.domain.uart.UartException
import com.yunext.iot.domain.uart.UartStatus
import com.yunext.iot.repository.ProtocolRepository
import com.yunext.iot.repository.UartRepository
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.compoent.effectCompleted
import com.yunext.iot.ui.compoent.effectIdle
import com.yunext.iot.ui.protocol.ActionInputData
import com.yunext.iot.ui.protocol.ActionOutputData
import com.yunext.iot.ui.protocol.ProjectVO
import com.yunext.iot.ui.protocol.ProtocolAction
import com.yunext.iot.ui.protocol.ProtocolVO
import com.yunext.iot.ui.protocol.desc
import com.yunext.iot.ui.protocol.isEmpty
import com.yunext.iot.ui.protocol.title
import com.yunext.iot.ui.uart.ComInfoVO
import com.yunext.kotlin.kmp.common.util.currentTime
import com.yunext.kotlin.kmp.common.util.hdUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

data class SerialProtocolState(
    val projectList: List<ProjectVO> = emptyList(),
    val protocolList: List<ProtocolVO> = emptyList(),
//    val receiveData: String = "",
//    val sendUartDataEffect: Effect<Uart, ByteArray> = effectIdle(),
) {
    companion object {
        val EMPTY = SerialProtocolState()
    }
}


class SerialProtocolVM(
    private val snap: Snapshot,
    private val protocolRepo: ProtocolRepository
) : ViewModel() {

    private val projectListFlow: MutableStateFlow<List<ProjectVO>> = MutableStateFlow(emptyList())
    private val protocolListFlow: MutableStateFlow<List<ProtocolVO>> = MutableStateFlow(emptyList())

    //    private val protocolDefaultListFlow: MutableStateFlow<List<ProtocolVO>> =
//        MutableStateFlow(emptyList())
    init {
    }

    private fun effectApplyProtocolList(effect: Effect<ActionInputData, ActionOutputData>) {
        val oldList = protocolListFlow.value

        fun apply(p: ProtocolVO, ef: Effect<ActionInputData, ActionOutputData>): ProtocolVO? {
            when (effect) {
                Effect.Completed -> {

                }

                is Effect.Fail<*> -> {
                    val input = effect.input as ActionInputData
                    if (p.cmd == input.protocol.cmd) {
                        if (!p.action.isEmpty && p.action.id == input.actionId) {
                            val newP = p.copy(
                                action = p.action.copy(
                                    effect = effect, outputFrame = HDProtocolFrame.EMPTY
                                )
                            )
                            println("==>失败 $newP ${effect.output}")
                            return newP
                        }
                    }
                }

                Effect.Idle -> {
                }

                is Effect.Progress<*, *> -> {
                    val input = effect.input as ActionInputData
                    if (input.protocol.cmd == p.cmd) {
                        val action = ProtocolAction(
                            effect = effect, inputFrame = input.frame,
                            req = input.req,
                            id = input.actionId
                        )
                        val newP = p.copy(action = action)
                        println("==>开始 $newP")
                        return newP
                    }
                }

                is Effect.Success<*, *> -> {
                    val input = effect.input as ActionInputData
                    val output = effect.output as ActionOutputData

                    if (p.cmd == input.protocol.cmd) {
                        if (!p.action.isEmpty && p.action.id == input.actionId){
                            if (p.cmdReal.arePicPull){
                                val payload = output.frame.payload.byteArray
                                val save = payload.copyOfRange(1,payload.size-1)
                                if (payload.size>1){
                                    // 保存到文件
                                    val req = p.action.req as HDRequest.PicPull
                                    val rootDir = "pic"
                                    // 应该改为：
                                    val picDir = File(rootDir)
                                    if (!picDir.exists()) {
                                        picDir.mkdirs() // 创建目录
                                    }
                                    val filepath = "${rootDir}/${req.picId.toHexString()}_${currentTime()}_${hdUUID(16)}.jpg"
                                    FileOutputStream(filepath).use { o ->
                                        o.write(save)
                                    }
                                    println("✅ 处理后的数据已保存到：$filepath（长度：${save.size} 字节）")
                                }
                            }
                            val newP = p.copy(
                                action = p.action.copy(
                                    effect = effect,
                                    outputFrame = output.frame,
                                )
                            )
                            println("==>成功 $newP")



                            return newP
                        }

                    }
                }
            }
            return null
        }

        val list = oldList.map { p ->
            apply(p, effect) ?: p
        }
        protocolListFlow.value = list
    }

    init {
        viewModelScope.launch {
            snap.sendCmdEffectFlow.collect {
                effectApplyProtocolList(it)
            }
        }
    }

//    private val protocolListFlow: Flow<List<ProtocolVO>> = combine(
//        protocolDefaultListFlow,
//        snap.sendCmdEffectFlow
//    ) { changed ->
//        val protocolList = changed[0] as List<ProtocolVO>
//        val effect: Effect<ActionInputData, ActionOutputData> =
//            changed[1] as Effect<ActionInputData, ActionOutputData>
//        val list = protocolList.map { p ->
////            println("${protocolList.size} -> effect $effect")
//
//            when (effect) {
//                Effect.Completed -> {
//                    if (p.cmd == HDProtocolCmd.HeartBeat.key) {
//                        println("Effect.Completed ==> $effect ${p.action}")
//                    }
//                    p
//                }
//
//                is Effect.Fail<*> -> {
//
//
//                    val input = effect.input as ActionInputData
//                    if (p.cmd == input.protocol.cmd) {
//                        val newP = p.copy(
//                            action = ProtocolAction(
//                                inputFrame = input.frame,
//                                id = input.actionId,
//                                effect = effect, outputFrame = HDProtocolFrame.EMPTY
//                            )
//                        )
//                        println("==>失败 $newP ${effect.output}")
//                        newP
//                    } else {
//                        p
//                    }
//
//                }
//
//                Effect.Idle -> {
//
//                    p
//                }
//
//                is Effect.Progress<*, *> -> {
//
//
//                    val input = effect.input as ActionInputData
//                    if (input.protocol.cmd == p.cmd) {
//                        val action = ProtocolAction(
//                            effect = effect, inputFrame = input.frame, id = input.actionId
//                        )
//                        val newP = p.copy(action = action)
//                        println("==>开始 $newP")
//                        newP
//                    } else {
//                        p
//                    }
//
//                }
//
//                is Effect.Success<*, *> -> {
//                    val input = effect.input as ActionInputData
//                    val output = effect.output as ActionOutputData
//
//                    if (p.cmd == input.protocol.cmd) {
//                        val newP = p.copy(
//                            action = ProtocolAction(
//                                effect = effect,
//                                outputFrame = output.frame,
//                                inputFrame = input.frame,
//                                id = input.actionId
//                            )
//                        )
//                        println("==>成功 $newP")
//                        newP
//                    } else {
//                        p
//                    }
//                }
//            }
//        }
//        list
//    }

    private companion object {
        private const val TAG = "SerialProtocolVM"
    }

    @Suppress("UNCHECKED_CAST")
    val state: StateFlow<SerialProtocolState> = combine(
        projectListFlow,
        protocolListFlow,
//        snap.receiverDataFlow,
//        snap.sendUartDataEffectFlow
    ) { data ->
        val projectList = data[0] as List<ProjectVO>
        val protocolList = data[1] as List<ProtocolVO>
//        val receiveData = data[2] as String
//        val sendUartDataEffect = data[3] as Effect<Uart, ByteArray>

        SerialProtocolState(
            projectList,
            protocolList = protocolList,
//            sendUartDataEffect = sendUartDataEffect,
//            receiveData = receiveData,

        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SerialProtocolState.EMPTY)

    fun listProtocol(project: ProjectVO) {
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    val proj = protocolRepo.list().singleOrNull {
                        it.key == project.key
                    } ?: throw IllegalStateException("不支持的Project:$project")
                    protocolRepo.listProtocol(proj).map {
                        val address = when (proj) {
                            HDProject.AI -> 0x01.toUByte()
                            HDProject.JML -> 0x01.toUByte()
                            HDProject.KSF -> 0xEE.toUByte()
                            HDProject.XW -> 0x01.toUByte()
                        }
                        ProtocolVO(
                            name = it.cmd.title,
                            desc = it.cmd.desc,
                            cmd = it.cmd.key,
                            proj = proj,
                            cmdStr = "(0x${it.cmd.key.toHexString()})",
                            cmdReal = it.cmd,
                            address = address
                        )
                    }
                }
                protocolListFlow.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun listProject() {
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    protocolRepo.list().map {
                        ProjectVO(key = it.key, name = it.name,it)
                    }
                }
                projectListFlow.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

}