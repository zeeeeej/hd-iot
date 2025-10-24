import com.yunext.iot.jni.NativeLibrary
import java.io.File

class NativeFunctions {

    companion object {


        @JvmStatic
        external fun addNumbers(a: Int, b: Int): Int

        @JvmStatic
        external fun getMessage(): String

        @JvmStatic
        external fun uartVersion(type: Int): String

        @JvmStatic
        external fun uartList(): Array<String>

        /**
         * @param com 串口路径
         * @param rate 波特率
         * @return 串口句柄
         */
        @JvmStatic
        external fun uartOpen(com: String, rate: Int): Long

        @JvmStatic
        external fun uartClose(handle: Long)

        @JvmStatic
        external fun uartWrite(handle: Long, data: ByteArray): Int

        @JvmStatic
        external fun uartRead(handle: Long, maxLength: Int): ByteArray?

    }
}