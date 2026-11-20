---
title: Install Gatling AI Server with Terraform
menutitle: Install with Terraform
seotitle: Install Gatling AI Server on AWS, Azure, or Google Cloud with Terraform
description: Install Gatling AI Server on AWS ECS Fargate, Azure Container Apps, or Google Cloud Run with the Gatling Terraform modules, then check, upgrade, and uninstall it.
lead: Install Gatling AI Server on AWS, Azure, or Google Cloud with a Gatling Terraform module.
date: 2026-11-19T00:00:00+00:00
---

Gatling publishes one Terraform module per cloud: `gatling/ai-server/aws`, `gatling/ai-server/azure`, and
`gatling/ai-server/gcp`. Each module runs one instance of the server on a managed container service, reads its secrets
from the cloud's secret store, and passes any other setting as an environment variable.

{{< alert warning >}}
The modules mount no persistent storage: the server's data resets at each restart. A restart loses the conversions in
progress, the uploads, the download links, the model token counts of the current quota window, the audit trail, and
the conversion history. To keep the token counts, the audit trail, and the history, use the
[Helm chart]({{< ref "/ai/server/installation/helm" >}}).
{{< /alert >}}

## Meet the prerequisites

Before you begin, make sure you have the following:

- What every installation needs, as [Install Gatling AI Server]({{< ref "/ai/server/installation/overview" >}}) describes.
- Terraform 1.5 or later.
- The command-line tool of your cloud, signed in: the AWS CLI, the Azure CLI, or the Google Cloud CLI.

## Install on AWS

