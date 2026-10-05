---
title: GraphQL Subscriptions
seotitle: Gatling GraphQL protocol reference - subscriptions
description: How to connect, subscribe and check messages of GraphQL subscriptions over WebSocket with Gatling.
lead: Learn how to run GraphQL subscriptions over WebSocket, and check the messages they push.
date: 2026-09-30T00:00:00+00:00
---

Gatling supports GraphQL subscriptions over WebSocket with the [`graphql-transport-ws`](https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md) protocol.

A virtual user has one WebSocket, over which it can multiplex several subscriptions.
Each subscription carries an id and messages are matched on it.

## Prerequisites

Gatling GraphQL SDK is not imported by default.

You have to manually add the following imports:

{{< include-code "imports" java kt scala >}}

You must register the [GraphQL protocol]({{< ref "/reference/script/graphql/protocol" >}}) and configure `wsBaseUrl` on the HTTP protocol.
The WebSocket path is the protocol `wsEndpoint`.

## Connect

Use `graphqlWs.connect` to open the WebSocket and perform the `connection_init` / `connection_ack` handshake.
You must connect before you subscribe.

{{< include-code "connect" java kt scala >}}

* `requestName`: override the request name.
* `endpoint`: override the protocol `wsEndpoint`.
* `connectionInitPayload`: set entries of the `connection_init` message payload, typically where the server expects credentials.
* `connectionInitPayloadJson`: set the whole payload at once as a JSON object that can contain Gatling Expression Language placeholders.
  It can't be combined with `connectionInitPayload`.
* `ackTimeout`: override how long to wait for the `connection_ack` message (default: 10 seconds).

Gatling reports the handshake under the names `graphql-ws connect`, `connection_init` and `connection_ack`.

## Subscribe

Use `subscribe` to subscribe with an inline document, or `subscribeFile` to load the document from a classpath resource.
The document must declare a subscription and be known when the Simulation is built.

Like [requests]({{< ref "/reference/script/graphql/requests" >}}), a subscription supports `requestName`, `operationName`, `variable`, `variables` and `variablesJson`.

{{< include-code "subscribe" java kt scala >}}

Use `subscriptionName` to set the id the subscription is registered under, which `unsubscribe` needs.
It defaults to the operation name.
A virtual user can't have two active subscriptions with the same id: give each concurrent subscription its own `subscriptionName`.

## Wait for messages

By default, `subscribe` doesn't wait for any message.
Use `await` to wait for messages and `checkNext` to define the checks to perform on each of them.
Each `checkNext` expects one single `next` message, and messages are reported on their own, under the name `<request name> next`.
Gatling measures the response time of each message from the message that came before it.
It's the inter message latency rather than the time since the subscription started.

{{< include-code "await" java kt scala >}}

* `checkNext` accepts a name to report the message under.
* `check` accepts the same criteria as [GraphQL requests]({{< ref "/reference/script/graphql/requests#checks" >}}), except that you use `graphqlWs.data`, `graphqlWs.errors` and `graphqlWs.extensions` in place of `graphql.data`, `graphql.errors` and `graphql.extensions`.
* `postCheck` applies a function on the Session resulting from the checks.
* `silent` doesn't report the message in the statistics.

The [error policy]({{< ref "/reference/script/graphql/protocol#error-handling" >}}) of the protocol applies to the messages.

Use `awaitNext` to wait for some more messages, with a timeout for each of them.

{{< include-code "await-next" java kt scala >}}

{{< alert tip >}}
If the server ends the subscription with a `complete` or an `error` message that goes through a check, Gatling considers the subscription is no longer active.
{{< /alert >}}

## Unsubscribe

Use `unsubscribe` with the subscription name to tell the server to stop the subscription.
By default, Gatling reports it under the name `subscription <subscription name> complete`.

{{< include-code "unsubscribe" java kt scala >}}

## Close

Use `close` to close the WebSocket. Gatling reports it under the name `graphql-ws close`.

{{< include-code "close" java kt scala >}}

## Reconnection

When Gatling reconnects the WebSocket, it performs the handshake again and subscribes again to the subscriptions that were active, the way real clients do, as the server forgot about them.

## Example

{{< include-code "example" java kt scala >}}
