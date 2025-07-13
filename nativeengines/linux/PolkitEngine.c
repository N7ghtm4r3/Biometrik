#include <polkit/polkit.h>
#include <glib.h>
#include <stdio.h>
#include <unistd.h>

typedef enum {

    AUTHENTICATION_SUCCESS = 0,

    AUTHENTICATION_FAILED = 1,

    HARDWARE_UNAVAILABLE = 2,

    AUTHENTICATION_NOT_SET = 3,

    FEATURE_UNAVAILABLE = 4

} AuthenticationResult;

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