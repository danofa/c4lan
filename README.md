#c4lan


####A multiplayer only connect 4 clone, playable on LAN

* 6 selectable icons to play with
* built in player to player chat
* server auto find
* background colour changeability
* play session score tracking
* window flashing alert to signal player turn

Players play by choosing start ( for start a server ) or join ( for join server ), program uses a multicast address to send a udp handshake packet for the initial connection, then switches to tcp for the remainder.