The `gatling/ai-server/aws` module runs the server on ECS Fargate, behind an HTTPS Application Load Balancer. Its
repository, [gatling/terraform-aws-ai-server](https://github.com/gatling/terraform-aws-ai-server), describes every
input.

### Know what the module creates

The module creates the following resources, each named after the `name` input, `gatling-ai-server` by default:

- An ECS cluster, and a Fargate service that runs one task, never two at once.
- An internet-facing Application Load Balancer, which serves HTTPS on port 443 with your ACM certificate, keeps a
  connection open for 630 seconds of silence, and checks the server's health on `/q/health/ready`.
- The security groups of the load balancer, open on port 443 to `load-balancer.ingress-cidrs`, `0.0.0.0/0` by
  default, and of the task, open on port 8080 to the load balancer only.
- The task's IAM roles. The execution role can read the secrets you name, and no other secret.
- A CloudWatch log group, `/ecs/<name>-service`, which keeps the server's log 30 days by default.

### Gather what you supply

The module creates no network, certificate, or DNS record. Before you apply it, make sure you have the following:

- A VPC with at least two public subnets in different Availability Zones for the load balancer, each a `/27` or larger
  with at least eight free IP addresses.
- Subnets for the task, in Availability Zones of the load balancer's subnets: public subnets, where the task gets a
  public IP address, the default, or private subnets with a NAT gateway (`task.assign-public-ip = false`). The task
  connects out over HTTPS to Docker Hub, which serves its image, to `auth.gatling.io`, and to your model endpoint.
- A hostname for the server, such as `gatling-ai.example.com`, in a DNS zone you manage, and an ACM certificate for it
  in the region you deploy to.

Then store the secrets in AWS Secrets Manager:

```console
aws secretsmanager create-secret --name gatling-ai-server/model-api-key --secret-string '<ModelAPIKey>'
aws secretsmanager create-secret --name gatling-ai-server/request-state-key --secret-string "$(openssl rand -base64 32)"
```

Each command prints the secret's ARN. Pass it whole, with the hyphen and the 6 random characters Secrets Manager adds
to the name: the module grants access to that exact ARN. A secret encrypted with a customer managed KMS key also needs
`kms:Decrypt` on that key for the role `<name>-execution-role` the module creates.

### Add the module

Add the module to your Terraform configuration:

```hcl
provider "aws" {
  region = "<Region>"
}

module "ai-server" {
  source  = "gatling/ai-server/aws"
  version = "~> 0.1"

  vpc-id = "<VpcId>"

  load-balancer = {
    subnets         = ["<PublicSubnetId1>", "<PublicSubnetId2>"]
    certificate-arn = "<CertificateARN>"
  }

  task = {
    subnets = ["<PublicSubnetId1>", "<PublicSubnetId2>"]
    image   = "gatlingcorp/gatling-ai-server:<version>"
  }

  server = {
    public-base-url = "https://gatling-ai.example.com"
    model = {
      base-url           = "https://api.anthropic.com/v1/"
      name               = "<ModelName>"
      api-key-secret-arn = "<ModelApiKeySecretARN>"
    }
    request-state-key-secret-arn = "<RequestStateKeySecretARN>"
  }
}
```

`server.public-base-url` is the `https://` URL of your hostname, with no path. Then apply it:

```console
terraform init
terraform apply
```

### Point your hostname at the load balancer

The module has three outputs:

| **Output** | **Value** |
| --- | --- |
| `load-balancer-dns-name` | The DNS name of the load balancer, which your hostname points to. |
| `load-balancer-zone-id` | The Route 53 hosted zone ID of the load balancer, for an alias record. |
| `url` | The server's public base URL, with no trailing slash. MCP clients connect to it followed by `/mcp`. |

With Route 53, add an alias record:

```hcl
resource "aws_route53_record" "ai-server" {
  zone_id = "<HostedZoneId>"
  name    = "gatling-ai.example.com"
  type    = "A"

  alias {
    name                   = module.ai-server.load-balancer-dns-name
    zone_id                = module.ai-server.load-balancer-zone-id
    evaluate_target_health = false
  }
}
```

With another DNS provider, create a `CNAME` record from your hostname to the `load-balancer-dns-name` output.

### Check the server on AWS

Check that the server is ready, and that the MCP endpoint asks for authentication:

```console
curl -fsS https://gatling-ai.example.com/q/health/ready
curl -i https://gatling-ai.example.com/mcp
```

The MCP endpoint answers `401 Unauthorized`, with a `WWW-Authenticate` header whose `resource_metadata` URL starts with
`server.public-base-url`: if it doesn't, fix that input. To follow the server's log, run:

```console
aws logs tail /ecs/gatling-ai-server-service --follow
```

### Know the limits {#aws-limits}

- The server's data resets at each restart: after an apply that changes the task, a Fargate maintenance, or a crash.
  ECS starts the new task once the old one has stopped, so the server is unavailable in between.
- The load balancer closes a connection after 630 seconds of silence: keep `GATLING_SERVER_SYNC_TIMEOUT` at or below
  its default, `PT9M30S`.
- `task.environment` is readable in the task definition and stored in the Terraform state: put no secret there.
- ECS reads the secrets when a task starts. After you change a secret's value, start a new task:

  ```console
  aws ecs update-service --cluster <name>-cluster --service <name>-service --force-new-deployment
  ```

## Install on Azure

The `gatling/ai-server/azure` module runs the server on Azure Container Apps, on the container app's own HTTPS
address. Its repository,
[gatling/terraform-azure-ai-server](https://github.com/gatling/terraform-azure-ai-server), describes every input.

### Know what the module creates

The module creates the following resources, each named after the `name` input, `gatling-ai-server` by default:

- A Container Apps environment, `<name>-env`, with no Log Analytics workspace: the server's log is streamed only.
- A container app, `<name>`, with one replica in single revision mode, and an external ingress to port 8080 on its own
  HTTPS address. The module sets the server's public URL to that address.
- A user-assigned identity, `<name>-identity`, that reads the secrets from your Key Vault. With
  `key-vault-authorization = "rbac"`, the default, the module grants it the role Key Vault Secrets User on the vault.
  With `"access-policy"`, it adds an access policy that lets it get secrets.

### Gather what you supply

The module creates no resource group, Key Vault, or secret. Before you apply it, make sure you have the following:

- An existing resource group.
- An existing Key Vault, and the right to write its secrets and grant access to it. When the vault uses Azure RBAC, the
  default of new vaults: the role Key Vault Secrets Officer on it, which Owner doesn't include, and the right to create
  role assignments on it, such as Owner, User Access Administrator, or Role Based Access Control Administrator. When it
  uses access policies: the secret Set permission, and the right to change its access policies.

Then store the secrets in your Key Vault:

```console
az keyvault secret set --vault-name <Vault> --name gatling-ai-model-api-key --value "<ApiKey>"
az keyvault secret set --vault-name <Vault> --name gatling-ai-request-state-key --value "$(openssl rand -base64 32)"
```

`key-vault-authorization` must match the vault's permission model:
`az keyvault show --name <Vault> --query properties.enableRbacAuthorization` prints `true` for `rbac`.

### Add the module

Add the module to your Terraform configuration:

```hcl
provider "azurerm" {
  features {}
  subscription_id = "<SubscriptionId>"
}

module "ai-server" {
  source  = "gatling/ai-server/azure"
  version = "~> 0.1"

  resource-group-name = "<ResourceGroup>"
  location            = "<Region>"
  key-vault-id        = "/subscriptions/<SubscriptionId>/resourceGroups/<ResourceGroup>/providers/Microsoft.KeyVault/vaults/<Vault>"

  server = {
    model = {
      base-url          = "https://api.anthropic.com/v1/"
      name              = "<ModelName>"
      api-key-secret-id = "https://<Vault>.vault.azure.net/secrets/gatling-ai-model-api-key"
    }
    request-state-key-secret-id = "https://<Vault>.vault.azure.net/secrets/gatling-ai-request-state-key"
  }

  container = {
    image = "gatlingcorp/gatling-ai-server:<version>"
  }
}

output "url" {
  value = module.ai-server.url
}
```

Then apply it:

```console
terraform init
terraform apply
```

Azure can take a few minutes to apply a new role assignment. The module waits 60 seconds after granting the role: if
the first `terraform apply` still fails because the identity can't read a secret, run it again.

### Use the outputs on Azure

The module has two outputs:

| **Output** | **Value** |
| --- | --- |
| `url` | The server's HTTPS URL, its `GATLING_SERVER_PUBLIC_BASE_URL`. MCP clients connect to it followed by `/mcp`. |
| `container-app-id` | The resource ID of the container app. |

Check that the server is ready, and that the MCP endpoint answers `401 Unauthorized`:

```console
curl -fsS "$(terraform output -raw url)/q/health/ready"
curl -i "$(terraform output -raw url)/mcp"
```

To follow the server's log, run:

```console
az containerapp logs show --name gatling-ai-server --resource-group <ResourceGroup> --follow
```

### Know the limits {#azure-limits}

- **Conversions longer than 3 minutes**: Azure Container Apps ends every HTTP request at 240 seconds. The module
  therefore sets `GATLING_SERVER_SYNC_TIMEOUT` to `PT3M` and `GATLING_MCP_CONTINUE_ON_RETRY` to `true`: a tool call
  waits 3 minutes for its conversion, 60 seconds under that limit, then hands a conversion still running back to a
  client on a stateless MCP session, which follows it by retrying. This needs the request state key. For a client that
  can't retry, a conversion that runs past 3 minutes is canceled. If you need longer conversions, use the
  [Helm chart]({{< ref "/ai/server/installation/helm" >}}) on AKS.
- **Data**: the server's data resets at each restart. A restart, a new revision, or a secret rotation starts the token
  counts, the audit trail, and the conversion history over.
- **One replica**: when a change creates a new revision, such as a new image, setting, CPU, or memory, Container Apps
  keeps the old replica until the new one is ready. For a few seconds, two replicas run and share the requests, and a
  request that reaches the replica without its conversion, upload, or download fails. Apply such changes when nobody
  converts.
- **Secrets**: with a secret ID without a version, as above, Container Apps reads a new version of the secret within 30
  minutes and restarts the server, which resets its data. A secret ID with a version pins that version.
- **URL**: the server's URL is the container app's own address. `container.environment` can't change it, nor the other
  settings the module sets.

## Install on Google Cloud

The `gatling/ai-server/gcp` module runs the server on Cloud Run, on the service's `run.app` URL. Its repository,
[gatling/terraform-gcp-ai-server](https://github.com/gatling/terraform-gcp-ai-server), describes every input.

### Know what the module creates

The module creates the following resources, each named after the `name` input, `gatling-ai-server` by default:

- A Cloud Run service with exactly one instance, public on its `run.app` URL. It keeps the CPU allocated between
  requests, so a conversion keeps running, and gives a request 630 seconds.
- A service account that can read the secrets you name, and nothing else.

The service has no `allUsers` IAM binding: the module turns off Cloud Run's invoker IAM check, and the server itself
requires a Gatling Enterprise sign-in.

### Gather what you supply

The module creates no project and no secret, and enables no API. Enable the Cloud Run Admin, Secret Manager, IAM, and
Cloud Resource Manager APIs in your project:

```console
gcloud services enable run.googleapis.com secretmanager.googleapis.com iam.googleapis.com \
  cloudresourcemanager.googleapis.com --project=<ProjectId>
```

Then store the secrets in Secret Manager, in the same project:

```console
read -rs MODEL_API_KEY   # paste the model endpoint's API key
printf '%s' "$MODEL_API_KEY" | gcloud secrets create gatling-ai-model-api-key --project=<ProjectId> --data-file=-
openssl rand -base64 32 | gcloud secrets create gatling-ai-request-state-key --project=<ProjectId> --data-file=-
```

### Add the module

Add the module to your Terraform configuration:

```hcl
provider "google" {
  project = "<ProjectId>"
  region  = "europe-west1"
}

module "ai-server" {
  source  = "gatling/ai-server/gcp"
  version = "~> 0.1"

  project-id = "<ProjectId>"
  region     = "europe-west1"

  server = {
    model = {
      base-url       = "https://api.anthropic.com/v1/"
      name           = "<ModelName>"
      api-key-secret = "gatling-ai-model-api-key"
    }
    request-state-key-secret = "gatling-ai-request-state-key"
  }

  container = {
    image = "docker.io/gatlingcorp/gatling-ai-server:<version>"
  }
}

output "url" {
  value = module.ai-server.url
}
```

Then apply it:

```console
terraform init
terraform apply
```

### Use the outputs on Google Cloud

The module has two outputs:

| **Output** | **Value** |
| --- | --- |
| `url` | The server's `run.app` URL, `https://<name>-<project number>.<region>.run.app`, which the module gives the server as `GATLING_SERVER_PUBLIC_BASE_URL`. MCP clients connect to it followed by `/mcp`. |
| `service-account-email` | The email of the service account the server runs as. |

Check that the server is ready, and that the MCP endpoint answers `401 Unauthorized`:

```console
curl -fsS "$(terraform output -raw url)/q/health/ready"
curl -i "$(terraform output -raw url)/mcp"
```

### Know the limits {#google-cloud-limits}

- **Data**: Cloud Run's filesystem is in memory. The server's SQLite file, with the token counts and the audit trail,
  and the conversion history count against the memory limit, and are lost when the instance stops. With the history
  on, raise `container.memory`. Above `4Gi`, raise `container.cpu` too.
- **Restarts**: the instance restarts at every change that makes a new revision, such as a new image or setting, and
  whenever Cloud Run restarts it. Cloud Run can also exceed one instance for a short time, and while a new revision
  takes over, a request may reach either one. A request that reaches an instance without its conversion, upload, or
  download fails: retry it.
- **Request timeout**: Cloud Run ends a request after 630 seconds. Keep `GATLING_SERVER_SYNC_TIMEOUT` at or under its
  default, `PT9M30S`.
- **Upload size**: Cloud Run caps an HTTP/1 request at 32 MiB. Keep `GATLING_UPLOAD_MAX_BYTES`, 10 MiB by default, under
  it.
- **Image**: Cloud Run resolves the image tag to a digest when it creates a revision, so `latest` doesn't follow new
  releases. Pin a version, and change it to upgrade.
- **Secrets**: the server reads the `latest` version of each secret when it starts. A new version takes effect at the
  next restart.
- **Custom domain**: the module serves the `run.app` URL only. A custom domain needs a load balancer in front of the
  service, which the module doesn't create.
- **Public access**: an organization policy that enforces `constraints/run.managed.requireInvokerIam`, which isn't
  enforced by default, refuses to turn off the invoker IAM check, so it refuses the service.

## Change a setting

Every other setting of the [configuration reference]({{< ref "/ai/server/configuration" >}}) goes in the module's
environment map: `task.environment` on AWS, `container.environment` on Azure and Google Cloud. For example, on Google
Cloud:

```hcl
container = {
  image = "docker.io/gatlingcorp/gatling-ai-server:<version>"
  environment = {
    QUARKUS_LOG_CONSOLE_JSON_ENABLED = "true"
  }
}
```

The map is stored in plain text in the Terraform state: put no secret there. Each module refuses a map that sets the
variables it sets itself. Apply the change with `terraform apply`: it restarts the server.

## Upgrade the server

Set the image to the new version, `task.image` on AWS or `container.image` on Azure and Google Cloud, then run
`terraform apply`. The upgrade restarts the server, which loses its conversions in progress. Upgrade when nobody
converts.

## Uninstall the server

Destroy the module's resources:

```console
terraform destroy
```

The secrets stay in your secret store: delete them when you no longer need them. On AWS, also delete the DNS record.
On Google Cloud, with `deletion-protection = true`, set it to `false` and apply before you destroy.

## Next steps

- [Review the configuration]({{< ref "/ai/server/configuration" >}}).
- [Connect your AI assistant]({{< ref "/ai/server/connect" >}}).
