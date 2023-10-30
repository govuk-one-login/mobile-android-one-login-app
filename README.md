# One Login - Android

This repository hosts the Android Mobile app for GOV.uk's One Login.

## Branching strategy and releases

This project uses a git-flow branching strategy, briefly:
- Create a new branch from `develop`
- Merge pull requests into `develop`
- At the point of preparing a new release create a `release/` branch from the specified commit in develop
- Name the branch using the appropriate semantic version for the release; for example `release/1.0.0`
  - The initial version number for the release is taken from the branch name; `v1.0.0`
  - Any subsequent pushes to the branch will increment the patch; `v1.0.1` 
- Any required fixes for the release should be pull requested into the release branch
- Once approval has been granted for the release to be published the release branch should be merged into `main`
- `main` should then be merged back into `develop`

## Getting started

A number of how-to documents are provided to get a new developer up to speed with:
- [pre-requisite software](docs/how-to/pre-requisite-software.md)
- [running the code](docs/how-to/running-the-code.md)
- [running the tests](docs/how-to/running-the-tests.md)
