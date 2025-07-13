package com.tecknobit.biometrik

import androidx.compose.runtime.*
import com.sun.jna.WString
import com.tecknobit.biometrik.enums.AuthenticationResult
import com.tecknobit.biometrik.enums.AuthenticationResult.Companion.toAuthenticationResult

@Composable
@ExperimentalComposeApi
actual fun BiometrikAuthenticator(
    state: BiometrikState,
    appName: String,
    title: String,
    reason: String,
    requestOnFirstOpenOnly: Boolean,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit,
    onFeatureUnavailable: @Composable () -> Unit,
    onAuthenticationNotSet: @Composable () -> Unit,
) {
    authenticateIfNeeded(
        state = state,
        onSkip = onSuccess,
        onAuth = {
            val nativeEngine = NativeEngine.getInstance()
            var result: AuthenticationResult? by remember { mutableStateOf(null) }
            LaunchedEffect(state.authAttemptsTrigger.value) {
                result = nativeEngine.requestAuth(
                    reason = WString(reason)
                ).toAuthenticationResult()
            }
            handleAuthenticationResult(
                state = state,
                authenticationResult = result,
                onSuccess = onSuccess,
                onFailure = onFailure,
                onHardwareUnavailable = onHardwareUnavailable,
                onFeatureUnavailable = onFeatureUnavailable,
                onAuthenticationNotSet = onAuthenticationNotSet
            )
        }
    )
}