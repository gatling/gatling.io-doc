---
title: SSE (Server Sent Event)
seotitle: Gatling SSE protocol reference
description: How to use the Server-Sent Events (SSE) support for connecting and performing checks on inbound messages.
lead: "Learn the possible SSE operations with Gatling: connect, close."
date: 2021-04-20T18:30:56+02:00
aliases:
  - /reference/script/protocols/sse/
---

SSE support is an extension to the HTTP SDK, whose entry point is the `sse(requestName: Expression[String])` method.

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

By default, a check only matches the messages received after it's set.
To also match messages received earlier, see [Match messages received before setting a check]({{< ref "#match-messages-received-before-setting-a-check" >}}).

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
  "id": "77535",
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

### Match messages received before setting a check

By default, a `setCheck` only matches the messages received after it's set.
If the expected message arrives earlier, for example as a consequence of a previous HTTP request, the `setCheck` times out.

To prevent this, enable the setCheck buffer on the HttpProtocol with `sseSetCheckBuffer(maxSize, maxAge)`:

| Parameter | Description |
| --- | --- |
| `maxSize` | The maximum number of buffered messages. When the buffer is full, Gatling drops the oldest message. Must be strictly positive. |
| `maxAge` | The maximum age of buffered messages. Gatling drops older messages. Must be strictly positive. |

{{< include-code "setCheckBuffer" >}}

With the buffer enabled:

- Gatling buffers the inbound messages that no check matched: the messages received while no check is pending, and the messages that don't match the pending check.
- When you call `setCheck`, Gatling first replays the buffered messages against the new checks, oldest first, as if they were received when the check was set. Matched messages leave the buffer, and the other ones stay for the next `setCheck`.
- When a check of a sequence passes, Gatling retries the buffered messages that didn't match it against the next checks of the sequence.
- When the stream automatically reconnects, Gatling keeps the buffer, as the stream resumes from the last received event ID (`Last-Event-ID`).

Only `setCheck` replays buffered messages: checks set on `get` or `post` only match messages received after connecting.
This buffer is independent from the one used by [`processUnmatchedMessages`]({{< ref "#processing-unmatched-messages" >}}).

{{< alert info >}}
`sseSetCheckBuffer` is available since Gatling 3.16.1 and the JavaScript and TypeScript SDK 3.16.101.
{{< /alert >}}

### Create a check

You can create checks for server events with `checkMessage`.
You can use almost all the same check criteria as for HTTP requests.

{{< include-code "create-single-check" >}}

You can have multiple criteria for a given message:

{{< include-code "create-multiple-checks" >}}

You can also apply a function on the Session resulting from the check, after it's been applied, with `postCheck`.
Throw an exception to make the check fail with the exception's message (in Scala, the function returns a `Validation`: a `Success` wrapping the new Session, or a `Failure`).

{{< include-code "post-check" >}}

### Apply checks conditionally

Use `checkIf` to apply checks only when a condition holds.
The condition is a Gatling Expression Language String that resolves a Boolean, or a function of the Session and, optionally, of the inbound message (the event, as a JSON String).

{{< include-code "check-if" >}}

{{< alert warning >}}
The argument order of the message-aware function differs between SDKs.
In JavaScript and TypeScript, the Session comes first, so you can pass either `(session) => …` or `(session, message) => …`.
In Java, Kotlin, and Scala, the message comes first: `(message, session)`.
{{< /alert >}}

### Matching messages

You can define `matching` criteria to filter messages you want to check.
Matching criterion is a standard check, except it doesn't take `saveAs`.
Non-matching messages will be ignored.

{{< include-code "check-matching" >}}

## Processing unmatched messages

You can use `processUnmatchedMessages` to process inbound messages that haven't been matched with a check and have been buffered.
By default, unmatched inbound messages are not buffered, you must enable this feature by setting the size of the buffer on the protocol with `.sseUnmatchedInboundMessageBufferSize(maxSize)`.
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
