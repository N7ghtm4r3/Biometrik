#pragma once

#ifdef WINDOWSHELLO_EXPORTS
    #define DLL_API __declspec(dllexport)
#else
    #define DLL_API __declspec(dllimport)
#endif

extern "C" {

    enum AuthenticationResult {

        HARDWARE_UNAVAILABLE = 2,

        FEATURE_UNAVAILABLE = 4,

        AUTHENTICATION_FAILED = 1,

        AUTHENTICATION_SUCCESS = 0,

        AUTHENTICATION_NOT_SET = 3

    };

    DLL_API AuthenticationResult requestAuth(const wchar_t* reason);
}