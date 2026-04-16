---
title: Gatling MCP Server
description: Connect AI assistants to Gatling Enterprise using the Model Context Protocol (MCP) server to query and manage your load testing infrastructure through natural language.
lead: The Gatling MCP Server exposes your Gatling Enterprise resources to AI assistants via the Model Context Protocol, enabling natural language interaction with your load testing infrastructure.
date: 2026-03-09T10:23:29+00:00
aliases:
  - /integrations/ai/extensions/mcp-server/
---

The Gatling MCP Server bridges AI applications with [Gatling Enterprise](https://gatling.io/platform),
enabling AI agents to interact with your load testing infrastructure through natural language.
Query teams, packages, simulations, and load generator locations directly from your AI assistant.

{{< arcade id="L0b4TuxIh06ZObAqJ37x" title="Introducing MCP Server" >}}

## Installation

{{< alert info >}}
The Gatling MCP Server currently only runs locally using `stdio` communication.
{{< /alert >}}

### Prerequisites

You need a [Gatling Enterprise](https://gatling.io/platform) account and an API token.
Tokens can be generated from the Gatling Enterprise UI under [**API Tokens**]({{< ref "/reference/administration/api-tokens" >}}).

In all our examples, the API token is provided via the `GATLING_ENTERPRISE_API_TOKEN` environment variable.

Depending on your chosen installation method, you also need:

- [Node.js](https://nodejs.org/) v20 or later (includes `npx`)
- [Docker](https://www.docker.com/) installed and running

### Usage with Claude

The simplest way to use the Gatling MCP Server is to use the [Gatling AI extensions plugin for Claude](https://github.com/gatling/gatling-ai-extensions).
However, you can also install it manually by adding the MCP server to the appropriate configuration files.

We recommended using Claude CLI as it is usually better integrated into IDEs (IntelliJ IDEA, VS Code...).

#### Claude CLI

These configurations assume that you have the `GATLING_ENTERPRISE_API_TOKEN` environment variable set in your shell.

Using **NPX**:

```shell
claude mcp add gatling \
  --env 'GATLING_ENTERPRISE_API_TOKEN=${GATLING_ENTERPRISE_API_TOKEN}' \
  -- npx -y @gatling.io/gatling-mcp-server
```

Using **Docker**:

```shell
claude mcp add gatling \
  --env 'GATLING_ENTERPRISE_API_TOKEN=${GATLING_ENTERPRISE_API_TOKEN}' \
  -- docker run --rm --interactive --env GATLING_ENTERPRISE_API_TOKEN gatlingcorp/gatling-mcp-server
```

#### Claude Desktop

Add the following to your `claude_desktop_config.json`:

- **macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`
- **Windows**: `%APPDATA%\Claude\claude_desktop_config.json`
- **Linux**: `~/.config/Claude/claude_desktop_config.json`

Using **NPX**:

```json
{
  "mcpServers": {
    "gatling": {
      "command": "npx",
      "args": ["-y", "@gatling.io/gatling-mcp-server"],
      "env": {
        "GATLING_ENTERPRISE_API_TOKEN": "<your-api-token>"
      }
    }
  }
}
```

Using **Docker**:

```json
{
  "mcpServers": {
    "gatling": {
      "command": "docker",
      "args": [
        "run",
        "--rm",
        "--interactive",
        "--env", "GATLING_ENTERPRISE_API_TOKEN",
        "gatlingcorp/gatling-mcp-server"
      ],
      "env": {
        "GATLING_ENTERPRISE_API_TOKEN": "<your-api-token>"
      }
    }
  }
}
```

### Usage with VS Code

Add the following to your VS Code `settings.json` or `.vscode/mcp.json`:

Using **NPX**:

```json
{
  "servers": {
    "gatling": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "@gatling.io/gatling-mcp-server"],
      "env": {
        "GATLING_ENTERPRISE_API_TOKEN": "<your-api-token>"
      }
    }
  }
}
```

Using **Docker**:

```json
{
  "servers": {
    "gatling": {
      "type": "stdio",
      "command": "docker",
      "args": [
        "run",
        "--rm",
        "--interactive",
        "--env", "GATLING_ENTERPRISE_API_TOKEN",
        "gatlingcorp/gatling-mcp-server"
      ],
      "env": {
        "GATLING_ENTERPRISE_API_TOKEN": "<your-api-token>"
      }
    }
  }
}
```

## Tools

The MCP server exposes the following tools:

- `list_gatling_enterprise_teams`: List all existing [teams]({{< ref "/reference/user-guide/teams" >}}) in Gatling Enterprise.
- `list_gatling_enterprise_packages`: List all [managed]({{< ref "/reference/run-tests/sources/package-conf" >}}) and [private packages]({{< ref "/reference/deploy/private-locations/private-packages" >}}) deployed in Gatling Enterprise.
- `list_gatling_enterprise_tests`: List all [tests (aka simulations)]({{< ref "/reference/run-tests/simulations/intro" >}}) deployed in Gatling Enterprise.
- `list_gatling_enterprise_locations`: List all [public]({{< ref "/reference/run-tests/simulations/test-as-code#locations-configuration" >}}) and [private locations]({{< ref "/reference/deploy/private-locations/introduction" >}}) where tests can be run on Gatling Enterprise.
