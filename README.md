c4lan
=====

A multiplayer only connect 4 clone, playable on LAN

      Connect 4 Clone
      ===============
            <p>A connect 4 clone, written in java, playable with 2 players only, over LAN, features:</p>
            <ul>
                <li>6 selectable icons to play with</li>
                <li>built in player to player chat</li>
                <li>server auto find</li>
                <li>background colour changeability</li>
                <li>play session score tracking</li>
                <li>window flashing alert to signal player turn</li>
            </ul> 

            <h3>What I coded: </h3>
            <p>Everything! :)<br/>
                Players play by choosing start ( for start a server ) or join ( for join server ), program uses a multicast address to 
                send a udp handshake packet for the initial connection, then switches to tcp for the remainder.
            </p>
            <p>I first coded the game server, to disconnect the client to client messaging code from the game itself, it functions as a repeater, and continues to check if both players are connected.
                If a disconnect occurs, the game server notifies the other client and the session is terminated. The client can then return back to the starting screen and choose to start or join another game.</p>

            <p>
                For the moves, the clients pass a 'move token' between them, which determines whose turn it is. At each turn the board is scanned to check for winning moves and a win message is sent to the server.
            </p>
