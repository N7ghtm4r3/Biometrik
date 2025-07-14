package com.tecknobit.biometrik.engines

import com.sun.jna.Native

internal interface MacOsEngine : NativeEngine

internal val macOsEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "LocalAuthenticationEngine",
        suffix = ".dylib"
    ),
    MacOsEngine::class.java
)