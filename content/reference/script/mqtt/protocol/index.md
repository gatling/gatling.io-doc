---
title: MQTT Protocol
seotitle: Gatling MQTT protocol reference - protocol configuration
description: How to use the MQTT support in Gatling to connect to a broker and perform checks against inbound messages.
lead: How to use the MQTT support in Gatling to connect to a broker and perform checks against inbound messages.
date: 2021-04-20T18:30:56+02:00
aliases:
  - /reference/script/protocols/mqtt/protocol/
---

{{< alert warning >}}
The MQTT protocol is not supported by the JavaScript SDK. If this functionality is important to you, add a comment to our [public roadmap](https://portal.productboard.com/gatling/1-gatling-roadmap/c/113-javascript-sdk-expansion?&utm_medium=docs&utm_source=callout)
{{< /alert >}}

{{< alert info >}}
MQTT 3.1, 3.1.1 and 5 are currently supported, but some of the new features introduced in 5 might be missing.
{{< /alert >}}

## Prerequisites

Gatling Enterprise MQTT DSL is not imported by default.

You have to manually add the following imports:

{{< include-code "imports" java kt scala >}}

## MQTT protocol

Use the `mqtt` object in order to create a MQTT protocol.

{{< include-code "protocol-sample" java kt scala >}}

## Request

Use the `mqtt("requestName")` method in order to create a MQTT request.

### `connect`

Your virtual users first have to establish a connection.

{{< include-code "connect" java kt scala >}}

### `subscribe`

Use the `subscribe` method to subscribe to an MQTT topic:

{{< include-code "subscribe" java kt scala >}}

### `publish`

Use the `publish` method to publish a message. You can use the same `Body` API as for HTTP request bodies:

{{< include-code "publish" java kt scala >}}

## MQTT checks

You can define blocking checks with `await` and non-blocking checks with `expect`.
Those can be set right after subscribing, or after publishing:

{{< include-code "check" java kt scala >}}

You can optionally define in which topic the expected message will be received:

You can optionally define check criteria to be applied on the matching received message:

You can use `waitForMessages` and block for all pending non-blocking checks:

{{< include-code "waitForMessages" java kt scala >}}

## Processing unmatched messages

You can use `processUnmatchedMessages` to process inbound messages that haven't been matched with a check and have been buffered.
By default, unmatched inbound messages are not buffered, you must enable this feature by setting the size of the buffer on the protocol with `.unmatchedInboundMessageQueueSize(maxSize)`.
The buffer is reset when:
* sending an outbound message
* calling `processUnmatchedMessages` so we don't present the same message twice

You can then pass your processing logic as a function.
The list of messages passed to this function is sorted in timestamp ascending (meaning older messages first).
It contains instances of type `io.gatling.mqtt.action.MqttInboundMessage`.

{{< include-code "process" java kt scala >}}

## MQTT configuration

MQTT support honors the ssl and netty configurations from `gatling.conf`.

## Example

{{< include-code "example" java kt scala >}}
