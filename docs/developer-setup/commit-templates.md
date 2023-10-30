# Commit template

The [Commit Template File] exists as a way to standardise the formatting of
commits within the repository. Using the [Commit Template File] is positively
encouraged. To use the commit template, run the following command within a
terminal window:

```shell
git config commit.template $(git rev-parse --show-toplevel)/.github/commit_template.txt
```

Android Studio automatically registers and utilise the commit template, should a
Developer prefer writing commits outside of a terminal window.

[Commit Template File]: /.github/commit_template.txt
