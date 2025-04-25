---
title: Gatling documentation
description: Documentation for the Gatling and Gatling Enterprise load testing tools.
date: 2021-04-20T18:30:56+02:00
cascade:
  docsRepo: https://github.com/gatling/gatling.io-doc/blob/main/content
ordering:
  - evaluate-enterprise
  - tutorials
  - guides
  - integrations
  - reference 
  - concepts
  - testing-concepts
  - release-notes
  - project
---

**Gatling is a high-performance load testing tool built for efficiency, automation, and code-driven testing workflows.**

Test scenarios are defined as code using an expressive DSL in Java, JavaScript, Scala, or Kotlin, making them easy to read, version, and maintain as part of your development workflow.

Gatling’s architecture is fully asynchronous. Virtual users are modeled as lightweight messages rather than threads, allowing you to simulate thousands of concurrent users with minimal system resources, ideal for modern, high-scale applications.

While Gatling offers robust support for **HTTP** out of the box, the load engine is protocol-agnostic. It also ships with **JMS** support and can be extended to handle other protocols.

## Need centralized test management and real-time reporting?

**Gatling Enterprise** extends the open-source capabilities with:

- A web UI to manage, launch, and monitor tests
- Real-time dashboards with detailed analytics
- CI/CD integration, advanced permissions, and support for hybrid/cloud deployments

Try **Gatling Enterprise** free for 14 days, no credit card required.[ Start your trial →](https://cloud.gatling.io?utm_source=docs)

Want to learn more about what Gatling Enterprise offers? [Compare OSS and Enterprise editions →](https://gatling.io/products/oss-vs-enterprise?utm_source=docs)

## Getting started

Ready to write and run your first tests? Start here:

- [Install Gatling]({{< ref "/tutorials/oss" >}})
- [Create a simulation with JavaScript]({{< ref "/tutorials/scripting-intro-js" >}})
- [Create a simulation with Java]({{< ref "/tutorials/scripting-intro" >}})
- [Create a simulation from Postman]({{< ref "/tutorials/postman" >}})
- [Create a simulation with the Recorder]({{< ref "/tutorials/recorder" >}})
- [Create a simulation in the GUI]({{< ref "/tutorials/gui" >}})

## Explore the docs

- [**Guides →**]({{< ref "/guides" >}}) Practical how-to content for writing and scaling tests, managing integrations, and working with Gatling Enterprise.
- [**Load Testing Concepts →**]({{< ref "/testing-concepts" >}}) Learn the key concepts behind performance testing, virtual users, injection profiles, and load models.
- [**Analytics & Metrics Concepts →**]({{< ref "/concepts" >}}) Understand the metrics exposed by Gatling Enterprise dashboards and how to interpret them.
- [**Reference →**]({{< ref "/reference" >}}) Complete documentation for Gatling’s SDKs, protocol support, and configuration options.
- [**Integrations →**]({{< ref "/integrations" >}}) Explore integrations with CI/CD pipelines, build systems, and observability tools like APM platforms.
- [**Release Notes →**]({{< ref "/release-notes" >}}) What’s new in the latest Gatling versions and feature releases.
- [**Migration Guides →**]({{< ref "/release-notes/upgrading" >}}) Instructions for upgrading from earlier versions of Gatling safely and effectively.

## Looking for help?

**Using Gatling Open Source?**

Ask your questions and get help from the community at [**community.gatling.io**](https://community.gatling.io/)

**Using Gatling Enterprise?**

Reach out to our support team directly from your workspace: [**cloud.gatling.io**](https://cloud.gatling.io/)