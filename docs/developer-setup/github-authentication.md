# Authenticating with the Github Packages Registry

Install the [Github CLI tool] first.

```shell
gh auth token
```

## Putting it all together

Could not find 'Github Package Registry' properties. Have you set them in
${System.getenv("HOME")}/.gradle/gradle.properties?
gpr.user=\${yourGithubUsernameWithoutDollarAndCurlyBraces}
gpr.token=\${yourGithubPersonalAccessTokenWithoutDollarAndCurlyBraces}"

[Github CLI tool]: https://cli.github.com/