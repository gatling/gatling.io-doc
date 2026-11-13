---
title: Gatling AI Server configuration reference
menutitle: Configuration
seotitle: Gatling AI Server configuration reference, every setting and its default
description: Every setting of Gatling AI Server, with its environment variable, its default, and what it controls.
lead: Every setting Gatling AI Server reads, grouped by what it controls, with its environment variable and default.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server reads its settings from the environment. With the
[Docker Compose installation]({{< ref "/ai/server/installation" >}}), the required ones go in `.env`, and the others
in an optional `server.env` file next to `compose.yaml`, one `NAME=value` per line.

## Understand how settings are read

Each setting has a key and an environment variable: the key in capitals, with every character that isn't a letter or
a digit replaced by `_`. For example, `gatling.server.public-base-url` is `GATLING_SERVER_PUBLIC_BASE_URL`.

The server reads a setting from these sources, from the highest priority to the lowest:

1. A system property, `-D<key>=<value>`, in `JDK_JAVA_OPTIONS`. Setting `JDK_JAVA_OPTIONS` replaces the image's own
   value, `-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError`: keep these two options.
2. An environment variable.
3. A `.env` file in the server's working directory, `/deployments/.env`, of `NAME=value` lines.
4. A properties file mounted at `/deployments/config/application.properties`, of `key=value` lines.
5. The defaults packaged in the image.

Durations use the ISO 8601 format: `PT15M` is 15 minutes, `PT9M30S` is 9 minutes 30 seconds, and `P1D` is one day.

## Set the required settings

The server refuses to start without these settings:

| **Setting** | **Environment variable** | **Description** |
| --- | --- | --- |
| `gatling.server.public-base-url` | `GATLING_SERVER_PUBLIC_BASE_URL` | The absolute URL MCP clients reach the server at, for example `https://gatling-ai.example.internal`. The server builds the upload and download URLs and its protected resource metadata from it. |
| `gatling.model.endpoint.base-url` | `GATLING_MODEL_ENDPOINT_BASE_URL` | The root of the OpenAI-compatible model endpoint (`<base-url>/chat/completions`) that ports the snippets under endpoint completion. HTTPS, unless the host is the loopback or `gatling.model.endpoint.allow-plain-http` is `true`. |
| `gatling.model.endpoint.model` | `GATLING_MODEL_ENDPOINT_MODEL` | The model's name at the provider. |
| `gatling.model.endpoint.api-key` | `GATLING_MODEL_ENDPOINT_API_KEY` | The bearer token sent to the model endpoint. A secret. A local endpoint that ignores it still needs a value. |

