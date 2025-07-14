package com.tecknobit.biometrik.engines

import com.sun.jna.Native

internal interface LinuxEngine : NativeEngine

internal val linuxEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "LinuxPolkitEngine",
        suffix = ".so"
    ),
    LinuxEngine::class.java
)