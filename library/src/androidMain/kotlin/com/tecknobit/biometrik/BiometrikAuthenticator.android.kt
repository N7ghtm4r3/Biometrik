package com.tecknobit.biometrik

import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import com.tecknobit.biometrik.enums.AuthenticationResult.*

@Composable
actual fun BiometrikAuthenticator(
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
        requestOnFirstOpenOnly = requestOnFirstOpenOnly,
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
            LaunchedEffect(Unit) {
                biometricPromptManager.show()
            }
            biometricResult?.let { result ->
                when (result) {
                    HARDWARE_UNAVAILABLE -> {
                        validAuthenticationAttempt()
                        onHardwareUnavailable()
                    }

                    FEATURE_UNAVAILABLE -> {
                        validAuthenticationAttempt()
                        onFeatureUnavailable()
                    }

                    AUTHENTICATION_FAILED -> {
                        onFailure()
                    }

                    AUTHENTICATION_SUCCESS -> {
                        validAuthenticationAttempt()
                        onSuccess()
                    }

                    AUTHENTICATION_NOT_SET -> {
                        validAuthenticationAttempt()
                        onAuthenticationNotSet()
                    }
                }
            }
        }
    )
}