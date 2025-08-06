---
title: Introduction to sources
menutitle: Introduction to sources
seotitle: Introduction to sources in Gatling Enterprise
description: Learn how to create and manage sources in Gatling Enterprise.
lead: Introduction to sources in Gatling Enterprise.
date: 2025-07-30T10:29:36+00:00
---

Gatling Enterprise provides multiple ways to manage your sources, which are the building blocks for your load tests. Sources are either packaged Gatling simulations or Git repositories containing Gatling simulations. This flexibility allows you to choose the best method for your team's workflow and project requirements.

## Available methods

- **Package Generation**: Create a package from your Gatling simulations, which can then be uploaded to Gatling Enterprise.
- **Package Configuration**: Configure and manage your Gatling packages within Gatling Enterprise.
- **Git Repository**: Create and manage Git repositories that contain your Gatling simulations, allowing you to build simulations on the fly.
- **Configuration as Code**: Define your Gatling simulations and their configurations in a code format, enabling automation and consistency across your load tests.

## Getting started

To get started with managing your sources in Gatling Enterprise, select between the following methods:

- **Package Generation**: Use the [Package Generation documentation]({{< ref "reference/run-tests/sources/package-gen" >}}) to create a package from your Gatling simulations.
- **Git Repository**: Use the [Git Repository documentation]({{< ref "reference/run-tests/sources/git-repository" >}}) to create and manage Git repositories that contain your Gatling simulations.

## Additional resources for packaged Gatling simulations

Packaged Gatling simulations offer multiple possible workflows that can be adapted to almost any use case from drag-and-drop to full automation. The following resources can help you create and manage Gatling packages:

- [Package Generation]({{< ref "reference/run-tests/sources/package-gen" >}}): Learn how to create a package from your Gatling simulations.
- [Package Configuration]({{< ref "reference/run-tests/sources/package-conf" >}}): Learn how to configure and manage your Gatling packages within Gatling Enterprise.
- [Configuration as Code]({{< ref "reference/run-tests/sources/configuration-as-code" >}}): Learn how to define your Gatling simulations and their configurations in a code format, enabling automation and consistency across your load tests.
- Build tools:
    - [Maven]({{< ref "integrations/build-tools/maven-plugin" >}}): Learn how to use Maven to build and manage your Gatling simulations.
    - [Gradle]({{< ref "integrations/build-tools/gradle-plugin" >}}): Learn how to use Gradle to build and manage your Gatling simulations.
    - [sbt]({{< ref "integrations/build-tools/sbt-plugin" >}}): Learn how to use sbt to build and manage your Gatling simulations.
    - [JavaScript CLI]({{< ref "integrations/build-tools/js-cli" >}}): Learn how to use JavaScript to build and manage your Gatling simulations.

By following these steps, you can effectively manage your sources in Gatling Enterprise and streamline your load testing process.
