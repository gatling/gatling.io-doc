---
title: Install Gatling AI Server with Helm
menutitle: Install with Helm
seotitle: Install Gatling AI Server on Kubernetes with the Gatling Helm chart
description: Install Gatling AI Server on your Kubernetes cluster with the Gatling Helm chart, expose it over TLS, then check, upgrade, and uninstall it.
lead: Install Gatling AI Server on your Kubernetes cluster with the Gatling Helm chart, behind your ingress controller.
date: 2026-11-19T00:00:00+00:00
---

The Gatling Helm chart `gatling-ai-server` deploys the server to your Kubernetes cluster. This page installs it with
the values it requires and an Ingress. The chart's repository,
[gatling/helm-gatling-ai-server](https://github.com/gatling/helm-gatling-ai-server), describes every value.

## Meet the prerequisites

Before you begin, make sure you have the following:

- What every installation needs, as [Install Gatling AI Server]({{< ref "/ai/server/installation/overview" >}}) describes: the
  model endpoint and its API key, and a hostname for the server.
- A Kubernetes cluster. The chart's manifests are checked against the API schemas of Kubernetes 1.33 to 1.37.
- [kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl), and [Helm](https://helm.sh/docs/intro/) 3.8 or later, or
  Helm 4.
- An ingress controller or a Gateway API implementation that serves your hostname over TLS.

## Know what the chart creates

The chart creates the following resources in the release's namespace, each named after the release:

- A Deployment of one pod, never more, with the `Recreate` strategy: an upgrade stops the old pod before it starts the
  new one. The pod runs as user 1001 and group 0, with a read-only root filesystem, no Linux capability, no privilege
  escalation, and the `RuntimeDefault` seccomp profile, which the restricted Pod Security Standard admits.
- A ClusterIP Service on port 80, which forwards to the server's plain HTTP port 8080.
- A PersistentVolumeClaim of 1 GiB for the server's data, on the cluster's default StorageClass.
- A ServiceAccount with no API token: the server calls no Kubernetes API.
- An Ingress, when you turn it on.

## Add the Gatling Helm repository

Add the repository, and update your local copy of it:

```console
helm repo add gatling https://helm.gatling.io
helm repo update
```

## Create the Secret

The chart holds no secret value: it reads the model endpoint's API key and the
[request state key]({{< ref "/ai/server/installation/overview#generate-the-request-state-key" >}}) from a Secret you create in
the namespace you install the chart in:

```console
kubectl create namespace gatling
kubectl --namespace gatling create secret generic gatling-ai-server \
  --from-literal=model-api-key=<your model endpoint API key> \
  --from-literal=request-state-key="$(openssl rand -base64 32)"
```

The keys `model-api-key` and `request-state-key` are the chart's defaults. To name them otherwise, set
`secret.modelApiKeyKey` and `secret.requestStateKeyKey`.

## Write the values

Save the following file as `gatling-ai-server.yaml`, with your own values:

```yaml
server:
  # The https URL your users reach, served by your ingress
  publicBaseUrl: https://gatling-ai.example.com
  model:
    baseUrl: https://api.anthropic.com/v1/
    name: claude-haiku-5-5
# The Secret you created
secret:
  name: gatling-ai-server
image:
  # Pin a release version
  tag: <version>
```

The chart refuses to render without `server.publicBaseUrl`, `server.model.baseUrl`, `server.model.name`, and
`secret.name`. Set `server.publicBaseUrl` to the URL your ingress serves, not the Service's address.

{{< alert warning >}}
Pin `image.tag` to a release version in production, and upgrade on purpose. Its default, `latest`, points to the
version Gatling published last.
{{< /alert >}}

## Configure the ingress

Whatever serves `server.publicBaseUrl` must terminate TLS and, from the client to the server:

- Keep a request open for at least 630 seconds. One tool call waits up to 9 minutes 30 seconds for its conversion, and
  the response comes at the end: set every idle, read, and request timeout on the path to 630 seconds or more.
- Accept request bodies of at least 10 MiB, the largest upload.
- Not buffer responses.
- Forward every path, and leave the `Authorization` header untouched.

To serve it with the chart's Ingress, add the following to `gatling-ai-server.yaml`, here for Ingress NGINX:

```yaml
ingress:
  enabled: true
  className: nginx
  annotations:
    nginx.ingress.kubernetes.io/proxy-read-timeout: "630"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "630"
    nginx.ingress.kubernetes.io/proxy-body-size: 10m
    nginx.ingress.kubernetes.io/proxy-buffering: "off"
  hosts:
    - host: gatling-ai.example.com
      paths:
        - path: /
          pathType: Prefix
  tls:
    - secretName: gatling-ai-server-tls
      hosts:
        - gatling-ai.example.com
```

`ingress.className` is your controller's IngressClass: leave it empty to use the cluster's default. `ingress.tls`
names the Secret that holds your certificate. Each controller sets the timeout, the body size, and the buffering its
own way:

| **Controller** | **Annotations** |
| --- | --- |
| [AWS Load Balancer Controller](https://kubernetes-sigs.github.io/aws-load-balancer-controller/latest/guide/ingress/annotations/) | `alb.ingress.kubernetes.io/target-type: ip` (the chart's Service is a ClusterIP), `alb.ingress.kubernetes.io/load-balancer-attributes: idle_timeout.timeout_seconds=630`, `alb.ingress.kubernetes.io/healthcheck-path: /q/health/ready` |
| [Ingress NGINX](https://kubernetes.github.io/ingress-nginx/user-guide/nginx-configuration/annotations/) (retired in March 2026) | `nginx.ingress.kubernetes.io/proxy-read-timeout: "630"`, `nginx.ingress.kubernetes.io/proxy-send-timeout: "630"`, `nginx.ingress.kubernetes.io/proxy-body-size: 10m`, `nginx.ingress.kubernetes.io/proxy-buffering: "off"` |

Other controllers keep these settings outside the Ingress: Traefik in its entry points' configuration, GKE in a
BackendConfig or, behind a GKE Gateway, a GCPBackendPolicy, whose backend timeout is 30 seconds by default. Instead of
the chart's Ingress, you can route a Gateway API HTTPRoute or your own load balancer to the Service, port 80, with the
same settings.

{{< alert info >}}
On OpenShift, the default `restricted-v2` SCC refuses the pod's fixed user 1001 and group 0: grant the release's
ServiceAccount an SCC that allows them, such as `nonroot-v2`. The OpenShift router also cuts a request after 30
seconds by default: set `haproxy.router.openshift.io/timeout: 630s` on the Route that serves the server.
{{< /alert >}}

## Install the chart

Install the chart with your values:

```console
helm install gatling-ai-server gatling/gatling-ai-server --namespace gatling --values gatling-ai-server.yaml
```

To install a given version of the chart, add `--version <chart version>`. To see every value, run
`helm show values gatling/gatling-ai-server`.

## Check the server

Wait until the pod is ready:

```console
kubectl --namespace gatling rollout status deployment/gatling-ai-server
```

The server is ready once it takes requests: readiness doesn't wait for your model endpoint, so the first conversion is
the reliable test of your key. Check that the server answers through your ingress, and that the MCP endpoint asks for
authentication:

```console
curl -fsS https://gatling-ai.example.com/q/health/ready
curl -i https://gatling-ai.example.com/mcp
```

The MCP endpoint answers `401 Unauthorized`, with a `WWW-Authenticate` header whose `resource_metadata` parameter
points to the server's OAuth protected resource metadata. MCP clients read it to find Gatling's sign-in service. Its
URL starts with `server.publicBaseUrl`: if it doesn't, fix that value.

Before your ingress serves the hostname, you can reach the server through the Service:

```console
kubectl --namespace gatling port-forward service/gatling-ai-server 8080:80
curl -fsS http://127.0.0.1:8080/q/health/ready
```

To follow the server's log, run:

```console
kubectl --namespace gatling logs deployment/gatling-ai-server --follow
```

## Change a setting

Every other setting of the [configuration reference]({{< ref "/ai/server/configuration" >}}) goes in `env`, as
`name` and `value` entries, or in `envFrom`, from a whole ConfigMap or Secret. For example, in `gatling-ai-server.yaml`:

```yaml
env:
  - name: GATLING_CONVERSIONS_HISTORY_ENABLED
    value: "true"
```

Apply it with `helm upgrade`, as below. If you raise `GATLING_SERVER_SYNC_TIMEOUT`, keep your ingress timeout at
least 60 seconds above it.

The server reads the Secret at startup. After you change one of its keys, restart the server:

```console
kubectl --namespace gatling rollout restart deployment/gatling-ai-server
```

## Keep the server's data

The server's data folder, `/deployments/data`, is on the PersistentVolumeClaim: the model token counts and the audit
trail, in an SQLite file, and the conversion history when you turn it on. They stay across a restart and an upgrade.

Everything else resets at each restart: the conversions in progress, the uploads, and the download links are in
memory. An upgrade, a `kubectl rollout restart`, or a new pod loses them.

- To use a claim of your own, set `persistence.existingClaim`. Its volume must be writable by user 1001 or group 0, and
  not mounted `noexec`.
- To change the claim the chart creates, set `persistence.storageClass` and `persistence.size`.
- With `persistence.enabled: false`, the data folder is an emptyDir, emptied at each restart.

## Upgrade the server

Set `image.tag` to the new version in `gatling-ai-server.yaml`, then run:

```console
helm repo update
helm upgrade gatling-ai-server gatling/gatling-ai-server --namespace gatling --values gatling-ai-server.yaml \
  --wait --timeout 10m
```

The upgrade stops the old pod, with its conversions in progress, then gives the new one up to 5 minutes to become
ready. Upgrade when nobody converts.

## Uninstall the server

Remove the release, then the Secret:

```console
helm uninstall gatling-ai-server --namespace gatling
kubectl --namespace gatling delete secret gatling-ai-server
```

`helm uninstall` deletes the PersistentVolumeClaim the chart created, with the token counts, the audit trail, and the
conversion history. A claim you set in `persistence.existingClaim` is kept.

## Next steps

- [Review the configuration]({{< ref "/ai/server/configuration" >}}).
- [Connect your AI assistant]({{< ref "/ai/server/connect" >}}).
