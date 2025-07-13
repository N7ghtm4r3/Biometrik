package com.tecknobit.biometrik

import com.sun.jna.Native

internal interface LinuxEngine : NativeEngine

// TODO: TO OPTIMIZE WHEN LINUX'S ONE INTEGRATED 
internal val linuxEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "LinuxPolkitEngine",
        suffix = ".so"
    ),
    LinuxEngine::class.java
)