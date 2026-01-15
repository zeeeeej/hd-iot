package com.yunext.iot.domain.protocol

enum class HDProtocolProperty(
    val key: UByte, val title: String,
    val len: Int, val type: String, val rw: String, val desc: String
) {
    Address(0x01.toUByte(),"从机通讯地址",1,"uint8_t","rw","从机通讯地址。"),
    SdkVersion(0x02.toUByte(),"Linux应用程序版本",0,"string","rw","Linux应用程序版本。"),
    Sn(0x03.toUByte(),"摄像头序列号",0,"string","rw","摄像头序列号。"),
    BaudRate(0x04.toUByte(),"波特率",1,"uint32_t","w","波特率。115200 230400 460800 921600 1500000" +
            "默认值460800，波特率延时2秒生效。" +
            "获取波特率，可以采用心跳轮询的方式。"),
    PicSize(0x05.toUByte(),"图片尺寸",4,"struct","rw","图片尺寸。"),
    PicCompressRatio(0x06.toUByte(),"图片压缩率",1,"uint8_t","rw","图片压缩率。"),
    PicBrightness(0x07.toUByte(),"图片亮度",1,"uint8_t","rw","图片亮度。"),
    PicParams(0x08.toUByte(),"图片参数",1,"uint8_t","r","图片参数。"),
    UTC(0x09.toUByte(),"UTC时间戳",4,"uint32_t","rw","UTC时间戳，单位秒。"),
    GyroscopeSnapAngel(0x0A.toUByte(),"陀螺仪抓图角度",2,"uint8_t","rw","陀螺仪抓图角度。角度B为0表示关闭角度B抓图。"),
    GyroscopeSnapDirect(0x0B.toUByte(),"陀螺仪抓图方向",2,"-","rw","陀螺仪抓图方向。0x00逆时针；0x01顺时针。"),
    GyroscopeSnapConfig(0x0C.toUByte(),"陀螺仪抓图配置",-1,"uint8_t","rw","陀螺仪抓图配置。"),
    GyroscopeStatus(0x0D.toUByte(),"陀螺仪开启状态",1,"uint8_t","rw","陀螺仪开启状态。0x00关闭；0x01开启。"),
    MaxPic(0x0E.toUByte(),"最多开门图片",1,"uint8_t","rw","摄像头最多存储最近多少次开门图片。默认10"),
    PWM(0x0F.toUByte(),"pwm占空比",1,"uint8_t","rw","设置加热丝pwm占空比，值为30、40、50、60，默认30"),
    PitchAngle(0x10.toUByte(),"俯仰角",4,"Int32_t","r","俯仰角，实际值的100倍，补码形式，若无此参数，上报0X7FFFFFFF"),
    RollAngle(0x11.toUByte(),"横滚角",4,"Int32_t","r","横滚角，实际值的100倍，补码形式，若无此参数，上报0X7FFFFFFF。"),
    YawAngle(0x12.toUByte(),"偏航角",4,"Int32_t","r","偏航角，实际值的100倍，补码形式，若无此参数，上报0X7FFFFFFF。"),
    Acceleration(0x13.toUByte(),"加速度",4,"Int32_t","r","加速度，实际值的100倍，补码形式，若无此参数，上报0X7FFFFFFF。"),
    LatestAngel(0x14.toUByte(),"最近一次的开门角度",5,"struct","rw","最近一次的开门角度，若无开门事件，时间戳和开门角度都上报0(5个字节都是0)。"),
    SystemVersion(0x15.toUByte(),"Linux系统版本",0,"string","rw","Linux系统版本。"),

    ;
}