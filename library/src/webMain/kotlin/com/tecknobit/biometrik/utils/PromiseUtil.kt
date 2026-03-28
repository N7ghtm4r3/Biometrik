@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.biometrik.utils

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.Promise

/**
 * Wrapper method to use await correctly a [Promise]
 *
 * @return the result of the promise as [T]
 *
 * @param T the type the promise have to return when resolved
 */
expect suspend fun <T : JsAny?> Promise<T>.await(): T