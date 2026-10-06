---
title: Project Setup
seotitle: Gatling GraphQL protocol reference - project setup
description: How to set up your project to use the Gatling GraphQL protocol.
lead: Learn about how to set up your project to use the Gatling GraphQL protocol.
date: 2026-09-30T00:00:00+00:00
---

## License and limitations {#license}

**The Gatling GraphQL component is distributed under the
[Gatling Enterprise Component License]({{< ref "/project/licenses/enterprise-component" >}}).**

The Gatling GraphQL protocol can be used with both the [Community Edition](https://gatling.io/products) and
[Enterprise](https://gatling.io/products) versions of Gatling.

Its usage is unlimited when running tests on [Gatling Enterprise](https://gatling.io/products). When used with
[Gatling Community Edition](https://gatling.io/products), usage is limited to:

- 5 users maximum
- 5 minute duration tests

Limits after which the test will stop. This means that all locally executed tests using the GraphQL protocol are limited by these values.

## Adding the Gatling GraphQL dependency {#gatling-graphql-dependency}

The Gatling GraphQL plugin is not included with Gatling by default. Add the Gatling GraphQL dependency, in addition to the
usual Gatling dependencies.

GraphQL requires Gatling {{< var gatlingVersion >}} or later.

For Java or Kotlin:

{{< include-file >}}
1-Maven: includes/dependency.maven.java.md
2-Gradle: includes/dependency.gradle.java.md
{{< /include-file >}}

For JavaScript/TypeScript:

```console
npm install @gatling.io/graphql@{{< var gatlingJsVersion >}}
```

For Scala:

{{< include-file >}}
1-Maven: includes/dependency.maven.scala.md
2-Gradle: includes/dependency.gradle.scala.md
3-sbt: includes/dependency.sbt.scala.md
{{< /include-file >}}

## Getting started with the demo project {#demo-project}

A [demo project](https://github.com/gatling/gatling-graphql-demo) is available to help you get started with the Gatling GraphQL protocol.
