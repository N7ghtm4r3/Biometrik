package com.tecknobit.biometrik

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import com.tecknobit.biometrik.enums.AuthenticationResult
import com.tecknobit.biometrik.enums.AuthenticationResult.*

/**
 * Component used to perform the bio-authentication to authorize the user
 *
 * - `Android` under the hood uses the [BiometricPrompt](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) APIs
 * - `iOs` and native `macOs` under the hood uses the [local authentication](https://developer.apple.com/documentation/localauthentication) APIs
 * - `JVM` under the hood uses the native APIs provided by the different OSs:
 *    - On `Windows` uses the [Windows Hello](https://learn.microsoft.com/en-us/windows/apps/develop/security/windows-hello) APIs
 *    - On `Linux` uses the [Polkit](https://www.freedesktop.org/software/polkit/docs/latest/index.html) APIs
 *    - On`MacOs` uses the [local authentication](https://developer.apple.com/documentation/localauthentication) APIs
 * - `Web` under the hood uses the [WebAuthn](https://developer.mozilla.org/en-US/docs/Web/API/Web_Authentication_API) APIs
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

/**
 * Method used to perform the bio-authentication whether the [state] allows to do that
 *
 * @param state The state used to manage the lifecycle of the component
 * @param onSkip The callback to invoke whether the bio-authentication is to skip
 * @param onAuth The callback to invoke whether the bio-authentication is to perform
 */
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

/**
 * Method used to handle the result of an authentication request
 *
 * @param state The state used to manage the lifecycle of the component
 * @param authenticationResult The result of the authentication request
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