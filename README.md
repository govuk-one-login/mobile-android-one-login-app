# One Login - Android

This repository hosts the Android Mobile app for GOV.UK's One Login.

## Branching strategy and releases

This project uses a git-flow branching strategy, briefly:

- Create a new branch from `develop`
- Merge pull requests into `develop`
- At the point of preparing a new release create a `release/` branch from the specified commit in
  develop
- Name the branch using the appropriate semantic version for the release; for
  example `release/1.0.0`
    - The initial version number for the release is taken from the branch name; `v1.0.0`
    - Any subsequent pushes to the branch will increment the patch; `v1.0.1`
- Any required fixes for the release should be pull requested into the release branch
- Once approval has been granted for the release to be published the release branch should be merged
  into `main`
- `main` should then be merged back into `develop`

## Getting started

A number of how-to documents are provided to get a new developer up to speed with:

### Developer setup

- [Using the provided git commit template](docs/developer-setup/commit-templates.md)
- [Enabling the provided git hooks](docs/developer-setup/git-hooks.md)
- [Authenticating with Github](docs/developer-setup/github-authentication.md) to enable the ability
  to download packages from the organisation's Github package registry.

### Developer how-to guides

- [pre-requisite software](docs/how-to/pre-requisite-software.md)
- [running the code](docs/how-to/running-the-code.md)
- [running the tests](docs/how-to/running-the-tests.md)

## Updating gradle-wrapper

Gradle SHA pinning is in place through the `distributionSha256Sum` attribute in gradle-wrapper.properties. This means the gradle-wrapper must be upgraded properly through the `./gradlew wrapper` command.
Example gradle-wrapper.properties
```
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionSha256Sum=2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046
distributionUrl=https\://services.gradle.org/distributions/gradle-8.10.2-bin.zip
networkTimeout=10000
validateDistributionUrl=true
 ```

Use the following command to update the gradle wrapper. Run the same command twice, [reason](https://sp4ghetticode.medium.com/the-elephant-in-the-room-how-to-update-gradle-in-your-android-project-correctly-09154fe3d47b).

```bash
./gradlew wrapper --gradle-version=8.10.2 --distribution-type=bin --gradle-distribution-sha256-sum=31c55713e40233a8303827ceb42ca48a47267a0ad4bab9177123121e71524c26
```

Flags:
- `gradle-version` self explanatory
- `distribution-type` set to `bin` short for binary refers to the gradle bin, often in this format `gradle-8.10.2-bin.zip`
- `gradle-distribution-sha256-sum` the SHA 256 checksum from this [page](https://gradle.org/release-checksums/), pick the binary checksum for the version used

The gradle wrapper update can include:
- gradle-wrapper.jar
- gradle-wrapper.properties
- gradlew
- gradlew.bat

You can use the following command to check the SHA 256 checksum of a file

```bash
shasum -a 256 gradle-8.10.2-bin.zip
```
