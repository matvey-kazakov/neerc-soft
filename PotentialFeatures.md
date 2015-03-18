# Potential Features for NEERC Chat #

_This is just a collective list of all features that **may** probably ever be implemented.
Persistence of a particular feature in the list doesn't yet mean that the feature is worth implementing or even that at least it is an improvement but not worsening._

_You may add any of your ideas below._

## Networking ##
  * [issue3](https://code.google.com/p/neerc-soft/issues/detail?id=3): **Automatic reconnect**
  * [issue7](https://code.google.com/p/neerc-soft/issues/detail?id=7): **Display connection status**
  * [issue12](https://code.google.com/p/neerc-soft/issues/detail?id=12): **Load messages and tasks from server at login**: currently you lose all regular chat messages and reload all important tasks twice after reconnect.
  * **Allow several concurrent sessions for same user** (may be useful for manager users)
  * **Allow wildcards in login-to-IP binding** (might be useful for manager users, however this is quite insecure without passwords)

## Logging ##
  * **Fix double/triple timestamps**
  * **Remove duplicate 'important' messages** dumped on each reconnect
  * [issue6](https://code.google.com/p/neerc-soft/issues/detail?id=6): ~~**Fix encoding** in console (1251 to 866)~~

## Task manager ##
  * [issue4](https://code.google.com/p/neerc-soft/issues/detail?id=4): **Make tasks progress visible to everybody** (this might really help to self-organize)
  * **Make other minor stuff visible to everybody** (contest duration clock, help button, etc.)
  * **Allow everybody to add tasks**
  * [issue1](https://code.google.com/p/neerc-soft/issues/detail?id=1): **Task scheduling** for particular time (either absolute or measured from contest start)

## UI ##
  * **Separate 'flood' from 'important messages'** (either simply by color/font-size or even move them into separate frame; _in case if color/font-size gradation usage several priority categories may be done: technical problems with computers, rules clarifications (..."may they use mobile phones at practice session?"), problems with teams, critical problems (eg. if some shit happened, and Matvey does not respond to the walkietalkie, or the walkietalkie battery got down, or hall-manager accidentally took the walkietalkie out of the hall), etc._) **Updated, see [Rooms](PotentialFeatures#Rooms.md)**.
  * **Improve 'alerts' on tasks/'important messages'**; here several different complementary features may be implemented:
    * All new messages (and tasks) in chat are highlighted in bright color (and the highlight gets duller as the message gets older)
    * Make separate 'always-on-top' window for upper 'important-messages' panel (volunteers sometimes may stare to the monitor or do some other stuff at the computer, you know...)
    * [issue19](https://code.google.com/p/neerc-soft/issues/detail?id=19): Make chat client pop-up on top of all other windows each time a task is assigned
    * [issue18](https://code.google.com/p/neerc-soft/issues/detail?id=18): Make chat client beep over PC-speaker (echo \x07 to the console) each time a task is assigned (the chat-guy may be sleeping on the key-board ;) )
    * Highlight uncompleted tasks with bold red letters (it fixes the problem when the task is actually done but the chat-guy forgot to tick it in the chat)
    * Introduce some 'reaction-points' (penalty time =))) for halls for ticking tasks quickly (or some negative-points for not doing this)
  * **Make separate request-type for questions concerning list of computers/teams** like "which teams are not here yet?" or "which computers are "good" for teams to sit and which are not?" or "which computers don't have wallpapers yet" (this might be useful on management-side)
  * ~~**Generate single message-box for bunch of assigned tasks** (so there will be no need to click 'Ok' for 5 times)~~
  * [issue9](https://code.google.com/p/neerc-soft/issues/detail?id=9): **Colorize user logins in chat window** in order to distinguish them quickly
  * [issue20](https://code.google.com/p/neerc-soft/issues/detail?id=20): **Fix timer plugin functionality** (it displayed contest time and status in chat window)


---


## More detail on some features ##

### Rooms ###

**In short:** attach a second input box to the top frame, making top & bottom frames independent

Top frame is used for:
  * 'new task' messages and other announcements
  * two-way communication with jury and Matvey
  * allow red color for everyone here, for situations requiring _immediate_ reaction (any problems with contestants etc), or use different colors to distinguish messages and responses.

Bottom frame remains for off-topic chat, important messages no longer replicate here.
Optionally, this frame may be minimized or hidden when there are any pending tasks.

A brief chat history (last 10-30 messages) for both rooms should be tracked by server, and sent to clients on login.
Original timestamps should also be preserved, obsolete important messages appearing with current timestamp are very confusing sometimes.