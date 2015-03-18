# Quick add & assign task commands #

  * `@task halls description` --- usual todo task, has alias `@todo`
  * `@confirm halls description` --- task with checkbox, has alias `@ok`
  * `@okfail halls description` --- task with ok/fail options, has alias `@reason`
  * `@question halls description` --- task requiring text answer, has alias `@q`

Multiple tasks can be entered in single message, separated by line breaks.

Messages starting with @something are not actually sent to chat.

Instead of `halls` any comma-delimited user or group list may be specified, e.g. `hall1,hall2`
(`halls` is actually an alias to `Users` group).
Names in user list are case-insensitive.
Or it could be omitted, then task has to be assigned later manually.

# Connection commands #

  * `/dc` --- force disconnect from server
  * `/rc` --- force reconnect to server (closing any previous connection)