# ID2010_Programming-of-Interactive-Systems
Deliverables for the course ID2010 taken as a part of the masters program in Software Engineering of Distributed Systems at KTH

The assignments for the course are 

## 1. Chat Service

The requirement is to implement Away From Keyboard(AFK) and user status. Below is the detailed information.

An AFK (Away From Keyboard) detector that automatically notifies the other clients when someone has been inactive for a certain period of time. The message should be automatically generated when the timeout period expires. Add a command in the client for turning on and off the display of such automated messages.

Make the user information useful to the maintainer of the chat service, so that the chat server console prints when someone connects and disconnects. In the case of a disconnect, also print session time (time between connect and disconnect), and how many messages or Kbytes that user has sent.

Enable all clients connected to the service to see whenever a client joins or leaves the chat. Add a user command to the client that allows the user to list users currently joined to the service. If the client uses the .name command, this should be seen by all connected clients.


## 2. Tag Game

The challenge is to create the Game of Tag for mobile software agents.

The game of tag is a child's game, played minimally by two players. It
is very simple. One player is choosen to be 'it'. That player's task
is to run up to one of the other players and touch him or her, saying
"You're it!", whereupon the 'it' property and the associated task is
transferred to the other player. The players who are currently not
'it', try to evade being tagged, usually by running and hiding.

Starting from the provided software, you must design and implement the
game of tag for at least three mobile agents. The playfield consists
of three or more Bailiffs (execution servers).

You can modify the given sources freely, and add classes and
interfaces as desired.

There are two important requirements on which the game's
implementation depend:

 (1) tagging can only be done between players in the same Bailiff

 (2) the tag (the 'it' property) must be passed reliably from one
     player to another. It must not be lost or duplicated during the
     transaction.

From the given code (Dexter) we see how the Jini middleware can help
each player to dynamically discover and communicate with Bailiffs.

From (1) we realise that a player needs to know how the other players
are distributed among the Bailiffs. The player being 'it' needs to
find a Bailiff with players in it, move there, select a victim, and
then attempt to tag that victim. The other players all want to know
where the player being 'it' is located, so that they can avoid being
tagged, by moving to another Bailiff if the player being 'it' arrives
in their Bailiff.

So, here is what is needed:

  (a) Each player can be either 'it' or 'not it', and depending on
      what it currently is, its behaviour changes appropriately.

  (b) A player needs cooperation from the Bailiffs so that is can:
      (1) Get a list of players currently located in a Bailiff
      (2) Query each player if they are 'it' or not.
      (3) Try to tag a player

  (c) From (b) it follows that each player must have a unique id, so
  that it can:
      (1) Recognise itself in a list of players (b.1)
      (2) Specify some other player (b.2 and b.3)

  (d) Finally, there must be some mechanism that determines exactly if
      a player can be tagged or not. This has to do with the way
      mobility actually works. When a mobile object (e.g. a player)
      'moves' from one Bailiff to another, it does not really move at
      all. It sends a copy of itself to the other Bailiff, and if the
      copy was successful, the original object terminates its thread
      of execution and goes to garbage collection. Thus, if a player
      is tagged at the wrong moment, the copy of the player that moved
      away stays untagged, while the original object instance is
      tagged instead. When the original instance dies, it takes the
      'it' property with it into oblivion, and the tag is lost from
      the game.
