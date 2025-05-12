---
title: JMS
seotitle: Gatling JMS protocol reference
description: How to use the Java Message Service (JMS) support in Gatling to connect to a broker and perform checks against inbound messages.
lead: DSL for JMS
date: 2021-04-20T18:30:56+02:00
---

JMS support was initially contributed by [Jason Koch](https://github.com/jasonk000).

{{< alert warning >}}
The JMS protocol is not supported by Gatling JS.
{{< /alert >}}

## Prerequisites

### Imports

Gatling JMS DSL is not imported by default.

You have to manually add the following imports:

{{< include-code "imprts" java kt scala >}}

### JMS client implementation {#broker}

*Java Message Service* (JMS), now known as *Jakarta Messaging* is an API.
In order to use it, you must provide your client vendor-specific implementation, eg `org.apache.activemq:activemq-broker` if you are using ActiveMQ.

### Using legacy javax.jms package {#legacy}

Since 3.14.0, Gatling targets the new `jakarta.jms` package.

If you're still using a JMS client that requires the legacy `javax.jms` package and can't upgrade to a modern version, you have to use a wrapper library such as [com.github.marschall:jakarta-jms-adapter](https://github.com/marschall/jakarta-jms-adapter).

Usage is very simple, you just jave to wrap your `java.jms.ConnectionFactory` with a `com.github.marschall.jakartajmsadapter.JakartaConnectionFactory`.

## JMS protocol {#protocol}

Use the `jms` object in order to create a JMS protocol.

#### `connectionFactory`

The first mandatory step is to configure the `ConnectionFactory`.

You can either configure one to be retrieved with a JNDI lookup:

{{< include-code "jndi" java kt scala >}}

or directly instantiate one with your JMS broker's Java client library, eg:

{{< include-code "prog" java kt scala >}}

#### Other options

{{< include-code "options" java kt scala >}}

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

See below for a few examples:

{{< include-code "message" java kt scala >}}

### Extra options

* `jmsType`
* `property`

{{< include-code "extra" java kt scala >}}

## JMS check {#check}

Gatling JMS's support only current supports the following checks:
* [`bodyBytes`]({{< ref "../core/checks#bodybytes" >}})
* [`bodyLength`]({{< ref "../core/checks#bodylength" >}})
* [`bodyString`]({{< ref "../core/checks#bodystring" >}})
* [`substring`]({{< ref "../core/checks#substring" >}})
* [`jsonPath`]({{< ref "../core/checks#jsonpath" >}})
* [`jmesPath`]({{< ref "../core/checks#jmespath" >}})
* [`xpath`]({{< ref "../core/checks#xpath" >}})

It also supports `jmsProperty` for checking JMS properties on reply messages.

{{< include-code "jmsPropertyCheck" java kt scala >}}

In addition, there's `simpleCheck`:

{{< include-code "simple" java kt scala >}}

## Example

Short example, assuming ActiveMQ on localhost, using a `reqReply` query, to the queue named "jmstestq":

{{< include-code "example-simulation" java kt scala >}}
