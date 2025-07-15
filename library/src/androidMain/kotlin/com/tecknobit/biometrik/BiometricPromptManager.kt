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

/**
 * The `BiometricPromptManager` class allows to perform the bio-authentication request on the `Android`'s devices managing
 * the prompt where the user can perform that authentication
 *
 * @property title The title to display on the prompt
 * @property reason The reason of the authentication request
 * @property activity The activity where the authentication request has been requested
 *
 * @author Tecknobit - N7ghtm4r3
 */
class BiometricPromptManager(
    private val title: String,
    private val reason: String,
    private val activity: AppCompatActivity,
) {

    companion object {

        /**
         * `ALLOWED_AUTHENTICATORS` The authenticators allowed to perform the authentication request
         */
        private const val ALLOWED_AUTHENTICATORS = BIOMETRIC_STRONG or DEVICE_CREDENTIAL

    }

    /**
     * `resultChannel` The channel where the result of the authentication request is sent
     */
    private val resultChannel = Channel<AuthenticationResult>(
        capacity = UNLIMITED
    )

    /**
     * `prompt` The instance which provides a way to display the authentication prompt and handles natively the result
     */
    private val prompt = BiometricPrompt(
        activity,
        object : BiometricPrompt.AuthenticationCallback() {

            /**
             * Called when an unrecoverable error has been encountered and authentication has stopped.
             *
             * <p>After this method is called, no further events will be sent for the current
             * authentication session.
             *
             * @param errorCode An integer ID associated with the error.
             * @param errString A human-readable string that describes the error.
             */
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                resultChannel.trySend(AUTHENTICATION_FAILED)
            }

            /**
             * Called when a biometric (e.g. fingerprint, face, etc.) is recognized, indicating that the
             * user has successfully authenticated.
             *
             * <p>After this method is called, no further events will be sent for the current
             * authentication session.
             *
             * @param result An object containing authentication-related data.
             */
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                resultChannel.trySend(AUTHENTICATION_SUCCESS)
            }

            /**
             * Called when a biometric (e.g. fingerprint, face, etc.) is presented but not recognized as
             * belonging to the user.
             */
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                resultChannel.trySend(AUTHENTICATION_FAILED)
            }
        }
    )

    /**
     * `promptResults` The flow state which receive and share the results of the authentication request
     */
    val promptResults = resultChannel.receiveAsFlow()

    /**
     * Method used to show the [prompt] and to send the result of the authentication request
     */
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

    /**
     * Method used to perform the authentication request and collect its result
     */
    private fun performAuth() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(reason)
            .setAllowedAuthenticators(ALLOWED_AUTHENTICATORS)
            .build()
        prompt.authenticate(promptInfo)
    }

}