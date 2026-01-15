package com.yunext.iot.ui.protocol

import com.yunext.iot.domain.project.HDProject
import com.yunext.iot.domain.protocol.HDExtraProtocolProperty
import com.yunext.iot.domain.protocol.HDProtocolProperty

data class PropertyVo(
    val id: HDProtocolProperty,
    val name: String,
    val desc: String,
    val value: String
)

fun HDProject.listProperty():List<PropertyVo>{
    return when(this){
        HDProject.AI -> {
            HDProtocolProperty.entries.map {
                PropertyVo(id = it,name = it.name,desc=it.name,"")
            }
        }
        HDProject.JML ->  HDProtocolProperty.entries.map {
            PropertyVo(id = it,name = it.name,desc=it.name,"")
        }
        HDProject.KSF ->  HDProtocolProperty.entries.map {
            PropertyVo(id = it,name = it.name,desc=it.name,"")
        }
        HDProject.XW ->  HDProtocolProperty.entries.map {
            PropertyVo(id = it,name = it.name,desc=it.name,"")
        }
    }
}

data class ExtraPropertyVo(
    val id: HDExtraProtocolProperty,
    val name: String,
    val desc: String,
    val value: String
)

fun HDProject.listExtraProperty():List<ExtraPropertyVo>{
    return when(this){
        HDProject.AI -> {
            HDExtraProtocolProperty.entries.map {
                ExtraPropertyVo(id = it,name = it.name,desc=it.name,"")
            }
        }
        HDProject.JML ->  HDExtraProtocolProperty.entries.map {
            ExtraPropertyVo(id = it,name = it.name,desc=it.name,"")
        }
        HDProject.KSF ->  HDExtraProtocolProperty.entries.map {
            ExtraPropertyVo(id = it,name = it.name,desc=it.name,"")
        }
        HDProject.XW ->  HDExtraProtocolProperty.entries.map {
            ExtraPropertyVo(id = it,name = it.name,desc=it.name,"")
        }
    }
}

