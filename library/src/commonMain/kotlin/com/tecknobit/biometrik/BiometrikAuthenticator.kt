package com.tecknobit.biometrik

import androidx.compose.runtime.Composable
import com.tecknobit.biometrik.enums.AuthenticationResult
import com.tecknobit.biometrik.enums.AuthenticationResult.*


@Composable
expect fun BiometrikAuthenticator(
    state: BiometrikState = rememberBiometrikState(),
    appName: String,
    title: String,
    reason: String,
    requestOnFirstOpenOnly: Boolean = true,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit = onSuccess,
    onFeatureUnavailable: @Composable () -> Unit = onSuccess,
    onAuthenticationNotSet: @Composable () -> Unit = onSuccess,
)

@Composable
internal fun authenticateIfNeeded(
    state: BiometrikState,
    onSkip: @Composable () -> Unit,
    onAuth: @Composable () -> Unit,
) {
    if (state.isAuthToSkip())
        onSkip()
    else
        onAuth()
}

@Composable
internal fun handleAuthenticationResult(
    state: BiometrikState,
    authenticationResult: AuthenticationResult?,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit = onSuccess,
    onFeatureUnavailable: @Composable () -> Unit = onSuccess,
    onAuthenticationNotSet: @Composable () -> Unit = onSuccess,
) {
    authenticationResult?.let { result ->
        when (result) {
            HARDWARE_UNAVAILABLE -> {
                state.validAuthenticationAttempt()
                onHardwareUnavailable()
            }

            FEATURE_UNAVAILABLE -> {
                state.validAuthenticationAttempt()
                onFeatureUnavailable()
            }

            AUTHENTICATION_FAILED -> onFailure()
            AUTHENTICATION_SUCCESS -> {
                state.validAuthenticationAttempt()
                onSuccess()
            }

            AUTHENTICATION_NOT_SET -> {
                state.validAuthenticationAttempt()
                onAuthenticationNotSet()
            }
        }
    }
}