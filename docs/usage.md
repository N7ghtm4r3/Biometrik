## Default

In your `App.kt` file you can simply integrate the following component and customize the authentication flow as you
need

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

## Using custom state

You can also allow the user to retry to authenticate using a custom `state`

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
        state.reAuth() // retry to authenticate
        // UI action
        Button(
            onClick = {
                state.reAuth() // retry to authenticate
            }
        ) {
            Text(
                text = "Retry"
            )
        }
    }
)
```