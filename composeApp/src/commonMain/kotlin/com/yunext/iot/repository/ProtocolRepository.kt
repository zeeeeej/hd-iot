package com.yunext.iot.repository

import com.yunext.iot.domain.project.HDProject
import com.yunext.iot.domain.project.ProjectDomain
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.HDProtocolFrameType
import com.yunext.iot.domain.protocol.ProtocolDomain
import com.yunext.iot.domain.protocol.ProtocolInfo

interface ProtocolRepository : ProtocolDomain,ProjectDomain


class ProtocolRepositoryImpl : ProtocolRepository {

    override suspend fun listProtocol(project: HDProject): List<ProtocolInfo> {
        return when (project) {
            HDProject.AI -> {
                BasicCmdList + listOf(
                    HDProtocolCmd.PicSnapC6,
                    HDProtocolCmd.PicInfoC7,
                    HDProtocolCmd.PicDeleteC8,
                    HDProtocolCmd.PicPullC9,
                    HDProtocolCmd.PicPullCompleteCA,
                    HDProtocolCmd.ExtraPropertyGet,
                    HDProtocolCmd.ExtraPropertySet,
                    HDProtocolCmd.DoorStatusBroadcastCD,
                )
            }
            HDProject.JML -> {
                BasicCmdList + listOf(
                    HDProtocolCmd.PicSnap06,
                    HDProtocolCmd.PicInfo07,
                    HDProtocolCmd.PicDelete08,
                    HDProtocolCmd.PicPull09,
                    HDProtocolCmd.PicPullComplete0A,
                    HDProtocolCmd.DoorStatusBroadcastCD,
                )
            }
            HDProject.KSF -> {
                BasicCmdList + listOf(
                    HDProtocolCmd.PicSnap16,
                    HDProtocolCmd.PicInfo17,
                    HDProtocolCmd.PicDelete18,
                    HDProtocolCmd.PicPull19,
                    HDProtocolCmd.PicPullComplete1A,
                    HDProtocolCmd.ExtraPropertyGet,
                    HDProtocolCmd.ExtraPropertySet,
                    HDProtocolCmd.RandomAddressBroadcast,
                    HDProtocolCmd.ValidateRandomAddressBroadcast,
                    HDProtocolCmd.GetDirection,
                    HDProtocolCmd.CheckDirectionBroadcast,
                    HDProtocolCmd.DoorStatusBroadcastCE,
                    HDProtocolCmd.Event,
                )

            }

            HDProject.XW -> {
                BasicCmdList + listOf(
                    HDProtocolCmd.PicSnapC6,
                    HDProtocolCmd.PicInfoC7,
                    HDProtocolCmd.PicDeleteC8,
                    HDProtocolCmd.PicPullC9,
                    HDProtocolCmd.PicPullCompleteCA,
                    HDProtocolCmd.ExtraPropertyGet,
                    HDProtocolCmd.ExtraPropertySet,
                    HDProtocolCmd.DoorStatusBroadcastCD,
                    HDProtocolCmd.PUSHNotice,
                    HDProtocolCmd.PUSH,
                )
            }
        } .map {
            ProtocolInfo(it,project)
        }
    }

    companion object{
        private val BasicCmdList:List<HDProtocolCmd>  by lazy {
            listOf(
                HDProtocolCmd.HeartBeat,
                HDProtocolCmd.PropertyGet,
                HDProtocolCmd.PropertySet,
                HDProtocolCmd.FactoryReset,
                HDProtocolCmd.Reboot,
                HDProtocolCmd.SystemOTANotice,
                HDProtocolCmd.SystemOTA,
                HDProtocolCmd.AppOTANotice,
                HDProtocolCmd.AppOTA,
            )
        }
    }
}