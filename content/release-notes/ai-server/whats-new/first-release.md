---
title: First release
seotitle: What's new in Gatling AI Server 2026.47.1, the first release
description: Gatling AI Server 2026.47.1 release notes, the first release.
lead: Gatling AI Server 2026.47.1 is the first release of the self-hosted JMeter to Gatling converter.
date: 2026-11-24
---

## Convert JMeter test plans from your AI assistant

Gatling AI Server converts JMeter test plans into Gatling simulations written in Java. It is a Model Context Protocol
(MCP) server: your users hand a `.jmx` plan to the AI assistant they already use, such as Claude Code, and get back a
Gatling project.

## Run it on your own infrastructure

You run Gatling AI Server as a Docker image, `gatlingcorp/gatling-ai-server`, on your own infrastructure. The plan's code snippets
are ported by the OpenAI-compatible model endpoint you configure. Install it on Kubernetes with the Gatling Helm chart,
or on AWS, Azure, or Google Cloud with a Gatling Terraform module, as
[Install Gatling AI Server]({{< ref "/ai/server/installation/overview" >}}) describes.

## Sign in with your Gatling Enterprise account

Your users sign in with their Gatling Enterprise account, and the server validates every request's token.

## Download a Gatling project the server compiled

Each conversion ends with a one-time download link to a Gatling Java project, Maven or Gradle, that the server checked
by compiling it, with a `README.md` that says what was converted and what was left out.

See the [introduction to Gatling AI Server]({{< ref "/ai/server/introduction" >}}) to get started.
