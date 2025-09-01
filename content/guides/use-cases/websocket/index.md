---
menutitle: Testing WebSocket
title: How to test the WebSocket protocol
seotitle: Learn how to load test WebSocket with Gatling
lead: Learn how to load test WebSocket with Gatling
date: 2025-04-03T13:35:00+02:00
aliases:
  - /guides/complex-use-cases/websocket/
---

{{< alert info >}}
This guide showcases the Java SDK, but the same process applies to the Kotlin and Scala SDKs
{{< /alert >}}

WebSocket is a bidirectional communication protocol enabling real-time data exchange between clients and servers. Unlike HTTP, WebSockets maintain a persistent connection for instant, efficient communication. WebSockets are ideal for applications needing real-time updates, like live chat, multiplayer gaming, collaborative editing, in-app notifications, and trading platforms, enhancing user experience and operational efficiency.

## Prerequisites

- Gatling version `{{< var gatlingVersion >}}` or higher
- Clone [the Gatling `devrel` repository](https://github.com/gatling/devrel-projects) and cd into the `articles/websocketguide` folder.

## Launch the Server

Let's create a WebSocket server in JavaScript that we will load test:

{{< include-code "websocketserver#web-example" js >}}

This code creates a WebSocket server on port `8765`. When someone connects, it sends a random number of messages with a random interval between `0` and `1` second between each message. 

Now let's load test it.

## Test Creation

### Create the Scenario

In this guide, we create a scenario using Gatling. Our user connects to the server, checks for incoming messages, and prints the rest of the messages:

{{< include-code "WebSocketSample#websocket-example" java >}}

The `httpProtocol` allows Gatling to connect to our WebSocket application. After that, we connect to our application and check if the server returns the correct message. `ProcessUnmatchedMessage` processes inbound messages that haven’t been matched with a check and have been buffered. In our case, this allows us to display the messages the server sends.

### Generate the User

We start with a single user to verify our simulation works correctly. To do that, add the following code to your simulation:

{{< include-code "WebSocketSample#user-example" java >}}

### Running the Test

To run the simulation:

1. Place the simulation file in your project's test directory.
2. Run using Maven:

```bash
mvn gatling:test
```

## Best Practices

1. Use secure WebSocket connections (wss://).
2. Use a buffer or queue to handle incoming messages asynchronously.
3. Add appropriate pauses between requests to simulate real user behavior.
4. Include proper error handling.

This basic implementation should get you started with load testing WebSocket applications. If you need a more detailed use case, you can see the list of available commands [here]({{< ref "/reference/script/websocket/" >}}).
