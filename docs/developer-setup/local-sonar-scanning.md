# Scanning the project into a local instance of sonar

## Initial set up

The initial set up is only performed once on a per-project basis.

- First, follow the directions within the [Dependencies tutorial].
- Start the local instance of sonar that Homebrew installed:
  ```shell
  sonar start
  ```
- Manually create a project, using the `sonar.projectKey` from the [Root sonar configuration].
- Generate a token by choosing to Analyze your project locally.

## Scanning the project

- Use the `SONAR_TOKEN` when calling the `localProcess` script. When forgetting the value, simply
  create another token within the sonar instance:
  ```shell
  SONAR_TOKEN="replace_me" ./.sh/sonar/localProcess
  ```

[Dependencies tutorial]: ./homebrew-dependencies.md
[Root sonar configuration]: ../../buildLogic/plugins/src/main/kotlin/uk/gov/sonar/root-config.gradle.kts
