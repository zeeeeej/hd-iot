package com.yunext.iot.domain.project

interface ProjectDomain {
    suspend fun list(): List<HDProject> {
        return listOf(HDProject.KSF, HDProject.JML, HDProject.XW, HDProject.AI)
    }
}

