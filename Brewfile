# Using this file:
# `brew bundle --no-lock` in the root directory of the repository.

# Taps (data sources)
tap "homebrew/cask-versions" # Access alternative versions for homebrew casks

# terminal programs
brew "awscli" unless system "aws", "--version"
brew "cocogitto" unless system "cog", "--version"
brew "gh" unless system "gh", "--version"
brew "git-lfs" unless system "git", "lfs", "version"
brew "gnupg" unless system "gpg", "--version"
brew "gpg2" unless system "gpg2", "--version"
brew "gnu-sed" unless system "gsed", "--version"
brew "jq" unless system "jq", "--version"
brew "nvm" unless system "command", "-v", "nvm"
brew "pinentry" unless system "pinentry", "--version"
brew "pinentry-mac" unless system "pinentry-mac", "--version"
brew "rbenv" unless system "rbenv", "--version"
brew "rbenv-bundler" unless system "which", "rbenv-bundler"
brew "ruby-build" unless system "ruby-build", "--version"
brew "shellcheck" unless system "shellcheck", "--version"
brew "sonarqube-lts" unless system "which", "sonar"
brew "vale" unless system "vale", "--version"

# casks (binary apps, such as from installers)
cask "android-commandlinetools" unless system "which", "avdmanager", "lint", "retrace", "sdkmanager", "apkanalyzer", "screenshot2"
cask "android-platform-tools" unless system "which", "adb", "etc1tool", "fastboot", "hprof-conv", "make_f2fs", "make_f2fs_casefold", "mke2fs"
cask "android-studio" unless system "mdfind", "-name", "Android Studio.app"
cask "android-file-transfer" unless system "mdfind", "-name", "Android File Transfer.app"
cask "docker" unless system "mdfind", "-name", "Docker.app"
cask "git-credential-manager" unless system "git-credential-manager", "--version"
cask "homebrew/cask-versions/oracle-jdk17" unless system "/usr/libexec/java_home", "-v", "17", "--failfast"
