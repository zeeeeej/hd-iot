import com.yunext.iot.jni.NativeLibrary
import java.io.File

class NativeFunctions {

    companion object {


        @JvmStatic
        external fun addNumbers(a: Int, b: Int): Int

        @JvmStatic
        external fun getMessage(): String

        @JvmStatic
        external fun uartVersion(type:Int): String

        @JvmStatic
        external fun uartList(): Array<String>


    }
}