package com.tecknobit.biometrik.engines

import com.sun.jna.Library
import com.sun.jna.Native

/**
 * The `MacOsEngine` interface allow to access to the native API to request the bio-authentication using
 * the [local authentication](https://developer.apple.com/documentation/localauthentication) APIs
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see Library
 * @see NativeEngine
 */
internal interface MacOsEngine : NativeEngine

/**
 * `macOsEngine` instance of the [MacOsEngine] engine
 */
internal val macOsEngine = Native.load(
    extractDllAbsolutePath(
        dllName = "LocalAuthenticationEngine",
        suffix = ".dylib"
    ),
    MacOsEngine::class.java
)