---
title: Gatling AI Server configuration reference
menutitle: Configuration
seotitle: Gatling AI Server configuration reference, every setting and its default
description: The settings of Gatling AI Server you can change, with their environment variable, their default, and what they control.
lead: The settings you can change in Gatling AI Server, grouped by what they control, with their environment variable and default.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server reads its settings from environment variables. Where you set them depends on how you
[installed it]({{< ref "/ai/server/installation/overview" >}}):

- With the [Helm chart]({{< ref "/ai/server/installation/helm" >}}), the required settings are `server.*` values, the
  two secrets are keys of the Secret you created, and every other setting goes in `env` or `envFrom`.
- With a [Terraform module]({{< ref "/ai/server/installation/terraform" >}}), the required settings are inputs of the
  module, the two secrets are references to your cloud's secret store, and every other setting goes in the module's
  environment map.

## Find where each setting goes

The following table gives, for each installation, the value or the input that sets each setting:

| **Setting** | **Helm chart** | **AWS module** | **Azure module** | **Google Cloud module** |
| --- | --- | --- | --- | --- |
| `GATLING_SERVER_PUBLIC_BASE_URL` | `server.publicBaseUrl` | `server.public-base-url` | Set by the module, to the container app's address | Set by the module, to the service's `run.app` URL |
| `GATLING_MODEL_ENDPOINT_BASE_URL` | `server.model.baseUrl` | `server.model.base-url` | `server.model.base-url` | `server.model.base-url` |
| `GATLING_MODEL_ENDPOINT_MODEL` | `server.model.name` | `server.model.name` | `server.model.name` | `server.model.name` |
| `GATLING_MODEL_ENDPOINT_API_KEY` | The Secret's key `model-api-key` | `server.model.api-key-secret-arn`, a Secrets Manager secret | `server.model.api-key-secret-id`, a Key Vault secret | `server.model.api-key-secret`, a Secret Manager secret |
| `GATLING_MCP_REQUEST_STATE_KEY` | The Secret's key `request-state-key` | `server.request-state-key-secret-arn` | `server.request-state-key-secret-id` | `server.request-state-key-secret` |
| Any other setting | `env`, `envFrom` | `task.environment` | `container.environment` | `container.environment` |

Each module refuses an environment map that sets a variable the module sets itself. The Azure module also sets
`GATLING_SERVER_SYNC_TIMEOUT` and `GATLING_MCP_CONTINUE_ON_RETRY`, as its
[limits]({{< ref "/ai/server/installation/terraform#azure-limits" >}}) explain. A module's environment map is stored in
plain text in the Terraform state: put no secret there.

## Start from an example

The following settings are what you most likely change beyond the required ones. It's a starting point: keep the lines
you need, and find every setting in the reference below.

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
  # files.
  - name: GATLING_UPLOAD_MAX_BYTES
    value: "26214400"
  # Wait at most 4 minutes 30 seconds per tool call. Keep it under your
  # MCP client's tool timeout.
  - name: GATLING_SERVER_SYNC_TIMEOUT
    value: PT4M30S
  # Keep a history of the conversions on the data volume, so users can
  # list, download again, and delete their own conversions.
  - name: GATLING_CONVERSIONS_HISTORY_ENABLED
    value: "true"
  # Delete a conversion from the history 14 days after it started.
  - name: GATLING_CONVERSIONS_HISTORY_RETENTION
    value: P14D
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
  GATLING_CONVERSIONS_HISTORY_ENABLED        = "true"
  GATLING_CONVERSIONS_HISTORY_RETENTION      = "P14D"
  QUARKUS_LOG_CONSOLE_JSON_ENABLED           = "true"
}
```

On Azure, leave `GATLING_SERVER_SYNC_TIMEOUT` out: the module sets it, and refuses it in the map. With the Terraform
modules, the conversion history resets at each restart.

## Understand how settings are read

Each setting has a key and an environment variable: the key in capitals, with every character that isn't a letter or
a digit replaced by `_`. For example, `gatling.server.public-base-url` is `GATLING_SERVER_PUBLIC_BASE_URL`.

The server reads a setting from these sources, from the highest priority to the lowest:

1. A system property, `-D<key>=<value>`, in `JDK_JAVA_OPTIONS`. Setting `JDK_JAVA_OPTIONS` replaces the image's own
   value, `-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError`: keep these two options.
2. An environment variable.
3. A properties file mounted at `/deployments/config/application.properties`, of `key=value` lines.
4. The defaults packaged in the image.

Durations use the ISO 8601 format: `PT15M` is 15 minutes, `PT9M30S` is 9 minutes 30 seconds, and `P1D` is one day.

## Set the required settings

The server refuses to start without these settings:

- `GATLING_SERVER_PUBLIC_BASE_URL`

  The absolute URL MCP clients reach the server at, for example `https://gatling-ai.example.internal`. The server
  builds the upload and download URLs and its protected resource metadata from it.

  Required\
  Key: `gatling.server.public-base-url`

