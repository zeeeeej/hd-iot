package com.yunext.iot.datasource

import com.yunext.iot.domain.uart.Uart
import com.yunext.iot.model.UartManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface UartDatasource {
    suspend fun list(): List<Uart>
    suspend fun open(path: String, rate: Int): Long
    suspend fun close(handle: Long): Boolean
    suspend fun writeDelay(handle: Long, data: ByteArray,delay:Int): Int
    suspend fun write(handle: Long, data: ByteArray): Int
    suspend fun read(handle: Long, max: Int,timeout:Int = 3): ByteArray
    suspend fun readExpect(handle: Long, expect: Int,timeout:Int = 3): ByteArray
}

class UartDatasourceImpl(private val uartManager: UartManager) : UartDatasource {
    override suspend fun list(): List<Uart> {
        return suspendCancellableCoroutine<List<Uart>> { con ->
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

    override suspend fun writeDelay(handle: Long, data: ByteArray,delay:Int): Int {
        return suspendCancellableCoroutine { con ->
            try {
                val result = uartManager.writeDelay(handle,data,delay)
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

    override suspend fun read(handle: Long, max: Int,timeout:Int): ByteArray {
        return suspendCancellableCoroutine { con ->
            try {
                val result = uartManager.read(handle,max,timeout)
                con.resume(result)
            } catch (e: Throwable) {
                con.resumeWithException(e)
            }

            con.invokeOnCancellation {

            }
        }
    }

    override suspend fun readExpect(handle: Long, expect: Int, timeout: Int): ByteArray {
        return suspendCancellableCoroutine { con ->
            try {
                val result = uartManager.readExpect(handle,expect,timeout)
                con.resume(result)
            } catch (e: Throwable) {
                con.resumeWithException(e)
            }

            con.invokeOnCancellation {

            }
        }
    }

}