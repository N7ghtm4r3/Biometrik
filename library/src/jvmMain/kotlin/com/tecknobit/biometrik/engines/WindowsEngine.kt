package com.tecknobit.biometrik.engines

import com.sun.jna.Library
import com.sun.jna.Native

/**
 * The `WindowsEngine` interface allow to access to the native API to request the bio-authentication using
 * the [Windows Hello](https://learn.microsoft.com/en-us/windows/apps/develop/security/windows-hello) APIs
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see Library
 * @see NativeEngine
 */
internal interface WindowsEngine : NativeEngine

/**
 * `windowsEngine` instance of the [WindowsEngine] engine
 */
internal val windowsEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "WindowsHelloEngine",
        suffix = ".dll"
    ),
    WindowsEngine::class.java
)