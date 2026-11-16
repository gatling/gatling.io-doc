---
title: MQTT Protocol
seotitle: Gatling MQTT protocol reference - protocol configuration
description: How to use the MQTT support in Gatling to connect to a broker, publish and subscribe with MQTT 3.1.1 and 5, and perform checks against inbound messages.
lead: How to use the MQTT support in Gatling to connect to a broker and perform checks against inbound messages.
date: 2021-04-20T18:30:56+02:00
aliases:
  - /reference/script/protocols/mqtt/protocol/
---

{{< alert info >}}
Gatling supports MQTT 3.1, 3.1.1 and 5. Enable MQTT 5 with `mqttVersion_5` on the protocol to use the options marked as MQTT 5 only on this page.
Gatling doesn't support MQTT 5 topic aliases and enhanced authentication.
{{< /alert >}}

{{< alert warning >}}
The JavaScript and TypeScript SDK doesn't support the MQTT 5 options, `unsubscribe`, `retain` on `publish`, `asyncNameResolution`, `perUserNameResolution`,
`subscribeTimeout` and `resendAttemptsMax` yet. The samples for these features only cover Java, Kotlin and Scala.
{{< /alert >}}

## Prerequisites

Gatling Enterprise MQTT SDK is not imported by default.

You have to manually add the following imports:

{{< include-code "imports" >}}

## MQTT protocol

Use the `mqtt` object in order to create a MQTT protocol.

{{< include-code "protocol-sample" >}}

### Use per virtual user client certificates

With `useTls(true)`, you can provide a client certificate for each virtual user with `perUserKeyManagerFactory`, either with a function that returns a [javax.net.ssl.KeyManagerFactory](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/KeyManagerFactory.html) (see the sample above) or, more simply, by storing all your keys in a single PKCS#12 file with one key entry (alias) per virtual user.
The password is optional.

{{< include-code "perUserKeyManagerFactory-keystore" >}}

Note that:

* The file is located either on the classpath or as an absolute path on the filesystem.
* Key entries are assigned to virtual users in the alphabetical order of their aliases: the virtual user with id 1 gets the first alias, and so on.
* Each key entry is used by one single virtual user. If the file contains fewer key entries than virtual users, the run is stopped.
* On Gatling Enterprise with multiple load generators, aliases are split so that each load generator gets its own distinct subset, hence a key entry is never used by 2 different load generators.

See [HTTP protocol]({{< ref "/reference/script/http/protocol#peruserkeymanagerfactory" >}}) for a sample script that generates such a keystore.

### Resolve the broker hostname

Gatling resolves the broker hostname on each connect and reconnect, and picks a random address among the resolved ones, so virtual users spread over all the broker's addresses and follow DNS changes.
A DNS resolution failure fails the connect and triggers a reconnect.

By default, Gatling uses Java's blocking DNS resolver, like the HTTP protocol.
Use `asyncNameResolution` to switch to Gatling's non-blocking DNS resolver, and `perUserNameResolution` to give each virtual user its own DNS cache.
These options work like their [HTTP protocol counterparts]({{< ref "/reference/script/http/protocol#asyncnameresolution" >}}).

{{< include-code "dns" java kt scala >}}

### Configure acknowledgement timeouts and resends

Gatling resends the packets the broker doesn't acknowledge within `resendDelay`. Use `resendAttemptsMax` to limit the number of resends, 3 by default.
When the broker still hasn't acknowledged a packet one resend delay after the last resend, the publish, subscribe or unsubscribe fails.
Set `resendAttemptsMax` to 0 to disable resends and turn `resendDelay` into an acknowledgement timeout.

Use `subscribeTimeout` to define how long the broker can take to acknowledge a subscribe, 3 seconds by default.
When the timeout expires, the subscribe fails, but the connection stays open.

{{< include-code "timeouts-resends" java kt scala >}}

### Configure MQTT 5 connection options

With MQTT 5, you can configure:

| Option | Description |
| --- | --- |
| `sessionExpiryInterval` | How long the broker keeps the session after the connection closes. When not set, the session expires with the connection, unless `cleanSession` is `false`, in which case it never expires, like with MQTT 3.1.1. |
| `connectUserProperty` | A user property to send in the CONNECT packet. |
| `publishUserProperty` | A user property to send with every PUBLISH packet, before the publish's own user properties. |

You can call `connectUserProperty` and `publishUserProperty` several times, also with the same name.
Define names and values as Gatling Expression Language Strings or functions.

{{< include-code "mqtt5-protocol" java kt scala >}}

Gatling also respects the limits the broker sends in its CONNACK packet:

- a publish fails without being sent when its QoS exceeds the broker's Maximum QoS, when it's retained and the broker doesn't support retained messages, or when the packet exceeds the broker's Maximum Packet Size
- Gatling applies the broker's Server Keep Alive

### Set MQTT 5 last will properties

