/**
 * @file WindowsHelloEngine.cpp
 * @brief Implementation of the authentication engine using Windows Hello.
 *
 * This source file implements the authentication logic exposed in the corresponding
 * header file (@ref WindowsHelloEngine.h), including the interface for invoking
 * Windows Hello to request user authentication.
 *
 * It relies on Windows native APIs (e.g., Windows Biometric Framework, Win32, etc.)
 * to perform secure biometric or PIN-based authentication.
 *
 * The implementation handles the result mapping to the @ref AuthenticationResult enum,
 * ensuring a clear and consistent interface for consumers of the DLL.
 *
 * @note This file is compiled as part of the DLL when `WINDOWSHELLO_EXPORTS` is defined
 *
 * @author
 * Tecknobit - N7ghm4r3
 *
 * @version 1.0.0
 *
 * @see WindowsHelloEngine.h
 */

#include "pch.h"
#include "WindowsHelloEngine.h"

using namespace winrt;
using namespace Windows::Security::Credentials::UI;

/**
 * @brief Requests biometric or secure authentication from the user.
 *
 * Triggers the operating system’s authentication dialog, such as Windows Hello.
 *
 * @param reason The reason why it is requested the authentication
 *
 * @return An @ref AuthenticationResult indicating the result of the authentication request
 */
AuthenticationResult requestAuth(const wchar_t* reason) {
    init_apartment();
    auto result = UserConsentVerifier::RequestVerificationAsync(reason).get();
    switch (result)
    {
    case UserConsentVerificationResult::Verified:
        return AUTHENTICATION_SUCCESS;
    case UserConsentVerificationResult::DeviceNotPresent:
        return HARDWARE_UNAVAILABLE;
    case UserConsentVerificationResult::DeviceBusy:
        return HARDWARE_UNAVAILABLE;
    case UserConsentVerificationResult::NotConfiguredForUser:
        return AUTHENTICATION_NOT_SET;
    case UserConsentVerificationResult::DisabledByPolicy:
        return FEATURE_UNAVAILABLE;
    default:
        return AUTHENTICATION_FAILED;
    }
}