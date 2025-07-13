package com.tecknobit.biometrik

import com.sun.jna.Native

internal interface WindowsEngine : NativeEngine

// TODO: TO OPTIMIZE WHEN LINUX'S ONE INTEGRATED 
internal val windowsEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "WindowsHelloEngine"
    ),
    WindowsEngine::class.java
)