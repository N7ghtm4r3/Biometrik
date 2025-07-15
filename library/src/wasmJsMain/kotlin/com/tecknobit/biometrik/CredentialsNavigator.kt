package com.tecknobit.biometrik

import kotlin.js.Promise

/**
 * The `CredentialsNavigator` class maps the native [navigator.credentials](https://developer.mozilla.org/en-US/docs/Web/API/Credential_Management_API)
 * instance
 *
 * @author Tecknobit - N7ghtm4r3
 */
internal external object CredentialsNavigator {

    /**
     * Method used to create a new public key credential using the provided options.
     *
     * Official documentation [here](https://developer.mozilla.org/en-US/docs/Web/API/CredentialsContainer/create)
     *
     * @param publicKey An object containing the parameters for credential creation, following the `WebAuthn` specification
     *
     * @return the promise created after the key creation as [Promise] of [PublicKeyCredential]
     */
    fun create(
        publicKey: JsAny,
    ): Promise<PublicKeyCredential>

    /**
     * Method used to retrieve an existing key credential using the provided options.
     *
     * Official documentation [here](https://developer.mozilla.org/en-US/docs/Web/API/CredentialsContainer/get)
     *
     * @param publicKey An object containing the parameters for credential retrieval, following the `WebAuthn` specification
     *
     * @return the promise created after the key retrieval as [Promise] of [PublicKeyCredential]
     */
    fun get(
        publicKey: JsAny,
    ): Promise<PublicKeyCredential>

}