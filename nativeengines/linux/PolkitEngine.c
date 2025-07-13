#include <polkit/polkit.h>
#include <glib.h>
#include <stdio.h>
#include <unistd.h>

__attribute__((visibility("default")))
int requestAuth(const wchar_t* reason) {
    GError *error = NULL;
    PolkitAuthority *authority = polkit_authority_get_sync(NULL, &error);
    if (!authority) {
        g_printerr("Errore polkit authority: %s\n", error->message);
        g_error_free(error);
        return -1;
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
        g_printerr("Errore autorizzazione: %s\n", error->message);
        g_error_free(error);
        g_object_unref(subject);
        g_object_unref(authority);
        return -1;
    }
    int authorized = polkit_authorization_result_get_is_authorized(result);
    g_object_unref(result);
    g_object_unref(subject);
    g_object_unref(authority);
    return authorized ? 1 : 0;
}