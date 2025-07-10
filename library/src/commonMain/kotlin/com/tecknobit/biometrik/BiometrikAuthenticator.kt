package com.tecknobit.biometrik

import androidx.compose.runtime.Composable

internal var alreadyAuthenticated = false

@Composable
expect fun BiometrikAuthenticator(
    title: String,
    reason: String,
    requestOnFirstOpenOnly: Boolean = true,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit = onSuccess,
    onFeatureUnavailable: @Composable () -> Unit = onSuccess,
    onAuthenticationNotSet: @Composable () -> Unit = onSuccess,
)

internal fun validAuthenticationAttempt() {
    alreadyAuthenticated = true
}

@Composable
internal fun authenticateIfNeeded(
    requestOnFirstOpenOnly: Boolean = true,
    onSkip: @Composable () -> Unit,
    onAuth: @Composable () -> Unit,
) {
    if (requestOnFirstOpenOnly && alreadyAuthenticated)
        onSkip()
    else
        onAuth()
}