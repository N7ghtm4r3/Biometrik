@file:OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)

package com.tecknobit.biometrik

import androidx.compose.runtime.Composable
import kotlinx.cinterop.*
import platform.Foundation.NSError
import platform.LocalAuthentication.*

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
            val context = LAContext()
            val errorPointer = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
            val canEvaluate = context.canEvaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                error = errorPointer.ptr
            )
            if (canEvaluate) {
                nativeHeap.free(errorPointer)
                if (context.biometryType == LABiometryTypeNone) {
                    validAuthenticationAttempt()
                    onHardwareUnavailable()
                } else {
                    context.evaluatePolicy(
                        policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                        localizedReason = reason
                    ) { success, _ ->
                        if (success) {
                            validAuthenticationAttempt()
                            onSuccess()
                        } else
                            onFailure()
                    }
                }
            } else {
                val errorCode = errorPointer.value?.code
                nativeHeap.free(errorPointer)
                when (errorCode) {
                    LAErrorBiometryNotEnrolled -> {
                        validAuthenticationAttempt()
                        onAuthenticationNotSet()
                    }

                    LAErrorBiometryNotAvailable -> {
                        validAuthenticationAttempt()
                        onFeatureUnavailable()
                    }

                    LAErrorPasscodeNotSet -> {
                        validAuthenticationAttempt()
                        onAuthenticationNotSet()
                    }

                    else -> onFailure()
                }
            }
        }
    )
}