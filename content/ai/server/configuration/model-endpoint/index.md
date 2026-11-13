---
title: Model endpoint and token budget settings for Gatling AI Server
menutitle: Model endpoint and tokens
seotitle: Gatling AI Server settings for the model endpoint, the model calls, and the token budgets
description: Tune how Gatling AI Server calls your model endpoint, how many model calls run at once, and how many model tokens a file and the whole server may spend.
lead: Tune how Gatling AI Server calls your model endpoint, and bound the model tokens the conversions spend.
date: 2026-11-23T00:00:00+00:00
---

The [model]({{< ref "/ai/server/introduction" >}}) ports the code snippets of a plan to Java, and the server calls it
through your model endpoint. It does so under endpoint completion, the default. Completion is the part of a
conversion that ports the snippets. Under endpoint completion, your model endpoint does it. Under caller completion,
the user's AI assistant does it with its own model, and the server doesn't call your model endpoint.

Each setting on this page is an environment variable. The
[configuration reference]({{< ref "/ai/server/configuration/overview#find-where-each-setting-goes" >}}) says where it goes
with your installation.

## Configure the model endpoint

The server refuses to start without your model endpoint's URL, its model name, and its API key: the
[required settings]({{< ref "/ai/server/configuration/overview#set-the-required-settings" >}}) describe them. The following
settings tune the requests to the model endpoint:

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_MODEL_ENDPOINT_ALLOW_PLAIN_HTTP` | `false` | Lets the base URL use plain HTTP to a host other than the loopback. The API key and every prompt then cross the network in clear. |
| `GATLING_MODEL_ENDPOINT_OUTPUT_LIMIT_PARAMETER` | <code>max_<wbr>completion_<wbr>tokens</code> | The request member that carries the output-token cap: the default, for OpenAI, or `max_tokens`, for Ollama and older providers. |
| `GATLING_MODEL_ENDPOINT_MAX_RETRIES` | `2` | How many times a failed request is retried, from 0 to 10. A request fails on an HTTP 408, 429, or 5xx answer, a timeout, or a connection failure. |
| `GATLING_MODEL_ENDPOINT_RETRY_BACKOFF` | `PT1S` | The longest wait before the first retry. It doubles on each retry, up to 60 seconds. |

## Tune each model request

The following settings shape each request that ports a snippet:

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_COMPLETION_MODEL_MODEL` | The endpoint's model name | The model named in each request. By default, the model name of the required settings. |
| `GATLING_COMPLETION_MODEL_TEMPERATURE` | None: the provider's default applies | The sampling temperature, from 0 to 2. Some models, OpenAI's reasoning models among them, refuse any value but their default. |
| `GATLING_COMPLETION_MODEL_MAX_OUTPUT_TOKENS` | `8192` | The output tokens requested per call. Set it to the model's own limit when that's lower. |
| `GATLING_COMPLETION_MODEL_MAX_CONCURRENCY` | `8` | How many snippets of one file are ported at once, from 1 to 64. No more model calls run at once than the [model calls in flight](#bound-the-model-calls) allow, so keep it at or under that setting. |

## Bound the model calls

A tenant is the group of users a server serves. Gatling AI Server serves one, every user who signs in, so a setting
"per tenant" applies to the whole server. The following settings bound the model calls in flight:

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_WORKFLOW_MAX_MODEL_CALLS_PER_TENANT` | `8` | The model calls in flight at once, for the whole server. |
| `GATLING_WORKFLOW_MODEL_CALL_WAIT` | `PT2M` | How long a model call waits for its turn before it fails. The conversion can retry it. |

## Set the token budgets

A model counts its input and its output in tokens, and providers bill by the token. The server bounds the tokens the
conversions spend in two ways: a quota for the whole server per window of time, and a bound per uploaded file.

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_WORKFLOW_MODEL_TOKEN_QUOTA` | `20000000` | The model tokens, prompt and answer, the server may spend per quota window. Every user shares it. Once it's spent, model calls fail until the next window. The quota must be at least 1: there's no unlimited value. To lift the cap, set a high number. The count starts over with each new pod, task, revision or instance. |
| `GATLING_WORKFLOW_MODEL_TOKEN_QUOTA_PERIOD` | `P1D` | The length of the quota window, in whole seconds, at least one. Windows are fixed: `P1D` starts a new one at midnight UTC. |
| `GATLING_CONVERSION_TOKENS_PER_FILE` | `1200000` | The model tokens one uploaded file may spend, before its snippets add theirs. |
| `GATLING_CONVERSION_TOKENS_PER_SNIPPET` | `280000` | The model tokens each snippet adds to its file's bound. |

A file's bound is `GATLING_CONVERSION_TOKENS_PER_FILE`, plus `GATLING_CONVERSION_TOKENS_PER_SNIPPET` for each snippet
the converter couldn't translate. With the defaults, a file of five snippets may spend 2,600,000 tokens. Past its
bound, a file's remaining snippets aren't ported: they keep their stubs.

## Limit the model's share of a file

The following setting keeps the model from writing nearly all of a file:

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_CONVERSION_MAX_MODEL_SHARE` | `0.95` | The largest share of a file's executable lines the model may write, between 0 and 1. The snippets beyond it aren't ported: they keep their stubs. |
