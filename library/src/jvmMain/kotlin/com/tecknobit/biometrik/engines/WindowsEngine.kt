package com.tecknobit.biometrik.engines

import com.sun.jna.Native

internal interface WindowsEngine : NativeEngine

internal val windowsEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "WindowsHelloEngine",
        suffix = ".dll"
    ),
    WindowsEngine::class.java
)