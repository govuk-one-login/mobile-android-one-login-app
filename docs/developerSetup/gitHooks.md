# Git hooks

Git hooks are scripts that execute in an event-driven way. Each type of event
has the ability to include a script to run. What gets executed is contextually
arbitrary - common examples are to enforce linting rules before committed code
enters the code base.

The [Project's Git Hooks] contain the customised scripts for the project. In
order to use this folder, run the following command in a terminal window:

```shell
git config core.hooksPath $(git rev-parse --show-toplevel)/.githooks
```

To selectively enable a single hook, run the following command in a terminal window
at the project's root directory:

```shell
ln -s ../../.githooks/<hook> .git/hooks/<hook>
```

Please see the [Git Hook Framework] for more information on the types of events
available.

[Project's Git Hooks]: /.githooks

[Git Hook Framework]: https://git-scm.com/docs/githooks
