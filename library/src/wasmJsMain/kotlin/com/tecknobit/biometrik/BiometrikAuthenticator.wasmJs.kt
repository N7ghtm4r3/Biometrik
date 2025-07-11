package com.tecknobit.biometrik

import androidx.compose.runtime.Composable
import org.khronos.webgl.Uint8Array

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
    val challenge = Uint8Array(32)
    loadChallenge(
        challenge = challenge
    )
    val publicKey = createPublicKey(
        challenge = challenge
    )
    val options = createOptions(
        publicKey = publicKey
    )
    requestAuth(
        options = options
    )
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
): JsAny = js("({ challenge : challenge.buffer, timeout : 60000, userVerification : 'preferred' })")

private fun createOptions(
    publicKey: JsAny,
): JsAny = js("({ publicKey : publicKey})")

private fun requestAuth(
    options: JsAny,
): Unit = js(
    """
        {
            window.navigator.credentials.get(options).await()
        }
    """
)
