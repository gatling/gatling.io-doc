---
title: JMS
seotitle: Gatling JMS protocol reference
description: How to use the Java Message Service (JMS) support in Gatling to connect to a broker and perform checks against inbound messages.
lead: SDK for JMS
date: 2021-04-20T18:30:56+02:00
aliases:
  - /reference/script/protocols/jms/
---

JMS support was initially contributed by [Jason Koch](https://github.com/jasonk000).

## Prerequisites

### Dependency {#dependency}

For Java, Kotlin, and Scala, the Gatling JMS SDK is included in the `io.gatling.highcharts:gatling-charts-highcharts` dependency: there's nothing to add.

For JavaScript and TypeScript, add the `@gatling.io/jms` package, available since Gatling JavaScript SDK 3.16.101:

```console
npm install @gatling.io/jms@{{< var gatlingJsVersion >}}
```

### Imports

The Gatling JMS SDK isn't imported by default. Add the following imports:

{{< include-code "imprts" >}}

### JMS client implementation {#broker}

*Java Message Service* (JMS), now known as *Jakarta Messaging*, is an API.
In order to use it, you must provide your client vendor-specific implementation, for example `org.apache.activemq:activemq-broker` if you are using ActiveMQ.

For Java, Kotlin, and Scala, add it as a dependency in the same scope as the Gatling dependencies.

For JavaScript and TypeScript, declare it as a [JVM dependency]({{< ref "/integrations/build-tools/js-cli#jvm-dependencies" >}}) in the `gatling` section of your `package.json`.
Gatling downloads it when running or packaging your simulations:

```json
{
  "gatling": {
    "jvmDependencies": {
      "artifacts": ["org.apache.activemq:activemq-client:6.4.0"]
    }
  }
}
```

### Using legacy javax.jms package {#legacy}

Since 3.14.0, Gatling targets the new `jakarta.jms` package.

If you're still using a JMS client that requires the legacy `javax.jms` package and can't upgrade to a modern version, you have to use a wrapper library such as [com.github.marschall:jakarta-jms-adapter](https://github.com/marschall/jakarta-jms-adapter).

Wrap your `javax.jms.ConnectionFactory` with a `com.github.marschall.jakartajmsadapter.JakartaConnectionFactory`.

This isn't available in JavaScript and TypeScript, where the JMS client must implement the `jakarta.jms` package.

## JMS protocol {#protocol}

Use the `jms` object in order to create a JMS protocol.

#### `connectionFactory`

The first mandatory step is to configure the `ConnectionFactory`.

You can either configure one to be retrieved with a JNDI lookup:

{{< include-code "jndi" >}}

or directly instantiate one with your JMS broker's Java client library, for example:

{{< include-code "prog" java kt scala >}}

Instantiating a `ConnectionFactory` isn't available in JavaScript and TypeScript: use a JNDI lookup.

#### Other options

The following example shows the other protocol options:

{{< include-code "options" >}}

## JMS request

Use the `jms("requestName")` method in order to create a JMS request.

### Request type

Currently, `requestReply` and `send` (fire and forget) requests are supported.

### Destination

Define the target destination with `queue("queueName")` or alternatively with `destination(JmsDestination)`.

Optionally, you can define a reply destination with `replyQueue("responseQueue")` or `replyDestination(JmsDestination)`. Otherwise, Gatling will use a dynamic queue.

If you do so, you have the possibility of not setting the `JMSReplyTo` header with `noJmsReplyTo`.

Additionally, for the reply destination, you can define a JMS selector with `selector`

If you have the need to measure the time when a message arrive at a message queue different from the `replyDestination(JmsDestination)`,
you can additionally define a `trackerDestination(JmsDestination)`.

### Message

* `textMessage`
* `bytesMessage`
* `mapMessage`
* `objectMessage` for `java.io.Serializable` payloads

In JavaScript and TypeScript, `objectMessage` sends strings, numbers, and booleans as is, arrays as `java.util.ArrayList`, and objects as `java.util.HashMap`.
A string is a Gatling Expression Language string: a lone `"#{attribute}"` sends the value of the Session attribute as is, which must be serializable.
To always send a string, pass a function.

See below for a few examples:

{{< include-code "message" >}}

### Extra options

* `jmsType`
* `property`
* `priority`: the JMS priority of the message, from 0 (lowest) to 9 (highest). A priority outside this range fails the request. Defaults to the JMS default priority, 4.
* `timeToLive`: how long the JMS provider keeps the message before it expires it. Defaults to the JMS default, no expiration.
* `correlationId`: the `JMSCorrelationID` header of the message, for example a business key from a feeder.

`priority` and `timeToLive` are passed to the JMS producer when it sends the message: setting `JMSPriority` or `JMSExpiration` with `property` has no effect, as the JMS provider overwrites them.
Likewise, `property("JMSCorrelationID", ...)` sets a regular property, not the header: use `correlationId` instead.

With `matchByCorrelationId`, Gatling matches the reply with the correlation id you set, and only generates a random one when you don't.
Two requests in flight with the same correlation id can't be told apart: the second one fails.

{{< include-code "extra" >}}

These options are available since Gatling 3.16.1.

## JMS check {#check}

Gatling JMS's support only current supports the following checks:
* [`bodyBytes`]({{< ref "/concepts/checks#bodybytes" >}})
* [`bodyLength`]({{< ref "/concepts/checks#bodylength" >}})
* [`bodyString`]({{< ref "/concepts/checks#bodystring" >}})
* [`substring`]({{< ref "/concepts/checks#substring" >}})
* [`jsonPath`]({{< ref "/concepts/checks#jsonpath" >}})
* [`jmesPath`]({{< ref "/concepts/checks#jmespath" >}})
* [`xpath`]({{< ref "/concepts/checks#xpath" >}})

It also supports `jmsProperty` for checking JMS properties on reply messages.

{{< include-code "jmsPropertyCheck" >}}

In addition, there's `simpleCheck`. In JavaScript and TypeScript, its function receives a read-only view of the reply, whose `text()` returns the body of a text message:

{{< include-code "simple" >}}

## Example

Short example, assuming ActiveMQ on localhost, using a `reqReply` query, to the queue named "jmstestq":

{{< include-code "example-simulation" >}}
