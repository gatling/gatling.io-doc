---
title: Network requirements for Gatling AI Server
menutitle: Network
seotitle: Gatling AI Server network requirements, health check, timeouts, and TLS
description: Check the network requirements of Gatling AI Server before you install it, from the load balancer timeout to the outbound connections.
lead: Plan the instance, what serves it, and the outbound connections Gatling AI Server needs before you install it.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server is one container that listens on port 8080 over plain HTTP. Your users' AI assistants reach it
through an ingress controller or a load balancer that terminates TLS, and it calls two services over HTTPS.

## Meet the requirements

Make sure your infrastructure meets the following requirements:

| **Requirement** | **Why** |
| --- | --- |
| Run a single instance. | The server keeps conversions, uploads, and download links in memory. A second instance would share none of them, so a request routed to it would fail. |
| Check health on `/q/health/ready` only. | The readiness check says when the server takes requests. The startup check (`/q/health/started`) probes the model endpoint, which some providers, Anthropic among them, keep down while conversions run: an orchestrator watching them would restart a working server. |
| Set the idle (read) timeout of your load balancer or ingress controller to at least 630 seconds. | One tool call waits up to 9 minutes 30 seconds for its conversion (`GATLING_SERVER_SYNC_TIMEOUT`), and the response comes at the end. A shorter timeout cuts long conversions. If you raise `GATLING_SERVER_SYNC_TIMEOUT`, keep that timeout at least 60 seconds above it. Azure Container Apps is the exception, as [the Azure module](#with-a-terraform-module) describes. |
| Allow outbound HTTPS to `auth.gatling.io`. | The server reads the discovery document and the public keys (JWKS) of Gatling's sign-in service to validate each request's token. |
| Allow outbound HTTPS to your model endpoint. | The server sends the code snippets to port to it, for example `api.anthropic.com`. |
| Terminate TLS in front of the server. | The server serves plain HTTP. Put an ingress controller or a load balancer in front of it, with a certificate for the hostname your users reach. |
| Set `GATLING_SERVER_PUBLIC_BASE_URL` to the URL users reach. | The server builds the upload and download URLs and its OAuth protected resource metadata from it. It must be the HTTPS URL your ingress or load balancer serves, not the container's address. |

## Configure what serves the server

Whatever serves the server's URL, an ingress controller, a gateway, or a cloud load balancer, must, from the client to
the server:

- Keep a request open for at least 630 seconds: every idle, read, and request timeout on the path.
- Accept request bodies up to the largest upload, 10 MiB by default (`GATLING_UPLOAD_MAX_BYTES`).
- Not buffer responses, so that the server's answers reach the client as it sends them.
- Forward every path to port 8080 of the container: the MCP endpoint `/mcp`, the uploads under `/uploads`, the
  downloads under `/downloads`, and the OAuth protected resource metadata the server publishes for MCP clients, under
  `/.well-known/oauth-protected-resource`.
- Leave the `Authorization` header untouched: MCP requests carry the user's token in it, and uploads and downloads a
  one-time token.

### With the Helm chart

The chart creates the server's Service, and an Ingress when you turn it on, but sets no timeout, body size, or
buffering: your ingress controller does. Set them in `ingress.annotations`, as
[Install with Helm]({{< ref "/ai/server/installation/helm#configure-the-ingress" >}}) describes. A Gateway API HTTPRoute
or a load balancer of your own, routed to the chart's Service, needs the same settings.

### With a Terraform module

The AWS and Google Cloud modules configure the platform in front of the server for you:

- **AWS**: the Application Load Balancer terminates TLS with your ACM certificate, forwards every path to the task's
  port 8080, and closes a connection only after 630 seconds of silence.
- **Google Cloud**: Cloud Run serves the `run.app` URL over HTTPS, and ends a request only after 630 seconds. It
  accepts HTTP/1 requests of up to 32 MiB.

Azure is the exception. Azure Container Apps ends every HTTP request at 240 seconds, so the Azure module makes a tool
call wait 3 minutes 30 seconds (`GATLING_SERVER_SYNC_TIMEOUT=PT3M30S`), then hands a conversion still running back to
a client on a stateless MCP session, which follows it by retrying (`GATLING_MCP_CONTINUE_ON_RETRY=true`). For a client
that can't retry, a conversion that runs past 3 minutes 30 seconds is canceled. For longer conversions, use the Helm
chart on AKS. [The Azure limits]({{< ref "/ai/server/installation/terraform#azure-limits" >}}) say more.

## Understand who connects to what

Your users' browsers open Gatling's sign-in page on `auth.gatling.io` when their AI assistant connects for the first
time. Their AI assistants then call the server through your ingress or load balancer. The server never calls Gatling
Enterprise and needs no inbound connection from Gatling.

## Next steps

- [Install the server]({{< ref "/ai/server/installation/overview" >}}).
