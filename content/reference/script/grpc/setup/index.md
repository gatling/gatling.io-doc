---
title: Project Setup
seotitle: Gatling gRPC protocol reference - project setup
description: How to set up your project to use the Gatling gRPC protocol
lead: Learn about how to set up your project to use the Gatling gRPC protocol
date: 2023-08-24T11:27:53+0200
aliases:
  - /reference/script/protocols/grpc/setup/
---

Under the hood, Gatling uses [gRPC-Java](https://grpc.io/docs/languages/java/) to implement its protocol support.

{{< alert warning >}}
The gRPC protocol is not supported by the JavaScript SDK. If this functionality is important to you, add a comment to our [public roadmap](https://portal.productboard.com/gatling/1-gatling-roadmap/c/113-javascript-sdk-expansion?&utm_medium=docs&utm_source=callout)
{{< /alert >}}

## License and limitations {#license}

**The Gatling gRPC component is distributed under the
[Gatling Enterprise Component License]({{< ref "/project/licenses/enterprise-component" >}}).**

The Gatling gRPC protocol can be used with both the [Open Source](https://gatling.io/products/) and
[Enterprise](https://gatling.io/products/) versions of Gatling.

Its usage is unlimited when running on [Gatling Enterprise](https://gatling.io/products/). When used with
[Gatling Open Source](https://gatling.io/products/), usage is limited to:

- 5 users maximum
- 5 minute duration tests

Limits after which the test will stop.

## Getting started with the demo project {#demo-project}

A [demo project](https://github.com/gatling/gatling-grpc-demo) is available with most combinations of currently
supported languages and build tools:

- [Java with Gradle](https://github.com/gatling/gatling-grpc-demo/tree/main/java/gradle)
- [Java with Maven](https://github.com/gatling/gatling-grpc-demo/tree/main/java/maven)
- [Kotlin with Gradle](https://github.com/gatling/gatling-grpc-demo/tree/main/kotlin/gradle)
- [Kotlin with Maven](https://github.com/gatling/gatling-grpc-demo/tree/main/kotlin/maven)
- [Scala with sbt](https://github.com/gatling/gatling-grpc-demo/tree/main/scala/sbt)

It also contains a [demo server](https://github.com/gatling/gatling-grpc-demo/tree/main/server) that you can use if
you want to run the example scenarios over a working server.

## Adding the Gatling gRPC dependency {#gatling-grpc-dependency}

The Gatling gRPC plugin is not included with Gatling by default. Add the Gatling gRPC dependency, in addition to the
usual Gatling dependencies.

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

## Configuring Protobuf code generation {#protobuf-codegen}

By default, gRPC uses [Protocol Buffers (protobuf)](https://grpc.io/docs/what-is-grpc/introduction/#working-with-protocol-buffers)
to serialize messages, so we also show typical configurations to generate your gRPC method descriptors and data access
classes from proto files. Note that if your gRPC service uses something else (e.g. JSON messages), you will need to use
appropriate configurations for your use case instead.

### Protobuf for Java

For a project running Java with either Maven or Gradle: 

{{< include-file >}}
1-Maven: includes/proto.maven.java.md
2-Gradle: includes/proto.gradle.java.md
{{< /include-file >}}

### Protobuf for Kotlin

For a project running Kotlin with either Maven or Gradle:

{{< include-file >}}
1-Maven: includes/proto.maven.kotlin.md
2-Gradle: includes/proto.gradle.kotlin.md
{{< /include-file >}}

### Protobuf for Scala

For an sbt project running Scala, add the ScalaPB plugin in `project/plugins.sbt`:

```scala
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")

libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "compilerplugin" % "0.11.14"
)
```

And configure it in your project settings (`build.sbt`):

```scala
val commons = Seq(
  PB.protocVersion := "3.25.2" // scalapb.compiler.Version.protobufVersion may point to an older version
)

val scalaSettings: Seq[Def.Setting[_]] = commons ++ Seq(
  Test / PB.targets := Seq(
    scalapb.gen() -> (Test / sourceManaged).value
  ),
  libraryDependencies ++= Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime"      % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion % "test"
  )
)

scalaSettings
```

Use `javaSettings` instead if you would rather use POJOs instead of objects generated by [ScalaPB](https://scalapb.github.io/):

```scala
val javaSettings: Seq[Def.Setting[_]] = commons ++ Seq(
  Test / PB.targets := Seq(
    PB.gens.java -> (Test / sourceManaged).value,
    PB.gens.plugin("grpc-java") -> (Test / sourceManaged).value
  ),
  libraryDependencies ++= Seq(
    ("io.grpc" % "protoc-gen-grpc-java" % scalapb.compiler.Version.grpcJavaVersion).asProtocPlugin()
  )
)

javaSettings
```

Add your proto files to the `src/test/protobuf` directory.

Check the demo project for a full example:
[Gatling gRPC Scala demo with sbt](https://github.com/gatling/gatling-grpc-demo/tree/main/scala/sbt).
