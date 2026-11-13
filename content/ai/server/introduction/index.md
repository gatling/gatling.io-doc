---
title: Introduction to Gatling AI Server
menutitle: Introduction
seotitle: What Gatling AI Server is and how it converts JMeter test plans
description: Learn what Gatling AI Server is, how it converts JMeter test plans into Gatling Java projects, and how it fits in your infrastructure.
lead: Gatling AI Server is an MCP server you run on your own infrastructure. Your AI assistant hands it a JMeter test plan and gets back a Gatling Java project.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server converts JMeter test plans into Gatling simulations written in Java. It is a
[Model Context Protocol](https://modelcontextprotocol.io) (MCP) server: your users keep working in the AI assistant
they already use, such as Claude Code, hand it a `.jmx` plan, and download a Gatling project the server checked by
compiling it.

You run the server on your own infrastructure, as a Docker image. Your users sign in with their Gatling Enterprise
account, and the server ports the plan's code snippets with the model you choose.

{{< alert info >}}
Gatling AI Server isn't the [Gatling MCP Server]({{< ref "/ai/plugins/mcp-server" >}}). The Gatling MCP Server runs
locally and drives your Gatling Enterprise resources with an API token. Gatling AI Server runs on your infrastructure
and converts JMeter test plans. You can configure both in the same AI assistant.
{{< /alert >}}

## Understand how a conversion works

A conversion goes through these steps:

1. **The assistant uploads the plan.** It sends a `.jmx` file, or a zip of a folder that holds the plan and the files
   it reads, to the server.
2. **The converter translates the plan.** A converter turns the plan's elements into a Gatling simulation, with no
   model involved.
3. **The guardian screens the snippets.** Some elements hold code the converter can't translate, such as JSR223 or
   BeanShell scripts. The converter leaves a stub method for each one, and the guardian flags snippet text that
   addresses an AI model before any model reads it.
4. **A model ports the code snippets.** A model ports each snippet's code to Java. Only these snippets reach a model:
   by default the model endpoint you configure, or the user's own AI assistant when the operator allows it and the
   user asks for it.
5. **The server screens and compiles the ported code.** The guardian screens every ported body before the server puts
   it in the project. A snippet counts as ported only when its Java body compiles. A body that doesn't compile goes
   back to the model for a bounded number of repairs, after which the stub stays in the project.
6. **The assistant downloads the project.** The server returns a summary of the conversion and a one-time download
   link to a Maven or Gradle project, with a `README.md` that says what the conversion did.

## Understand the architecture

Gatling AI Server sits between your users' AI assistants and two services it calls over HTTPS:

- **Gatling's sign-in service** (`auth.gatling.io`), where users sign in with their Gatling Enterprise account, and
  whose public keys the server validates every request's token against.
- **Your model endpoint**, any OpenAI-compatible endpoint, which ports the code snippets.

The following diagram shows how the components connect:

{{< img src="architecture.png" alt="Your users’ AI assistant reaches Gatling AI Server through your TLS reverse proxy; the server keeps its SQLite file on a data volume, sends code snippets to your model endpoint and validates tokens against Gatling’s sign-in service, where users sign in from their browser" >}}

## Check what this release converts

This release converts JMeter test plans only, from a `.jmx` file or a zip that holds one or more plans. It serves two
MCP tools: `storage.prepare_upload`, which prepares the upload of a plan, and `converters.from_jmeter`, which converts
it. Your AI assistant calls them for you.

## Next steps

- [Check the network requirements]({{< ref "/ai/server/network" >}}).
- [Install the server with Docker Compose]({{< ref "/ai/server/installation" >}}).
- [Connect your AI assistant]({{< ref "/ai/server/connect" >}}).
