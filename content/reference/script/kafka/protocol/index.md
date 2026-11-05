---
title: Kafka Protocol
seotitle: Gatling Kafka protocol reference - protocol configuration
description: How to configure the Kafka protocol in Gatling, including the brokers, the client properties, SASL and TLS security, reply matching, and producer performance.
lead: Learn how to configure the Kafka protocol, secure the connections to the brokers, and match replies to their requests.
date: 2026-11-05T00:00:00+00:00
---

## Add the imports

The Gatling Kafka SDK isn't imported by default. Add the following imports:

{{< include-code "imports" java kt scala >}}

## Configure the protocol

Use the `kafka` object to create a Kafka protocol, then register it like any other protocol:

{{< include-code "protocol-sample" java kt scala >}}

The protocol accepts the following options:

| **Option** | **Default** | **Description** |
| --- | --- | --- |
| `bootstrapServers` | | The brokers to bootstrap from, as `host:port`. Required. |
| `property`, `properties` | none | Kafka client properties for the producers and the reply consumer. |
| `producerProperty`, `producerProperties` | none | Kafka producer properties, overriding the shared ones. |
| `consumerProperty`, `consumerProperties` | none | Kafka consumer properties for the reply consumer, overriding the shared ones. |
| `saslPlain`, `saslScramSha256`, `saslScramSha512`, `saslOauthBearer`, `saslAwsMskIam`, `saslAwsMskIamProfile`, `saslAwsMskIamRole`, `saslGssapi` | none | The [SASL authentication](#sasl). |
| `useTls`, `trustStore`, `keyStore` | no TLS | The [TLS configuration](#tls). |
| `matchByKey`, `matchByHeader`, `matchByValue` | `matchByKey` | How [a reply is matched to its request](#matching). |
| `replyTimeout` | 60 seconds | How long a request waits for its reply, and a `consume` or a subscription for its record, unless they override it. |
| `consumeBuffer` | 10,000 records, 60 seconds | The [records kept](#consume-buffer) for a `consume` or a subscription that starts after its record is read. |
| `schemaRegistry`, `schemaRegistryBasicAuth`, `schemaRegistryProperty`, `schemaRegistryProperties` | none | The [schema registry]({{< ref "/reference/script/kafka/schema-registry" >}}) of the Avro, Protobuf, and JSON Schema keys and values. |

## Set Kafka client properties {#properties}

The protocol passes Kafka client properties to the producers and to the reply consumer.
Refer to the [producer](https://kafka.apache.org/documentation/#producerconfigs) and [consumer](https://kafka.apache.org/documentation/#consumerconfigs) configurations of Kafka for the available properties.

{{< include-code "properties" java kt scala >}}

The plugin sets some properties itself, and refuses them right away:

- `bootstrap.servers`: use `bootstrapServers` instead
- `key.serializer`, `value.serializer`, `key.deserializer`, and `value.deserializer`: the plugin turns keys and values into bytes itself, from Gatling bodies or with a [schema registry]({{< ref "/reference/script/kafka/schema-registry" >}})

The reply consumer doesn't join a consumer group, so it needs no `group.id` and commits no offsets, see [how replies are read](#reply-consumer).

## Secure the connections {#security}

The typed security options below generate the Kafka client properties for SASL and TLS: `security.protocol`, `sasl.mechanism`, `sasl.jaas.config`, and the `sasl.*` and `ssl.*` properties of each mechanism.
They apply to the producers and to the reply consumer alike.

The plugin picks `security.protocol` from the options you set:

| **Options** | **`security.protocol`** |
| --- | --- |
| A SASL mechanism, without TLS | `SASL_PLAINTEXT` |
| A SASL mechanism, with TLS | `SASL_SSL` |
| `saslAwsMskIam`, `saslAwsMskIamProfile`, `saslAwsMskIamRole` | `SASL_SSL`, always |
| TLS only | `SSL` |

The protocol uses a single SASL mechanism, a single trust store, and a single key store: setting a second one fails right away.

The Kafka clients are shared by all the virtual users, so the credentials are plain values, resolved once for the whole run, not Gatling Expression Language Strings.

{{< alert warning >}}
A raw property can't redefine a property that a typed option generates, such as `security.protocol`, `sasl.mechanism`, or `sasl.jaas.config`: the simulation fails at startup.
Use either the typed option or the raw properties for a given key.
{{< /alert >}}

Raw properties for keys the typed options don't generate complete them:

{{< include-code "security-properties" java kt scala >}}

### Authenticate with SASL {#sasl}

`saslPlain`, `saslScramSha256`, and `saslScramSha512` authenticate with a username and a password, with the PLAIN, SCRAM-SHA-256, and SCRAM-SHA-512 mechanisms:

{{< include-code "sasl" java kt scala >}}

### Authenticate with OAuth {#oauth}

`saslOauthBearer` authenticates with the OAUTHBEARER mechanism.
The Kafka client gets its tokens from the OAuth token endpoint with the client credentials grant, with an optional scope.

{{< include-code "oauth" java kt scala >}}

{{< alert info >}}
Since Kafka 4, a Kafka client only calls the token endpoints listed in the JVM system property `org.apache.kafka.sasl.oauthbearer.allowed.urls`.
The plugin adds the protocol's token endpoint to this property, keeping the endpoints already listed.
As it's a system property, it applies to the whole JVM.
{{< /alert >}}

### Authenticate to Amazon MSK with IAM {#aws-msk-iam}

The plugin authenticates to Amazon MSK with IAM with one of these options:

- `saslAwsMskIam`: with the credentials of the AWS default credentials provider chain
- `saslAwsMskIamProfile(profileName)`: with the credentials of a named profile of the AWS credentials files
- `saslAwsMskIamRole(roleArn)`: by assuming a role through AWS STS, with the credentials of the default credentials provider chain, typically for a cluster in another AWS account. It optionally takes the role session's name, the external id that the role's trust policy requires, and the region of the STS endpoint, in this order.

MSK only serves IAM authentication over TLS, so the plugin always uses TLS with it, trusting the JVM's default certificates unless you set a trust store.

{{< include-code "aws-msk-iam" java kt scala >}}

It needs the [Amazon MSK Library for AWS IAM](https://github.com/aws/aws-msk-iam-auth), `software.amazon.msk:aws-msk-iam-auth`, which the plugin doesn't bring:
add it in the same scope as the Gatling dependencies. Without it, the simulation fails at startup.

### Authenticate with Kerberos {#gssapi}

`saslGssapi` authenticates with the GSSAPI mechanism, logging in to Kerberos from a keytab.
It takes the Kerberos principal name the brokers run as, the path of the client's keytab, and the client's principal.

{{< include-code "gssapi" java kt scala >}}

### Use TLS {#tls}

`useTls` connects to the brokers over TLS, trusting the JVM's default certificates.
`trustStore` trusts the certificates of a store instead, and `keyStore` authenticates the client with the certificate and key of a store, for mutual TLS. Both imply TLS.

{{< include-code "tls" java kt scala >}}

Stores take a path, a password, empty for a store without one such as PEM, and an optional type: `JKS`, `PKCS12`, or `PEM`.
Without a type, Kafka uses its default type, `JKS`.
The key store's password is also the key's.

## Match replies to their requests {#matching}

A [request-reply]({{< ref "/reference/script/kafka/actions#request-reply" >}}) sends a request, then waits for its reply, which the system under test produces on a reply topic.
The request and its reply carry the same correlation id, so that Gatling can tell which request a reply answers.
The protocol defines where this id is:

{{< include-code "matching" java kt scala >}}

- `matchByKey`, the default: the reply has the request's key. A request without a key fails.
- `matchByHeader(name)`: the reply has the request's header with this name. When the request doesn't set this header, Gatling sets it to a random UUID.
- `matchByValue(check)`: the request and the reply carry the id in their value, where the check finds it, typically a `jsonPath` or a `regex` check. The ids are compared as text. A request whose value has no id fails, and a reply whose value has none is ignored.

The system under test must copy the correlation id from the request to its reply.

The same matching applies to the records that a [`consume`]({{< ref "/reference/script/kafka/actions#consume" >}}) or a [subscription]({{< ref "/reference/script/kafka/actions#subscribe" >}}) waits for, with the correlation id it provides.

{{< alert warning >}}
The `matchByValue` check runs without the virtual user's session, so it can't use the virtual user's attributes, such as `#{orderId}`.
It can't correlate values serialized with a schema registry either: the simulation fails at startup if you combine them.
{{< /alert >}}

Two requests, consumes, or subscriptions waiting at the same time on the same topic must have different correlation ids: the second one fails.

### Understand how replies are read {#reply-consumer}

Gatling reads the reply topics, and the topics of the consumes and subscriptions, with a consumer shared by all the virtual users of the protocol.
This consumer doesn't join a consumer group: it reads every partition of a reply topic, starting from its end, from before the first request on this topic is sent.
It ignores the replies no virtual user waits for.

As a result, when you run a test on several load generators, each one reads every reply, including the replies to the requests of the other load generators, which it ignores.

### Keep the records nobody waits for yet {#consume-buffer}

On the topics of the consumes and subscriptions, a record may be read before a virtual user waits for it, for example when a `consume` comes after other actions.
Gatling keeps these records, at most 10,000, for at most 60 seconds, the oldest being dropped first, so that a `consume` or a subscription that starts later still finds its record.

On a busy topic, 10,000 records may cover a short time, and a `consume` that starts later than that after its record was read times out although the record came.
Gatling then logs a warning naming `consumeBuffer`, which sets the number of records kept and how long they're kept:

{{< include-code "consume-buffer" java kt scala >}}

Raise the number of records as the memory of the load generator allows.

The kept records only matter for a `consume`, and for a [subscription]({{< ref "/reference/script/kafka/actions#subscribe" >}}) placed after the action that makes its record.
A subscription placed before this action, or attached to a `send` or a `requestReply`, waits before its record can come.

## Configure timeouts {#timeouts}

`replyTimeout` limits how long a request-reply waits for its reply, and a `consume` or a subscription for its record, 60 seconds by default.
Each action can override it.

{{< include-code "reply-timeout" java kt scala >}}

The Java and Kotlin `replyTimeout` also takes a number of seconds.

## Understand producer performance {#performance}

Each protocol configuration has a single Kafka producer, shared by all the virtual users of all the scenarios using it, and closed when the run ends.

### Configure batching like the system under test {#linger}

A Kafka producer waits up to `linger.ms` milliseconds to batch records together.
Since Kafka 4, `linger.ms` defaults to 5 milliseconds instead of 0.
With few virtual users, there are few records to batch, so this wait drives the response time of `send`.

Set the producer properties that drive latency, such as `linger.ms`, `batch.size`, `acks`, and `compression.type`, to the same values as the producers of the system under test, so that Gatling measures what the system's own producers would experience:

{{< include-code "linger" java kt scala >}}

### Understand how records are sent {#senders}

A Kafka producer may block while it waits for the metadata of a topic, or for room in its buffer.
So that virtual users never block, Gatling hands the records to 8 dedicated sender threads per producer, which send them.
The records of a topic without an explicit partition are sent from the same thread, in order.

Each of the first 8 topics of a producer gets a sender thread of its own, so a topic waiting for its metadata doesn't delay the records of another topic.
The records with an explicit partition are sent from another thread than their topic's.
Past these, topics share the sender threads.

The virtual users serialize the records themselves, so that serialization spreads over their threads, except the keys and values of a [schema registry]({{< ref "/reference/script/kafka/schema-registry#serialization" >}}) until their schema is cached.

Each sender thread holds a bounded number of records waiting to be sent.
When it's full, because the brokers or the producer can't keep up with the load, the send fails right away with a `Kafka producer saturated` error, instead of piling up records without bound.
