package com.tecknobit.biometrik

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import kotlin.random.Random

@Composable
fun rememberBiometrikState(
    requestOnFirstOpenOnly: Boolean = true,
): BiometrikState {
    return rememberSaveable(
        saver = BiometrikStateSaver
    ) {
        BiometrikState(
            requestOnFirstOpenOnly = requestOnFirstOpenOnly
        )
    }
}

class BiometrikState internal constructor(
    val requestOnFirstOpenOnly: Boolean = true,
    internal var alreadyAuthenticated: Boolean = false,
    initial: Long = -1,
) {

    internal val authAttemptsTrigger: MutableState<Long> = mutableLongStateOf(initial)

    fun reAuth() {
        if (!requestOnFirstOpenOnly || !alreadyAuthenticated)
            authAttemptsTrigger.value = Random.nextLong()
    }

    internal fun validAuthenticationAttempt() {
        alreadyAuthenticated = true
    }

    internal inline fun isAuthToSkip() = requestOnFirstOpenOnly && alreadyAuthenticated

}

internal object BiometrikStateSaver : Saver<BiometrikState, Array<Any>> {

    override fun restore(
        value: Array<Any>,
    ): BiometrikState {
        return BiometrikState(
            requestOnFirstOpenOnly = value[0] as Boolean,
            alreadyAuthenticated = value[1] as Boolean,
            initial = value[2] as Long
        )
    }

    override fun SaverScope.save(
        value: BiometrikState,
    ): Array<Any> {
        return arrayOf(
            value.requestOnFirstOpenOnly,
            value.alreadyAuthenticated,
            value.authAttemptsTrigger.value
        )
    }

}