package com.yunext.iot.domain

interface HDClipboard  {
    fun copy(text: String):Boolean
    fun paste(): String?
}

expect fun generateHDClipboard() :HDClipboard