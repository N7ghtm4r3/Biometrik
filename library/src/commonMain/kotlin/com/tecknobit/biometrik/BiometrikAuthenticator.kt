package com.tecknobit.biometrik

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import com.tecknobit.biometrik.enums.AuthenticationResult
import com.tecknobit.biometrik.enums.AuthenticationResult.*

@Composable
@ExperimentalComposeApi
expect fun BiometrikAuthenticator(
    state: BiometrikState = rememberBiometrikState(),
    appName: String,
    title: String,
    reason: String,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit = onSuccess,
    onFeatureUnavailable: @Composable () -> Unit = onSuccess,
    onAuthenticationNotSet: @Composable () -> Unit = onSuccess,
)

@Composable
@ExperimentalComposeApi
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
@ExperimentalComposeApi
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