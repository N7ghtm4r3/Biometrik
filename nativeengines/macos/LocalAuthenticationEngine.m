/**
 * @file AuthenticationEngine.m
 * @brief Implements biometric authentication using Apple's LocalAuthentication framework.
 *
 * This file defines the {@link AuthenticationResult} enum representing
 * possible authentication statuses and the function {@link requestAuth}
 * which prompts the user for biometric authentication (Touch ID / Face ID).
 *
 * The authentication result is returned as an {@link AuthenticationResult} value
 *
 * @author Tecknobit - N7ghtm4r3
 * @version 1.0.0
 */

#import <Foundation/Foundation.h>
#import <LocalAuthentication/LocalAuthentication.h>

/**
 * @enum AuthenticationResult
 * @brief Represents the detectable statuses handled by the component
 *
 * @author Tecknobit - N7ghtm4r3
 */
typedef NS_ENUM(NSInteger, AuthenticationResult) {

    /**
     * This status indicates the device has not the physical hardware to perform the
     * bio-authentication or is not currently available to serve the request
     */
    HardwareUnavailable = 2,

    /**
     * This status indicates the feature of the bio-authentication is not provided by the device,
     * or it has been disabled following internal policies
     */
    FeatureUnavailable = 4,

    /**
     * This status indicates that authentication has failed either due to errors during the request
     * or because the result was actually unauthorized
     */
    AuthenticationFailed = 1,

    /**
     * This status indicates that authentication request has been successful
     */
    AuthenticationSuccess = 0,

    /**
     * This status indicates the authentication is not actually set by the user, so cannot be
     * performed any type of authentication
     */
    AuthenticationNotSet = 3
};

/**
 * @brief Requests biometric or secure authentication from the user.
 *
 * Triggers the operating system’s authentication dialog using `LocalAuthentication`
 *
 * @param reason The reason why it is requested the authentication
 *
 * @return An @ref AuthenticationResult indicating the result of the authentication request
 */
AuthenticationResult requestAuth(const char *reason) {
    NSString *nsReason = [NSString stringWithUTF8String:reason];
    __block AuthenticationResult result = AuthenticationFailed;

    dispatch_semaphore_t sema = dispatch_semaphore_create(0);

    dispatch_async(dispatch_get_main_queue(), ^{
        LAContext *context = [[LAContext alloc] init];
        NSError *error = nil;

        if (![context canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:&error]) {
            switch (error.code) {
                case LAErrorBiometryNotEnrolled:
                case LAErrorPasscodeNotSet:
                    result = AuthenticationNotSet; break;
                case LAErrorBiometryNotAvailable:
                    result = FeatureUnavailable; break;
                case LAErrorBiometryLockout:
                    result = AuthenticationFailed; break;
                default:
                    result = AuthenticationFailed; break;
            }
            dispatch_semaphore_signal(sema);
            return;
        }

        [context evaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics
                localizedReason:nsReason
                          reply:^(BOOL success, NSError * _Nullable authError) {
            result = success ? AuthenticationSuccess : AuthenticationFailed;
            dispatch_semaphore_signal(sema);
        }];
    });

    dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, 30 * NSEC_PER_SEC);
    if (dispatch_semaphore_wait(sema, timeout) != 0) {
        return AuthenticationFailed;
    }

    return result;
}