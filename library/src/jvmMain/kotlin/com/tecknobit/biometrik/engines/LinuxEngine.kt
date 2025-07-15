package com.tecknobit.biometrik.engines

import com.sun.jna.Library
import com.sun.jna.Native

/**
 * The `LinuxEngine` interface allow to access to the native API to request the bio-authentication using
 * the uses the [Polkit](https://www.freedesktop.org/software/polkit/docs/latest/index.html) APIs
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see Library
 * @see NativeEngine
 */
internal interface LinuxEngine : NativeEngine

/**
 * `linuxEngine` instance of the [LinuxEngine] engine
 */
internal val linuxEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "LinuxPolkitEngine",
        suffix = ".so"
    ),
    LinuxEngine::class.java
)