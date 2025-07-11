package com.tecknobit.biometrik

import androidx.compose.runtime.Composable


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