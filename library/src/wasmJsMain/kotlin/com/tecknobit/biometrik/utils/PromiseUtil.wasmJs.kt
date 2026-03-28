@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.biometrik.utils

import kotlinx.coroutines.await
import kotlin.js.Promise

/**
 * Wrapper method to use await correctly a [Promise]
 *
 * @return the result of the promise as [T]
 *
 * @param T the type the promise have to return when resolved
 */
actual suspend fun <T : JsAny?> Promise<T>.await(): T {
    return this.await()
}