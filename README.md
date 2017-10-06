demo-ws-chat
============

## Install

`mvn clean install`

## Usage

### Server

First of all, start the `/chat` server

`java -cp target/demo-ws-chat-*-uberjar.jar com.romajs.ws.WebSocketServerApplication`

From server, type any key to exit, after server start.

### Client

Then you can spawn multiple clients, to connect into the `/chat` server.

`java -cp target/demo-ws-chat-*-uberjar.jar com.romajs.ws.client.ChatClientEndpoint`

Each client receives messages from another clients, but doesn't receive any message from itself.

From clients, type `exit` to close the session and exit.
