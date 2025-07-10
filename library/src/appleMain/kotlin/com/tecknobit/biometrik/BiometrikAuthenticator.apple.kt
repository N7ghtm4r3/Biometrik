@file:OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)

package com.tecknobit.biometrik

import androidx.compose.runtime.*
import kotlinx.cinterop.*
import platform.Foundation.NSError
import platform.LocalAuthentication.*

@Composable
actual fun BiometrikAuthenticator(
    state: BiometrikState,
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

@Composable
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

@Composable
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