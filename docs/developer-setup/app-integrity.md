### App Integrity

As part of the login process [Firebase App Check](https://firebase.google.com/docs/app-check/android/play-integrity-provider)
is used in conjunction with some backend functions to ensure we are dealing with a
legitimate version of the app.

To build the app it requires that a `debugAppCheckToken` is provided. This should be set in
your local `~/.gradle/gradle.properties` file. This is a debug secret and will need to be
acquired via the relevant internal channels. However the app should build if there is an empty 
or 'dummy' string in the `gradle.properties` file.