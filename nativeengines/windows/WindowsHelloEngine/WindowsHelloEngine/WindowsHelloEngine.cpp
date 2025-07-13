#include "pch.h"
#include "WindowsHelloEngine.h"

using namespace winrt;
using namespace Windows::Security::Credentials::UI;

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