package com.tecknobit.biometrik

import androidx.compose.runtime.*
import com.tecknobit.biometrik.enums.AuthenticationResult
import com.tecknobit.biometrik.enums.AuthenticationResult.*
import com.tecknobit.kmprefs.KMPrefs
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array

private const val NOT_SUPPORTED_ERROR = "NotSupportedError"

private const val NOT_PRESENT_ERROR = "NotPresentError"

private const val INVALID_STATE_ERROR = "InvalidStateError"

/**
 * Component used to perform the bio-authentication to authorize the user.
 *
 * It under the hood uses the [WebAuthn](https://developer.mozilla.org/en-US/docs/Web/API/Web_Authentication_API) APIs
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
            var bioAuthAvailable: Boolean? by remember { mutableStateOf(null) }
            LaunchedEffect(Unit) {
                try {
                    val available: JsBoolean =
                        PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable().await()
                    bioAuthAvailable = available.toBoolean()
                } catch (_: JsException) {
                    bioAuthAvailable = false
                }
            }
            bioAuthAvailable?.let { available ->
                if (available) {
                    performBioAuth(
                        state = state,
                        appName = appName,
                        onSuccess = onSuccess,
                        onFailure = onFailure,
                        onHardwareUnavailable = onHardwareUnavailable,
                        onFeatureUnavailable = onFeatureUnavailable,
                        onAuthenticationNotSet = onAuthenticationNotSet
                    )
                } else {
                    state.validAuthenticationAttempt()
                    onFeatureUnavailable()
                }
            }
        }
    )
}

@Composable
@ExperimentalComposeApi
private fun performBioAuth(
    state: BiometrikState,
    appName: String,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit,
    onFeatureUnavailable: @Composable () -> Unit,
    onAuthenticationNotSet: @Composable () -> Unit,
) {
    val credentialsNavigator = credentialsNavigator()
    val challenge = Uint8Array(32)
    loadChallenge(
        challenge = challenge
    )
    val localStorage = KMPrefs(
        path = appName
    )
    val keyId = localStorage.retrieveString(
        key = appName
    )
    if (keyId == null) {
        registerNewKeyAndAuth(
            state = state,
            challenge = challenge,
            appName = appName,
            credentialsNavigator = credentialsNavigator,
            localStorage = localStorage,
            onSuccess = onSuccess,
            onFailure = onFailure,
            onHardwareUnavailable = onHardwareUnavailable,
            onFeatureUnavailable = onFeatureUnavailable,
            onAuthenticationNotSet = onAuthenticationNotSet
        )
    } else {
        retrieveExistingKeyAndAuth(
            state = state,
            challenge = challenge,
            credentialsNavigator = credentialsNavigator,
            onSuccess = onSuccess,
            onFailure = onFailure,
            onHardwareUnavailable = onHardwareUnavailable,
            onFeatureUnavailable = onFeatureUnavailable,
            onAuthenticationNotSet = onAuthenticationNotSet
        )
    }
}

private fun loadChallenge(
    challenge: Uint8Array,
): Unit = js(
    """
        {
            window.crypto.getRandomValues(challenge)
        }
    """
)

private fun credentialsNavigator(): CredentialsNavigator = js("window.navigator.credentials")

@Composable
@ExperimentalComposeApi
private fun registerNewKeyAndAuth(
    state: BiometrikState,
    challenge: Uint8Array,
    appName: String,
    credentialsNavigator: CredentialsNavigator,
    localStorage: KMPrefs,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit,
    onFeatureUnavailable: @Composable () -> Unit,
    onAuthenticationNotSet: @Composable () -> Unit,
) {
    val publicKey = createPublicKey(
        challenge = challenge,
        appName = appName,
        appId = TextEncoder().encode(
            input = appName
        )
    )
    handleAuth(
        state = state,
        authRoutine = {
            val publicKeyCredential = credentialsNavigator.create(
                publicKey = publicKey
            ).await<PublicKeyCredential>()
            localStorage.storeString(
                key = appName,
                value = publicKeyCredential.id.toString()
            )
        },
        onSuccess = onSuccess,
        onFailure = onFailure,
        onHardwareUnavailable = onHardwareUnavailable,
        onFeatureUnavailable = onFeatureUnavailable,
        onAuthenticationNotSet = onAuthenticationNotSet
    )
}

private fun createPublicKey(
    challenge: Uint8Array,
    appName: String,
    appId: Uint8Array,
): JsAny = js(
    """
    ({
        publicKey: {
            challenge: challenge,
            rp: { name: appName },
            user: {
               id: appId,
               name: appName,
               displayName: appName
            },
            pubKeyCredParams: [{ type: "public-key", alg: -7 }],
            authenticatorSelection: {
               authenticatorAttachment: 'platform',
               userVerification: 'required'
            },
            timeout: 60000,
            attestation: "none"
        }
    })
    """
)

@Composable
@ExperimentalComposeApi
private fun retrieveExistingKeyAndAuth(
    state: BiometrikState,
    challenge: Uint8Array,
    credentialsNavigator: CredentialsNavigator,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit,
    onFeatureUnavailable: @Composable () -> Unit,
    onAuthenticationNotSet: @Composable () -> Unit,
) {
    val publicKey = obtainPublicKey(
        challenge = challenge
    )
    handleAuth(
        state = state,
        authRoutine = {
            credentialsNavigator.get(
                publicKey = publicKey
            ).await<PublicKeyCredential>()
        },
        onSuccess = onSuccess,
        onFailure = onFailure,
        onHardwareUnavailable = onHardwareUnavailable,
        onFeatureUnavailable = onFeatureUnavailable,
        onAuthenticationNotSet = onAuthenticationNotSet
    )
}

@Composable
@ExperimentalComposeApi
private fun handleAuth(
    state: BiometrikState,
    authRoutine: suspend () -> Unit,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
    onHardwareUnavailable: @Composable () -> Unit,
    onFeatureUnavailable: @Composable () -> Unit,
    onAuthenticationNotSet: @Composable () -> Unit,
) {
    var result: AuthenticationResult? by remember { mutableStateOf(null) }
    LaunchedEffect(state.authAttemptsTrigger.value) {
        try {
            authRoutine()
            result = AUTHENTICATION_SUCCESS
        } catch (e: JsException) {
            val error = e.thrownValue
            result = if (error == null)
                AUTHENTICATION_FAILED
            else {
                val errorString = error.toString()
                if (errorString.startsWith(NOT_PRESENT_ERROR))
                    AUTHENTICATION_NOT_SET
                else if (errorString.startsWith(NOT_SUPPORTED_ERROR) ||
                    errorString.startsWith(INVALID_STATE_ERROR)
                ) {
                    HARDWARE_UNAVAILABLE
                } else
                    AUTHENTICATION_FAILED
            }
        }
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

private fun obtainPublicKey(
    challenge: Uint8Array,
): JsAny = js(
    """
    ({
        publicKey: {
            challenge: challenge,
            timeout: 60000,
            userVerification: 'required'
        }
    })
    """
)