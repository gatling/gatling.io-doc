---
title: Connect your AI assistant to Gatling AI Server
menutitle: Connect
seotitle: Connect Claude Code to Gatling AI Server and sign in with Gatling Enterprise
description: Add Gatling AI Server to Claude Code, for yourself or for your whole team, and sign in with your Gatling Enterprise account.
lead: Add your team's Gatling AI Server to Claude Code and sign in with your Gatling Enterprise account.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server is an MCP server that your AI assistant reaches over HTTPS. To connect, you need the server's URL
from your operator, followed by `/mcp`, for example `https://gatling-ai.example.internal/mcp`, and a Gatling Enterprise
account.

## Add the server to Claude Code

Run the following command, with your server's URL:

```console
claude mcp add --transport http --scope user gatling-ai https://<your-server>/mcp
```

The `--scope user` option makes the server available in every project you open with Claude Code.

{{< alert info >}}
The name `gatling-ai` keeps this server apart from the [Gatling MCP Server]({{< ref "/ai/plugins/mcp-server" >}}),
which its documentation registers as `gatling`. You can register both in the same Claude Code.
{{< /alert >}}

## Sign in

In Claude Code, run `/mcp` and choose `gatling-ai`. Your browser opens Gatling's sign-in page: sign in with your
Gatling Enterprise account. Claude Code then lists the server's tools.

## Share the server with your team

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
the server, 9 minutes 30 seconds by default. Each team member signs in once with `/mcp`, as above.

{{< alert tip >}}
To set the same timeout for the user-scoped server, register it with
`claude mcp add-json --scope user gatling-ai '{"type":"http","url":"https://<your-server>/mcp","timeout":600000}'`
instead.
{{< /alert >}}

## Use another MCP client

This release supports Claude Code. Gatling's sign-in service admits only the MCP clients Gatling allows, so another
client may be refused at sign-in, even when it supports the Streamable HTTP transport and OAuth with Client ID Metadata
Documents.

## Next steps

- [Convert a JMeter test plan]({{< ref "/ai/server/convert" >}}).
