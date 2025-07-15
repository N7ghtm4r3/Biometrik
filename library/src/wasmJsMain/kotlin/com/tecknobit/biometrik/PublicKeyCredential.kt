package com.tecknobit.biometrik

import kotlin.js.Promise

/**
 * The `PublicKeyCredential` class maps the native [PublicKeyCredential](https://developer.mozilla.org/en-US/docs/Web/API/PublicKeyCredential/response)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
internal external object PublicKeyCredential : JsAny {

    /**
     * `id` The identifier of the key
     */
    val id: JsString

    /**
     * Method used to resolve if a user-verifying platform authenticator is present.
     *
     * Official documentation [here](https://developer.mozilla.org/en-US/docs/Web/API/PublicKeyCredential/isUserVerifyingPlatformAuthenticatorAvailable_static)
     *
     * @return a promise with the verification as [Promise] of [JsBoolean]
     */
    fun isUserVerifyingPlatformAuthenticatorAvailable(): Promise<JsBoolean>
}