### App Integrity

As part of the login process [Firebase App Check](https://firebase.google.com/docs/app-check/android/play-integrity-provider) is used in conjunction with some backend 
functions to ensure we are dealing with a legitimate version of the app.

So, to run the production or integration variant of the app, you'll need to install a release build
from Google Play.

To build and debug locally, you'll need to use the build or staging variant of the app with a [debug App Check token]:

1. Get debug App Check tokens for the build and staging environments.
   
   These are debug secrets and will need to be acquired via the relevant internal channels (see
   [how to get a debug App Check token (internal)]).

2. Add the tokens to your local `~/.gradle/gradle.properties`

```properties
# ~/.gradle/gradle.properties

debugBuildAppCheckToken=<your app check token for the build environment>
debugStagingAppCheckToken=<your app check token for the staging environment>
```

Note that the app should build if there is an empty or 'dummy' string in the `gradle.properties` file.

[debug App Check token]: https://firebase.google.com/docs/app-check/android/debug-provider
[how to get a debug App Check token (internal)]: https://govukverify.atlassian.net/wiki/x/EYB4FQE