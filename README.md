#c4lan


####A multiplayer only connect 4 clone, playable on LAN

* 6 selectable icons to play with
* built in player to player chat
* server auto find
* background colour changeability
* play session score tracking
* window flashing alert to signal player turn


Players play by choosing start ( for start a server ) or join ( for join server ), program uses a multicast address to send a udp handshake packet for the initial connection, then switches to tcp for the remainder.

=
![c4game_start](https://cloud.githubusercontent.com/assets/6975806/4513767/519c2a18-4b53-11e4-9d4d-72d2e5ddba55.png)
=
![c4game_playing](https://cloud.githubusercontent.com/assets/6975806/4513766/519b2352-4b53-11e4-9ee7-280004f45bbd.png)
