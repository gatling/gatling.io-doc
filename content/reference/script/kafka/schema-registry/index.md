---
title: Schema Registry
seotitle: Gatling Kafka protocol reference - Avro, Protobuf, and JSON Schema with a schema registry
description: How to produce Avro, Protobuf, and JSON Schema keys and values with a Confluent schema registry in Gatling, and how to decode and check the replies.
lead: Learn how to produce Avro, Protobuf, and JSON Schema records with a schema registry, and check their replies.
date: 2026-11-05T00:00:00+00:00
---

## Understand schema registry support

Gatling produces Avro, Protobuf, and JSON Schema keys and values with a schema registry, with [Confluent's serializers](https://docs.confluent.io/platform/current/schema-registry/fundamentals/serdes-develop/index.html), so that the system under test reads them as it reads the records of its own producers.

You write the key or the value as JSON, with any Gatling body, and Gatling converts it with its schema, then serializes it with Confluent's serializer for its format.
Serialization is part of the response time of the request, see [where serialization happens](#serialization).

## Add Confluent's serializers {#serializers}

The plugin doesn't bring Confluent's serializers. Add the serializer of each format you use, from Confluent's repository, `https://packages.confluent.io/maven/`, in the same scope as the Gatling dependencies:

| **Format** | **Serializer dependency** |
| --- | --- |
| Avro | `io.confluent:kafka-avro-serializer` |
| Protobuf | `io.confluent:kafka-protobuf-serializer` |
| JSON Schema | `io.confluent:kafka-json-schema-serializer` |

For example, for Avro:

{{< include-file >}}
1-Maven: includes/serializer.maven.md
2-Gradle: includes/serializer.gradle.md
3-sbt: includes/serializer.sbt.md
{{< /include-file >}}

Confluent's serializers bring Confluent's own build of the Kafka clients (`org.apache.kafka:kafka-clients` with a `-ccs` version), which can replace the Apache Kafka clients the plugin depends on. The plugin is tested with both.
Gatling Enterprise runs your simulations with the Apache Kafka clients the plugin depends on, whatever version your project resolves locally.

The plugin is tested with Confluent's serializers 8.4.0.
Gatling works with Protobuf 3.x and 4.x, whichever version the Protobuf serializer brings.

{{< alert warning >}}
The Protobuf and JSON Schema serializers bring jars distributed under the [Confluent Community License](https://www.confluent.io/confluent-community-license/):
`io.confluent:kafka-protobuf-provider` and `io.confluent:kafka-json-schema-provider`.
Check that this license suits your use before adding them.
{{< /alert >}}

When a simulation uses a format whose serializer is missing, it fails at startup, naming the dependency to add.

## Configure the schema registry {#registry}

Configure the schema registry on the protocol:

{{< include-code "registry" java kt scala >}}

| **Option** | **Description** |
| --- | --- |
| `schemaRegistry` | The URL of the schema registry, or several ones, comma-separated. |
| `schemaRegistryBasicAuth` | The username and password of the registry's basic authentication. |
| `schemaRegistryProperty`, `schemaRegistryProperties` | Properties of Confluent's serializers and deserializers, such as `auto.register.schemas`, `use.latest.version`, or `value.subject.name.strategy`. `schema.registry.url` is refused: use `schemaRegistry` instead. |

Confluent's serializers register the schema of a key or a value in the registry, under the subject that the subject name strategy derives from the topic, unless you change it with these properties.
Refer to [Confluent's documentation](https://docs.confluent.io/platform/current/schema-registry/fundamentals/serdes-develop/index.html) for the available properties.

## Produce Avro, Protobuf, and JSON Schema values {#values}

Pass a schema registry body to `value`, with the schema and the payload, a Gatling body that renders JSON.

### Avro {#avro}

`avro` takes an Avro schema, and a payload in Avro's JSON encoding, where the non-null value of a union is wrapped in an object keyed by its type:

{{< include-code "avro" java kt scala >}}

### Protobuf {#protobuf}

`protobuf` takes a `.proto` schema, whose first message is the value's, and a payload in the [Protobuf JSON mapping](https://protobuf.dev/programming-guides/json/):

{{< include-code "protobuf" java kt scala >}}

### JSON Schema {#json-schema}

`jsonSchema` takes a JSON schema, and a JSON payload:

{{< include-code "json-schema" java kt scala >}}

The serializer only validates the payload against the schema with the schema registry property `json.fail.invalid.schema` set to `true`.

### Use the schema of a subject {#subject}

`avroSubject`, `protobufSubject`, and `jsonSchemaSubject` use the latest schema of a subject in the registry, instead of a schema in the simulation.
Gatling reads it once per run, when the first request uses it.

{{< include-code "subject" java kt scala >}}

The format of the subject's schema must be the format of the body, and Gatling doesn't resolve schema references: a schema referencing other schemas fails.

## Produce schema registry keys {#keys}

Keys accept the same schema registry bodies as values:

{{< include-code "key" java kt scala >}}

{{< alert warning >}}
A request-reply whose key uses a schema registry can't match its reply by key, as its bytes are only known once serialized: use [`matchByHeader`]({{< ref "/reference/script/kafka/protocol#matching" >}}).
And `matchByValue` can't correlate values serialized with a schema registry: use `matchByKey` or `matchByHeader`.
In both cases, the simulation fails at startup.
{{< /alert >}}

## Understand where serialization happens {#serialization}

Confluent's serializers may call the schema registry, to register or look up a schema, so Gatling serializes the first key or value of a schema, topic, and format on a [sender thread]({{< ref "/reference/script/kafka/protocol#senders" >}}), never on the threads of the virtual users.
Once the serializer has cached the schema, the virtual users serialize the next ones themselves, which spreads serialization over their threads.

When you set `use.latest.version` to `true`, or set `latest.cache.ttl.sec`, with `schemaRegistryProperty`, the serializer looks the schema up again after a while, so Gatling keeps serializing on the sender threads.

In both cases, serialization is part of the response time of the request.

## Decode replies {#decode}

When the system under test replies with Avro, Protobuf, or JSON Schema, decode the reply of a request-reply with `decodeAvroReply`, `decodeProtobufReply`, or `decodeJsonSchemaReply`, with the deserializer of Confluent's serializer dependency:

{{< include-code "decode" java kt scala >}}

The decoded reply is JSON, in the Protobuf JSON mapping for Protobuf, with the fields at their default value included:

- `bodyString`, `substring`, `regex`, `jsonPath`, and `jmesPath` read the decoded JSON
- `bodyBytes`, `bodyLength`, and `bodyStream` still read the record's bytes

Decoding happens outside of the response time. A reply that can't be decoded fails the request.
`consume` and subscriptions don't decode: their checks read the record's value as is.
