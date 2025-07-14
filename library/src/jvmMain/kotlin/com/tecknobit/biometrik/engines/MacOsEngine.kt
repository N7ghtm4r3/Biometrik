package com.tecknobit.biometrik.engines

import com.sun.jna.Native

internal interface MacOsEngine : NativeEngine

internal val macOsEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "LocalAuthentication",
        suffix = ".dylib"
    ),
    MacOsEngine::class.java
)