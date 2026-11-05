---
title: Actions
seotitle: Gatling Kafka protocol reference - send, request-reply, consume, and subscribe actions
description: How to produce Kafka records with Gatling, measure request-reply latency, and wait for the records another system produces, blocking or not, to measure end-to-end latency.
lead: Learn how to produce records, wait for their replies, and measure the end-to-end latency of a pipeline.
date: 2026-11-05T00:00:00+00:00
---

## Understand Kafka actions

Kafka actions start with `kafka(requestName)`, where the request name is a Gatling Expression Language String or a function.
Then pick what the action does:

| **Action** | **Does** | **Response time** |
| --- | --- | --- |
| [`send(topic)`](#send) | Produces a record. | Until the broker acknowledges the record. |
| [`requestReply(requestTopic, replyTopic)`](#request-reply) | Produces a record, then waits for its reply. | Until the reply is read. |
| [`consume(topic)`](#consume) | Waits for a record that another system produces. | From when the action starts, or from a timestamp you provide, until the record is read. |
| [`subscribe(topic)`](#subscribe) | Starts waiting for a record that another system produces, without blocking the virtual user, which collects it later with `kafka.poll`. | From when the subscription starts, or from a timestamp you provide, until the record is read. |

Topics are Gatling Expression Language Strings or functions.

## Produce records {#send}

`send` produces a record on a topic. Its value is required:

{{< include-code "send" java kt scala >}}

The record also accepts:

| **Option** | **Description** |
| --- | --- |
| `key` | The record's key: a Gatling Expression Language String or a function, encoded as UTF-8, a Gatling body, or a [schema registry body]({{< ref "/reference/script/kafka/schema-registry" >}}). |
| `header(name, value)` | A header. The name and the value are Gatling Expression Language Strings, the value can also be a function. The value is encoded as UTF-8. Call it once per header. |
| `partition` | The record's partition: an integer, a Gatling Expression Language String, or a function. Without it, the producer's partitioner picks the partition, typically from the key. |
| `subscribe` | A subscription to a record that another system produces because of this one, tracked before the record is sent, see [attach subscriptions](#subscribe-attached). Call it once per subscription. |

{{< include-code "send-options" java kt scala >}}

### Set the value {#value}

The value is any Gatling body, as with [HTTP request bodies]({{< ref "/reference/script/http/request#request-body" >}}).
Text bodies are encoded with their charset, and byte bodies are sent as is:

{{< include-code "bodies" java kt scala >}}

The value can also be an Avro, Protobuf, or JSON Schema value, serialized with a [schema registry]({{< ref "/reference/script/kafka/schema-registry" >}}).

### Use functions {#functions}

The request name, the topic, the key, the header values, and the partition can be functions of the session:

{{< include-code "functions" java kt scala >}}

### Understand what Gatling measures {#send-response-time}

The response time of `send` lasts from when the virtual user sends the record until the broker acknowledges it, as the producer's `acks` property defines.
It includes:

- waiting for a [sender thread]({{< ref "/reference/script/kafka/protocol#senders" >}})
- the serialization with the schema registry, if any
- the producer's batching, see [`linger.ms`]({{< ref "/reference/script/kafka/protocol#linger" >}})
- sending the record to the broker and waiting for the acknowledgement

A record that the producer fails to send fails the request, with the cause.

## Send a request and wait for its reply {#request-reply}

`requestReply` produces a request on the request topic, then waits for the reply that the system under test produces on the reply topic.
It accepts the same `value`, `key`, `header`, and `partition` as `send`.

{{< include-code "request-reply" java kt scala >}}

The request-reply also accepts:

| **Option** | **Description** |
| --- | --- |
| `check` | [Checks]({{< ref "/reference/script/kafka/checks" >}}) on the reply. |
| `replyTimeout` | How long to wait for the reply, overriding the protocol's. The Java and Kotlin `replyTimeout` also takes a number of seconds. |
| `decodeAvroReply`, `decodeProtobufReply`, `decodeJsonSchemaReply` | Decode the reply with the [schema registry]({{< ref "/reference/script/kafka/schema-registry#decode" >}}) before the checks. |
| `subscribe` | A subscription to another record that the system produces because of this request, besides its reply, tracked before the request is sent, see [attach subscriptions](#subscribe-attached). |

Gatling matches the reply to its request with the correlation id that the protocol defines: by default, the reply has the request's key.
See [how to match replies to their requests]({{< ref "/reference/script/kafka/protocol#matching" >}}).

The response time of a request-reply lasts from when the request is sent until its reply is read.
Checks then run on the reply, outside of the response time.
A reply that doesn't come within the reply timeout fails the request.

Before sending the first request on a reply topic, Gatling waits for its consumer to be ready to read the replies on this topic.
This wait isn't part of the response time.

## Wait for a record from another system {#consume}

`consume` doesn't produce anything: it blocks the virtual user until a record that another system produces on a topic is read, typically a pipeline that processes a record sent earlier, and measures the end-to-end latency.
`correlationId` provides the correlation id of the record to wait for, a Gatling Expression Language String or a function, matched as the protocol defines, like [a reply]({{< ref "/reference/script/kafka/protocol#matching" >}}).

{{< include-code "consume" java kt scala >}}

The consume also accepts:

| **Option** | **Description** |
| --- | --- |
| `check` | [Checks]({{< ref "/reference/script/kafka/checks" >}}) on the record. |
| `timeout` | How long to wait for the record, overriding the protocol's `replyTimeout`. The Java and Kotlin `timeout` also takes a number of seconds. |
| `startTimestampFrom(attributeName)` | The session attribute holding the start of the response time, in epoch milliseconds. |

By default, the response time lasts from when the action starts until the record is read.
With `startTimestampFrom`, it starts at the timestamp in the session attribute instead, typically saved right before sending the record that the pipeline processes.
A record read before this timestamp is ignored, as it's an older record with the same correlation id.

The timeout counts from when the virtual user starts waiting.
A record that doesn't come within the timeout fails the request.

The record may be read before the virtual user starts waiting for it, while it runs other actions: Gatling keeps the records that no virtual user waits for yet, by default for up to 60 seconds and up to 10,000 records, the oldest being dropped first.
On a busy topic, 10,000 records may cover less time than the virtual user takes to start waiting, and the record it waits for is dropped before: the consume times out although the record came.
Gatling then logs a warning naming the protocol's [`consumeBuffer`]({{< ref "/reference/script/kafka/protocol#consume-buffer" >}}) option, which raises these limits.

When the topic is a static String, Gatling reads it from when the simulation starts.
When it's a Gatling Expression Language String or a function, Gatling reads it from when a virtual user first waits on it.

For a record that comes from a record or a request that Gatling sends, prefer a [subscription attached to the send](#subscribe-attached): it doesn't depend on the kept records, and it doesn't need `startTimestampFrom`.

## Subscribe to records without blocking {#subscribe}

`subscribe` starts waiting for a record that another system produces on a topic, and lets the virtual user continue right away, for example with the HTTP request that makes the system produce the record.
Later, `kafka.poll` (`kafka.poll()` in Java and Kotlin) waits for the virtual user's subscriptions to complete.
`correlationId` provides the correlation id of the record, a Gatling Expression Language String or a function, matched as the protocol defines, like [a reply]({{< ref "/reference/script/kafka/protocol#matching" >}}).

{{< include-code "subscribe" java kt scala >}}

The subscription accepts the same options as [`consume`](#consume): `check`, `timeout`, and `startTimestampFrom`.

### Place a subscription before what makes its record {#subscribe-order}

A subscription is tracked before the virtual user continues, once Gatling's consumer reads the topic, so the record can't come before the subscription.
Place it before the action that makes the system produce the record, such as an HTTP request.

A subscription placed after this action may miss its record, as for a `consume`: it only finds the record among the records Gatling keeps, see [`consumeBuffer`]({{< ref "/reference/script/kafka/protocol#consume-buffer" >}}).

### Attach subscriptions to a send or a request-reply {#subscribe-attached}

`send` and `requestReply` also accept `subscribe`, with a request name, a topic, and a correlation id, or with a subscription built with `kafka(requestName).subscribe(topic)`, to set checks and a timeout.
Gatling tracks the attached subscriptions before sending anything, so the record can't come first.
Call `subscribe` once per subscription.

{{< include-code "subscribe-attached" java kt scala >}}

The response time of an attached subscription lasts from when it starts, right before the record or the request is sent, until the subscription's record is read: the end-to-end latency of the pipeline, without `startTimestampFrom`.
Waiting for Gatling's consumer to read the subscription's topic isn't part of the response time of the `send` or the `requestReply`.
Prefer this form to measure Kafka-to-Kafka pipelines.

### Understand what Gatling measures and when the session changes {#subscribe-response-time}

The response time of a subscription lasts from when it starts, or from the timestamp of `startTimestampFrom`, until its record is read.
Gatling logs it then, or when the subscription times out, whether or not the virtual user polls: polling doesn't change the response time.
The timeout counts from when the subscription starts waiting.

A subscription's checks run when its record is read, on the session as it was when the subscription started.
What they save, and their failures, only apply to the virtual user's session at `kafka.poll`, which waits for every pending subscription to complete, by its record or by its timeout.
A virtual user that doesn't poll keeps its session as is, but its subscriptions are still logged.

A virtual user that ends with pending subscriptions logs them as failed, with `Virtual user ended before its record`.
