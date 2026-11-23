---
title: Connect your AI assistant to Gatling AI Server
menutitle: Connect
seotitle: Connect your AI assistant to Gatling AI Server and sign in with Gatling Enterprise
description: Add Gatling AI Server to your AI assistant, such as Claude Code, VS Code, or Cursor, and sign in with your Gatling Enterprise account.
lead: Add your team's Gatling AI Server to your AI assistant and sign in with your Gatling Enterprise account.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server is an MCP server that your AI assistant reaches over HTTPS. To connect, you need the server's URL
from your operator, followed by `/mcp`, for example `https://gatling-ai.example.internal/mcp`, and a Gatling Enterprise
account.

Your AI assistant needs to support remote MCP servers over the Streamable HTTP transport, with OAuth sign-in. The
first time it calls the server, your browser opens Gatling's sign-in page: sign in with your Gatling Enterprise account.

The examples below register the server as `gatling-ai`. This name keeps it apart from the
[Gatling MCP Server]({{< ref "/ai/plugins/mcp-server" >}}), which its documentation registers as `gatling`, so you can
register both in the same assistant.

## Check the supported AI assistants

Gatling's sign-in service admits only the AI assistants Gatling allows:

- Claude Code
- VS Code, with GitHub Copilot
- Cursor
- ChatGPT and Codex
- GitHub Copilot CLI
- Postman
- Zed
- Kiro
- Google Antigravity
- opencode
- Amp
- goose

Another assistant is refused at sign-in. To use one that isn't listed,
[open a support ticket](https://gatlingcorp.atlassian.net/servicedesk/customer/portal/8).

## Connect Claude Code

Run the following command, with your server's URL:

```console
claude mcp add --transport http --scope user gatling-ai https://<your-server>/mcp
```

The `--scope user` option makes the server available in every project you open. Then run `/mcp` in Claude Code,
choose `gatling-ai`, and sign in.

To give everyone who works on a repository the same server, commit a `.mcp.json` file at the repository's root:

```json
{
  "mcpServers": {
    "gatling-ai": {
      "type": "http",
      "url": "https://<your-server>/mcp",
      "timeout": 600000
    }
  }
}
```

The `timeout` of 600,000 milliseconds, 10 minutes, keeps Claude Code waiting longer than one conversion can take on
the server, 9 minutes 30 seconds by default. Each team member signs in once with `/mcp`.

## Connect VS Code

Add the server to a `.vscode/mcp.json` file at the root of your workspace, or to your user configuration with the
**MCP: Open User Configuration** command:

```json
{
  "servers": {
    "gatling-ai": {
      "type": "http",
      "url": "https://<your-server>/mcp"
    }
  }
}
```

Start the server from the file, or with the **MCP: List Servers** command, and sign in when VS Code asks. GitHub
Copilot Chat then lists the server's tools in agent mode.

## Connect Cursor

Add the server to `~/.cursor/mcp.json` for every project, or to `.cursor/mcp.json` at a project's root:

```json
{
  "mcpServers": {
    "gatling-ai": {
      "url": "https://<your-server>/mcp"
    }
  }
}
```

Cursor then lists `gatling-ai` in its MCP settings, where you sign in.

## Connect another AI assistant

For any other supported assistant, add a remote MCP server named `gatling-ai` with the URL
`https://<your-server>/mcp`, as its documentation describes, and sign in when it asks. If the assistant lets you set a
tool call timeout, set it above 10 minutes.

## Next steps

- [Convert a JMeter test plan]({{< ref "/ai/server/convert" >}}).
