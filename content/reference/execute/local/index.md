---
menutitle: Test locally
title: Local test execution with Gatling
seotitle: Test locally with Gatling
description: Instructions for testing Gatling in your local environment for crafting, debugging, and fixing.
lead: Learn the commands to start a Gatling test in your local environment for crafting, debugging, and fixing.
date: 2023-12-20T08:54:00+00:00
---

## Introduction

You can run Gatling simulations using your local computer or by using Gatling Enterprise, our load test orchestration platform. The primary purposes of local test execution are simulation crafting, debugging, and learning. Gatling supports 3 ways to launch load tests:

- Using one of our build tool plugins
- Using the JavaScript CLI
- Using Gatling Enterprise see the [Gatling Enterprise platform documentation]({{< ref "reference/execute/cloud/" >}})

This page contains the commands to start a load test from your local development environment using the Maven wrapper and the JavaScript CLI. The Maven wrapper also works with the [standalone bundle]({{< ref "/reference/install/oss/#use-the-standalone-bundle" >}}), meant for users who have corporate network restrictions. 



{{< alert info >}} 
Detailed documentation for working with the launchers is available in the build tools section: 

- [Maven plugin]({{< ref "/reference/integrations/build-tools/maven-plugin/" >}})
- [JavaScript CLI]({{< ref "/reference/integrations/build-tools/js-cli/" >}})
- [Gradle plugin]({{< ref "/reference/integrations/build-tools/gradle-plugin/" >}})
- [sbt plugin]({{< ref "/reference/integrations/build-tools/sbt-plugin/" >}})
{{< /alert  >}} 

## Use Maven or the standalone bundle

If you installed Gatling using a `ZIP` file download, it comes pre-loaded with a fully functioning load test. You can run this test locally to instantly experience Gatling's functionality and reporting features. The following sections help you to run your first test. The provided commands work for Maven and the standalone bundle.

### Run a Gatling simulation

Use the following command to start Gatling in interactive mode:

{{< code-toggle console >}}
Linux/MacOS: ./mvnw gatling:test
Windows: mvnw.cmd gatling:test

{{</ code-toggle >}}

### Start the Gatling Recorder

The [Gatling Recorder]({{< ref "/reference/script/protocols/http/recorder/" >}}) allows you to capture browser-based actions and convert them into a script. Use the following command to launch the Recorder:

{{< code-toggle console >}}
Linux/MacOS: ./mvnw gatling:recorder
Windows: mvnw.cmd gatling:recorder
{{</ code-toggle >}}

## Use the JavaScript or TypeScript SDKs

If you installed Gatling JavaScript SDK using the `ZIP` file download it comes pre-loaded with a fully functioning load test. You can run this test locally to instantly experience Gatling's functionality and reports feature. The following sections help you to run your first test. The provided commands work for JavaScript and TypeScript.

### Run a Gatling simulation

You can run the pre-configured demo simulation from the `src/` folder with the following command:

