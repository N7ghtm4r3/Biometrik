package com.tecknobit.biometrik.engines

import com.sun.jna.Library
import com.sun.jna.WString
import java.io.File

private const val OS_NAME_KEY = "os.name"

private const val WINDOWS_OS = "Windows"

private const val MACOS = "Mac OS"

internal interface NativeEngine : Library {

    companion object {

        fun getInstance(): NativeEngine {
            val currentOs = System.getProperty(OS_NAME_KEY)
            return if (currentOs.startsWith(WINDOWS_OS))
                windowsEngine
            else if (currentOs.startsWith(MACOS))
                macOsEngine
            else
                linuxEngine
        }

    }

    fun requestAuth(
        reason: WString,
    ): Int

}

internal fun extractDllAbsolutePath(
    dllName: String,
    suffix: String
): String {
    val dllContent = NativeEngine::class.java.classLoader!!.getResourceAsStream(dllName + suffix)
    val tmpDll = File.createTempFile(dllName, suffix)
    tmpDll.deleteOnExit()
    dllContent.use { input ->
        tmpDll.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return tmpDll.absolutePath
}