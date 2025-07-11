package com.tecknobit.biometrik

import androidx.compose.runtime.*
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
                val available: JsBoolean = PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable().await()
                bioAuthAvailable = available.toBoolean()
            }
            bioAuthAvailable?.let { available ->
                if (available) {
                    val credentialsNavigator = credentialsNavigator()
                    val challenge = Uint8Array(32)
                    loadChallenge(
                        challenge = challenge
                    )
                    val publicKey = createPublicKey(
                        challenge = challenge,
                        appName = appName,
                        appId = TextEncoder().encode(
                            input = appName
                        )
                    )
                    LaunchedEffect(Unit) {
                        val a = credentialsNavigator.create(
                            publicKey = publicKey
                        ).await<PublicKeyCredential>()
                        println(a.id)
                    }
                    onSuccess()
                } else {
                    onFeatureUnavailable()
                }
            }
        }
    )
//    val challenge = Uint8Array(32)
//    loadUInt8Array(
//        array = challenge
//    )
//    val userId = Uint8Array(16)
//    loadUInt8Array(
//        array = userId
//    )
//    val publicKey = createPublicKey(
//        challenge = challenge,
//        appName = window.navigator.appName,
//        userId = userId
//    )
//    registerPublicKey(
//        publicKey = publicKey
//    )


//    var authenticated :Boolean? by remember { mutableStateOf(null) }
//    try {
//        requestAuth(
//            options = options
//        )
//        authenticated = true
//    } catch (e: Throwable) {
//        val error = e.toJsReference()
//        println(error)
//        authenticated = false
//    }
//    authenticated?.let { success ->
//        if(success) {
//            onSuccess()
//        } else {
//            onFailure()
//        }
//    }
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

private fun credentialsNavigator(): CredentialsNavigator = js("window.navigator.credentials")

private fun registerPublicKey(
    publicKey: JsAny,
): Unit = js(
    """
       {
           const cred = window.navigator.credentials.create(publicKey).await();
           console.log(cred.id);
       }
    """
)

