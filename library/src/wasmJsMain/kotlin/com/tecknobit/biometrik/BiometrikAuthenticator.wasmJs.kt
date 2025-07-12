package com.tecknobit.biometrik

import androidx.compose.runtime.*
import com.tecknobit.kmprefs.KMPrefs
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array

@Composable
actual fun BiometrikAuthenticator(
    state: BiometrikState,
    appName: String,
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
            var bioAuthAvailable: Boolean? by remember { mutableStateOf(null) }
            LaunchedEffect(Unit) {
                try {
                    val available: JsBoolean =
                        PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable().await()
                    bioAuthAvailable = available.toBoolean()
                } catch (e: JsException) {
                    bioAuthAvailable = false
                }
            }
            bioAuthAvailable?.let { available ->
                if (available) {
                    performBioAuth(
                        appName = appName,
                        onSuccess = onSuccess,
                        onFailure = onFailure
                    )
                } else {
                    onFeatureUnavailable()
                    state.validAuthenticationAttempt()
                }
            }
        }
    )
}

@Composable
private fun performBioAuth(
    appName: String,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
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
            challenge = challenge,
            appName = appName,
            credentialsNavigator = credentialsNavigator,
            localStorage = localStorage,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    } else {
        retrieveExistingKeyAndAuth(
            keyId = keyId,
            challenge = challenge,
            credentialsNavigator = credentialsNavigator,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}

private fun credentialsNavigator(): CredentialsNavigator = js("window.navigator.credentials")

@Composable
private fun registerNewKeyAndAuth(
    challenge: Uint8Array,
    appName: String,
    credentialsNavigator: CredentialsNavigator,
    localStorage: KMPrefs,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
) {
    val publicKey = createPublicKey(
        challenge = challenge,
        appName = appName,
        appId = TextEncoder().encode(
            input = appName
        )
    )
    var keyId = ""
    var keyRegisteredSuccessfully: Boolean? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        try {
            val publicKeyCredential = credentialsNavigator.create(
                publicKey = publicKey
            ).await<PublicKeyCredential>()
            keyId = publicKeyCredential.id.toString()
            localStorage.storeString(
                key = appName,
                value = keyId
            )
            keyRegisteredSuccessfully = true
        } catch (e: JsException) {
            keyRegisteredSuccessfully = false
        }
    }
    keyRegisteredSuccessfully?.let { success ->
        if (success) {
            retrieveExistingKeyAndAuth(
                keyId = keyId,
                challenge = challenge,
                credentialsNavigator = credentialsNavigator,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        } else
            onFailure()
    }
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
private fun retrieveExistingKeyAndAuth(
    keyId: String,
    challenge: Uint8Array,
    credentialsNavigator: CredentialsNavigator,
    onSuccess: @Composable () -> Unit,
    onFailure: @Composable () -> Unit,
) {
    val publicKey = obtainPublicKey(
        keyId = TextEncoder().encode(
            input = keyId
        ),
        challenge = challenge
    )
    var success: Boolean? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        try {
            credentialsNavigator.get(
                publicKey = publicKey
            ).await<PublicKeyCredential>()
            success = true
        } catch (e: JsException) {
            e.printStackTrace()
            success = false
        }
    }
    success?.let {
        if (it) {
            onSuccess()
        } else
            onFailure()
    }
}

private fun obtainPublicKey(
    keyId: Uint8Array,
    challenge: Uint8Array,
): JsAny = js(
    """
    ({
        publicKey: {
            challenge: challenge,
            timeout: 60000,
            userVerification: 'required',
            allowCredentials: [{
                type: 'public-key',
                id: keyId
            }],
            authenticatorSelection: {
               authenticatorAttachment: 'platform',
               userVerification: 'required'
            }
        }
    })
    """
)

private fun loadChallenge(
    challenge: Uint8Array,
): Unit = js(
    """
        {
            window.crypto.getRandomValues(challenge)
        }
    """
)