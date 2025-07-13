package com.tecknobit.biometrik

import com.sun.jna.Library
import com.sun.jna.WString
import java.io.File

private const val DLL_SUFFIX = ".dll"

private const val OS_NAME_KEY = "os.name"

private const val WINDOWS_OS = "Windows"

internal interface NativeEngine : Library {

    companion object {

        fun getInstance(): NativeEngine {
            val currentOs = System.getProperty(OS_NAME_KEY)
            return if (currentOs.startsWith(WINDOWS_OS))
                windowsEngine
            else
                windowsEngine // TODO: TO REPLACE WITH THE linuxEngine INSTEAD
        }

    }

    fun requestAuth(
        reason: WString,
    ): Int

}

internal fun extractDllAbsolutePath(
    dllName: String,
): String {
    val dllContent = NativeEngine::class.java.classLoader!!.getResourceAsStream(dllName + DLL_SUFFIX)
    val tmpDll = File.createTempFile(dllName, DLL_SUFFIX)!!
    tmpDll.deleteOnExit()
    dllContent.use { input ->
        tmpDll.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return tmpDll.absolutePath
}