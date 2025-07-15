/**
 * @file WindowsHelloEngine.h
 * @brief Header file for the authentication engine using Windows Hello.
 *
 * This file defines the detectable authentication statuses and provides
 * the interface for requesting user authentication through Windows Hello.
 *
 * It also manages the dynamic link library (DLL) import/export mechanism:
 * - When the `WINDOWSHELLO_EXPORTS` macro is defined (typically during DLL compilation),
 *   the symbols are exported using `__declspec(dllexport)`
 * - When the macro is not defined (e.g., when using the DLL in another project),
 *   the symbols are imported using `__declspec(dllimport)`
 *
 * This ensures that the same header can be used for both building the DLL and linking against it.
 *
 * @author
 * Tecknobit - N7ghm4r3
 *
 * @version 1.0.0
 */

#pragma once

#ifdef WINDOWSHELLO_EXPORTS
    #define DLL_API __declspec(dllexport)
#else
    #define DLL_API __declspec(dllimport)
#endif

extern "C" {

    /**
     * @enum AuthenticationResult
     * @brief Represents the detectable statuses handled by the component
     *
     * @author Tecknobit - N7ghtm4r3
     */
    enum AuthenticationResult {

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

    };

    /**
     * @brief Requests biometric or secure authentication from the user.
     *
     * Triggers the operating system’s authentication dialog, such as Windows Hello.
     *
     * @param reason The reason why it is requested the authentication
     *
     * @return An @ref AuthenticationResult indicating the result of the authentication request
     */
    DLL_API AuthenticationResult requestAuth(const wchar_t* reason);
}