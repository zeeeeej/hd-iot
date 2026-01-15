package com.yunext.iot.ui.protocol

import androidx.compose.runtime.Stable
import com.yunext.iot.domain.project.HDProject
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.HDProtocolFrame
import com.yunext.iot.domain.protocol.HDRequest
import com.yunext.iot.domain.protocol.HDResponse
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.compoent.effectIdle

@Stable
data class ProtocolAction(
    val id: String,
    val req:HDRequest,
//    val resp:HDResponse,
    val inputFrame: HDProtocolFrame,
    val outputFrame: HDProtocolFrame = HDProtocolFrame.EMPTY,
    val effect: Effect<ActionInputData, ActionOutputData> = effectIdle()
) {
    companion object {
        val EMPTY = ProtocolAction(
            "",
            req = HDRequest.Empty,
            inputFrame = HDProtocolFrame.EMPTY,
            outputFrame = HDProtocolFrame.EMPTY,
            effect = effectIdle()
        )
    }
}

val ProtocolAction.isEmpty: Boolean
    get() = this == ProtocolAction.EMPTY

data class ProtocolVO(
    val cmd: UByte,
    val name: String,
    val desc: String,
    val proj: HDProject,
    val address: UByte,
    val cmdStr: String,
    val cmdReal: HDProtocolCmd,
    val action: ProtocolAction = ProtocolAction.EMPTY
) {
    companion object {
        val EMPTY = ProtocolVO(
            0.toUByte(),
            "",
            "",
            proj = HDProject.KSF,
            address = 0x00.toUByte(),
            "",
            HDProtocolCmd.DoorStatusBroadcastCD,
            action = ProtocolAction.EMPTY
        )
    }
}

val ProtocolVO.isEmpty: Boolean
    get() = this == ProtocolVO.EMPTY

data class ProjectVO(
    val key: String,
    val name: String,
    val proj :HDProject
) {
    companion object {
        val EMPTY = ProjectVO("", "",HDProject.KSF)
    }
}

val ProjectVO.isEmpty: Boolean
    get() = this == ProjectVO.EMPTY

val HDProtocolCmd.title: String
    get() {
        return when (this) {
            HDProtocolCmd.HeartBeat -> "心跳"
            HDProtocolCmd.PropertyGet -> "查询属性"
            HDProtocolCmd.PropertySet -> "设置属性"
            HDProtocolCmd.FactoryReset -> "摄像头恢复出厂设置"
            HDProtocolCmd.Reboot -> "重启摄像头"
            HDProtocolCmd.PicSnap06 -> "主动抓图"
            HDProtocolCmd.PicSnapC6 -> "主动抓图"
            HDProtocolCmd.PicSnap16 -> "主动抓图"
            HDProtocolCmd.PicInfo07 -> "查询摄像头存储的图片信息"
            HDProtocolCmd.PicInfoC7 -> "查询摄像头存储的图片信息"
            HDProtocolCmd.PicInfo17 -> "查询摄像头存储的图片信息"
            HDProtocolCmd.PicDelete08 -> "删除图片"
            HDProtocolCmd.PicDeleteC8 -> "删除图片"
            HDProtocolCmd.PicDelete18 -> "删除图片"
            HDProtocolCmd.PicPull09 -> "拉取图片"
            HDProtocolCmd.PicPullC9 -> "拉取图片"
            HDProtocolCmd.PicPull19 -> "拉取图片"
            HDProtocolCmd.PicPullComplete0A -> "图片拉取完成"
            HDProtocolCmd.PicPullCompleteCA -> "图片拉取完成"
            HDProtocolCmd.PicPullComplete1A -> "图片拉取完成"
            HDProtocolCmd.DoorState -> ""
            HDProtocolCmd.SystemOTANotice -> "Linux系统升级通知"
            HDProtocolCmd.SystemOTA -> "发送Linux系统升级包"
            HDProtocolCmd.AppOTANotice -> "摄像头应用升级通知"
            HDProtocolCmd.AppOTA -> "发送摄像头应用升级包"
            HDProtocolCmd.ExtraPropertyGet -> "查询额外属性"
            HDProtocolCmd.ExtraPropertySet -> "设置额外属性"
            HDProtocolCmd.RandomAddressBroadcast -> "随机分配地址广播"
            HDProtocolCmd.ValidateRandomAddressBroadcast -> "验证随机分配地址广播"
            HDProtocolCmd.GetDirection -> "查询摄像头从机地址和左右门对应关系"
            HDProtocolCmd.CheckDirectionBroadcast -> "验证摄像头从机地址和左右门对应关系广播"
            HDProtocolCmd.DoorStatusBroadcastCE -> "广播门开事件"
            HDProtocolCmd.DoorStatusBroadcastCD -> "广播门开事件"
            HDProtocolCmd.Event -> "事件广播"
            HDProtocolCmd.PUSH -> "push文件通知"
            HDProtocolCmd.PUSHNotice -> "push文件"
        }
    }


