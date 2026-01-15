package com.yunext.iot.domain.protocol

enum class HDExtraProtocolProperty(
    val key: UByte, val title: String,
    val len: Int, val type: String, val rw: String, val desc: String
) {

    AppVersion(0xD4.toUByte(),"hd_app版本",1,"uint8_t","rw","hd_app版本。"),
    Direction(0xDE.toUByte(),"direction",1,"uint8_t","rw","direction。0:未知 1:右门 2:左门。"),
    Freezer(0xDF.toUByte(),"冷柜类型",1,"uint8_t","rw","冷柜类型。 1:单门 2:双门。"),
    GyroscopeSnapAngelExtra(0xDA.toUByte(),"陀螺仪抓图角度",2,"struct","rw","陀螺仪抓图角度。角度B为0表示关闭角度B抓图。。"),
    GyroscopeAngel(0xA0.toUByte(),"获取当前陀螺仪角度",2,"uint16_t","r","获取当前陀螺仪角度。"),
    DeviceInfo(0xA1.toUByte(),"获取设备信息",20,"struct","r","获取设备信息。"),
    ModelName(0xCC.toUByte(),"模型文件名称",0,"string","r","模型文件名称。"),
    Debug(0xD1.toUByte(),"DEBUG开关",1,"uint8_t","rw","DEBUG开关 1:开启；0:关闭。"),
    DebugActionID(0xD2.toUByte(),"测试触发action_id",0,"struct","rw","测试触发action_id。见action_id定义。"),
    DebugGyroscopeAngel(0xD3.toUByte(),"测试触发陀螺仪角度",1,"uint8_t","rw","测试触发陀螺仪角度。"),
//    HDSoVersion(0xD4.toUByte(),"HDso版本",0,"string","r","hd_uart.so版本。"),
    FactoryMode(0xD5.toUByte(),"工厂模式开关",1,"uint8_t","rw","工厂模式开关 1:开启；0:关闭。"),
    Push(0xD6.toUByte(),"Push",0,"struct","w","请求：| md5 | file_size | file_path |\n" +
            "应答：| result |\n"),
    Pull(0xD7.toUByte(),"Pull",0,"string","w","请求：| file_path |\n" +
            "应答：| result | md5 | file_size | file_path |\n"),


    ;
}