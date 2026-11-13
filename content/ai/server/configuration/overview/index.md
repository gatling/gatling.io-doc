---
title: Gatling AI Server configuration reference
menutitle: Overview
seotitle: Gatling AI Server configuration reference, where each setting goes and how the server reads it
description: Find where each setting of Gatling AI Server goes with your installation, how the server reads its settings, the settings it requires, and the pages that describe the others.
lead: Where each setting of Gatling AI Server goes with your installation, how the server reads it, and the settings it can't start without.
date: 2026-11-23T00:00:00+00:00
---

Gatling AI Server reads its settings from environment variables. This page says where they go with your
installation, how the server reads them, and which ones it requires. Three pages describe the settings you can
change, by topic, each with its default:

- [Model endpoint and token budgets]({{< ref "/ai/server/configuration/model-endpoint" >}}): how the server calls your
  model endpoint, how many model calls run at once, and the tokens they may spend.
- [Conversions and limits]({{< ref "/ai/server/configuration/conversions-and-limits" >}}): what a conversion may do
  and how long it may take, the MCP endpoint and uploads, and the work and memory the server takes on.
- [Telemetry and logs]({{< ref "/ai/server/configuration/telemetry-and-logs" >}}): OpenTelemetry traces and the
  server's log.

## Find where each setting goes

Where you set a setting depends on how you [installed the server]({{< ref "/ai/server/installation/overview" >}}).
The required settings have inputs of their own, the two secrets come from your secret store, and every other setting
is an environment variable. Choose your installation:

{{< include-file >}}
1-Helm chart: helm.md
2-AWS: aws.md
3-Azure: azure.md
4-Google Cloud: google-cloud.md
{{< /include-file >}}

## Understand how settings are read

Each setting has an environment variable, the name these pages give, and a key. The environment variable is the key
in capitals, with every character that isn't a letter or a digit replaced by `_`: the key
`gatling.server.public-base-url` is `GATLING_SERVER_PUBLIC_BASE_URL`.

Environment variables work on every installation, and they're what the Helm chart and the Terraform modules set. On
another container platform, you can keep settings in a file instead: `/deployments/.env`, one `NAME=value` line each,
under the same names as the environment variables.

The server reads a setting from these sources, from the highest priority to the lowest:

1. A system property, `-D<key>=<value>`, in `JDK_JAVA_OPTIONS`. Setting `JDK_JAVA_OPTIONS` replaces the image's own
   value, `-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError`: keep these two options.
2. An environment variable.
3. A `.env` file in the server's working directory, `/deployments/.env`, of `NAME=value` lines named as the
   environment variables.
4. A properties file mounted at `/deployments/config/application.properties`, of `key=value` lines.
5. The defaults packaged in the image.

The server reads its settings when it starts: a change takes effect at the next restart. Durations use the ISO 8601
format: `PT15M` is 15 minutes, `PT9M30S` is 9 minutes 30 seconds, and `P1D` is one day.

## Set the required settings

The server refuses to start without these settings:

| **Environment variable** | **What it does** |
| --- | --- |
| `GATLING_SERVER_PUBLIC_BASE_URL` | The absolute URL MCP clients reach the server at, for example `https://gatling-ai.example.internal`. The server builds the upload and download URLs and its protected resource metadata from it. The Azure and Google Cloud modules set it themselves. |
| `GATLING_MODEL_ENDPOINT_BASE_URL` | The root of the OpenAI-compatible model endpoint (`<base-url>/chat/completions`) that ports the snippets under endpoint completion. HTTPS, unless the host is the loopback or `GATLING_MODEL_ENDPOINT_ALLOW_PLAIN_HTTP` is `true`. |
| `GATLING_MODEL_ENDPOINT_MODEL` | The model's name at the provider. |
| `GATLING_MODEL_ENDPOINT_API_KEY` | The bearer token sent to the model endpoint. A secret. A local endpoint that ignores it still needs a value. |

Set this one too, although the server starts without it:

| **Environment variable** | **What it does** |
| --- | --- |
| `GATLING_MCP_REQUEST_STATE_KEY` | A secret of at least 32 random bytes, in base64, that signs the state of the questions a conversion asks a client on a stateless MCP session, in which the server can't send requests to the client. Without it, such a client is asked no question, and the server warns at startup. [Generate the request state key]({{< ref "/ai/server/installation/overview#generate-the-request-state-key" >}}) shows how. |

## Start from an example

The following settings are a common starting point beyond the required ones. Keep the lines you need, and find the
others on the topic pages above.

With the Helm chart, they go in `env`, in your values file:

```yaml
env:
  # Ask the model for up to 16384 output tokens per call. Set it to
  # your model's own limit when that's lower.
  - name: GATLING_COMPLETION_MODEL_MAX_OUTPUT_TOKENS
    value: "16384"
  # Port at most 4 snippets of one file at once, to stay under your
  # model provider's rate limits.
  - name: GATLING_COMPLETION_MODEL_MAX_CONCURRENCY
    value: "4"
  # Accept uploads of up to 25 MiB, for large plans and their data
  # files, and raise your ingress's body size limit to match.
  - name: GATLING_UPLOAD_MAX_BYTES
    value: "26214400"
  # Wait at most 4 minutes 30 seconds per tool call. Keep it under your
  # MCP client's tool timeout.
  - name: GATLING_SERVER_SYNC_TIMEOUT
    value: PT4M30S
  # Write one JSON object per log line, for your log collector.
  - name: QUARKUS_LOG_CONSOLE_JSON_ENABLED
    value: "true"
```

With a Terraform module, they go in the environment map, `task.environment` on AWS and `container.environment` on
Azure and Google Cloud:

```hcl
environment = {
  GATLING_COMPLETION_MODEL_MAX_OUTPUT_TOKENS = "16384"
  GATLING_COMPLETION_MODEL_MAX_CONCURRENCY   = "4"
  GATLING_UPLOAD_MAX_BYTES                   = "26214400"
  GATLING_SERVER_SYNC_TIMEOUT                = "PT4M30S"
  QUARKUS_LOG_CONSOLE_JSON_ENABLED           = "true"
}
```

On Azure, leave `GATLING_SERVER_SYNC_TIMEOUT` out: the module sets it, and refuses it in the map.
