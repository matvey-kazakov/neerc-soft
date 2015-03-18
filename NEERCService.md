# Introduction #

We need to make our own jabber server component (see [Jabber Component Protocol](http://xmpp.org/extensions/xep-0114.html)), to which clients will communicate independently of MUC (chat) service.

Similar to MUC, it has fixed JID (neerc.%hostname%), known by client.

# Use cases #

Clients can send following queries via iq stanzas:
  * Query user list with group and power flag
  * Query current task list (including task status for current user or all users)
  * ~~Query current permissions for this service (admin/user)~~ (included in user list)
  * Change task status for a task for sending user
  * Add new task (admin only)
  * Remove task (admin only)
  * Assign task to user/group
  * ~~Subscribe to task list/status updates~~ (task updates will be broadcasted to all users in list)

Groups and admin list are stored in component configuration (or somehow extracted from MUC service...)
On any task change, component should notify all ~~subscribed~~ users.

# Protocol #

## Querying user list ##

Request:
```
<iq type="get" to="neerc.localhost">
    <query xmlns="http://neerc.ifmo.ru/protocol/neerc#users" />
</iq>
```

Response:
```
<iq type="result" from="neerc.localhost" to="hall2@localhost/Miranda">
  <query xmlns="http://neerc.ifmo.ru/protocol/neerc#users">
    <user name="admin" group="Admins" power="yes" />
    <user name="hall1" group="Halls" power="no" />
...
    <user name="hall7" group="Halls" power="no" />
  </query>
</iq>
```

## Querying task list ##

Request:
```
<iq type="get" to="neerc.localhost">
  <query xmlns="http://neerc.ifmo.ru/protocol/neerc#tasks" />
</iq>
```

Response:
```
<iq from="neerc.localhost" type="result" to="admin@localhost/Kopete" >
  <query xmlns="http://neerc.ifmo.ru/protocol/neerc#tasks">
    <task title="Do some work" type="todo" id="12345">
      <status for="hall1" type="none" />
      <status for="hall2" type="running" />
      <status for="hall3" type="success" />
    </task>
    <task title="You here?" type="confirm" id="12346">
      <status for="hall1" type="none" />
      <status for="hall2" type="success" />
    </task>
    <task title="Wallpapers set?" type="okfail" id="12347">
      <status for="hall1" type="none" />
      <status for="hall2" type="success" />
      <status for="hall3" type="fail" value="none of 'em" />
    </task>
    <task title="???" type="question" id="12348">
      <status for="hall1" type="none" />
      <status for="hall2" type="success" value="42" />
    </task>
    ...
  </query>
</iq>
```


## Creating/updating task (admin only) ##

Request:
```
<iq type="set" to="neerc.localhost">
   <!-- absent id attribute means new task -->
   <task xmlns="http://neerc.ifmo.ru/protocol/neerc#task" title="Do some work" type="todo">
     <status for="hall1" type="none" />
     <status for="hall2" type="running" />
     <status for="hall3" type="success" />
   </task>
</iq>
```
To delete an existing task, specify "remove" as its type:
```
<iq type="set" to="neerc.localhost">
   <task xmlns="http://neerc.ifmo.ru/protocol/neerc#task" id="12346" type="remove" />
</iq>
```

Response:

Error 403 if not admin, otherwise irrelevant.

Server also should notify everyone of this change using messages with same packet attached as extension.

## Updating task status ##

Request:
```
<iq type="set" to="neerc.localhost" from="hall2">
   <taskstatus xmlns="http://neerc.ifmo.ru/protocol/neerc#taskstatus" id="12345" type="success" value="done" />
</iq>
```

Response:

irrelevant, server also should notify everyone of this change using task packet (see above)