# Links #

  * [Openfire 3.6.4 Linux](http://www.igniterealtime.org/downloadServlet?filename=openfire/openfire_3_6_4.tar.gz)
  * [Openfire 3.6.4 Win](http://www.igniterealtime.org/downloadServlet?filename=openfire/openfire_3_6_4.zip)
  * [Openfire Properties](http://www.igniterealtime.org/community/docs/DOC-1061)

# Server installation #
  1. Start server
    * On Linux:
```
./bin/openfire start
```
    * On Windows:
```
./bin/openfire.exe start
```
  1. Open http://localhost:9090 and follow installation instructions (use **Embedded Database** and set server name to **localhost**)
  1. Restart server
    * On Linux:
```
./bin/openfire stop
./bin/openfire start
```
    * On Windows:
```
./bin/openfire.exe stop
./bin/openfire.exe start
```
  1. Open http://localhost:9090 and login as admin
  1. Install "User Import Export" plugin
  1. Import users from openfire-users.xml
  1. Add openfire property: xmpp.client.idle=60000
  1. Create persistent room with following settings:
    * Room Name: neerc
    * Broadcast Presence for: Moderator, Participant, Visitor
    * Show Real JIDs of Occupants to: Moderator
    * Room Options: Log Room Conversations
  1. Modify group chat service settings
    * History Settings: Show Entire Chat History

# XEPs #

Full list: http://xmpp.org/extensions/

Useful for us:
  1. [Multi-User Chat](http://xmpp.org/extensions/xep-0045.html)
  1. [XMPP Ping](http://xmpp.org/extensions/xep-0199.html)
  1. [Flexible Offline Message Retrieval](http://xmpp.org/extensions/xep-0013.html) (???)
  1. [Data Forms](http://xmpp.org/extensions/xep-0004.html) - task management
  1. [Message Receipts](http://xmpp.org/extensions/xep-0184.html) (for private messages, not needed for MUC)

# Regression #

Sorted by importance:

| **Feature** | **Status** |
|:------------|:-----------|
| TaskMessage | work in progress (See [NEERCService](NEERCService.md)) |
| Power users | implemented |
| ServerMessage (join, left) | implemented |
| UserMessage (public) | implemented |
| TimerMessage |  |
| WelcomeMessage | don't needed - use history |
| UserMessage (private) |  |
| PingMessage | don't needed? |
| UserListUpdateMessage | don't needed |
| LoginMessage | don't needed |

# Open Questions #

  1. History size (seconds, since, max)?
  1. Restrict login by IP.

# Implementation Details #

Each MUC Message stored on server, so history can be easily retrieved by clients.