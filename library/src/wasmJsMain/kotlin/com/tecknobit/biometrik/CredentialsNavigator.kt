package com.tecknobit.biometrik

import kotlin.js.Promise

internal external object CredentialsNavigator {

    fun create(
        publicKey: JsAny,
    ): Promise<PublicKeyCredential>

    fun get(
        publicKey: JsAny,
    ): Promise<PublicKeyCredential>

}