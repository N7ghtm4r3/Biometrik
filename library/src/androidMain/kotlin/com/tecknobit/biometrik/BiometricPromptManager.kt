package com.tecknobit.biometrik

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.*
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import com.tecknobit.biometrik.enums.AuthenticationResult
import com.tecknobit.biometrik.enums.AuthenticationResult.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricPromptManager(
    private val title: String,
    private val reason: String,
    private val activity: AppCompatActivity,
) {

    companion object {

        private const val ALLOWED_AUTHENTICATORS = BIOMETRIC_STRONG or DEVICE_CREDENTIAL

    }

    private val resultChannel = Channel<AuthenticationResult>(
        capacity = UNLIMITED
    )

    private val prompt = BiometricPrompt(
        activity,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                resultChannel.trySend(AUTHENTICATION_FAILED)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                resultChannel.trySend(AUTHENTICATION_SUCCESS)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                resultChannel.trySend(AUTHENTICATION_FAILED)
            }
        }
    )

    val promptResults = resultChannel.receiveAsFlow()

    fun show() {
        val manager = from(activity)
        when (manager.canAuthenticate(ALLOWED_AUTHENTICATORS)) {
            BIOMETRIC_SUCCESS, BIOMETRIC_STATUS_UNKNOWN -> performAuth()
            BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(HARDWARE_UNAVAILABLE)
                return
            }

            BIOMETRIC_ERROR_NO_HARDWARE, BIOMETRIC_ERROR_UNSUPPORTED -> {
                resultChannel.trySend(FEATURE_UNAVAILABLE)
                return
            }

            BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(AUTHENTICATION_NOT_SET)
                return
            }

            BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                resultChannel.trySend(AUTHENTICATION_FAILED)
                return
            }
        }
    }

    private fun performAuth() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(reason)
            .setAllowedAuthenticators(ALLOWED_AUTHENTICATORS)
            .build()
        prompt.authenticate(promptInfo)
    }

}