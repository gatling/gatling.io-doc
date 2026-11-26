---
title: Conversion and limit settings for Gatling AI Server
menutitle: Conversions and limits
seotitle: Gatling AI Server settings for conversions, the MCP endpoint, uploads, and limits
description: Choose what a Gatling AI Server conversion may do and how long it may take, tune the MCP endpoint and uploads, and bound the work and memory the server takes on.
lead: Choose what a conversion may do and how long it may take, tune the MCP endpoint and uploads, and bound the work and memory the server takes on.
date: 2026-11-23T00:00:00+00:00
---

Each setting on this page is an environment variable. The
[configuration reference]({{< ref "/ai/server/configuration/overview#find-where-each-setting-goes" >}}) says where it goes
with your installation.

## Configure the conversions

The following settings control what a conversion may do. Three terms come up:

- **Completion** is the part of a conversion that ports the plan's code snippets to Java. Under endpoint completion,
  your [model endpoint]({{< ref "/ai/server/configuration/model-endpoint" >}}) ports them. Under caller completion,
  the user's own AI assistant does.
- **Elicitation** is a question the server asks the user, through a form the AI assistant shows: an MCP form.
- **The guardian** screens the plan text of every code snippet before a model reads it, and every ported body before
  the server puts it in the project, as
  [Data and security]({{< ref "/ai/server/data-and-security#understand-the-guardian" >}}) describes.

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_CONVERSION_COMPLETION_MODES` | `endpoint,caller` | The completion modes a conversion may ask for, separated by commas: `endpoint`, `caller`, or both. Leaving `endpoint` out keeps every snippet away from the model endpoint. |
| `GATLING_CONVERSION_ELICITATION_MODES` | `ask,never` | Whether a conversion may ask the user questions (`ask`), about its partially ported snippets and its JDBC and TCP requests, or not (`never`). A call that names neither runs as `never` when the server allows it, and as `ask` otherwise. |
| `GATLING_GUARDIAN_STRICTNESS` | `standard` | How strictly the guardian screens the plan and every ported body: `standard` or `strict`. A conversion can raise the level, never lower it. |
| `GATLING_CONVERSION_REPAIR_ATTEMPTS` | `3` | How many times the snippets that don't compile are sent back to the model. |

What a conversion's download holds is not a server setting: the AI assistant asks for the whole project (`project`) or
the simulation file alone (`simulation_file_only`) with each conversion, as
[Convert a plan]({{< ref "/ai/server/convert#download-the-project" >}}) describes.

## Set the time limits of a conversion

The following settings bound how long a conversion takes, and how long its download stays available:

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_CONVERSION_MAX_ACTIVE_TIME` | `PT1H` | The most time one conversion may spend working. |
| `GATLING_CONVERSION_ANSWER_TIMEOUT` | `PT30M` | How long a conversion waits for the user's or the AI assistant's answer. |
| `GATLING_CONVERSION_DOWNLOAD_TTL` | `PT15M` | How long a conversion's one-time download URL is valid. |

## Configure the JMeter converter and the compilation

The server checks every generated simulation by compiling it. The following settings tune how it reads a JMeter
upload, and how it compiles:

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_CONVERTER_JMETER_DEFAULT_SCRIPT_LANGUAGE` | `groovy` | The language of a JSR223 element that names none: `groovy`, `beanshell`, `javascript`, `jexl`, or `java`. |
| `GATLING_CONVERTER_JMETER_MAX_ENTRIES` | `256` | The most entries, folders included, an uploaded zip may hold. |
| `GATLING_VALIDATION_RELEASE` | `21` | The Java release the generated simulations are compiled for, from 21 to 25. |
| `GATLING_VALIDATION_MAX_CONCURRENT` | `4` | The most compilations at once. |

## Configure the MCP server and HTTP

The following settings control the MCP endpoint, uploads, and browser access. Some of them apply to a client on a
stateless MCP session: a session in which the server can't send requests to the client.

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_SERVER_SYNC_TIMEOUT` | `PT9M30S` | The longest one tool call waits for its conversion. The model's time limits inside a call are shares of it. Keep it under the MCP client's tool timeout, and set your load balancer's idle timeout as [Network]({{< ref "/ai/server/network" >}}) describes. On Azure, the module sets it to `PT3M`. |
| `GATLING_MCP_REQUEST_STATE_KEY` | None | A secret of at least 32 random bytes, in base64, that signs the state of the questions a conversion asks a client on a stateless session. Without it, such a client is asked no question, and the server warns at startup. Generate one with `openssl rand -base64 32`. |
| `GATLING_MCP_CONTINUE_ON_RETRY` | `false` | `true` hands a conversion still running at the end of the wait back to a client on a stateless session, which follows it by retrying, instead of canceling it. Needs the request state key. On Azure, the module sets it to `true`. |
| `GATLING_MCP_ELICITATION_TIMEOUT` | `PT5M` | How long an MCP form waits for the user's answer. |
| `GATLING_UPLOAD_MAX_BYTES` | `10485760` (10 MiB) | The largest upload, in bytes. The HTTP body limit and each converter's largest input follow it. Raise the body size your ingress or load balancer accepts to match. It must stay at or under the [memory cap for files](#configure-the-limits), 128 MiB by default, or the server refuses to start. On Cloud Run, keep it under 32 MiB, the largest HTTP/1 request Cloud Run accepts. |
| `GATLING_UPLOAD_TTL` | `PT15M` | How long an upload URL accepts its upload. |
| `QUARKUS_HTTP_CORS_ORIGINS` | None: only the server's own origin | The browser origins, besides the server's own, allowed to call `/mcp`: a web MCP client's. |

## Configure the limits

The following settings bound the work the server takes on, and the memory it uses. A tenant is the group of users a
server serves. Gatling AI Server serves one, every user who signs in, so a setting "per tenant" applies to the whole
server. The limits of the model calls and of the tokens they spend are under
[Model endpoint and token budgets]({{< ref "/ai/server/configuration/model-endpoint#bound-the-model-calls" >}}).

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_WORKFLOW_MAX_RUNNING_RUNS` | `8` | Conversions running at once. |
| `GATLING_WORKFLOW_MAX_QUEUED_RUNS` | `64` | Conversions waiting to run. Beyond it, a conversion is refused, and the AI assistant can retry it. |
| `GATLING_STORE_INMEMORY_RATE_LIMIT_CAPACITY` | `60` | Tool calls in a burst. |
| `GATLING_STORE_INMEMORY_RATE_LIMIT_PERIOD` | `PT1M` | How long the burst takes to refill. |
| `GATLING_STORE_INMEMORY_MAX_BYTES_PER_TENANT` | `134217728` (128 MiB) | The memory cap for files: the memory the uploaded and generated files may take at once, shared by every user. Size it against the container's memory, of which the server's heap may take 75%. It must be at least the largest upload: to accept uploads above 128 MiB, raise it. |
| `GATLING_STORE_INMEMORY_MAX_TOTAL_BYTES` | `536870912` (512 MiB) | The memory every tenant's files may take at once. The server doesn't start with a cap per tenant above it: when you raise the memory cap for files above 512 MiB, raise this one to the same value. |
| `GATLING_STORE_INMEMORY_FILE_TTL` | `P1D` | How long an uploaded or generated file is kept. |
| `GATLING_STORE_INMEMORY_TASK_RETENTION` | `P1D` | How long an ended conversion stays readable. |