With MQTT 5, the last will supports the same properties as [publish](#set-mqtt-5-publish-properties), plus `delayInterval`, the time the broker waits after the connection is lost before publishing the last will.

{{< include-code "mqtt5-last-will" java kt scala >}}

## Request

Use the `mqtt("requestName")` method in order to create a MQTT request.

### `connect`

Your virtual users first have to establish a connection.

{{< include-code "connect" >}}

### `subscribe`

Use the `subscribe` method to subscribe to an MQTT topic:

{{< include-code "subscribe" >}}

#### Set MQTT 5 subscription options

With MQTT 5, set subscription options on `subscribe`. Gatling keeps them when it restores the subscriptions after a reconnect.

{{< include-code "subscribe-mqtt5" java kt scala >}}

#### Tell subscriptions apart with Subscription Identifiers

With MQTT 5, use `subscriptionIdentifier` to set a Subscription Identifier between 1 and 268,435,455 on a subscription.
The broker adds it to the messages it forwards for this subscription.

When a subscribe with a Subscription Identifier has an `await` or `expect`, only the messages carrying this identifier match, which tells apart overlapping subscriptions.
Inbound messages expose their Subscription Identifiers, see [Processing unmatched messages](#processing-unmatched-messages).

{{< include-code "subscription-identifier" java kt scala >}}

#### Use shared subscriptions

With MQTT 5, subscribe to a `$share/<group>/<topic filter>` topic to join a shared subscription: the broker spreads the messages among the subscribers of the group.
`await` and `expect` match the messages received on the topic filter, without the `$share/<group>/` prefix.
A subscribe with `noLocal` on a shared subscription fails, instead of getting the connection closed by the broker.

{{< include-code "shared-subscription" java kt scala >}}

### `unsubscribe`

Use the `unsubscribe` method to unsubscribe from an MQTT topic. Gatling logs the response once the broker acknowledges the UNSUBSCRIBE packet.

{{< include-code "unsubscribe" java kt scala >}}

### `publish`

Use the `publish` method to publish a message. You can use the same `Body` API as for HTTP request bodies:

{{< include-code "publish" >}}

Use `retain` to override the protocol's retain flag for one publish:

{{< include-code "publish-retain" java kt scala >}}

#### Set MQTT 5 publish properties

With MQTT 5, set the following properties on `publish`:

| Property | Description |
| --- | --- |
| `userProperty` | A user property. Call it several times to add several user properties, also with the same name. |
| `contentType` | The content type of the payload. |
| `payloadFormatUtf8` | Flag the payload as UTF-8 encoded text. Brokers may validate it, and refuse the message with `PAYLOAD_FORMAT_INVALID` otherwise. |
| `messageExpiryInterval` | How long the broker keeps the message for subscribers that haven't received it yet. |
| `responseTopic` | The topic responders publish their response to. |
| `correlationData` | The Correlation Data, as a String encoded in UTF-8. |
| `correlationDataBytes` | The Correlation Data, as bytes. |

{{< include-code "publish-mqtt5" java kt scala >}}

### Understand how Gatling handles broker refusals

Gatling marks requests as failed when the broker refuses them:

- subscribe, unsubscribe and publish fail when the broker answers with a failure reason code in SUBACK, UNSUBACK, PUBACK or PUBREC. With MQTT 3.1.1, a SUBACK with the 0x80 failure return code fails the subscribe.
- when the broker refuses the connection in CONNACK for a reason that a new attempt can't fix, such as bad credentials, a client identifier the broker rejects, or a refused last will, Gatling doesn't reconnect.
- when an MQTT 5 broker sends a DISCONNECT packet, Gatling logs the reason and closes the connection. It reconnects, except when the reason is `NOT_AUTHORIZED` or `SESSION_TAKEN_OVER`.

When Gatling reconnects and the broker doesn't have a session for the client anymore, for example with `cleanSession(true)`, Gatling subscribes again to the topics the virtual user subscribed to.

## MQTT checks

You can define blocking checks with `await` and non-blocking checks with `expect`.
Those can be set right after subscribing, or after publishing.
After publishing, you can optionally define in which topic the expected message is received, and define check criteria to apply on the matching received message:

{{< include-code "check" >}}

You can use `waitForMessages` and block for all pending non-blocking checks:

{{< include-code "waitForMessages" >}}

### Match topic filters with wildcards

The topic of `subscribe`, and the expected topic of `publish`, can be a topic filter with the `+` single level and `#` multi level wildcards.
`await` and `expect` then match the messages received on any topic matching the filter.
When a message matches several pending checks, Gatling picks the ones on the exact topic first, then the topic filters in the order the checks were registered.

{{< include-code "check-wildcards" java kt scala >}}

### Correlate responses on the MQTT 5 Correlation Data

With MQTT 5, use `correlateByCorrelationData` instead of `correlateBy` to match responses with the publish expecting them on the Correlation Data property, which responders send back unchanged.
Every publish with `await` or `expect` must then set `correlationData` or `correlationDataBytes`, otherwise Gatling fails when building the simulation.

{{< include-code "correlate-correlation-data" java kt scala >}}

## Processing unmatched messages

You can use `processUnmatchedMessages` to process inbound messages that haven't been matched with a check and have been buffered.
By default, unmatched inbound messages are not buffered, you must enable this feature by setting the size of the buffer on the protocol with `.unmatchedInboundMessageBufferSize(maxSize)`.
The buffer is reset when:
* sending an outbound message
* calling `processUnmatchedMessages` so we don't present the same message twice

You can then pass your processing logic as a function.
The list of messages passed to this function is sorted in timestamp ascending (meaning older messages first).
It contains instances of type `io.gatling.mqtt.action.MqttInboundMessage`.

{{< include-code "process" >}}

The topic can be a topic filter with wildcards: Gatling then merges the messages of all the matching topics, in the order it received them.
With MQTT 5, `subscriptionIdentifiers` returns the Subscription Identifiers of the virtual user's subscriptions the message matched, or an empty list if none of them has one.

{{< include-code "process-subscription-identifiers" java kt scala >}}

## MQTT configuration

MQTT support honors the ssl and netty configurations from `gatling.conf`.

## Example

{{< include-code "example" >}}
