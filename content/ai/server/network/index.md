---
title: Network requirements for Gatling AI Server
menutitle: Network
seotitle: Gatling AI Server network requirements, health check, timeouts, and TLS
description: Check the network requirements of Gatling AI Server before you install it, from the reverse proxy timeout to the outbound connections.
lead: Plan the instance, the reverse proxy, and the outbound connections Gatling AI Server needs before you install it.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server is one container that listens on port 8080 over plain HTTP. Your users' AI assistants reach it
through a reverse proxy or a load balancer that terminates TLS, and it calls two services over HTTPS.

## Meet the requirements

Make sure your infrastructure meets the following requirements:

| **Requirement** | **Why** |
| --- | --- |
| Run a single instance. | The server keeps conversions, uploads, and download links in memory. A second instance would share none of them, so a request routed to it would fail. |
| Check health on `/q/health/ready` only. | The readiness check says when the server takes requests. The startup check (`/q/health/started`) probes the model endpoint, which some providers, Anthropic among them, keep down while conversions run: an orchestrator watching them would restart a working server. |
| Set the idle (read) timeout of your load balancer or reverse proxy to at least 630 seconds. | One tool call waits up to 9 minutes 30 seconds for its conversion (`GATLING_SERVER_SYNC_TIMEOUT`), and the response comes at the end. A shorter timeout cuts long conversions. If you raise `GATLING_SERVER_SYNC_TIMEOUT`, keep the proxy timeout at least 60 seconds above it. |
| Allow outbound HTTPS to `auth.gatling.io`. | The server reads the discovery document and the public keys (JWKS) of Gatling's sign-in service to validate each request's token. |
| Allow outbound HTTPS to your model endpoint. | The server sends the code snippets to port to it, for example `api.anthropic.com`. |
| Terminate TLS in front of the server. | The server serves plain HTTP. Put a reverse proxy or a load balancer in front of it, with a certificate for the hostname your users reach. |
| Set `GATLING_SERVER_PUBLIC_BASE_URL` to the URL users reach. | The server builds the upload and download URLs and its OAuth protected resource metadata from it. It must be the HTTPS URL of your proxy, not the container's address. |

## Configure the reverse proxy

Configure the reverse proxy or load balancer in front of the server as follows:

- Forward every path to port 8080 of the container: the MCP endpoint `/mcp`, the uploads under `/uploads`, the
  downloads under `/downloads`, and the OAuth protected resource metadata the server publishes for MCP clients, under
  `/.well-known/oauth-protected-resource`.
- Leave the `Authorization` header untouched: MCP requests carry the user's token in it, and uploads and downloads a
  one-time token.
- Accept request bodies up to the largest upload, 10 MiB by default (`GATLING_UPLOAD_MAX_BYTES`).

## Understand who connects to what

Your users' browsers open Gatling's sign-in page on `auth.gatling.io` when their AI assistant connects for the first
time. Their AI assistants then call the server through your proxy. The server never calls Gatling Enterprise and
needs no inbound connection from Gatling.

## Next steps

- [Install the server with Docker Compose]({{< ref "/ai/server/installation" >}}).
