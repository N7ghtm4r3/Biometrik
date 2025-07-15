package com.tecknobit.biometrik

import androidx.compose.runtime.*
import com.sun.jna.WString
import com.tecknobit.biometrik.engines.NativeEngine
import com.tecknobit.biometrik.enums.AuthenticationResult
import com.tecknobit.biometrik.enums.AuthenticationResult.Companion.toAuthenticationResult

/**
 * Component used to perform the bio-authentication to authorize the user.
 *
 * It under the hood uses the native APIs provided by the different OSs:
 * - On `Windows` uses the [Windows Hello](https://learn.microsoft.com/en-us/windows/apps/develop/security/windows-hello) APIs
 * - On `Linux` uses the [Polkit](https://www.freedesktop.org/software/polkit/docs/latest/index.html) APIs
 * - On`MacOs` uses the [local authentication](https://developer.apple.com/documentation/localauthentication) APIs
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
            val nativeEngine = remember { NativeEngine.getInstance() }
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