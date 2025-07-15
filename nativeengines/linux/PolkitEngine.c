/**
 * @file PolkitEngine.c
 * @brief Implementation of the authentication engine using Polkit.
 *
 * It relies on Polkit APIs and D-Bus for secure authentication and authorization.
 *
 * The implementation handles the result mapping to the @ref AuthenticationResult enum,
 * ensuring a clear and consistent interface for consumers of the shared library
 *
 * @author
 * Tecknobit - N7ghm4r3
 *
 * @version 1.0.0
 */

#include <polkit/polkit.h>
#include <glib.h>
#include <stdio.h>
#include <unistd.h>

/**
 * @enum AuthenticationResult
 * @brief Represents the detectable statuses handled by the component
 *
 * @author Tecknobit - N7ghtm4r3
 */
typedef enum {

    /**
     * This status indicates the device has not the physical hardware to perform the
     * bio-authentication or is not currently available to serve the request
     */
    HARDWARE_UNAVAILABLE = 2,

    /**
     * This status indicates the feature of the bio-authentication is not provided by the device,
     * or it has been disabled following internal policies
     */
    FEATURE_UNAVAILABLE = 4,

    /**
     * This status indicates that authentication has failed either due to errors during the request
     * or because the result was actually unauthorized
     */
    AUTHENTICATION_FAILED = 1,

    /**
     * This status indicates that authentication request has been successful
     */
    AUTHENTICATION_SUCCESS = 0,

    /**
     * This status indicates the authentication is not actually set by the user, so cannot be
     * performed any type of authentication
     */
    AUTHENTICATION_NOT_SET = 3

} AuthenticationResult;

/**
 * @brief Requests biometric or secure authentication from the user.
 *
 * Triggers the operating system’s authentication dialog using `Polkit`
 *
 * @param reason The reason why it is requested the authentication
 *
 * @return An @ref AuthenticationResult indicating the result of the authentication request
 */
__attribute__((visibility("default")))
AuthenticationResult requestAuth(const wchar_t* reason) {
    GError *error = NULL;
    PolkitAuthority *authority = polkit_authority_get_sync(NULL, &error);
    if (!authority) {
        if (error && g_error_matches(error, G_DBUS_ERROR, G_DBUS_ERROR_SERVICE_UNKNOWN))
            return HARDWARE_UNAVAILABLE;
        else
            return FEATURE_UNAVAILABLE;
    }
    PolkitSubject *subject = polkit_unix_process_new(getpid());
    PolkitAuthorizationResult *result = polkit_authority_check_authorization_sync(
        authority,
        subject,
        "org.freedesktop.policykit.exec",
        NULL,
        POLKIT_CHECK_AUTHORIZATION_FLAGS_ALLOW_USER_INTERACTION,
        NULL,
        &error
    );
    if (!result) {
        g_object_unref(subject);
        g_object_unref(authority);
        return AUTHENTICATION_NOT_SET;
    }
    int authorized = polkit_authorization_result_get_is_authorized(result);
    g_object_unref(result);
    g_object_unref(subject);
    g_object_unref(authority);
    return authorized ? AUTHENTICATION_SUCCESS : AUTHENTICATION_FAILED;
}