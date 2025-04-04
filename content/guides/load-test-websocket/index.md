---
title: How to load test websocket
menutitle: Load test websocket
seotitle: How to load test websocket using Gatling
description: Set up websocketload test with Gatling
lead: Learn how to implement and run websocket load tests with Gatling
date: 2025-04-03T13:35:00+02:00

---

WebSocket is a bidirectional communication protocol enabling real-time data exchange between clients and servers. Unlike HTTP, WebSockets maintain a persistent connection for instant, efficient communication. WebSockets are ideal for applications needing real-time updates, like live chat, multiplayer gaming, collaborative editing, in-app notifications, and trading platforms, enhancing user experience and operational efficiency. In this guide we will use how to load test this type of application using Gatling.

## Prerequisites

- Gatling version `{{< var gatlingVersion >}}` or higher
- An account on Gatling Entreprise
- Clone [this code](https://github.com/gatling/devrel-projects) and cd into the `articles/websocketguide` folder.

## Launch the server

Let's create a websocket server in javascript that we will load test after:

{{< include-code "websocketserver#web-example" js>}}

This code create a websocket server on the port `8765` and when someone connect we send a random number of messages with a random interval between `0` and `1` seconde between each messages. Now let's load test it.


## Test Creation

### Create the Scenario

In this tutorial, we will create a small scenario using Gatling. Our user will connect to the server and check for incomming message and print the rest of the messages:


{{< include-code "WebSocketSample#websocket-example" java>}}

The `httpProtocol` permit to Gatling to connect to our websocket application, after that we connect to our application and check if the server return us the good message. `ProcessUnmatchedMessage` permit to process inbound messages that haven’t been matched with a check and have been buffered, in our case this permit us to display the message that the server send. If you need to have a more detailled use case, you can see the list of available command [here]({{< ref "../reference/script/protocols/websocket/" >}}).

### Generate the user

We start with a single user to verify our simulation works correctly. In order to do that add this code in your simulation

{{< include-code "WebSocketSample#user-example" java>}}


### Running the Test

To run the simulation:

1. Place the simulation file in your project's test directory
2. Run using Maven:

```bash
mvn gatling:test
```

## Best Practices

1. Use secure WebSocket connections (wss://) 
2. Use a buffer or queue to handle incoming messages asynchronously
3. Add appropriate pauses between requests to simulate real user behavior
4. Include proper error handling

This basic implementation should get you started with load testing websocket application. Adjust the parameters and scenarios based on your specific needs.