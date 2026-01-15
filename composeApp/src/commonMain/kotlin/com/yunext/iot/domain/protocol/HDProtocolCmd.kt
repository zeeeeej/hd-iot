package com.yunext.iot.domain.protocol

enum class HDProtocolCmd(
    val key: UByte,
    val broadcast: Boolean = false,
    val internal: Boolean = false
) {
    HeartBeat(0x01.toUByte()),
    PropertyGet(0x02.toUByte()),
    PropertySet(0x03.toUByte()),
    FactoryReset(0x04.toUByte()),
    Reboot(0x05.toUByte()),
    PicSnap06(0x06.toUByte()),
    PicSnapC6(0xC6.toUByte()),
    PicSnap16(0x16.toUByte()),
    PicInfo07(0x07.toUByte()),
    PicInfoC7(0xC7.toUByte()),
    PicInfo17(0x17.toUByte()),
    PicDelete08(0x08.toUByte()),
    PicDeleteC8(0xC8.toUByte()),
    PicDelete18(0x18.toUByte()),
    PicPull09(0x09.toUByte()),
    PicPullC9(0xC9.toUByte()),
    PicPull19(0x19.toUByte()),
    PicPullComplete0A(0x0A.toUByte()),
    PicPullCompleteCA(0xCA.toUByte()),
    PicPullComplete1A(0x1A.toUByte()),
    DoorState(0x0D.toUByte()),
    SystemOTANotice(0x0B.toUByte()),
    SystemOTA(0x0C.toUByte()),
    AppOTANotice(0x1B.toUByte()),
    AppOTA(0x1C.toUByte()),
    ExtraPropertyGet(0xC2.toUByte()),
    ExtraPropertySet(0xC3.toUByte()),
    RandomAddressBroadcast(0x11.toUByte(), broadcast = true),
    ValidateRandomAddressBroadcast(0x12.toUByte(), broadcast = true),
    GetDirection(0x13.toUByte()),
    CheckDirectionBroadcast(0x14.toUByte(), broadcast = true),
    DoorStatusBroadcastCE(0xCE.toUByte(), broadcast = true),
    DoorStatusBroadcastCD(0xCD.toUByte(), broadcast = true),
    Event(0xCF.toUByte(), broadcast = true),
    PUSH(0xCB.toUByte(), broadcast = true),
    PUSHNotice(0xCC.toUByte()),
    ;
}


val HDProtocolCmd.arePicPull: Boolean
    get() = this == HDProtocolCmd.PicPull19 ||
            this == HDProtocolCmd.PicPull09 ||
            this == HDProtocolCmd.PicPullC9

val HDProtocolCmd.arePicDelete: Boolean
    get() = this == HDProtocolCmd.PicDelete08 ||
            this == HDProtocolCmd.PicDelete18 ||
            this == HDProtocolCmd.PicDeleteC8

val HDProtocolCmd.arePicSnap: Boolean
    get() = this == HDProtocolCmd.PicSnap16 ||
            this == HDProtocolCmd.PicSnap06 ||
            this == HDProtocolCmd.PicSnapC6

val HDProtocolCmd.arePicInfo: Boolean
    get() = this == HDProtocolCmd.PicInfo17 ||
            this == HDProtocolCmd.PicInfo07 ||
            this == HDProtocolCmd.PicInfoC7

val HDProtocolCmd.arePicPullComplete: Boolean
    get() = this == HDProtocolCmd.PicPullComplete0A ||
            this == HDProtocolCmd.PicPullCompleteCA ||
            this == HDProtocolCmd.PicPullComplete1A