## Android

To correctly integrate **Biometrik** on the android target you need to following
these simple steps:

### AppCompact implementation

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

### Adapting the MainActivity

The next step is to adapt your `MainActivity` to extends the `AppCompatActivity` activity type:

```kotlin

// before it extended ComponentActivity instead
class MainActivity : AppCompatActivity() {

    ...

}
```

### Configuring theme

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

## Wasm

To correctly integrate **Biometrik** on the wasm target you need to enable the handling of the native exceptions thrown
by `JavaScript`. You can do this by adding the following compiler option:

```kotlin
compilerOptions {
    freeCompilerArgs.add("-Xwasm-attach-js-exception")
}
```