package com.tecknobit.biometrik.enums

enum class AuthenticationResult(
    val code: Int,
) {

    HARDWARE_UNAVAILABLE(2),

    FEATURE_UNAVAILABLE(4),

    AUTHENTICATION_FAILED(1),

    AUTHENTICATION_SUCCESS(0),

    AUTHENTICATION_NOT_SET(3);

    companion object {

        fun Int.toAuthenticationResult(): AuthenticationResult? {
            return when (this) {
                0 -> AUTHENTICATION_SUCCESS
                1 -> AUTHENTICATION_FAILED
                2 -> HARDWARE_UNAVAILABLE
                3 -> AUTHENTICATION_NOT_SET
                4 -> FEATURE_UNAVAILABLE
                else -> null
            }
        }

    }

}
