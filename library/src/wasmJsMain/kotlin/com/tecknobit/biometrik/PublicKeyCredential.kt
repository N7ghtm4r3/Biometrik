package com.tecknobit.biometrik

import kotlin.js.Promise

internal external object PublicKeyCredential : JsAny {

    val id: JsString

    fun isUserVerifyingPlatformAuthenticatorAvailable(): Promise<JsBoolean>
}