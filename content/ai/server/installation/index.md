---
title: Install Gatling AI Server with Docker Compose
menutitle: Installation
seotitle: Install Gatling AI Server with Docker Compose
description: Install Gatling AI Server on your own infrastructure with Docker Compose, then check it answers.
lead: Start Gatling AI Server with one Docker Compose file and a few settings.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server ships as the Docker image `gatlingcorp/gatling-ai-server`, for `linux/amd64` and `linux/arm64`. This
page runs it with Docker Compose, from the files of the
[gatling-ai-server-demo](https://github.com/gatling/gatling-ai-server-demo) repository, which also holds an example
JMeter plan to convert.

## Meet the prerequisites

Before you begin, make sure you have the following:

- Docker Engine 25 or later, with Compose v2.
- A Gatling Enterprise account for each user. Users sign in with it: there's nothing to register with Gatling and no
  Gatling Enterprise API token to create.
- An API key for an OpenAI-compatible model endpoint: Anthropic, OpenAI, Azure OpenAI, or a gateway of your own.
- A hostname for the server and a TLS reverse proxy or load balancer that serves it, as the
  [network requirements]({{< ref "/ai/server/network" >}}) describe.

## Create the Compose file

Create a folder for the server, and save the following file in it as `compose.yaml`:

```yaml
# Gatling AI Server with Docker Compose: one server for your teams, which converts JMeter plans to Gatling
# simulations for the MCP clients your users already work in (Claude Code).
#
#   cp .env.example .env     # set the server's URL and your model endpoint
#   docker compose up -d --wait
#   docker compose logs -f gatling-ai-server
#   docker compose down      # stop; add -v to also forget the token counts
#
# Users sign in with their Gatling Enterprise account: the server trusts Gatling's sign-in service
# (https://auth.gatling.io/auth, realm gatling) by default, so you set no identity setting.
# https://docs.gatling.io/ai/server/installation/ explains each step.

name: gatling-ai-server

services:
  gatling-ai-server:
    image: gatlingcorp/gatling-ai-server:${GATLING_AI_SERVER_VERSION:-latest}
    # Each setting goes to the server under its own name. The server refuses to start without its public URL and the
    # model endpoint; this file also requires the request state key.
    environment:
      # The URL your users' MCP clients reach the server at. Behind a TLS reverse proxy, its https URL.
      GATLING_SERVER_PUBLIC_BASE_URL: ${GATLING_SERVER_PUBLIC_BASE_URL:?set GATLING_SERVER_PUBLIC_BASE_URL in .env}
      # Any OpenAI-compatible endpoint: Anthropic, OpenAI, Azure OpenAI, a gateway of your own.
      GATLING_MODEL_ENDPOINT_BASE_URL: ${GATLING_MODEL_ENDPOINT_BASE_URL:?set GATLING_MODEL_ENDPOINT_BASE_URL in .env}
      GATLING_MODEL_ENDPOINT_MODEL: ${GATLING_MODEL_ENDPOINT_MODEL:?set GATLING_MODEL_ENDPOINT_MODEL in .env}
      GATLING_MODEL_ENDPOINT_API_KEY: ${GATLING_MODEL_ENDPOINT_API_KEY:?set GATLING_MODEL_ENDPOINT_API_KEY in .env}
      # Signs the state of the questions a conversion asks a stateless MCP client: 32 random bytes in base64.
      GATLING_MCP_REQUEST_STATE_KEY: ${GATLING_MCP_REQUEST_STATE_KEY:?set GATLING_MCP_REQUEST_STATE_KEY in .env}
      # Gatling's sign-in service. Leave it at its default.
      GATLING_IDENTITY_AUTH_SERVER_URL: ${GATLING_IDENTITY_AUTH_SERVER_URL:-https://auth.gatling.io/auth}
      # One JSON object per log line, for a log collector.
      QUARKUS_LOG_CONSOLE_JSON_ENABLED: ${QUARKUS_LOG_CONSOLE_JSON_ENABLED:-false}
    # Further settings (timeouts for a slow model, upload size, limits), when the file exists: see
    # https://docs.gatling.io/ai/server/configuration/.
    env_file:
      - path: ./server.env
        required: false
    # Published on every interface, so that one server serves your teams. Put a TLS reverse proxy in front of it, or
    # set GATLING_BIND_ADDRESS=127.0.0.1 to try it on your own machine only.
    ports:
      - ${GATLING_BIND_ADDRESS:-0.0.0.0}:${GATLING_HOST_PORT:-8080}:8080
    # The server writes only its data volume and a small /tmp: the rest of its filesystem is read-only, and it runs
    # with no Linux capability.
    read_only: true
    volumes:
      - gatling-data:/deployments/data
      - type: tmpfs
        target: /tmp
        tmpfs:
          size: 64m
    security_opt:
      - no-new-privileges:true
    cap_drop:
      - ALL
    # Healthy once the server is ready to take requests. Readiness only: the startup check probes the model endpoint,
    # and some providers, Anthropic among them, keep it down while conversions work.
    healthcheck:
      test:
        - CMD-SHELL
        - curl -fsS -o /dev/null -m 4 http://127.0.0.1:8080/q/health/ready
      interval: 10s
      timeout: 5s
      retries: 3
      start_period: 60s
      start_interval: 2s
    deploy:
      # One replica: the server keeps its work in memory, so a second one would share none of it.
      replicas: 1
      resources:
        limits:
          cpus: "2"
          memory: ${GATLING_SERVER_MEMORY:-2g}
    restart: unless-stopped
    stop_grace_period: 30s
    logging:
      driver: json-file
      options:
        max-size: 10m
        max-file: "3"

volumes:
  # The model token counts and the audit trail, kept across a restart. Conversions and uploads are in memory.
  gatling-data:
```

The named volume `gatling-data` on `/deployments/data` keeps the model token counts and the audit trail across a
restart. Without it, they go with the container.

## Set the settings

Save the following file next to `compose.yaml` as `.env.example`, copy it to `.env`, and fill in `.env`:

```properties
# Copy to .env and fill in. docker compose reads .env from this folder.

# The image's version on Docker Hub (gatlingcorp/gatling-ai-server). latest is the version Gatling published last: pin a
# release version in production, and upgrade on purpose.
GATLING_AI_SERVER_VERSION=latest

# The URL your users' MCP clients reach the server at: your reverse proxy's https URL for a shared server,
# http://localhost:8080 to try it on your own machine.
GATLING_SERVER_PUBLIC_BASE_URL=https://gatling-ai.example.internal

# Where the server listens on this host. 0.0.0.0 serves your network; 127.0.0.1 only this machine.
#GATLING_BIND_ADDRESS=0.0.0.0
#GATLING_HOST_PORT=8080

# The model that ports the code snippets of your plans (JSR223, BeanShell...). Any OpenAI-compatible endpoint works:
# Anthropic as below, OpenAI (https://api.openai.com/v1/ and an OpenAI model), Azure OpenAI, or your own gateway.
GATLING_MODEL_ENDPOINT_BASE_URL=https://api.anthropic.com/v1/
GATLING_MODEL_ENDPOINT_MODEL=claude-haiku-5-5
GATLING_MODEL_ENDPOINT_API_KEY=

# Required: 32 random bytes in base64, for example the output of: openssl rand -base64 32
GATLING_MCP_REQUEST_STATE_KEY=

# The server's memory limit; the JVM's heap takes 75% of it.
#GATLING_SERVER_MEMORY=2g
```

Set at least the following values:

| **Variable** | **Value** |
| --- | --- |
| `GATLING_AI_SERVER_VERSION` | The image version to run, `latest` by default. |
| `GATLING_SERVER_PUBLIC_BASE_URL` | The HTTPS URL your users reach, served by your reverse proxy, for example `https://gatling-ai.example.internal`. |
| `GATLING_MODEL_ENDPOINT_BASE_URL`, `GATLING_MODEL_ENDPOINT_MODEL`, `GATLING_MODEL_ENDPOINT_API_KEY` | Your model endpoint, the model's name at the provider, and your API key. |
| `GATLING_MCP_REQUEST_STATE_KEY` | The output of `openssl rand -base64 32`. |

{{< alert warning >}}
Pin `GATLING_AI_SERVER_VERSION` to a release version in production, and upgrade on purpose. The `latest` tag points to
the version Gatling published last.
{{< /alert >}}

Every other setting goes in an optional `server.env` file next to `compose.yaml`, one `NAME=value` per line, which
Compose passes to the server when it exists. The [configuration reference]({{< ref "/ai/server/configuration" >}})
lists them.

## Start the server

Start the server and wait until it's healthy:

```console
docker compose up -d --wait
```

The server is healthy once it's ready to take requests: the healthcheck doesn't wait for your model endpoint. The
startup check, `/q/health/started`, reports whether the endpoint accepts your key, but some providers, Anthropic among
them, keep it down while conversions run. The first conversion is the reliable test.

Then put your TLS reverse proxy in front of port 8080, serving the URL you set in `GATLING_SERVER_PUBLIC_BASE_URL`,
as the [network requirements]({{< ref "/ai/server/network" >}}) describe.

{{< alert tip >}}
To try the server on your own machine without a reverse proxy, set `GATLING_SERVER_PUBLIC_BASE_URL=http://localhost:8080`
and `GATLING_BIND_ADDRESS=127.0.0.1` in `.env`.
{{< /alert >}}

## Check the server

Check that the server is ready:

```console
curl -fsS https://gatling-ai.example.internal/q/health/ready
```

Then check that the MCP endpoint asks for authentication:

```console
curl -i https://gatling-ai.example.internal/mcp
```

The server answers `401 Unauthorized`, with a `WWW-Authenticate` header whose `resource_metadata` parameter points to
the server's OAuth protected resource metadata. MCP clients read it to find Gatling's sign-in service. Its URL starts
with `GATLING_SERVER_PUBLIC_BASE_URL`: if it doesn't, fix that setting.

To follow the server's log, run:

```console
docker compose logs -f gatling-ai-server
```

## Stop the server

Stop the server with:

```console
docker compose down
```

Add `-v` to also remove the data volume, with the token counts and the audit trail.

## Next steps

- [Review the configuration]({{< ref "/ai/server/configuration" >}}).
- [Connect your AI assistant]({{< ref "/ai/server/connect" >}}).