val HDProtocolCmd.desc: String
    get() {
        return when (this) {
            HDProtocolCmd.HeartBeat -> "摄像头的保活心跳，用来探测指定地址从机是否连接正常，请求的消息体+1=返回消息体。(数据循环0x00-0xFF)"
            HDProtocolCmd.PropertyGet -> ""
            HDProtocolCmd.PropertySet -> ""
            HDProtocolCmd.FactoryReset -> ""
            HDProtocolCmd.Reboot -> ""
            HDProtocolCmd.PicSnap06 -> ""
            HDProtocolCmd.PicSnapC6 -> ""
            HDProtocolCmd.PicSnap16 -> ""
            HDProtocolCmd.PicInfo07 -> ""
            HDProtocolCmd.PicInfoC7 -> ""
            HDProtocolCmd.PicInfo17 -> ""
            HDProtocolCmd.PicDelete08 -> ""
            HDProtocolCmd.PicDeleteC8 -> ""
            HDProtocolCmd.PicDelete18 -> ""
            HDProtocolCmd.PicPull09 -> ""
            HDProtocolCmd.PicPullC9 -> ""
            HDProtocolCmd.PicPull19 -> ""
            HDProtocolCmd.PicPullComplete0A -> ""
            HDProtocolCmd.PicPullCompleteCA -> ""
            HDProtocolCmd.PicPullComplete1A -> ""
            HDProtocolCmd.DoorState -> ""
            HDProtocolCmd.SystemOTANotice -> ""
            HDProtocolCmd.SystemOTA -> ""
            HDProtocolCmd.AppOTANotice -> ""
            HDProtocolCmd.AppOTA -> ""
            HDProtocolCmd.ExtraPropertyGet -> ""
            HDProtocolCmd.ExtraPropertySet -> ""
            HDProtocolCmd.RandomAddressBroadcast -> ""
            HDProtocolCmd.ValidateRandomAddressBroadcast -> ""
            HDProtocolCmd.GetDirection -> ""
            HDProtocolCmd.CheckDirectionBroadcast -> ""
            HDProtocolCmd.DoorStatusBroadcastCE -> ""
            HDProtocolCmd.DoorStatusBroadcastCD -> ""
            HDProtocolCmd.Event -> ""
            HDProtocolCmd.PUSH -> ""
            HDProtocolCmd.PUSHNotice -> ""
        }
    }


//val ProjectVOMapSaver = run {
//    val key = "key"
//    val nameKey = "name"
//
//    mapSaver<ProjectVO>(
//        save = { project ->
//            mapOf(
//                key to project.key,
//                nameKey to project.name,
//            )
//        },
//        restore = { map ->
//            ProjectVO(
//                key = map[key] as String,
//                name = map[nameKey] as String,
//            )
//        }
//    )
//}
//
//val ProtocolVOMapSaver = run {
//    val cmdKey = "cmd"
//    val nameKey = "name"
//    val descKey = "desc"
//    val projectKeyKey = "projectKey"
//    val cmdStrKey = "cmdStr"
//
//    mapSaver<ProtocolVO>(
//        save = { protocol ->
//            mapOf(
//                cmdKey to protocol.cmd.toInt(),
//                nameKey to protocol.name,
//                descKey to protocol.desc,
//                projectKeyKey to protocol.projectKey,
//                cmdStrKey to protocol.cmdStr
//            )
//        },
//        restore = { map ->
//            ProtocolVO(
//                cmd = (map[cmdKey] as Int).toUByte(),
//                name = map[nameKey] as String,
//                desc = map[descKey] as String,
//                projectKey = map[projectKeyKey] as String,
//                cmdStr = map[cmdStrKey] as String
//            )
//        }
//    )
//}

val HDProject.defaultAddress:UByte
    get() = when(this){
        HDProject.AI -> 0x01
        HDProject.JML -> 0x01
        HDProject.KSF ->0xEE
        HDProject.XW -> 0x01
    }.toUByte()