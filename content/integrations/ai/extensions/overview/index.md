---
title: Overview
description: Overview of the Gatling AI extensions including installation, requirements, and available skills.
lead: The Gatling AI extensions provides skills and an MCP server to help you deploy and run load tests on Gatling Enterprise directly from your IDE.
date: 2026-03-09T10:23:29+00:00
---

## Installation

### Installation for Claude Code

The plugin can be installed directly from our marketplace via Claude Code's plugin system.

First, configure the marketplace:

```
/plugin marketplace add gatling/gatling-ai-extensions
```

Then, install the plugin:

```
/plugin install gatling@gatling-ai-extensions
```

Or browse for the plugin via `/plugin > Discover`.

Finally, reload Claude.

## Gatling AI Extensions

The plugin is a Gatling projects helper to deploy and run tests on [Gatling Enterprise](https://gatling.io/platform).
It contains a set of skills and an MCP server.

The AI extensions are packaged as a plugin inside a Claude Marketplace but all components can be used or run manually
in any LLM client that supports skills or local MCP servers.

### Skills

#### Bootstrap a Gatling project

Skill: [`gatling-bootstrap-project`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-bootstrap-project/SKILL.md)

Create a new Gatling project from scratch by either the prompt or by using the following command inside Claude Code:

```console
/Gatling:gatling-bootstrap-project java maven
```

#### Build tools

Skill: [`gatling-build-tools`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-build-tools/SKILL.md)

Deploy and start Gatling tests on Gatling Enterprise using your project's build tool (Maven, Gradle, sbt, or the
JavaScript CLI). Detects the build tool, verifies prerequisites, runs the deploy command, and optionally starts a test
run.

#### Configuration as Code

Skill: [`gatling-configuration-as-code`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-configuration-as-code/SKILL.md)

Generate or update the `.gatling/package.conf` package descriptor file for Gatling Enterprise deployments.
Collects simulation class names, queries your account for teams, packages, tests, and locations, then writes or updates
the configuration file following Configuration as Code rules.

#### Convert a JMeter Test Plan to Gatling

Skill: [`gatling-convert-from-jmeter`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-convert-from-jmeter/SKILL.md)

The skill will generally be loaded automatically by the LLM when needed, based on your natural language queries, when trying to convert an [Apache JMeter™](https://jmeter.apache.org/) test.
In Claude Code, you can also explicitly invoke it, for instance:

```console
/Gatling:gatling-convert-from-jmeter
```

Using this skill, the LLM will look for JMeter files to convert, and for an existing Gatling project as a destination. It will also help you set up a new Gatling project if necessary.

Do not hesitate to give more context from the start if you want less back and forth with the LLM; for instance, if you want to convert the `Test Plan.jmx` file to a new Java/Maven Gatling project:

```console
/Gatling:gatling-convert-from-jmeter Test Plan.jmx java maven
```

#### Convert a LoadRunner Script to Gatling

Skill: [`gatling-convert-from-loadrunner`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-convert-from-loadrunner/SKILL.md)

The skill will generally be loaded automatically by the LLM when needed, based on your natural language queries, when trying to convert a [LoadRunner](https://www.opentext.com/products/professional-performance-engineering) script.
In Claude Code, you can also explicitly invoke it, for instance:

```console
/Gatling:gatling-convert-from-loadrunner
```

Do not hesitate to give more context from the start if you want less back and forth with the LLM; for instance, if you want to convert a LoadRunner project to a new Java/Maven Gatling project:

```console
/Gatling:gatling-convert-from-loadrunner WebHttpHtml1.zip java maven
```

You can download a [sample LoadRunner project](https://github.com/gatling/gatling-ai-extensions/raw/refs/heads/main/plugins/gatling/skills/gatling-convert-from-loadrunner/samples/EcommApp.zip) to try out the skill.

### MCP Server

For the Gatling MCP server details, tools reference, and setup instructions,
see the [dedicated page]({{< ref "./mcp-server" >}}).

When using either the skills or the MCP server, a valid [**API Token**]({{< ref "/reference/administration/api-tokens" >}})
is required with at least the **Configure** role provided as environment variable `GATLING_ENTERPRISE_API_TOKEN`.
