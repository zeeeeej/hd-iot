package com.yunext.iot.domain

sealed interface HDResult<out T> {
    data class Success<T>(val data: T) : HDResult<T>
    data class Fail(val throwable: Throwable) : HDResult<Nothing>
}

fun<T> T.success():HDResult<T>{
    return HDResult.Success(this)
}

fun<T>  Throwable.fail():HDResult<T>{
    return HDResult.Fail(this)
}

fun a(a:HDResult<String>){
    when(a){
        is HDResult.Fail -> TODO()
        is HDResult.Success<String> -> {
            val data = a.data
        }
    }
}