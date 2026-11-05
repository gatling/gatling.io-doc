---
title: Actions
seotitle: Gatling Kafka protocol reference - send, request-reply, and expect actions
description: How to produce Kafka records with Gatling, measure request-reply latency, and wait for the records another system produces to measure end-to-end latency.
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
| [`expect(topic)`](#expect) | Waits for a record that another system produces. | From when the action starts, or from a timestamp you provide, until the record is read. |

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

Gatling matches the reply to its request with the correlation id that the protocol defines: by default, the reply has the request's key.
See [how to match replies to their requests]({{< ref "/reference/script/kafka/protocol#matching" >}}).

The response time of a request-reply lasts from when the request is sent until its reply is read.
Checks then run on the reply, outside of the response time.
A reply that doesn't come within the reply timeout fails the request.

Before sending the first request on a reply topic, Gatling waits for its consumer to be ready to read the replies on this topic.
This wait isn't part of the response time.

## Wait for a record from another system {#expect}

`expect` doesn't produce anything: it waits for a record that another system produces on a topic, typically a pipeline that processes a record sent earlier, and measures the end-to-end latency.
`correlationId` provides the correlation id of the record to wait for, a Gatling Expression Language String or a function, matched as the protocol defines, like [a reply]({{< ref "/reference/script/kafka/protocol#matching" >}}).

{{< include-code "expect" java kt scala >}}

The expect also accepts:

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

The record may be read before the virtual user starts waiting for it, while it runs other actions: Gatling keeps the records that no virtual user waits for yet, for up to 60 seconds and up to 10,000 records, the oldest being dropped first.
When the topic is a static String, Gatling reads it from when the simulation starts.
When it's a Gatling Expression Language String or a function, Gatling reads it from when a virtual user first waits on it.
