---
title: MCP tools of Gatling AI Server
menutitle: MCP tools
seotitle: Gatling AI Server MCP tools, what each one does and the arguments it takes
description: Find the MCP tools Gatling AI Server serves to your AI assistant, what each one does, its main arguments, what it writes, and the settings that govern it.
lead: The MCP tools your AI assistant calls on Gatling AI Server, what each one does, and the settings that govern it.
date: 2026-11-23T00:00:00+00:00
---

Your AI assistant works with Gatling AI Server through MCP tools. You don't call them yourself: you ask for a
conversion in plain language, as [Convert a plan]({{< ref "/ai/server/convert" >}}) describes, and the assistant picks
the tools and their arguments.

The server serves three tools, in two families: `storage` and `converters`. The settings of the
[configuration reference]({{< ref "/ai/server/configuration/overview" >}}) change what a call may do, not which tools your
assistant sees.

The server checks that each call's token carries the `mcp:tools` scope.

## Upload a file

The `storage` family has one tool:

| **Tool** | **What it does** | **Main arguments** | **Effect** |
| --- | --- | --- | --- |
| `storage.prepare_upload` | Mints a one-time upload URL and token. The assistant sends the file to that URL, outside the conversation, then passes the returned upload ID to the tool that reads the file. | `fileName`, optional: the name of the file to upload. A name that ends in `.jmx` names the simulation class: `checkout.jmx` gives `CheckoutSimulation`. | Creates a one-time upload slot in the server's memory. The file the assistant sends to it is kept in memory too. |

The following settings govern it, as
[Configure the MCP server and HTTP]({{< ref "/ai/server/configuration/conversions-and-limits#configure-the-mcp-server-and-http" >}})
describes:

- `GATLING_UPLOAD_MAX_BYTES`: the largest upload, 10 MiB by default.
- `GATLING_UPLOAD_TTL`: how long the upload URL accepts its upload, 15 minutes by default.

## Convert a plan

The `converters` family has two tools:

| **Tool** | **What it does** | **Main arguments** | **Effect** |
| --- | --- | --- | --- |
| `converters.from_jmeter` | Converts a JMeter test plan into a Gatling Java project, and checks the project by compiling it. The plan is a `.jmx` file, or a zip of the folder JMeter runs it from. Returns a one-time download link to the project, and the code snippets the converter couldn't translate. | `uploadId`, required, then `plan`, `build`, `packaging`, `completion`, `elicitation`, and `guardian`, all optional. The next table describes each. | Keeps the project in the server's memory, behind a one-time download link. Under endpoint completion, sends each snippet's code to your model endpoint. |
| `converters.porting_rules` | Returns the rules for porting a conversion's code snippets to Java, one entry per snippet language. Under caller completion, the assistant calls it once, before it ports the snippets. | `format`, required: `jmeter`. `languages`, required: the 1 to 16 snippet languages the conversion's result names. | Read-only. |

`converters.from_jmeter` takes the following arguments:

| **Argument** | **Values** | **What it does** |
| --- | --- | --- |
| `uploadId` | Required. | The upload ID `storage.prepare_upload` returned. One upload serves one call per plan. |
| `plan` | A path in the zip, such as `tests/checkout.jmx`. | The plan to convert. Required when the zip holds several plans that no other plan includes. Left out for a `.jmx` upload. |
| `build` | `maven` (default) or `gradle`. | The build the project ships with, wrapper included. The packaging `simulation_file_only` ignores it. |
| `packaging` | `project` (default) or `simulation_file_only`. | What the download holds: the whole Gatling project, or the simulation file alone. |
| `completion` | `endpoint` (default) or `caller`. | Who ports the code snippets: your model endpoint, or the assistant's own model. |
| `elicitation` | `never` (default) or `ask`. | Whether the server asks the user questions through an MCP form, about the partial ports and the plan's JDBC and TCP requests. It asks only when the assistant supports MCP forms. |
| `guardian` | `standard` or `strict`. | How strictly the [guardian]({{< ref "/ai/server/data-and-security#understand-the-guardian" >}}) screens this conversion. It can only raise the server's level, never lower it: a looser level is refused. Left out, the server's level applies. |

The following settings govern `converters.from_jmeter`:

- `GATLING_CONVERSION_COMPLETION_MODES` and `GATLING_CONVERSION_ELICITATION_MODES`: the modes a call may ask for. A call
  that asks for a mode the server doesn't allow is refused. A call that leaves `completion` out gets `endpoint` when
  the server allows it, and `caller` otherwise. A call that leaves `elicitation` out gets `never` when the server
  allows it, and `ask` otherwise. See
  [Configure the conversions]({{< ref "/ai/server/configuration/conversions-and-limits#configure-the-conversions" >}}).
- `GATLING_GUARDIAN_STRICTNESS`: the server's guardian level, which a call can raise but not lower. See
  [Configure the conversions]({{< ref "/ai/server/configuration/conversions-and-limits#configure-the-conversions" >}}).
- `GATLING_CONVERSION_DOWNLOAD_TTL`: how long the download link is valid, 15 minutes by default. See
  [Set the time limits of a conversion]({{< ref "/ai/server/configuration/conversions-and-limits#set-the-time-limits-of-a-conversion" >}}).
- `GATLING_CONVERTER_JMETER_MAX_ENTRIES`: the most entries an uploaded zip may hold. See
  [Configure the JMeter converter and the compilation]({{< ref "/ai/server/configuration/conversions-and-limits#configure-the-jmeter-converter-and-the-compilation" >}}).
- The model endpoint and the token budgets, under endpoint completion. See
  [Model endpoint and tokens]({{< ref "/ai/server/configuration/model-endpoint" >}}).

[Conversions and limits]({{< ref "/ai/server/configuration/conversions-and-limits" >}}) describes the other settings
that bound a conversion. `converters.porting_rules` has no setting.

## Next steps

- [Convert a JMeter test plan from your AI assistant]({{< ref "/ai/server/convert" >}}).
- [Understand what data leaves the server, and what it keeps]({{< ref "/ai/server/data-and-security" >}}).