- `GATLING_MODEL_ENDPOINT_BASE_URL`

  The root of the OpenAI-compatible model endpoint (`<base-url>/chat/completions`) that ports the snippets under
  endpoint completion. HTTPS, unless the host is the loopback or `GATLING_MODEL_ENDPOINT_ALLOW_PLAIN_HTTP` is `true`.

  Required\
  Key: `gatling.model.endpoint.base-url`

- `GATLING_MODEL_ENDPOINT_MODEL`

  The model's name at the provider.

  Required\
  Key: `gatling.model.endpoint.model`

- `GATLING_MODEL_ENDPOINT_API_KEY`

  The bearer token sent to the model endpoint. A secret. A local endpoint that ignores it still needs a value.

  Required\
  Key: `gatling.model.endpoint.api-key`

The Helm chart and the Terraform modules also take `GATLING_MCP_REQUEST_STATE_KEY` from your secret store, described
under [MCP server and HTTP](#configure-the-mcp-server-and-http). It's optional, but without it, stateless MCP clients
are asked no question.

## Configure the model endpoint

These settings tune the calls to the model endpoint:

- `GATLING_MODEL_ENDPOINT_ALLOW_PLAIN_HTTP`

  Lets the base URL use plain HTTP to a host other than the loopback. The API key and every prompt then cross the
  network in clear.

  Default: `false`\
  Key: `gatling.model.endpoint.allow-plain-http`

- `GATLING_MODEL_ENDPOINT_OUTPUT_LIMIT_PARAMETER`

  The request member that carries the output-token cap: `max_completion_tokens` (OpenAI) or `max_tokens` (Ollama and
  older providers).

  Default: `max_completion_tokens`\
  Key: `gatling.model.endpoint.output-limit-parameter`

- `GATLING_MODEL_ENDPOINT_MAX_RETRIES`

  How many times a failed request (HTTP 408, 429, or 5xx, a timeout, a connection failure) is retried, from 0 to 10.

  Default: `2`\
  Key: `gatling.model.endpoint.max-retries`

- `GATLING_MODEL_ENDPOINT_RETRY_BACKOFF`

  The longest wait before the first retry. It doubles on each retry, up to 60 seconds.

  Default: `PT1S`\
  Key: `gatling.model.endpoint.retry-backoff`

- `GATLING_COMPLETION_MODEL_MODEL`

  The model named in each completion request.

  Default: the value of `GATLING_MODEL_ENDPOINT_MODEL`\
  Key: `gatling.completion.model.model`

- `GATLING_COMPLETION_MODEL_TEMPERATURE`

  The sampling temperature, from 0 to 2. Some models, OpenAI's reasoning models among them, refuse any value but their
  default.

  Default: none, the provider's default applies\
  Key: `gatling.completion.model.temperature`

- `GATLING_COMPLETION_MODEL_MAX_OUTPUT_TOKENS`

  The output tokens requested per call. Set it to the model's own limit when that's lower.

  Default: `8192`\
  Key: `gatling.completion.model.max-output-tokens`

- `GATLING_COMPLETION_MODEL_MAX_CONCURRENCY`

  How many snippets of one file are ported at once, from 1 to 64. At most `GATLING_WORKFLOW_MAX_MODEL_CALLS_PER_TENANT`
  run at once, so keep it at or under that.

  Default: `8`\
  Key: `gatling.completion.model.max-concurrency`

## Configure the conversions

These settings control what a conversion may do, and how long it may take. What a conversion's download holds is not
a server setting: the AI assistant asks for the whole project (`project`) or the simulation file alone
(`simulation_file_only`) with each conversion, as [Convert a plan]({{< ref "/ai/server/convert#download-the-project" >}})
describes.

- `GATLING_CONVERSION_COMPLETION_MODES`

  The completion modes a conversion may ask for, separated by commas: `endpoint`, the model endpoint ports the
  snippets; `caller`, the user's own AI assistant does. Leaving `endpoint` out keeps every snippet away from the model
  endpoint.

  Default: `endpoint,caller`\
  Key: `gatling.conversion.completion-modes`

- `GATLING_CONVERSION_ELICITATION_MODES`

  Whether a conversion may ask the user questions through an MCP form (`ask`), about its partially ported snippets and
  its JDBC and TCP requests, or not (`never`). A conversion asks only when the assistant calls it with `ask`; one that
  names neither runs as `never`.

  Default: `ask,never`\
  Key: `gatling.conversion.elicitation-modes`

- `GATLING_CONVERSION_DOWNLOAD_TTL`

  How long a conversion's one-time download URL is valid.

  Default: `PT15M`\
  Key: `gatling.conversion.download-ttl`

- `GATLING_CONVERSION_MAX_ACTIVE_TIME`

  The most time one conversion may spend working.

  Default: `PT1H`\
  Key: `gatling.conversion.max-active-time`

- `GATLING_CONVERSION_ANSWER_TIMEOUT`

  How long a conversion waits for the user's or the AI assistant's answer.

  Default: `PT30M`\
  Key: `gatling.conversion.answer-timeout`

- `GATLING_CONVERSION_MAX_MODEL_SHARE`

  The largest share of a file's executable lines the model may write, between 0 and 1. The snippets beyond it are left
  as gaps.

  Default: `0.95`\
  Key: `gatling.conversion.max-model-share`

- `GATLING_CONVERSION_REPAIR_ATTEMPTS`

  How many times the snippets that don't compile are sent back to the model.

  Default: `3`\
  Key: `gatling.conversion.repair-attempts`

- `GATLING_CONVERSION_TOKENS_PER_FILE`

  The model tokens one uploaded file may spend, before its snippets add theirs.

  Default: `1200000`\
  Key: `gatling.conversion.tokens-per-file`

- `GATLING_CONVERSION_TOKENS_PER_SNIPPET`

  The model tokens each snippet adds to its file's bound.

  Default: `280000`\
  Key: `gatling.conversion.tokens-per-snippet`

- `GATLING_GUARDIAN_STRICTNESS`

  How strictly the guardian screens the plan and every ported body: `standard` or `strict`.

  Default: `standard`\
  Key: `gatling.guardian.strictness`

- `GATLING_CONVERTER_JMETER_DEFAULT_SCRIPT_LANGUAGE`

  The language of a JSR223 element that names none: `groovy`, `beanshell`, `javascript`, `jexl`, or `java`.

  Default: `groovy`\
  Key: `gatling.converter.jmeter.default-script-language`

- `GATLING_CONVERTER_JMETER_MAX_ENTRIES`

  The most entries, folders included, an uploaded zip may hold.

  Default: `256`\
  Key: `gatling.converter.jmeter.max-entries`

- `GATLING_VALIDATION_RELEASE`

  The Java release the generated simulations are compiled for, from 21 to 25.

  Default: `21`\
  Key: `gatling.validation.release`

- `GATLING_VALIDATION_MAX_CONCURRENT`

  The most compilations at once.

  Default: `4`\
  Key: `gatling.validation.max-concurrent`

## Configure the MCP server and HTTP

These settings control the MCP endpoint, uploads, and browser access:

- `GATLING_SERVER_SYNC_TIMEOUT`

  The longest one tool call waits for its conversion. The model's time limits inside a call are shares of it. Keep it
  under the MCP client's tool timeout, and set your load balancer's idle timeout as
  [Network]({{< ref "/ai/server/network" >}}) describes. The Azure module sets it to `PT3M30S`.

  Default: `PT9M30S`\
  Key: `gatling.server.sync-timeout`

- `GATLING_MCP_REQUEST_STATE_KEY`

  A secret of at least 32 random bytes, in base64, that signs the state of a stateless MCP client's questions. Without
  it, such a client is asked no question, and the server warns at startup. Generate one with
  `openssl rand -base64 32`.

  Default: none\
  Key: `gatling.mcp.request-state-key`

- `GATLING_MCP_CONTINUE_ON_RETRY`

  Hands a conversion still running at the end of the wait back to a stateless client, which follows it by retrying,
  instead of cancelling it. Needs the request state key. The Azure module sets it to `true`.

  Default: `false`\
  Key: `gatling.mcp.continue-on-retry`

- `GATLING_MCP_ELICITATION_TIMEOUT`

  How long an MCP form waits for the user's answer.

  Default: `PT5M`\
  Key: `gatling.mcp.elicitation-timeout`

- `GATLING_UPLOAD_MAX_BYTES`

  The largest upload, in bytes. The HTTP body limit and each converter's largest input follow it. The server
  refuses to start above `134217728` (128 MiB), the most memory its uploaded and generated files may take at once. On
  Cloud Run, keep it under 32 MiB, the largest HTTP/1 request Cloud Run accepts.

  Default: `10485760` (10 MiB)\
  Key: `gatling.upload.max-bytes`

- `GATLING_UPLOAD_TTL`

  How long an upload URL accepts its upload.

  Default: `PT15M`\
  Key: `gatling.upload.ttl`

- `QUARKUS_HTTP_CORS_ORIGINS`

  The browser origins, besides the server's own, allowed to call `/mcp`: a web MCP client's.

  Default: none, only the server's own origin\
  Key: `quarkus.http.cors.origins`

## Configure the limits

These settings bound the work the server takes on, and the memory it uses:

- `GATLING_WORKFLOW_MAX_RUNNING_RUNS`

  Conversions running at once.

  Default: `8`\
  Key: `gatling.workflow.max-running-runs`

- `GATLING_WORKFLOW_MAX_QUEUED_RUNS`

  Conversions waiting to run. Beyond it, a conversion is refused, to retry.

  Default: `64`\
  Key: `gatling.workflow.max-queued-runs`

- `GATLING_WORKFLOW_MAX_MODEL_CALLS_PER_TENANT`

  Model calls in flight at once.

  Default: `8`\
  Key: `gatling.workflow.max-model-calls-per-tenant`

- `GATLING_WORKFLOW_MODEL_CALL_WAIT`

  How long a model call waits for its turn before it fails, to retry.

  Default: `PT2M`\
  Key: `gatling.workflow.model-call-wait`

- `GATLING_WORKFLOW_MODEL_TOKEN_QUOTA`

  The model tokens, prompt and answer, the server may spend per quota window. Every user shares it.

  Default: `20000000`\
  Key: `gatling.workflow.model-token-quota`

- `GATLING_WORKFLOW_MODEL_TOKEN_QUOTA_PERIOD`

  The length of the quota window. `P1D` starts a new one at midnight UTC.

  Default: `P1D`\
  Key: `gatling.workflow.model-token-quota-period`

- `GATLING_STORE_INMEMORY_RATE_LIMIT_CAPACITY`

  Tool calls in a burst.

  Default: `60`\
  Key: `gatling.store.inmemory.rate-limit-capacity`

- `GATLING_STORE_INMEMORY_RATE_LIMIT_PERIOD`

  How long the burst takes to refill.

  Default: `PT1M`\
  Key: `gatling.store.inmemory.rate-limit-period`

- `GATLING_STORE_INMEMORY_FILE_TTL`

  How long an uploaded or generated file is kept.

  Default: `P1D`\
  Key: `gatling.store.inmemory.file-ttl`

- `GATLING_STORE_INMEMORY_TASK_RETENTION`

  How long an ended conversion stays readable.

  Default: `P1D`\
  Key: `gatling.store.inmemory.task-retention`

## Configure the SQLite file

The server keeps the model token counts and the audit trail in one SQLite file, in its data folder,
`/deployments/data`. The Helm chart keeps that folder on a persistent volume claim, so the token counts, the audit
trail, and the conversion history stay across a restart. The Terraform modules mount no volume there: the folder
resets at each restart.

- `GATLING_STORE_SQLITE_PATH`

  The file the model token counts and the audit trail are kept in. Its folder must be writable by the server's user,
  and not mounted `noexec`.

  Default: `/deployments/data/gatling.db`\
  Key: `gatling.store.sqlite.path`

- `GATLING_STORE_SQLITE_AUDIT_RETENTION`

  How long an audit record is kept.

  Default: `P7D`\
  Key: `gatling.store.sqlite.audit-retention`

- `GATLING_STORE_SQLITE_AUDIT_RECORDS_PER_TENANT`

  The most audit records kept. The oldest goes first.

  Default: `10000`\
  Key: `gatling.store.sqlite.audit-records-per-tenant`

- `GATLING_STORE_SQLITE_BUSY_TIMEOUT`

  How long a write waits for the file's lock.

  Default: `PT5S`\
  Key: `gatling.store.sqlite.busy-timeout`

## Keep a conversion history

The conversion history is off by default. Turned on, the server keeps a folder per conversion, and the AI assistant
gets four more tools:

- `conversions.read_all` lists the user's conversions, newest first.
- `conversions.read_one` reads one of them, with its outcome and its snippets.
- `conversions.create_download_link` gives a one-time URL that downloads a conversion's project again, valid for
  `GATLING_CONVERSION_DOWNLOAD_TTL`.
- `conversions.delete_one` deletes a conversion and its folder, stopping it first if it's still running. Each delete
  is recorded in the audit trail.

Each user sees only the conversions they started: another user's conversion is answered as a missing one. The server
serves no page and no review tool.

{{< alert warning >}}
Each conversion's folder holds the uploaded plan, the report, and the generated code, written to disk in clear. It
never holds the model's prompts or answers. Turning the history on is your acknowledgement of this, and the server
logs a warning at every startup that says so. The folders are in `/deployments/data/conversions`, in the data folder:
with the Terraform modules, which mount no volume there, the history is lost at each restart. On Cloud Run, the
folders also count against the memory limit.
{{< /alert >}}

- `GATLING_CONVERSIONS_HISTORY_ENABLED`

  `true` keeps a folder per conversion and adds the history's tools. Exactly `true` or `false`, in lower case: any
  other value, such as `TRUE`, `1`, or `yes`, stops the server at startup.

  Default: `false`\
  Key: `gatling.conversions.history.enabled`

- `GATLING_CONVERSIONS_HISTORY_RETENTION`

  How long a conversion's folder is kept, from the conversion's start. The server deletes older folders at startup and
  every hour, except a conversion's that's still running.

  Default: `P30D`\
  Key: `gatling.conversions.history.retention`

## Configure telemetry and logs

The server exports no metrics. These settings turn on OpenTelemetry traces and JSON logs:

- `QUARKUS_OTEL_SDK_DISABLED`

  `false` exports one OpenTelemetry trace per conversion, over OTLP.

  Default: `true`\
  Key: `quarkus.otel.sdk.disabled`

- `QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT`

  The OTLP collector the traces go to.

  Default: `http://localhost:4317/`\
  Key: `quarkus.otel.exporter.otlp.endpoint`

- `QUARKUS_OTEL_EXPORTER_OTLP_PROTOCOL`

  The OTLP protocol: `grpc` or `http/protobuf`.

  Default: `grpc`\
  Key: `quarkus.otel.exporter.otlp.protocol`

- `QUARKUS_OTEL_SERVICE_NAME`

  The service the traces are reported under.

  Default: `gatling-ai-server`\
  Key: `quarkus.otel.service.name`

- `QUARKUS_LOG_CONSOLE_JSON_ENABLED`

  `true` writes one JSON object per log line, with the trace ID, the span ID, and the tenant as fields.

  Default: `false`\
  Key: `quarkus.log.console.json.enabled`

- `QUARKUS_LOG_LEVEL`

  The level of the server's log.

  Default: `INFO`\
  Key: `quarkus.log.level`
