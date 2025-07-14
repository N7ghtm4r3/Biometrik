package com.tecknobit.biometrik

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import kotlin.random.Random

/**
 * Method used to create a state to attach to the [BiometrikAuthenticator] component to manage its lifecycle
 *
 * @param requestOneTimeOnly Whether the authentication must be requested just once or multiple times
 *
 * @return the state as [BiometrikState]
 */
@Composable
@ExperimentalComposeApi
fun rememberBiometrikState(
    requestOneTimeOnly: Boolean = true,
): BiometrikState {
    return rememberSaveable(
        saver = BiometrikStateSaver
    ) {
        BiometrikState(
            requestOneTimeOnly = requestOneTimeOnly
        )
    }
}

/**
 * The `BiometrikState` class allow to manage the lifecycle of a [BiometrikAuthenticator] component
 *
 * @property requestOneTimeOnly Whether the authentication must be requested just once or multiple times
 * @property alreadyAuthenticated Flag indicates whether a successful authentication has been already performed with
 * this state
 * @param initial Initial value of the trigger that handles retry attempts, allowing the user to re-authenticate
 *
 * @author Tecknobit - N7ghtm4r3
 */
@ExperimentalComposeApi
class BiometrikState internal constructor(
    val requestOneTimeOnly: Boolean = true,
    internal var alreadyAuthenticated: Boolean = false,
    initial: Long = -1,
) {

    /**
     * `authAttemptsTrigger` trigger that handles retry attempts, allowing the user to re-authenticate
     */
    internal val authAttemptsTrigger: MutableState<Long> = mutableLongStateOf(initial)

    /**
     * Method used to allow the user to re-authenticate if allowed by the rules used to create the state.
     *
     * This method updates the [authAttemptsTrigger] value and triggers
     * the [androidx.compose.runtime.LaunchedEffect] attached to the [BiometrikAuthenticator] to allow the user
     * to re-authenticate
     */
    fun reAuth() {
        if (!requestOneTimeOnly || !alreadyAuthenticated)
            authAttemptsTrigger.value = Random.nextLong()
    }

    /**
     * Method used to valid an authentication attempt and to set the [alreadyAuthenticated] value on `true`
     */
    internal fun validAuthenticationAttempt() {
        alreadyAuthenticated = true
    }

    /**
     * Method used to check whether the authentication is to skip following the rules used to create the state
     *
     * @return whether the authentication is to skip as [Boolean]
     */
    internal fun isAuthToSkip() = requestOneTimeOnly && alreadyAuthenticated

}

/**
 * The `BiometrikStateSaver` class provides functionality to save and restore a [BiometrikState]
 * across recompositions, and to persist its value
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see Saver
 */
@ExperimentalComposeApi
internal object BiometrikStateSaver : Saver<BiometrikState, Array<Any>> {

    /**
     * Convert the restored value back to the original Class. If null is returned the value will not
     * be restored and would be initialized again instead.
     */
    override fun restore(
        value: Array<Any>,
    ): BiometrikState {
        return BiometrikState(
            requestOneTimeOnly = value[0] as Boolean,
            alreadyAuthenticated = value[1] as Boolean,
            initial = value[2] as Long
        )
    }

    /**
     * Convert the value into a saveable one. If null is returned the value will not be saved.
     */
    override fun SaverScope.save(
        value: BiometrikState,
    ): Array<Any> {
        return arrayOf(
            value.requestOneTimeOnly,
            value.alreadyAuthenticated,
            value.authAttemptsTrigger.value
        )
    }

}