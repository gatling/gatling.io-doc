---
title: SSE (Server Sent Event)
seotitle: Gatling SSE protocol reference
description: How to use the Server-Sent Events (SSE) support for connecting and performing checks on inbound messages.
lead: "Learn the possible SSE operations with Gatling: connect, close."
date: 2021-04-20T18:30:56+02:00
aliases:
  - /reference/script/protocols/sse/
---

SSE support is an extension to the HTTP DSL, whose entry point is the `sse(requestName: Expression[String])` method.

## `sseName`

If you want to deal with several SSE streams per virtual users, you have to give them a name and pass this name on each SSE operation:
For example:

{{< include-code "sseName" >}}

Of course, this step is not required if you deal with one single SSE stream per virtual user.

## Connecting

The first thing is to connect the stream:

Gatling supports `GET` and `POST` requests:

{{< include-code "sseConnect" >}}

{{< alert tip >}}
Gatling automatically sets `Accept` header to `text/event-stream` and `Cache-Control` to `no-cache`.
{{< /alert >}}

## `close`

Once you're done with a SSE stream, you can close it.

{{< include-code "sseClose" >}}

## Checks

You deal with incoming messages with checks.

Beware of not missing messages that would be received prior to setting the check.

Gatling currently only supports blocking checks that will wait until receiving expected message or timing out.

When using SSE checks, Stream Events get turned into JSON, eg

```
id: 77535
event: snapshot
data: [{"symbol":"BTC","price":4628.27},{"symbol":"ETH","price":3249.19}]
```

is turned into:

```json
{
  "id": 77535,
  "event": "snapshot",
  "data": [
    {"symbol":"BTC","price":4628.27},
    {"symbol":"ETH","price":3249.19}
  ]
}
```

This way, you can apply checks on any Stream field with `jsonPath` and `jmesPath`.

### Set a check

You can set a check right after connecting:

{{< include-code "check-from-connect" >}}

Or you can set a check from main flow:

{{< include-code "check-from-flow" >}}

You can set multiple checks sequentially. Each one will expect one single frame.

You can configure multiple checks in a single sequence:

{{< include-code "check-single-sequence" >}}

You can also configure multiple check sequences with different timeouts:

{{< include-code "check-multiple-sequence" >}}

### Create a check

You can create checks for server events with `checkMessage`.
You can use almost all the same check criteria as for HTTP requests.

{{< include-code "create-single-check" >}}

You can have multiple criteria for a given message:

{{< include-code "create-multiple-checks" >}}

### Matching messages

You can define `matching` criteria to filter messages you want to check.
Matching criterion is a standard check, except it doesn't take `saveAs`.
Non-matching messages will be ignored.

{{< include-code "check-matching" >}}

## Processing unmatched messages

You can use `processUnmatchedMessages` to process inbound messages that haven't been matched with a check and have been buffered.
By default, unmatched inbound messages are not buffered, you must enable this feature by setting the size of the buffer on the protocol with `.sseUnmatchedInboundMessageQueueSize(maxSize)`.
The buffer is reset when:
* sending an outbound message
* calling `processUnmatchedMessages` so we don't present the same message twice

You can then pass your processing logic as a function.
The list of messages passed to this function is sorted in timestamp ascending (meaning older messages first).
It contains instances of types `io.gatling.http.action.sse.SseInboundMessage`.

Reminder: messages are JSON string versions of the events.

{{< include-code "process" >}}

## Configuration

SSE support introduces new HttpProtocol parameters:

{{< include-code "protocol" >}}

## Debugging

You can inspect streams if you add the following logger to your logback configuration:

```xml
<logger name="io.gatling.http.action.sse.fsm" level="DEBUG" />
```

## Example

Here's an example that runs against a stock market sample:

{{< include-code "stock-market-sample" >}}
