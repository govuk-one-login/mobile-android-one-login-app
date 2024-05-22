# Using this file:
# `brew bundle --no-lock` in the root directory of the repository.

# Taps (data sources)
tap "homebrew/homebrew-core" # Access alternative versions for homebrew casks

# terminal programs
brew "rbenv" unless system "rbenv", "--version"
brew "rbenv-bundler" unless system "which", "rbenv-bundler"
brew "ruby-build" unless system "ruby-build", "--version"
brew "vale" unless system "vale", "--version"

# casks (binary apps, such as from installers)