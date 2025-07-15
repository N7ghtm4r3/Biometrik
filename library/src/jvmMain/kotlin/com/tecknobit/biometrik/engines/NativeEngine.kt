package com.tecknobit.biometrik.engines

import com.sun.jna.Library
import com.sun.jna.WString
import java.io.File

/**
 * `OS_NAME_KEY` the constant for the `os.name` property
 */
private const val OS_NAME_KEY = "os.name"

/**
 * `WINDOWS_OS` the constant for the `Windows` value
 */
private const val WINDOWS_OS = "Windows"

/**
 * `MACOS` the constant for the `Mac OS` value
 */
private const val MACOS = "Mac OS"

/**
 * The `NativeEngine` interface allow to access to the native engines of each operating system using the
 * [JNA](https://github.com/java-native-access/jna) APIs
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see Library
 */
internal interface NativeEngine : Library {

    companion object {

        /**
         * Method used to get the proper engine to perform the native bio-authentication
         *
         * @return the engine as [NativeEngine]
         */
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

    /**
     * Mapper method used to request the native bio-authentication
     *
     * @param reason The reason why it is requested the authentication
     *
     * @return the result of the authentication as [Int]
     */
    fun requestAuth(
        reason: WString,
    ): Int

}

/**
 * Method used to extract the absolute path of the dll to execute to perform the native
 * bio-authentication.
 *
 * It creates a temporary file with the `dll`, `so` or `dylib` data because it is required a physical
 * copy of the binary file to be natively loaded via [com.sun.jna.Native.load] api
 *
 * @param dllName The name of the dll file (or `so` or `dylib`)
 * @param suffix The suffix of the dynamic-libraries file
 *
 * @return the absolute path of the copied native library as [String]
 */
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