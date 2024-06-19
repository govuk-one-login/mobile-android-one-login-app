# Developer dependencies outside of Android Studio

* [Homebrew] for the [Brew file] dependencies. This also installs Android Studio via a cask if it's
  not already installed:

  ```shell
  brew bundle --file=$(git rev-parse --show-toplevel)/Brewfile --no-lock
  ```

Other dependencies exist within the preceding list of configuration files. 

# Gotchas / considerations

* If a Developer has multiple Java Development Kit versions installed, the `org.gradle.java.home`
  property needs to point to a valid `JAVA_HOME` path. This property should exist within the User's
  `~/.gradle/gradle.properties` file.
* Be mindful that Homebrew by default is a 'Rolling release' package manager. This means that by
  default, it downloads the latest version of a dependency.

[Brew file]: ../../Brewfile
[Gem file]: ../../Gemfile
[Homebrew]: https://brew.sh
