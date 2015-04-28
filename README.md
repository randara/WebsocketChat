# WebSocket Chat

This is an exercise. Implement a simple chat server in Java. Based on WebSocket standalone lib http://java-websocket.org/

## Features

* One-to-one textual IMs
* Contact lists
* Presence 

# Compile and run server

```
mvn clean install
```

```
java -jar target/chat-1.0-SNAPSHOT-jar-with-dependencies.jar
```
# How to test

A Chrome WebSocket plugin can be used as a client. Tested with https://github.com/hakobera/Simple-WebSocket-Client

URL = ws://localhost:8887

# Messages Examples

## HELLO
{"content":"","sender":"bob","receiver":"server","messageType":"HELLO"}

## BYE
{"content":"","sender":"bob","receiver":"server","messageType":"BYE"}


## TEXT
{"content":"Hola.","sender":"bob","receiver":"alice","messageType":"TEXT"}


## SEARCH
{"content":"alice","sender":"bob","receiver":"server","messageType":"SEARCH"}


## INVITE
{"content":"","sender":"bob","receiver":"alice","messageType":"INVITE"}


## ACCEPT
{"content":"","sender":"alice","receiver":"bob","messageType":"ACCEPT"}


## DENY
{"content":"","sender":"alice","receiver":"bob","messageType":"DENY"}


# TODOs

* Persistence
* Java Client
* Javascript Client
* Client tests
* Integration Tests
* Auth
* Security Layer