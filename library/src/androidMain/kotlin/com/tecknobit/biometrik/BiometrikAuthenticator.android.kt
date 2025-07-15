package com.tecknobit.biometrik

import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*

/**
 * Component used to perform the bio-authentication to authorize the user.
 *
 * It under the hood uses the [BiometricPrompt](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) APIs
 *
 * @param state The state used to manage the lifecycle of the component
 * @param appName The name of the application where the authentication has been requested
 * @param reason The reason why it is requested the authentication
 * @param onSuccess The callback to invoke when the authentication was successful
 * @param onFailure The callback to invoke when the authentication was failed
 * @param onHardwareUnavailable The fallback to invoke when the device has not the required hardware to perform
 * a bio-authentication or cannot currently serve the request. By default, is not considered an authentication error so
 * will be invoked [onSuccess] if it has been not customized
 * @param onFeatureUnavailable The fallback to invoke when the feature of the bio-authentication is not provided
 * by the device, or it has been disabled following internal policies. By default, is not considered an authentication error
 * so will be invoked [onSuccess] if it has been not customized
 * @param onAuthenticationNotSet The fallback to invoke when the authentication is not actually set by the user,
 * so cannot be performed any type of authentication. By default, is not considered an authentication error
 * so will be invoked [onSuccess] if it has been not customized
 */
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