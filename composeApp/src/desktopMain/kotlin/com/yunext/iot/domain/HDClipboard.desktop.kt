package com.yunext.iot.domain

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

private class HDClipboardImpl : HDClipboard {
    private val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    override fun copy(text: String): Boolean {
        try {
            val stringSelection = StringSelection(text)
            clipboard.setContents(stringSelection, null)
            return true
        } catch (e: Exception) {
            return false
        }

    }

    override fun paste(): String? {
        return try {
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                clipboard.getData(DataFlavor.stringFlavor) as? String
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

actual fun generateHDClipboard(): HDClipboard {
    return HDClipboardImpl()
}