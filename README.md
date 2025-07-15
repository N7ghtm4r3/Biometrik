# Biometrik

![Maven Central](https://img.shields.io/maven-central/v/io.github.n7ghtm4r3/Biometrik.svg?label=Maven%20Central)

![Static Badge](https://img.shields.io/badge/android-4280511051)
![Static Badge](https://img.shields.io/badge/ios-445E91)
![Static Badge](https://img.shields.io/badge/desktop-006874)
![Static Badge](https://img.shields.io/badge/wasmjs-834C74)

**v1.0.0beta-01**

**Biometrik** allows to perform the bio-authentication on Compose Multiplatform applications leveraging the native APIs
provided by each platform

## Architecture

- `Android` under the hood uses
  the [BiometricPrompt](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) APIs
- `iOs` and native `macOs` under the hood uses
  the [local authentication](https://developer.apple.com/documentation/localauthentication) APIs
- `JVM` under the hood uses the native APIs provided by the different OSs:
  - On `Windows` uses the [Windows Hello](https://learn.microsoft.com/en-us/windows/apps/develop/security/windows-hello)
    APIs
  - On `Linux` uses the [Polkit](https://www.freedesktop.org/software/polkit/docs/latest/index.html) APIs
  - On`MacOs` uses the [local authentication](https://developer.apple.com/documentation/localauthentication) APIs
- `Web` under the hood uses the [WebAuthn](https://developer.mozilla.org/en-US/docs/Web/API/Web_Authentication_API) APIs

## Integration

### Implementation

#### Version catalog

```gradle
[versions]
biometrik = "1.0.0beta-01"

[libraries]
biometrik = { module = "io.github.n7ghtm4r3:Biometrik", version.ref = "biometrik" } 
```

#### Gradle

- Add the dependency

    ```gradle
    dependencies {
        implementation 'io.github.n7ghtm4r3:Biometrik:1.0.0beta-01'
    }
    ```

  #### Gradle (Kotlin)

    ```gradle
    dependencies {
        implementation("io.github.n7ghtm4r3:Biometrik:1.0.0beta-01")
    }
    ```

  #### Gradle (version catalog)

    ```gradle
    dependencies {
        implementation(libs.biometrik)
    }
    ```

## Setup

### Android

To correctly integrate **Biometrik** on the android target you need to following
these simple steps:

#### AppCompact integration

The native `BiometricPrompt` api requires that the activity which request the authentication must be an
`AppCompatActivity` activity, for this, you need to implement in your android's dependencies the following library:

```kotlin
sourceSets {

  androidMain.dependencies {

    ...
    implementation("androidx.appcompat:appcompat:1.7.1")
  }
}
```

The next step is to adapt your `MainActivity` to extends the `AppCompatActivity` activity type:

```kotlin

// before it extended ComponentActivity instead
class MainActivity : AppCompatActivity() {

  ...

}
```

The latest step is to change the theme of the `MainActivity` from the `AndroidManifest` file:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

  <application
          android:allowBackup="true"
          android:icon="@mipmap/ic_launcher"
          android:label="@string/app_name"
          android:roundIcon="@mipmap/ic_launcher_round"
          android:supportsRtl="true"
          android:theme="@android:style/Theme.Material.Light.NoActionBar">
    <activity
            android:exported="true"
            android:theme="@style/Theme.AppCompat.DayNight.NoActionBar"
    // e.g. this theme
    android:name=".MainActivity">
    <intent-filter>
      <action android:name="android.intent.action.MAIN"/>

      <category android:name="android.intent.category.LAUNCHER"/>
    </intent-filter>
  </activity>
</application>

        </manifest>
```

In the example, the `@style/Theme.AppCompat.DayNight.NoActionBar` style has been used, but you can implement any theme
as
long as it is based on the `Theme.AppCompat` style

### Wasm

To correctly integrate **Biometrik** on the wasm target you need to enable the handling of the native exceptions thrown
by `JavaScript`. You can do this by adding the following compiler option:

```kotlin
compilerOptions {
  freeCompilerArgs.add("-Xwasm-attach-js-exception")
}
```

This ensures that JavaScript exceptions are properly caught and propagated through Kotlin’s exception handling system
when targeting WebAssembly (WASM)

## Usage

In your `App.kt` file you can simply integrate the following component and customize the authentication flow as you
need:

``` kotlin
BiometrikAuthenticator(
    appName = "MyApplication",
    title = "Indicative title displayed if the native dialogs allow it",
    reason = "An indicative reason why the user have to authenticate",
    onSuccess = {
        // non-UI action
        println("User logged in!")
        // UI action
        WelcomeScreen()
    },
    onFailure = {
        // non-UI action
        println("User failed to login...")
        // UI action
        OpsScreen()
    }
)
```

You can also allow the user to retry to authenticate using a custom `state`:

``` kotlin
// create the custom state
val state = rememberBiometrikState()

// attach it to the component
BiometrikAuthenticator(
    state = state
    appName = "MyApplication",
    title = "Indicative title displayed if the native dialogs allow it",
    reason = "An indicative reason why the user have to authenticate",
    onSuccess = {
        // non-UI action
        println("User logged in!")
        // UI action
        WelcomeScreen()
    },
    onFailure = {
        // non-UI action
        state.reAuth()
        // UI action
        Button(
            onClick = {
                state.reAuth()
            }
        ) {
            Text(
                text = "Retry"
            )
        }
    }
)
```

## Native Fine-Tuning for JVM Platform

If you need to fine-tune native engines, follow the steps below depending on which engine you need to modify:

### Windows

If you need to change the Windows's engine you can find the [Visual Studio](https://visualstudio.microsoft.com)
documented
files project where you can apply your modification:

``` bash
nativeengines
├── windows
    └── ... files ...
```

The requirements are:

- The minimum required version is `Standard C++ 17 ISO (/std:c++17)`
- Include the required `windowsapp.lib` library in the additional linker input dependencies

### Linux

At the moment on the Linux's target it is supported the authentication via **Polkit**, but in the future releases will
be tried to implement also the biometric authentication as well.

To modify the native engine you can find its file on:

``` bash
nativeengines
├── linux
    └── PolkitEngine.c
```

After applied the modifications you needed, you can compile it with the below command:

```bash
gcc -fPIC -shared -o LinuxPolkitEngine.so  PolkitEngine.c $(pkg-config --cflags --libs polkit-gobject-1 gio-2.0 glib-2.0)
```

### MacOs

### JVM

On the `JVM` target if you need to change anything from the native engines, after applied the changes on the native
engine
code, you can run these following commands to compile the **dynamic linked libraries** and obtain the file to place into
the `resources` folder:

``` bash
resources
├── windows
│   └── WindowsHelloEngine.dll
├── linux
│   └── LinuxPolkitEngine.so
└── macos
    └── LocalAuthenticationEngine.dylib
```

## Support

If you need help using the library or encounter any problems or bugs, please contact us via the
following links:

- Support via <a href="mailto:infotecknobitcompany@gmail.com">email</a>
- Support via <a href="https://github.com/N7ghtm4r3/Biometrik/issues/new">GitHub</a>

Thank you for your help!

## Donations

If you want support project and developer

| Crypto                                                                                              | Address                                          | Network  |
|-----------------------------------------------------------------------------------------------------|--------------------------------------------------|----------|
| ![](https://img.shields.io/badge/Bitcoin-000000?style=for-the-badge&logo=bitcoin&logoColor=white)   | **3H3jyCzcRmnxroHthuXh22GXXSmizin2yp**           | Bitcoin  |
| ![](https://img.shields.io/badge/Ethereum-3C3C3D?style=for-the-badge&logo=Ethereum&logoColor=white) | **0x1b45bc41efeb3ed655b078f95086f25fc83345c4**   | Ethereum |
| ![](https://img.shields.io/badge/Solana-000?style=for-the-badge&logo=Solana&logoColor=9945FF)       | **AtPjUnxYFHw3a6Si9HinQtyPTqsdbfdKX3dJ1xiDjbrL** | Solana   |

If you want support project and developer
with <a href="https://www.paypal.com/donate/?hosted_button_id=5QMN5UQH7LDT4">PayPal</a>

Copyright © 2025 Tecknobit

# Biometrik


# TODO:

## Jvm

### Linux

To docu about how to compile the linuxnative engine and that for the moment is used without bioauth

### MacOs 

To docu about for the jvm running on mac the command to build the shared lib is the following:

```bash
clang -framework Foundation -framework LocalAuthentication -shared -o LocalAuthenticationEngine.dylib LocalAuthenticationEngine.m
```