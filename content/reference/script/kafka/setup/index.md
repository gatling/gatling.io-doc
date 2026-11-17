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
When the test stops, Gatling logs which limit it exceeded.

## Get started with the demo project {#demo-project}

A [demo project](https://github.com/gatling/gatling-kafka-demo) is available for each supported language and build tool:

- [Java with Gradle](https://github.com/gatling/gatling-kafka-demo/tree/main/java/gradle)
- [Java with Maven](https://github.com/gatling/gatling-kafka-demo/tree/main/java/maven)
- [Kotlin with Gradle](https://github.com/gatling/gatling-kafka-demo/tree/main/kotlin/gradle)
- [Kotlin with Maven](https://github.com/gatling/gatling-kafka-demo/tree/main/kotlin/maven)
- [Scala with sbt](https://github.com/gatling/gatling-kafka-demo/tree/main/scala/sbt)
- [JavaScript](https://github.com/gatling/gatling-kafka-demo/tree/main/javascript)
- [TypeScript](https://github.com/gatling/gatling-kafka-demo/tree/main/typescript)

## Add the Gatling Kafka dependency {#gatling-kafka-dependency}

The Gatling Kafka plugin isn't included with Gatling by default. Add the Gatling Kafka dependency, in addition to the
usual Gatling dependencies.

The Gatling Kafka plugin was introduced in Gatling 3.16 and isn't compatible with older versions.

For Java or Kotlin:

{{< include-file >}}
1-Maven: includes/dependency.maven.java.md
2-Gradle: includes/dependency.gradle.java.md
{{< /include-file >}}

For JavaScript/TypeScript:

```console
npm install @gatling.io/kafka@{{< var gatlingJsVersion >}}
```

For Scala:

{{< include-file >}}
1-Maven: includes/dependency.maven.scala.md
2-Gradle: includes/dependency.gradle.scala.md
3-sbt: includes/dependency.sbt.scala.md
{{< /include-file >}}

The plugin brings the Apache Kafka client, `org.apache.kafka:kafka-clients`.

## Add the optional libraries {#optional-libraries}

Some features need a library that the plugin doesn't bring. Add it when you use the feature:

- For Java, Kotlin, and Scala, in the same scope as the Gatling dependencies.
- For JavaScript and TypeScript, as a [JVM dependency]({{< ref "/integrations/build-tools/js-cli#jvm-dependencies" >}}) in the `gatling` section of your `package.json`, with the Maven repository it comes from, if it's not Maven Central. For example, for Avro:

```json
{
  "gatling": {
    "jvmDependencies": {
      "artifacts": ["io.confluent:kafka-avro-serializer:8.4.0"],
      "repositories": ["https://packages.confluent.io/maven/"]
    }
  }
}
```

The libraries are:

| **Feature** | **Library** |
| --- | --- |
| [Avro, Protobuf, and JSON Schema]({{< ref "/reference/script/kafka/schema-registry#serializers" >}}) | Confluent's serializer of the format, such as `io.confluent:kafka-avro-serializer`, from Confluent's repository |
| [Amazon MSK IAM authentication]({{< ref "/reference/script/kafka/protocol#aws-msk-iam" >}}) | `software.amazon.msk:aws-msk-iam-auth` |

Gatling fails at startup, naming the missing library, when a simulation uses one of these features without it.
