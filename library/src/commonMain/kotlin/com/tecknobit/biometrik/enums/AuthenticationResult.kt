package com.tecknobit.biometrik.enums

/**
 * `AuthenticationResult` are the detectable statuses handled by the component
 *
 * @property code The indicative code of the status
 *
 * @author Tecknobit - N7ghm4r3
 */
enum class AuthenticationResult(
    val code: Int,
) {

    /**
     * `HARDWARE_UNAVAILABLE` this status indicates the device has not the physical hardware to perform the
     * bio-authentication or is not currently available to serve the request
     */
    HARDWARE_UNAVAILABLE(2),

    /**
     * `FEATURE_UNAVAILABLE` this status indicates the feature of the bio-authentication is not provided by the device or
     * it has been disabled following internal policies
     */
    FEATURE_UNAVAILABLE(4),

    /**
     * `AUTHENTICATION_FAILED` this status indicates that authentication has failed either due to errors during the request
     * or because the result was actually unauthorized
     */
    AUTHENTICATION_FAILED(1),

    /**
     * `AUTHENTICATION_SUCCESS` this status indicates that authentication request has been successful
     */
    AUTHENTICATION_SUCCESS(0),

    /**
     * `AUTHENTICATION_NOT_SET` this status indicates the authentication is not actually set by the user, so cannot be
     * performed any type of authentication
     */
    AUTHENTICATION_NOT_SET(3);

    companion object {

        /**
         * Method used to convert an integer number to the related authentication result value
         *
         * @return the authentication result as nullable [AuthenticationResult], null when the integer value
         * is not a valid [AuthenticationResult.code]
         */
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
