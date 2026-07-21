---
title: Gatling MCP Server
description: Connect AI assistants to Gatling Enterprise using the Model Context Protocol (MCP) server to query and manage your load testing infrastructure through natural language.
lead: The Gatling MCP Server exposes your Gatling Enterprise resources to AI assistants via the Model Context Protocol, enabling natural language interaction with your load testing infrastructure.
date: 2026-03-09T10:23:29+00:00
aliases:
  - /ai/mcp-server/
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

- [Node.js](https://nodejs.org/) v24 or later (includes `npx`)
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

The MCP server exposes the following tools, grouped by resource:

### Teams

- `teams.read_all`: List all [teams]({{< ref "/reference/user-guide/teams" >}}) visible to the API token.

### Packages

- `packages.read_all`: List all [managed]({{< ref "/reference/run-tests/sources/package-conf" >}}) and [private]({{< ref "/reference/deploy/private-locations/private-packages" >}}) packages visible to the API token.

### Source repositories

- `source_repositories.read_all` / `source_repositories.read_one`: List or get the details of registered [git repositories]({{< ref "/reference/deploy/private-locations/build-from-git" >}}) a test can build from.
- `source_repositories.create_one`: Register a new source repository.
- `source_repositories.delete_one`: Delete a source repository.

### Locations

- `locations.read_all`: List all [managed]({{< ref "/reference/run-tests/tests/test-as-code#locations-configuration" >}}) and [private locations]({{< ref "/reference/deploy/private-locations/introduction" >}}) where tests can be run.

### Tests

- `tests.read_all` / `tests.read_one`: List or get the details of [tests]({{< ref "/reference/run-tests/tests/intro" >}}).
- `tests.create_one`: Create a new test from a package or source repository.
- `tests.patch_one`: Update a test's configuration.
- `tests.delete_one`: Delete a test.
- `tests.start_one`: Start a run for a test.

### Runs

- `runs.read_all` / `runs.read_one`: List or get the details of runs.
- `runs.read_status`: Get a run's current status.
- `runs.read_run_logs`: Get a run's execution logs.
- `runs.read_report_requests` / `runs.read_report_groups`: Get a run's per-request or per-group performance statistics.
- `runs.create_public_link`: Create a public, unauthenticated link to a run's dashboard.
- `runs.patch_one`: Update a run's details.
- `runs.stop_one`: Stop a running run.

Each tool's own description states its required role (Read, Configure, or Start) and any other constraints. [See the `gatling-mcp` skill below]({{< ref "#skill" >}}) for the concepts and workflows that tie them together.

## Skill

When installed via the [Gatling AI extensions plugin for Claude](https://github.com/gatling/gatling-ai-extensions), the MCP server is paired with the `gatling-mcp` skill, which documents these tools' terminology (tests, runs, sources, packages, locations, teams), required roles, and recommended workflows so the AI assistant uses them correctly.

## Example use cases

Once connected, you can drive Gatling Enterprise entirely through natural language. A few things to try:

### Clean up tests

```
Rename all my tests that still use the old naming convention.
```

Lists your existing tests, then updates the ones that match.

### Register a new test

```
Create a test for this repo, build it with Maven, and run it from the Paris location.
```

Registers the source repository if needed, then creates a build-from-sources test. If the project isn't git-buildable, pair this with the [`gatling-build-tools`]({{< ref "./skills#build-tools" >}}) skill to package and upload the artifact first, then create a test pointing at the resulting package instead.

### Diagnose a failed run

```
My last run failed, what happened?
```

Pulls the run's logs and cross-references its performance report to tell you whether it was a build error, an injection crash, or a stop criteria breach.

### Analyze results

```
What's the p95 response time for the login request in the last run?
```

Reads the run's per-request report and pulls out the matching request.
