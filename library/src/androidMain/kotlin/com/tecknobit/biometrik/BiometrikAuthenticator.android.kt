package com.tecknobit.biometrik

import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*

@Composable
@ExperimentalComposeApi
actual fun BiometrikAuthenticator(
    state: BiometrikState,
    appName: String,
    title: String,
    reason: String,
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
            val activity = LocalActivity.current as AppCompatActivity
            val biometricPromptManager = remember {
                BiometricPromptManager(
                    title = title,
                    reason = reason,
                    activity = activity
                )
            }
            val biometricResult by biometricPromptManager.promptResults.collectAsState(
                initial = null
            )
            LaunchedEffect(state.authAttemptsTrigger.value) {
                biometricPromptManager.show()
            }
            handleAuthenticationResult(
                state = state,
                authenticationResult = biometricResult,
                onSuccess = onSuccess,
                onFailure = onFailure,
                onHardwareUnavailable = onHardwareUnavailable,
                onFeatureUnavailable = onFeatureUnavailable,
                onAuthenticationNotSet = onAuthenticationNotSet
            )
        }
    )
}