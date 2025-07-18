---
title: WebSocket
seotitle: Gatling WebSocket protocol reference
description: How to use the WebSocket support for connecting and performing checks on inbound frames.
lead: "Learn the possible WebSocket operations with Gatling: connect, close, send."
date: 2021-04-20T18:30:56+02:00
aliases:
  - /reference/script/protocols/websocket/
---

WebSocket support was initially contributed by [Andrew Duffy](https://github.com/amjjd).

WebSocket support is an extension to the HTTP DSL, whose entry point is the `ws` method.

WebSocket protocol is very different from the HTTP one as the communication is 2 ways: both client-to-server and server-to-client, so the model is different from the HTTP request/response pair.

As a consequence, the main HTTP branch and a WebSocket branch can exist in a Gatling scenario in a dissociated way, in parallel.
When doing so, each flow branch has its own state, so a user might have to reconcile them, for example when capturing data from a WebSocket check and wanting this data to be available to the HTTP branch.

## `wsName`

If you want to deal with several WebSockets per virtual users, you have to give them a name and pass this name on each ws operation:

{{< include-code "wsName" >}}

If you set an explicit name for the WebSocket, you'll have to make it explicit for every other WebSocket actions you'll define later in the scenario.

Of course, this step is not required if you deal with one single WebSocket per virtual user.

## `connect`

The first thing is to connect a WebSocket:

{{< include-code "wsConnect" >}}

You can specify a subprotocol:

{{< include-code "subprotocol" >}}

You can define a chain of actions to be performed after (re-)connecting with `onConnected`:

{{< include-code "onConnected" >}}

## `close`

Once you're done with a WebSocket, you can close it:

{{< include-code "close" >}}

## Send a message

You may send text or binary messages:

* `sendText(text: Expression[String])`
* `sendBytes(bytes: Expression[Array[Byte]])`

For example:

{{< include-code "send" >}}

Note that:

* `ElFileBody`, `PebbleStringBody` and `PebbleFileBody` can be used with `sendText`
* `RawFileBody` and `ByteArrayBody` can be used with `sendBytes`.

See [HTTP request body]({{< ref "http/request#request-body" >}}) for more information.

## Checks

Gatling currently only supports blocking checks that will wait until receiving expected message or timing out.

### Set a check

You can set a check right after connecting:

{{< include-code "check-from-connect" >}}

Or you can set a check right after sending a message to the server:

{{< include-code "check-from-message" >}}

You can set multiple checks sequentially. Each one will expect one single frame.

You can configure multiple checks in a single sequence:

{{< include-code "check-single-sequence" >}}

You can also configure multiple check sequences with different timeouts:

{{< include-code "check-multiple-sequence" >}}

### Create a check

You can create checks for text and binary frames with `checkTextMessage` and `checkBinaryMessage`.
You can use almost all the same check criteria as for HTTP requests.

{{< include-code "create-single-check" >}}

You can have multiple criteria for a given message:

{{< include-code "create-multiple-checks" >}}

checks can be marked as `silent`.
Silent checks won't be reported whatever their outcome.

{{< include-code "silent-check" >}}

### Matching messages

You can define `matching` criteria to filter messages you want to check.
Matching criterion is a standard check, except it doesn't take `saveAs`.
Non-matching messages will be ignored.

{{< include-code "matching" >}}

## Processing unmatched messages

You can use `processUnmatchedMessages` to process inbound messages that haven't been matched with a check and have been buffered.
By default, unmatched inbound messages are not buffered, you must enable this feature by setting the size of the buffer on the protocol with `.wsUnmatchedInboundMessageQueueSize(maxSize)`.
The buffer is reset when:
* sending an outbound message 
* calling `processUnmatchedMessages` so we don't present the same message twice

You can then pass your processing logic as a function.
The list of messages passed to this function is sorted in timestamp ascending (meaning older messages first).
It contains instances of types `io.gatling.http.action.ws.WsInboundMessage.Text` and `io.gatling.http.action.ws.WsInboundMessage.Binary`.

{{< include-code "process" >}}

JavaScript/TypeScript has two utility methods to help differentiate binary and text messages, both imported from `@gatling.io/http`:

- `isWsInboundMessageBinary`
- `isWsInboundMessageText`

## Configuration

WebSocket support introduces new HttpProtocol parameters:

{{< include-code "protocol" >}}

## Debugging

You can inspect WebSocket traffic if you add the following logger to your logback configuration:

```xml
<logger name="io.gatling.http.action.ws.fsm" level="DEBUG" />
```

## Example

Here's an example that runs against [Play 2.2](https://www.playframework.com/download#older-versions)'s chatroom sample (beware that this sample is missing from Play 2.3 and above):

{{< include-code "chatroom-example" >}}
