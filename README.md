JSR-356
=======

This repository contains a client-server version of the TicTacToe game.

The server depends on javax.websocket-api 1.0-b13 and on tyrus-1.0-b12

Also, tyrus-1.0-b12 depends on javax.websocket-api-1.0-b13 which is NOT the latest version of the specification.

At this moment, the latest promoted build of GlassFish (b78) is shipped with tyrus-1.0-b12 and javax.websocket-api-1.0-b13.
Hence, in order to run the server, you can simply install GlassFish 4 b78
http://dlc.sun.com.edgesuite.net/glassfish/4.0/promoted/glassfish-4.0-b78.zip

Currently, we have 4 clients:
* A JavaFX 2.x client, depending on the same javax.websocket-api 1.0-b13.
* An HTML 5 client, leveraging Angular.js
* An iOS client
* An Android client

All these clients can connect to the same backend.
