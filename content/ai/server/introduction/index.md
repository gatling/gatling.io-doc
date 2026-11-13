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
they already use, hand it a `.jmx` plan, and download a Gatling project the server checked by compiling it.

You run the server on your own infrastructure: on Kubernetes with the Gatling Helm chart, or on AWS, Azure, or Google
Cloud with a Gatling Terraform module. Your users sign in with their Gatling Enterprise account.

The server translates the plan itself. Only the code snippets it can't translate, such as JSR223 or BeanShell scripts,
go to the model: the large language model (LLM) that ports the snippets to Java. The rest of your plan stays away from
the model, which saves tokens. The server calls the model through your model endpoint, any OpenAI-compatible API
(Anthropic, OpenAI, Azure OpenAI, or a gateway of your own), under the model name you configure.

Your users download a Maven or Gradle project, with a `README.md` that says what the conversion did.

{{< alert info >}}
Gatling AI Server isn't the [Gatling MCP Server]({{< ref "/ai/plugins/mcp-server" >}}). The Gatling MCP Server runs
locally and drives your Gatling Enterprise resources with an API token. Gatling AI Server runs on your infrastructure
and converts JMeter test plans. You can configure both in the same AI assistant.
{{< /alert >}}

## Understand the architecture

Gatling AI Server sits between your users' AI assistants and two services it calls over HTTPS:

- **Gatling's sign-in service** (`auth.gatling.io`), where users sign in with their Gatling Enterprise account, and
  whose public keys the server validates every request's token against.
- **Your model endpoint**, any OpenAI-compatible endpoint, which ports the code snippets.

The following diagram shows how the components connect:

{{< img src="architecture.png" alt="Your users’ AI assistant reaches Gatling AI Server through your TLS reverse proxy; the server sends code snippets to your model endpoint and validates tokens against Gatling’s sign-in service, auth.gatling.io, where users sign in from their browser" >}}

## Check what Gatling AI Server converts

Gatling AI Server currently converts JMeter test plans, from a `.jmx` file or a zip that holds one or more plans. Your
AI assistant calls its three MCP tools for you: `storage.prepare_upload` prepares the upload of a plan,
`converters.from_jmeter` converts it, and `converters.porting_rules` returns the rules for porting the code snippets the
assistant ports itself. [MCP tools]({{< ref "/ai/server/tools" >}}) describes each one. More source formats and
features will follow.

## Next steps

- [Check the network requirements]({{< ref "/ai/server/network" >}}).
- [Install the server with Helm or Terraform]({{< ref "/ai/server/installation/overview" >}}).
- [Connect your AI assistant]({{< ref "/ai/server/connect" >}}).
