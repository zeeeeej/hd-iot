package com.yunext.iot.datasource

import com.yunext.iot.model.Com
import com.yunext.iot.model.UartManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface UartDatasource {
    suspend fun list(): List<Com>
    suspend fun open(path: String, rate: Int): Boolean
    suspend fun close(path: String): Boolean
}

class UartDatasourceImpl(private val uartManager: UartManager) : UartDatasource {
    override suspend fun list(): List<Com> {
        return suspendCancellableCoroutine<List<Com>> { con ->
            try {
                val list = uartManager.list()
                con.resume(list)
            } catch (e: Throwable) {
                con.resumeWithException(e)
            }

            con.invokeOnCancellation {

            }
        }
    }

    override suspend fun open(path: String, rate: Int): Boolean {
        return suspendCancellableCoroutine { con ->
            try {
                val result = uartManager.open(path, rate)
                con.resume(result)
            } catch (e: Throwable) {
                con.resumeWithException(e)
            }

            con.invokeOnCancellation {

            }
        }
    }

    override suspend fun close(path: String): Boolean {
        return suspendCancellableCoroutine { con ->
            try {
                val result = uartManager.close(path)
                con.resume(result)
            } catch (e: Throwable) {
                con.resumeWithException(e)
            }

            con.invokeOnCancellation {

            }
        }
    }

}