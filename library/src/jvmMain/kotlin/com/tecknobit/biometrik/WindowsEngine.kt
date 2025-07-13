package com.tecknobit.biometrik

import com.sun.jna.Native

internal interface WindowsEngine : NativeEngine

internal val windowsEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "WindowsHelloEngine"
    ),
    WindowsEngine::class.java
)