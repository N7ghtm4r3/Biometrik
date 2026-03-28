# Overview

**Biometrik** allows to perform the bio-authentication on Compose Multiplatform applications leveraging the native APIs
provided by each platform

## Architecture

- `Android` under the hood uses
  the [BiometricPrompt](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) APIs
- `iOs` and native `macOs` under the hood uses
  the [local authentication](https://developer.apple.com/documentation/localauthentication) APIs
- `JVM` under the hood uses the native APIs provided by the different OSs:
    - On `Windows` uses
      the [Windows Hello](https://learn.microsoft.com/en-us/windows/apps/develop/security/windows-hello)
      APIs
    - On `Linux` uses the [Polkit](https://www.freedesktop.org/software/polkit/docs/latest/index.html) APIs
    - On`MacOs` uses the [local authentication](https://developer.apple.com/documentation/localauthentication) APIs
- `Web` under the hood uses the [WebAuthn](https://developer.mozilla.org/en-US/docs/Web/API/Web_Authentication_API) APIs

## Implementation

### Gradle short

```groovy
dependencies {
    implementation 'io.github.n7ghtm4r3:Biometrik:1.0.1'
}
```

### Gradle (Kotlin)

```kotlin
dependencies {
    implementation("io.github.n7ghtm4r3:Biometrik:1.0.1")
}
```

### Gradle (version catalog)

#### libs.versions.toml

```toml
[versions]
biometrik = "1.0.1"

[libraries]
biometrik = { module = "io.github.n7ghtm4r3:Biometrik", version.ref = "biometrik" } 
```

#### build.gradle

```kotlin
dependencies {
    implementation(libs.biometrik)
}
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

If you want support project and developer with <a href="https://www.paypal.com/donate/?hosted_button_id=5QMN5UQH7LDT4">
PayPal</a>