The Docker Compose file also requires `GATLING_MCP_REQUEST_STATE_KEY`, described under
[MCP server and HTTP](#configure-the-mcp-server-and-http).

## Configure the identity

Users sign in through Gatling's sign-in service, where Gatling Enterprise accounts belong: a valid token proves its
holder is a Gatling Enterprise user. A request without a valid token gets a `401`. The server needs no Gatling
Enterprise API token, and you don't need to change these settings:

| **Setting** | **Environment variable** | **Default** | **Description** |
| --- | --- | --- | --- |
| `gatling.identity.auth-server-url` | `GATLING_IDENTITY_AUTH_SERVER_URL` | `https://auth.gatling.io/auth` | The base URL of Gatling's sign-in service, with no trailing slash. HTTPS, or HTTP to the loopback only. |
| `gatling.identity.realm` | `GATLING_IDENTITY_REALM` | `gatling` | The realm users sign in through. |

## Configure the model endpoint

These settings tune the calls to the model endpoint:

| **Setting** | **Environment variable** | **Default** | **Description** |
| --- | --- | --- | --- |
| `gatling.model.endpoint.allow-plain-http` | `GATLING_MODEL_ENDPOINT_ALLOW_PLAIN_HTTP` | `false` | Lets the base URL use plain HTTP to a host other than the loopback. The API key and every prompt then cross the network in clear. |
| `gatling.model.endpoint.output-limit-parameter` | `GATLING_MODEL_ENDPOINT_OUTPUT_LIMIT_PARAMETER` | `max_completion_tokens` | The request member that carries the output-token cap: `max_completion_tokens` (OpenAI) or `max_tokens` (Ollama and older providers). |
| `gatling.model.endpoint.max-retries` | `GATLING_MODEL_ENDPOINT_MAX_RETRIES` | `2` | How many times a failed request (HTTP 408, 429, or 5xx, a timeout, a connection failure) is retried, from 0 to 10. |
| `gatling.model.endpoint.retry-backoff` | `GATLING_MODEL_ENDPOINT_RETRY_BACKOFF` | `PT1S` | The longest wait before the first retry. It doubles on each retry, up to 60 seconds. |
| `gatling.completion.model.model` | `GATLING_COMPLETION_MODEL_MODEL` | The endpoint's model | The model named in each completion request. |
| `gatling.completion.model.temperature` | `GATLING_COMPLETION_MODEL_TEMPERATURE` | The provider's | The sampling temperature, from 0 to 2. Some models, OpenAI's reasoning models among them, refuse any value but their default. |
| `gatling.completion.model.max-output-tokens` | `GATLING_COMPLETION_MODEL_MAX_OUTPUT_TOKENS` | `8192` | The output tokens requested per call. Set it to the model's own limit when that's lower. |
| `gatling.completion.model.max-concurrency` | `GATLING_COMPLETION_MODEL_MAX_CONCURRENCY` | `8` | How many snippets of one file are ported at once, from 1 to 64. At most `gatling.workflow.max-model-calls-per-tenant` run at once, so keep it at or under that. |

## Configure the conversions

These settings control what a conversion may do, and how long it may take:

| **Setting** | **Environment variable** | **Default** | **Description** |
| --- | --- | --- | --- |
| `gatling.conversion.completion-modes` | `GATLING_CONVERSION_COMPLETION_MODES` | `endpoint,caller` | The completion modes a conversion may ask for: `endpoint`, the model endpoint ports the snippets; `caller`, the user's own AI assistant does. Leaving `endpoint` out keeps every snippet away from the model endpoint. |
| `gatling.conversion.elicitation-modes` | `GATLING_CONVERSION_ELICITATION_MODES` | `ask,never` | Whether a conversion may ask the user questions through an MCP form (`ask`), about its partially ported snippets and its JDBC and TCP requests, or not (`never`). |
| `gatling.conversion.download` | `GATLING_CONVERSION_DOWNLOAD` | `zip` | What a conversion's download holds. `zip`: the Gatling project, with a `README.md` that says what the conversion did. `java`: the simulation's `.java` file alone; when a snippet wasn't converted or a file the simulation needs was left out, a zip named after the simulation's class, `<Class>.zip`, that holds the `.java` file and the `README.md`. |
| `gatling.conversion.download-ttl` | `GATLING_CONVERSION_DOWNLOAD_TTL` | `PT15M` | How long a conversion's one-time download URL is valid. |
| `gatling.conversion.max-active-time` | `GATLING_CONVERSION_MAX_ACTIVE_TIME` | `PT1H` | The most time one conversion may spend working. |
| `gatling.conversion.answer-timeout` | `GATLING_CONVERSION_ANSWER_TIMEOUT` | `PT30M` | How long a conversion waits for the user's or the AI assistant's answer. |
| `gatling.conversion.max-model-share` | `GATLING_CONVERSION_MAX_MODEL_SHARE` | `0.95` | The largest share of a file's executable lines the model may write. The snippets beyond it are left as gaps. |
| `gatling.conversion.repair-attempts` | `GATLING_CONVERSION_REPAIR_ATTEMPTS` | `3` | How many times the snippets that don't compile are sent back to the model. |
| `gatling.conversion.tokens-per-file` | `GATLING_CONVERSION_TOKENS_PER_FILE` | `1200000` | The model tokens one uploaded file may spend, before its snippets add theirs. |
| `gatling.conversion.tokens-per-snippet` | `GATLING_CONVERSION_TOKENS_PER_SNIPPET` | `280000` | The model tokens each snippet adds to its file's bound. |
| `gatling.guardian.strictness` | `GATLING_GUARDIAN_STRICTNESS` | `standard` | How strictly the guardian screens the plan and every ported body: `standard` or `strict`. |
| `gatling.converter.jmeter.default-script-language` | `GATLING_CONVERTER_JMETER_DEFAULT_SCRIPT_LANGUAGE` | `groovy` | The language of a JSR223 element that names none: `groovy`, `beanshell`, `javascript`, `jexl`, or `java`. |
| `gatling.converter.jmeter.max-entries` | `GATLING_CONVERTER_JMETER_MAX_ENTRIES` | `256` | The most entries, folders included, an uploaded zip may hold. |
| `gatling.validation.release` | `GATLING_VALIDATION_RELEASE` | `21` | The Java release the generated simulations are compiled for, from 21 to 25. |
| `gatling.validation.max-concurrent` | `GATLING_VALIDATION_MAX_CONCURRENT` | `4` | The most compilations at once. |

## Configure the MCP server and HTTP

These settings control the MCP endpoint, uploads, and browser access:

| **Setting** | **Environment variable** | **Default** | **Description** |
| --- | --- | --- | --- |
| `gatling.server.sync-timeout` | `GATLING_SERVER_SYNC_TIMEOUT` | `PT9M30S` | The longest one tool call waits for its conversion. The model's time limits inside a call are shares of it. Keep it under the MCP client's tool timeout, and keep your proxy's idle timeout at least 60 seconds above it. |
| `gatling.mcp.request-state-key` | `GATLING_MCP_REQUEST_STATE_KEY` | None | A secret of at least 32 random bytes, in base64, that signs the state of a stateless MCP client's questions. Without it, such a client is asked no question, and the server warns at startup. Generate one with `openssl rand -base64 32`. |
| `gatling.mcp.continue-on-retry` | `GATLING_MCP_CONTINUE_ON_RETRY` | `false` | Hands a conversion still running at the end of the wait back to a stateless client, which follows it by retrying, instead of cancelling it. Needs the request state key. |
| `gatling.mcp.elicitation-timeout` | `GATLING_MCP_ELICITATION_TIMEOUT` | `PT5M` | How long an MCP form waits for the user's answer. |
| `gatling.tools.disabled` | `GATLING_TOOLS_DISABLED` | None | Tools to hide and refuse, by name, separated by commas. |
| `gatling.upload.max-bytes` | `GATLING_UPLOAD_MAX_BYTES` | `10485760` (10 MiB) | The largest upload. The HTTP body limit and each converter's largest input follow it. |
| `gatling.upload.ttl` | `GATLING_UPLOAD_TTL` | `PT15M` | How long an upload URL accepts its upload. |
| `quarkus.http.cors.origins` | `QUARKUS_HTTP_CORS_ORIGINS` | None | The browser origins, besides the server's own, allowed to call `/mcp`: a web MCP client's. |

## Configure the limits

These settings bound the work the server takes on, and the memory it uses:

| **Setting** | **Environment variable** | **Default** | **Description** |
| --- | --- | --- | --- |
| `gatling.workflow.max-running-runs` | `GATLING_WORKFLOW_MAX_RUNNING_RUNS` | `8` | Conversions running at once. |
| `gatling.workflow.max-queued-runs` | `GATLING_WORKFLOW_MAX_QUEUED_RUNS` | `64` | Conversions waiting to run. Beyond it, a conversion is refused, to retry. |
| `gatling.workflow.max-model-calls-per-tenant` | `GATLING_WORKFLOW_MAX_MODEL_CALLS_PER_TENANT` | `8` | Model calls in flight at once. |
| `gatling.workflow.model-call-wait` | `GATLING_WORKFLOW_MODEL_CALL_WAIT` | `PT2M` | How long a model call waits for its turn before it fails, to retry. |
| `gatling.workflow.model-token-quota` | `GATLING_WORKFLOW_MODEL_TOKEN_QUOTA` | `20000000` | The model tokens, prompt and answer, the server may spend per quota window. Every user shares it. |
| `gatling.workflow.model-token-quota-period` | `GATLING_WORKFLOW_MODEL_TOKEN_QUOTA_PERIOD` | `P1D` | The length of the quota window. `P1D` starts a new one at midnight UTC. |
| `gatling.store.inmemory.rate-limit-capacity` | `GATLING_STORE_INMEMORY_RATE_LIMIT_CAPACITY` | `60` | Tool calls in a burst. |
| `gatling.store.inmemory.rate-limit-period` | `GATLING_STORE_INMEMORY_RATE_LIMIT_PERIOD` | `PT1M` | How long the burst takes to refill. |
| `gatling.store.inmemory.max-total-bytes` | `GATLING_STORE_INMEMORY_MAX_TOTAL_BYTES` | `536870912` (512 MiB) | The memory the uploaded and generated files may take. Size it against the container's memory, of which the JVM takes 75%. |
| `gatling.store.inmemory.file-ttl` | `GATLING_STORE_INMEMORY_FILE_TTL` | `P1D` | How long an uploaded or generated file is kept. |
| `gatling.store.inmemory.task-retention` | `GATLING_STORE_INMEMORY_TASK_RETENTION` | `P1D` | How long an ended conversion stays readable. |

## Keep traces of the conversions

Traces are off by default. Turned on, the server writes one folder per conversion: the uploaded plan, the report, the
generated code, and the model's prompts and answers only if `gatling.trace.model-io` is `true`. The server writes them
in clear.

| **Setting** | **Environment variable** | **Default** | **Description** |
| --- | --- | --- | --- |
| `gatling.trace.enabled` | `GATLING_TRACE_ENABLED` | `false` | Writes a folder per conversion. |
| `gatling.sensitive-sinks.acknowledged` | `GATLING_SENSITIVE_SINKS_ACKNOWLEDGED` | `false` | Must be `true` for the server to start with traces on: your acknowledgement that plans and code are written in clear. |
| `gatling.trace.dir` | `GATLING_TRACE_DIR` | None | The absolute folder the traces go in, writable by the server's user. `/deployments/data/traces` keeps them on the data volume. |
| `gatling.trace.retention` | `GATLING_TRACE_RETENTION` | None: kept forever | How long a conversion's folder is kept. |
| `gatling.trace.model-io` | `GATLING_TRACE_MODEL_IO` | `false` | Also keeps the model's prompts and answers. |

## Configure telemetry and logs

The server exports no metrics. These settings turn on OpenTelemetry traces and JSON logs:

| **Setting** | **Environment variable** | **Default** | **Description** |
| --- | --- | --- | --- |
| `quarkus.otel.sdk.disabled` | `QUARKUS_OTEL_SDK_DISABLED` | `true` | `false` exports one OpenTelemetry trace per conversion, over OTLP. |
| `quarkus.otel.exporter.otlp.endpoint` | `QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4317/` | The OTLP collector the traces go to. |
| `quarkus.otel.exporter.otlp.protocol` | `QUARKUS_OTEL_EXPORTER_OTLP_PROTOCOL` | `grpc` | `grpc` or `http/protobuf`. |
| `quarkus.otel.service.name` | `QUARKUS_OTEL_SERVICE_NAME` | `gatling-ai-server` | The service the traces are reported under. |
| `quarkus.log.console.json.enabled` | `QUARKUS_LOG_CONSOLE_JSON_ENABLED` | `false` | `true` writes one JSON object per log line, with the trace ID, the span ID, and the tenant as fields. |
| `quarkus.log.level` | `QUARKUS_LOG_LEVEL` | `INFO` | The level of the server's log. |
