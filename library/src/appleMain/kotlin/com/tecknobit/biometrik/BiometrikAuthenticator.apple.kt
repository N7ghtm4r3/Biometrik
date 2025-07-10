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
            memScoped {
                val context = LAContext()
                val errorPointer = alloc<ObjCObjectVar<NSError?>>()
                val canEvaluate = context.canEvaluatePolicy(
                    policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                    error = errorPointer.ptr
                )
                if (canEvaluate) {
                    if (context.biometryType == LABiometryTypeNone)
                        onHardwareUnavailable()
                    else {
                        context.evaluatePolicy(
                            policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                            localizedReason = reason
                        ) { success, _ ->
                            if (success) {
                                alreadyAuthenticated = true
                                onSuccess()
                            } else
                                onFailure()
                        }
                    }
                } else {
                    val errorCode = errorPointer.value?.code
                    when (errorCode) {
                        LAErrorBiometryNotEnrolled -> onAuthenticationNotSet()
                        LAErrorBiometryNotAvailable -> onFeatureUnavailable()
                        LAErrorPasscodeNotSet -> onAuthenticationNotSet()
                        else -> onFailure()
                    }
                }
            }
        }
    )
}