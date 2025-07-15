@file:OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)

package com.tecknobit.biometrik

import androidx.compose.runtime.*
import kotlinx.cinterop.*
import platform.Foundation.NSError
import platform.LocalAuthentication.*

/**
 * Component used to perform the bio-authentication to authorize the user.
 *
 * It under the hood uses the [local authentication](https://developer.apple.com/documentation/localauthentication) APIs
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
            val context = LAContext()
            val errorPointer = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
            val canEvaluate = context.canEvaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                error = errorPointer.ptr
            )
            if (canEvaluate) {
                authEvaluated(
                    state = state,
                    context = context,
                    errorPointer = errorPointer,
                    reason = reason,
                    onSuccess = onSuccess,
                    onFailure = onFailure,
                    onHardwareUnavailable = onHardwareUnavailable
                )
            } else {
                authNotEvaluated(
                    state = state,
                    errorPointer = errorPointer,
                    onFailure = onFailure,
                    onFeatureUnavailable = onFeatureUnavailable,
                    onAuthenticationNotSet = onAuthenticationNotSet
                )
            }
        }
    )
}

/**
 * Functional method used to perform the authentication after it has been evaluated
 *
 * @param state The state used to manage the lifecycle of the component
 * @param context The instance used to perform the local authentication request
 * @param errorPointer The pointer used to retrieve the error information, thrown whether the request to authenticate fails
 * @param reason The reason why it is requested the authentication
 * @param onSuccess The callback to invoke when the authentication was successful
 * @param onFailure The callback to invoke when the authentication was failed
 * @param onHardwareUnavailable The fallback to invoke when the device has not the required hardware to perform
 * a bio-authentication or cannot currently serve the request. By default, is not considered an authentication error so
 * will be invoked [onSuccess] if it has been not customized
 */
@Composable
@ExperimentalComposeApi
private fun authEvaluated(
    state: BiometrikState,
    context: LAContext,
    errorPointer: ObjCObjectVar<NSError?>,
    reason: String,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit,
) {
    nativeHeap.free(errorPointer)
    if (context.biometryType == LABiometryTypeNone) {
        state.validAuthenticationAttempt()
        onHardwareUnavailable()
    } else {
        var authenticatedSuccessfully: Boolean? by remember { mutableStateOf(null) }
        LaunchedEffect(state.authAttemptsTrigger.value) {
            authenticatedSuccessfully = null
            context.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = reason
            ) { success, _ ->
                authenticatedSuccessfully = success
            }
        }
        authenticatedSuccessfully?.let { success ->
            if (success) {
                state.validAuthenticationAttempt()
                onSuccess()
            } else
                onFailure()
        }
    }
}

/**
 * Functional method used to handle the failed authentication result
 *
 * @param state The state used to manage the lifecycle of the component
 * @param errorPointer The pointer used to retrieve the error information
 * @param onFailure The callback to invoke when the authentication was failed
 * @param onFeatureUnavailable The fallback to invoke when the feature of the bio-authentication is not provided
 * by the device, or it has been disabled following internal policies. By default, is not considered an authentication error
 * so will be invoked [onSuccess] if it has been not customized
 * @param onAuthenticationNotSet The fallback to invoke when the authentication is not actually set by the user,
 * so cannot be performed any type of authentication. By default, is not considered an authentication error
 * so will be invoked [onSuccess] if it has been not customized
 */
@Composable
@ExperimentalComposeApi
private fun authNotEvaluated(
    state: BiometrikState,
    errorPointer: ObjCObjectVar<NSError?>,
    onFailure: @Composable () -> Unit,
    onFeatureUnavailable: @Composable () -> Unit,
    onAuthenticationNotSet: @Composable () -> Unit,
) {
    val errorCode = errorPointer.value?.code
    nativeHeap.free(errorPointer)
    when (errorCode) {
        LAErrorBiometryNotEnrolled -> {
            state.validAuthenticationAttempt()
            onAuthenticationNotSet()
        }

        LAErrorBiometryNotAvailable -> {
            state.validAuthenticationAttempt()
            onFeatureUnavailable()
        }

        LAErrorPasscodeNotSet -> {
            state.validAuthenticationAttempt()
            onAuthenticationNotSet()
        }

        else -> onFailure()
    }
}