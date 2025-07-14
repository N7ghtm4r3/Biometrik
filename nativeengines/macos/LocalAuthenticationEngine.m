#import <Foundation/Foundation.h>
#import <LocalAuthentication/LocalAuthentication.h>

typedef NS_ENUM(NSInteger, AuthenticationResult) {
    HardwareUnavailable = 2,
    FeatureUnavailable = 4,
    AuthenticationFailed = 1,
    AuthenticationSuccess = 0,
    AuthenticationNotSet = 3
};

AuthenticationResult requestAuth(NSString *reason) {
    LAContext *context = [[LAContext alloc] init];
    NSError *error = nil;
    if (![context canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:&error]) {
        switch (error.code) {
            case LAErrorBiometryNotEnrolled:
                return AuthenticationNotSet;
            case LAErrorBiometryNotAvailable:
                return FeatureUnavailable;
            case LAErrorPasscodeNotSet:
                return AuthenticationNotSet;
            default:
                return AuthenticationFailed;
        }
    }
    if (@available(macOS 10.12.2, *))
        if (context.biometryType == LABiometryTypeNone)
            return HardwareUnavailable;
    dispatch_semaphore_t sema = dispatch_semaphore_create(0);
    __block BOOL success = NO;
    [context evaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics
            localizedReason: reason
                      reply:^(BOOL s, NSError * _Nullable authError) {
        success = s;
        dispatch_semaphore_signal(sema);
    }];
    dispatch_semaphore_wait(sema, DISPATCH_TIME_FOREVER);
    return success ? AuthenticationSuccess : AuthenticationFailed;
}