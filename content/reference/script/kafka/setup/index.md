---
title: Project Setup
seotitle: Gatling Kafka protocol reference - project setup
description: How to set up your project to use the Gatling Kafka protocol, including the plugin dependency and the optional libraries.
lead: Learn how to set up your project to use the Gatling Kafka protocol.
date: 2026-11-05T00:00:00+00:00
---

## Understand the license and limitations {#license}

**The Gatling Kafka component is distributed under the
[Gatling Enterprise Component License]({{< ref "/project/licenses/enterprise-component" >}}).**

The Gatling Kafka protocol can be used with both the [Community Edition](https://gatling.io/products) and
[Enterprise](https://gatling.io/products) versions of Gatling.

Its usage is unlimited when running tests on [Gatling Enterprise](https://gatling.io/products). When used with
[Gatling Community Edition](https://gatling.io/products), usage is limited to:

- 5 users maximum
- 5 minute duration tests

Limits after which the test will stop. This means that all locally executed tests using the Kafka protocol are limited by these values.

## Add the Gatling Kafka dependency {#gatling-kafka-dependency}

The Gatling Kafka plugin isn't included with Gatling by default. Add the Gatling Kafka dependency, in addition to the
usual Gatling dependencies.

The Gatling Kafka plugin requires Gatling 3.16.1 or later, and is available for Java, Kotlin, and Scala.
It isn't available for JavaScript and TypeScript.

For Java or Kotlin:

{{< include-file >}}
1-Maven: includes/dependency.maven.java.md
2-Gradle: includes/dependency.gradle.java.md
{{< /include-file >}}

For Scala:

{{< include-file >}}
1-Maven: includes/dependency.maven.scala.md
2-Gradle: includes/dependency.gradle.scala.md
3-sbt: includes/dependency.sbt.scala.md
{{< /include-file >}}

The plugin brings the Apache Kafka client, `org.apache.kafka:kafka-clients`.

## Add the optional libraries {#optional-libraries}

Some features need a library that the plugin doesn't bring. Add it in the same scope as the Gatling dependencies when you use the feature:

| **Feature** | **Library** |
| --- | --- |
| [Avro, Protobuf, and JSON Schema]({{< ref "/reference/script/kafka/schema-registry#serializers" >}}) | Confluent's serializer of the format, such as `io.confluent:kafka-avro-serializer`, from Confluent's repository |
| [Amazon MSK IAM authentication]({{< ref "/reference/script/kafka/protocol#aws-msk-iam" >}}) | `software.amazon.msk:aws-msk-iam-auth` |

Gatling fails at startup, naming the missing library, when a simulation uses one of these features without it.
