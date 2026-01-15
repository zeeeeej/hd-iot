package com.yunext.iot.domain

private class HDClipboardImpl :HDClipboard{
    override fun copy(text: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun paste(): String? {
        TODO("Not yet implemented")
    }
}


actual fun generateHDClipboard(): HDClipboard {
    return HDClipboardImpl()
}