package com.yunext.iot.datasource

import com.yunext.iot.domain.Com
import com.yunext.iot.model.UartManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface UartDatasource {
    suspend fun list(): List<Com>
    suspend fun open(path: String, rate: Int): Long
    suspend fun close(handle: Long): Boolean
    suspend fun write(handle: Long, data: ByteArray): Int
    suspend fun read(handle: Long, max: Int): ByteArray
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

    override suspend fun open(path: String, rate: Int): Long {
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

    override suspend fun close(handle: Long): Boolean {
        return suspendCancellableCoroutine { con ->
            try {
                val result = uartManager.close(handle)
                con.resume(result)
            } catch (e: Throwable) {
                con.resumeWithException(e)
            }

            con.invokeOnCancellation {

            }
        }
    }

    override suspend fun write(handle: Long, data: ByteArray): Int {
        return suspendCancellableCoroutine { con ->
            try {
                val result = uartManager.write(handle,data)
                con.resume(result)
            } catch (e: Throwable) {
                con.resumeWithException(e)
            }

            con.invokeOnCancellation {

            }
        }
    }

    override suspend fun read(handle: Long, max: Int): ByteArray {
        return suspendCancellableCoroutine { con ->
            try {
                val result = uartManager.read(handle,max)
                con.resume(result)
            } catch (e: Throwable) {
                con.resumeWithException(e)
            }

            con.invokeOnCancellation {

            }
        }
    }

}