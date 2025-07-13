package com.tecknobit.biometrik

import com.sun.jna.Native
import com.tecknobit.biometrik.WindowsEngine.Companion.DLL_PATH

internal interface WindowsEngine : NativeEngine {

    companion object {

        const val DLL_PATH = "library/src/jvmMain/resources/WindowsHelloEngine.dll"

    }

}

internal val windowsEngine = Native.load(
    DLL_PATH,
    WindowsEngine::class.java
)