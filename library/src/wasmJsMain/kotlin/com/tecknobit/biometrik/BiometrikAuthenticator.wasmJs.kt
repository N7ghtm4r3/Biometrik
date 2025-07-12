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
                    val credentialsNavigator = credentialsNavigator()
                    val localStorage = KMPrefs(
                        path = appName
                    )
                    val challenge = Uint8Array(32)
                    loadChallenge(
                        challenge = challenge
                    )
                    val keyId = localStorage.retrieveString(
                        key = appName
                    )
                    val publicKey: JsAny
                    if (keyId == null) {
                        publicKey = createPublicKey(
                            challenge = challenge,
                            appName = appName,
                            appId = TextEncoder().encode(
                                input = appName
                            )
                        )
                        LaunchedEffect(Unit) {
                            try {
                                val publicKeyCredential = credentialsNavigator.create(
                                    publicKey = publicKey
                                ).await<PublicKeyCredential>()
                                localStorage.storeString(
                                    key = appName,
                                    value = publicKeyCredential.id.toString()
                                )
                            } catch (e: JsException) {
                            }
                        }
                    } else {
                        publicKey = obtainPublicKey(
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
                } else {
                    onFeatureUnavailable()
                    state.validAuthenticationAttempt()
                }
            }
        }
    )
}

private fun credentialsNavigator(): CredentialsNavigator = js("window.navigator.credentials")

private fun loadChallenge(
    challenge: Uint8Array,
): Unit = js(
    """
        {
            window.crypto.getRandomValues(challenge)
        }
    """
)

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
               userVerification: "preferred"
            },
            timeout: 60000,
            attestation: "none"
        }
    })
    """
)

private fun obtainPublicKey(
    keyId: Uint8Array,
    challenge: Uint8Array,
): JsAny = js(
    """
    ({
        publicKey: {
            challenge: challenge,
            timeout: 60000,
            userVerification: "preferred",
            allowCredentials: [{
                type: "public-key",
                id: keyId
            }]
        }
    })
    """
)