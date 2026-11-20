---
title: Install Gatling AI Server
menutitle: Overview
seotitle: Install Gatling AI Server with Helm or Terraform
description: Meet the prerequisites of Gatling AI Server, gather its settings, and choose between the Helm chart and the Terraform modules to install it.
lead: Meet the prerequisites, gather the settings, then install the server with the Helm chart or a Terraform module.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server ships as the Docker image `gatlingcorp/gatling-ai-server`, for `linux/amd64` and `linux/arm64`.
Gatling publishes two ways to install it: a Helm chart for Kubernetes, and a Terraform module for each of AWS, Azure,
and Google Cloud. This page describes what every installation needs, whichever you choose.

## Meet the prerequisites

Before you begin, make sure you have the following:

- A Gatling Enterprise account for each user. Users sign in with it: there's nothing to register with Gatling and no
  Gatling Enterprise API token to create.
- An OpenAI-compatible model endpoint, the model's name at the provider, and an API key: Anthropic, OpenAI, Azure
  OpenAI, or a gateway of your own.
- Outbound HTTPS from the server to `auth.gatling.io`, Gatling's sign-in service, and to your model endpoint.
- An HTTPS URL for your users' MCP clients: a hostname and its TLS certificate, served by your ingress controller or
  load balancer. The Azure and Google Cloud modules serve the platform's own HTTPS address instead.

The [network requirements]({{< ref "/ai/server/network" >}}) list everything the server needs from the network.

## Gather the required settings

Gatling AI Server reads its settings from environment variables. Every installation sets the following ones:

| **Variable** | **Value** |
| --- | --- |
| `GATLING_SERVER_PUBLIC_BASE_URL` | The HTTPS URL your users' MCP clients reach, for example `https://gatling-ai.example.com`. It's the URL your ingress or load balancer serves, not the container's address: the server builds its upload and download URLs and its OAuth protected resource metadata from it. |
| `GATLING_MODEL_ENDPOINT_BASE_URL` | The root URL of your OpenAI-compatible model endpoint, for example `https://api.anthropic.com/v1/`. |
| `GATLING_MODEL_ENDPOINT_MODEL` | The model's name at the provider. |
| `GATLING_MODEL_ENDPOINT_API_KEY` | Your model endpoint's API key. A secret. |
| `GATLING_MCP_REQUEST_STATE_KEY` | The request state key, described below. A secret. |

The server refuses to start without the first four. The Helm chart and the Terraform modules read the two secrets
from a secret store: a Kubernetes Secret, AWS Secrets Manager, Azure Key Vault, or Google Cloud Secret Manager. The
Azure and Google Cloud modules set `GATLING_SERVER_PUBLIC_BASE_URL` themselves, to the platform's address.

The [configuration reference]({{< ref "/ai/server/configuration#find-where-each-setting-goes" >}}) says where each
setting goes with each installation, and lists every other setting.

## Generate the request state key

The request state key signs the state of the questions a conversion asks a stateless MCP client. It's optional, but
without it, such a client is asked no question, and the server logs a warning at startup. On Azure Container Apps, it
also lets a client follow a conversion that runs past 3 minutes.

Generate 32 random bytes in base64:

```console
openssl rand -base64 32
```

Store the output as a secret, next to the model endpoint's API key.
[Install with Helm]({{< ref "/ai/server/installation/helm" >}}) and
[Install with Terraform]({{< ref "/ai/server/installation/terraform" >}}) give the command for each secret store.

## Choose how to install

Each installation runs a single instance of the server:

| **Method** | **Runs on** | **URL** | **Kept across a restart** |
| --- | --- | --- | --- |
| [Helm chart]({{< ref "/ai/server/installation/helm" >}}) | Your Kubernetes cluster. | Your hostname, through your ingress controller or gateway. | The model token counts, the audit trail, and the conversion history, on a persistent volume claim. |
| [Terraform, AWS]({{< ref "/ai/server/installation/terraform#install-on-aws" >}}) | ECS Fargate, behind an Application Load Balancer. | Your hostname, with your ACM certificate. | Nothing. |
| [Terraform, Azure]({{< ref "/ai/server/installation/terraform#install-on-azure" >}}) | Azure Container Apps. | The container app's own HTTPS address. | Nothing. |
| [Terraform, Google Cloud]({{< ref "/ai/server/installation/terraform#install-on-google-cloud" >}}) | Cloud Run. | The service's `run.app` URL. | Nothing. |

Choose the Helm chart when you run Kubernetes, or when the token counts, the audit trail, and the conversion history
must survive a restart. Choose a Terraform module to run the server on a managed container service, with no cluster to
operate. On Azure Container Apps, a tool call waits at most 3 minutes: read
[its limits]({{< ref "/ai/server/installation/terraform#azure-limits" >}}) before you choose it.

On any other container platform, run the image `gatlingcorp/gatling-ai-server:<version>` with these environment
variables, one replica, port 8080 behind TLS with a read timeout of at least 630 seconds, and a writable
`/deployments/data`. Check its health on `/q/health/ready` only, as the
[network requirements]({{< ref "/ai/server/network" >}}) describe.

{{< alert warning >}}
Pin the image to a release version in production, and upgrade on purpose. The `latest` tag, the default of the chart
and of every module, points to the version Gatling published last.
{{< /alert >}}
