# Authenticating with the Github Packages Registry

Some of the dependencies declared for this project include packages stored on the
[Github Organisation's Packages Registry]. The [Github workflow files] pass secrets through
github action input variables which in turn set the relevant `GITHUB_ACTOR` and `GITHUB_TOKEN`
environment variables referred to within the [Project's Gradle settings]. This approach is less than
ideal for local development so with this in mind, what can a developer do in order to complete
their set up?

## The what: property keys

There are two gradle properties used to authenticate with the Github packages registry for local
development:

- `gpr.user`: The developer's Github account name. Associated with the `GITHUB_ACTOR` environment
  variable.
- `gpr.token`: The developer's Personal Access Token (PAT), generated in the
  [Github developer settings]. Associated with the `GITHUB_TOKEN` environment variable.

The proceeding command outputs the existing Personal Access Token, if the
[Github Command-Line Interface (CLI) tool] is installed:

```shell
gh auth token
```

## The where: `gradle.properties`

Because we no longer store credentials within the same directory structure as the git repository
itself, it's recommended to store these gradle properties within Gradle's user configuration, found
at `~/.gradle/gradle.properties`. When successfully edited, it should look similar to the proceeding
code block with unique values to the developer:

```properties
# Other properties within the file...

gpr.user=Octocat
gpr.token=ghp_1234567890abcdef
```

## The shortcut:

Install the [Github Command-Line Interface (CLI) tool] then run:
```shell
bash $(git rev-parse --show-toplevel)/.sh/setupDeveloperTokens && \
less ~/.gradle/gradle.properties
```

[Github Command-Line Interface (CLI) tool]: https://cli.github.com/
[Github developer settings]: https://github.com/settings/tokens
[Github Organisation's Packages Registry]: https://github.com/orgs/govuk-one-login/packages
[Github workflow files]: /.github/workflows
[Project's Gradle settings]: /settings.gradle.kts