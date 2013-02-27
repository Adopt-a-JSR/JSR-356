JSR-356
=======

This repository contains a client-server version of the TicTacToe game.

The server depends on javax.websocket-api 1.0-b13 and on tyrus-1.0-SNAPSHOT

Currently, tyrus-1.0-SNAPSHOT depends on javax.websocket-api-1.0-b13 which is NOT the latest version of the specification.

At this moment, the latest promoted build of GlassFish (b77) is shipped with tyrus-1.0-b11 which depends on javax.websocket-api-1.0b12
and this version is very different from 1.0-b13.
Hence, in order to run the server, you have to build the tyrus trunk and copy the jar files into the
glassfish/modules directory.

The client is a JavaFX 2.x client, depending on the same javax.websocket-api 1.0-b13.
