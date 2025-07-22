package com.yunext.iot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform