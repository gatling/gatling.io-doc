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

The modules mount no volume, and the server keeps nothing across a restart. A restart loses the conversions in
progress, the uploads, the download links, and the day's model token count.

## Meet the prerequisites

Before you begin, make sure you have the following:

- What every installation needs, as [Install Gatling AI Server]({{< ref "/ai/server/installation/overview" >}}) describes.
- Terraform 1.5 or later, with your cloud's provider: the AWS provider 6.0 or later, the AzureRM provider 4.0 or
  later, or the Google provider 7.0 or later.
- The command-line tool of your cloud, signed in: the AWS CLI, the Azure CLI, or the Google Cloud CLI.

## Install on AWS

The `gatling/ai-server/aws` module runs the server on ECS Fargate, behind an HTTPS Application Load Balancer. Its
repository, [gatling/terraform-aws-ai-server](https://github.com/gatling/terraform-aws-ai-server), describes every
input.

The following diagram shows how the components connect on AWS:

{{< img src="aws.png" alt="Your users' AI assistant sends MCP over HTTPS to an Application Load Balancer in your VPC's public subnets, which ends TLS with your ACM certificate, keeps idle connections 630 s and forwards HTTP on port 8080 to Gatling AI Server, one Fargate task with no volume; the task reads the secrets you created in Secrets Manager at start and sends its logs to a CloudWatch log group kept 30 days; the server sends code snippets to your model endpoint and validates tokens against Gatling's sign-in service, where users sign in from their browser" >}}

### Know what the module creates

The module creates the following resources, each named after the `name` input, `gatling-ai-server` by default:

- An ECS cluster, and a Fargate service that runs one task, never two at once.
- An internet-facing Application Load Balancer, which serves HTTPS on port 443 with your ACM certificate, keeps a
  connection open for 630 seconds of silence, and checks the server's health on `/q/health/ready`.
- The security groups of the load balancer, open on port 443 to `load_balancer.ingress_cidrs`, `0.0.0.0/0` by
  default, and of the task, open on port 8080 to the load balancer only.
- The task's IAM roles. The execution role can read the secrets you name, and no other secret.
- A CloudWatch log group, `/ecs/<name>-service`, which keeps the server's log 30 days by default.

### Gather what you supply

The module creates no network, certificate, or DNS record. Before you apply it, make sure you have the following:

- A VPC with at least two public subnets in different Availability Zones for the load balancer, each a `/27` or larger
  with at least eight free IP addresses.
- Subnets for the task, in Availability Zones of the load balancer's subnets: public subnets, where the task gets a
  public IP address, the default, or private subnets with a NAT gateway (`task.assign_public_ip = false`). The task
  connects out over HTTPS to Docker Hub, which serves its image, to `auth.gatling.io`, and to your model endpoint.
  Allow `api.gatling.io` too: it's the Gatling Enterprise API, which upcoming releases of the server call.
- A hostname for the server, such as `gatling-ai.example.com`, in a DNS zone you manage, and an ACM certificate for it
  in the region you deploy to.
- An AWS identity to run Terraform with, allowed to create, read, tag, update, and delete what the module manages: the
  ECS cluster, task definition, and service; the Elastic Load Balancing load balancer, target group, and listener; the
  EC2 security groups; the IAM roles and policy, with `iam:PassRole` on the two roles; and the CloudWatch Logs log
  group and its retention. It also reads the VPC, its subnets, and the Availability Zones, and needs the other EC2
  actions that Elastic Load Balancing requires to create the load balancer. In an account where ECS or Elastic Load
  Balancing has no service-linked role yet, it needs `iam:CreateServiceLinkedRole`. The module's
  [prerequisites](https://github.com/gatling/terraform-aws-ai-server#prerequisites) list these actions by service.

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

module "ai_server" {
  source  = "gatling/ai-server/aws"
  version = "~> 0.1"

  vpc_id = "<VpcId>"

  load_balancer = {
    subnets         = ["<PublicSubnetId1>", "<PublicSubnetId2>"]
    certificate_arn = "<CertificateARN>"
  }

  task = {
    subnets = ["<PublicSubnetId1>", "<PublicSubnetId2>"]
    image   = "gatlingcorp/gatling-ai-server:<version>"
  }

  server = {
    public_base_url = "https://gatling-ai.example.com"
    model = {
      base_url           = "https://api.anthropic.com/v1/"
      name               = "<ModelName>"
      api_key_secret_arn = "<ModelApiKeySecretARN>"
    }
    request_state_key_secret_arn = "<RequestStateKeySecretARN>"
  }
}
```

`server.public_base_url` is the `https://` URL of your hostname, with no path. Then apply it:

```console
terraform init
terraform apply
```

### Point your hostname at the load balancer

The module has three outputs:

| **Output** | **Value** |
| --- | --- |
| `load_balancer_dns_name` | The DNS name of the load balancer, which your hostname points to. |
| `load_balancer_zone_id` | The Route 53 hosted zone ID of the load balancer, for an alias record. |
| `url` | The server's public base URL, with no trailing slash. MCP clients connect to it followed by `/mcp`. |

With Route 53, add an alias record:

```hcl
resource "aws_route53_record" "ai_server" {
  zone_id = "<HostedZoneId>"
  name    = "gatling-ai.example.com"
  type    = "A"

  alias {
    name                   = module.ai_server.load_balancer_dns_name
    zone_id                = module.ai_server.load_balancer_zone_id
    evaluate_target_health = false
  }
}
```

With another DNS provider, create a `CNAME` record from your hostname to the `load_balancer_dns_name` output.

### Check the server on AWS

Check that the server is ready, and that the MCP endpoint asks for authentication:

```console
curl -fsS https://gatling-ai.example.com/q/health/ready
curl -i https://gatling-ai.example.com/mcp
```

The MCP endpoint answers `401 Unauthorized`, with a `WWW-Authenticate` header whose `resource_metadata` URL starts with
`server.public_base_url`: if it doesn't, fix that input. To follow the server's log, run:

```console
aws logs tail /ecs/gatling-ai-server-service --follow
```

### Know the limits {#aws-limits}

- The server restarts after an apply that changes the task, a Fargate maintenance, or a crash. ECS starts the new task
  once the old one has stopped, so the server is unavailable in between.
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

The following diagram shows how the components connect on Azure:

{{< img src="azure.png" alt="Your users' AI assistant reaches Gatling AI Server through the HTTPS ingress of an Azure Container Apps environment in your resource group, where the server runs as one container app with no volume and tool calls wait at most 3 minutes, under the 240 s request limit; it reads its secrets from your Azure Key Vault through a user-assigned managed identity, sends code snippets to your model endpoint and validates tokens against Gatling's sign-in service, where users sign in from their browser" >}}

### Know what the module creates

The module creates the following resources, each named after the `name` input, `gatling-ai-server` by default:

- A Container Apps environment, `<name>-env`, with no Log Analytics workspace: the server's log is streamed only.
- A container app, `<name>`, with one replica in single revision mode, and an external ingress to port 8080 on its own
  HTTPS address. The module sets the server's public URL to that address.
- A user-assigned identity, `<name>-identity`, that reads the secrets from your Key Vault. With
  `key_vault_authorization = "rbac"`, the default, the module grants it the role Key Vault Secrets User on the vault.
  With `"access-policy"`, it adds an access policy that lets it get secrets. With `container.registry.id`, it also
  grants it the role AcrPull on that registry.

### Gather what you supply

The module creates no resource group, Key Vault, or secret. Before you apply it, make sure you have the following:

- An existing resource group.
- An existing Key Vault, and the right to write its secrets and grant access to it. When the vault uses Azure RBAC, the
  default of new vaults: the role Key Vault Secrets Officer on it, which Owner doesn't include, and the right to create
  role assignments on it, such as Owner, User Access Administrator, or Role Based Access Control Administrator. When it
  uses access policies: the secret Set permission, and the right to change its access policies.
- The resource providers `Microsoft.App`, for Container Apps, and `Microsoft.ManagedIdentity`, for the identity,
  registered in your subscription. Registering one needs its `register/action` permission, which Contributor and Owner
  include:

  ```console
  az provider register --namespace Microsoft.App --wait
  az provider register --namespace Microsoft.ManagedIdentity --wait
  ```

Then store the secrets in your Key Vault:

```console
az keyvault secret set --vault-name <Vault> --name gatling-ai-model-api-key --value "<ApiKey>"
az keyvault secret set --vault-name <Vault> --name gatling-ai-request-state-key --value "$(openssl rand -base64 32)"
```

`key_vault_authorization` must match the vault's permission model:
`az keyvault show --name <Vault> --query properties.enableRbacAuthorization` prints `true` for `rbac`.

### Add the module

Add the module to your Terraform configuration:

```hcl
provider "azurerm" {
  features {}
  subscription_id = "<SubscriptionId>"
}

module "ai_server" {
  source  = "gatling/ai-server/azure"
  version = "~> 0.1"

  resource_group_name = "<ResourceGroup>"
  location            = "<Region>"
  key_vault_id        = "/subscriptions/<SubscriptionId>/resourceGroups/<ResourceGroup>/providers/Microsoft.KeyVault/vaults/<Vault>"

  server = {
    model = {
      base_url          = "https://api.anthropic.com/v1/"
      name              = "<ModelName>"
      api_key_secret_id = "https://<Vault>.vault.azure.net/secrets/gatling-ai-model-api-key"
    }
    request_state_key_secret_id = "https://<Vault>.vault.azure.net/secrets/gatling-ai-request-state-key"
  }

  container = {
    image = "gatlingcorp/gatling-ai-server:<version>"
  }
}

output "url" {
  value = module.ai_server.url
}
```

Then apply it:

```console
terraform init
terraform apply
```

Azure can take a few minutes to apply a new role assignment. The module waits 60 seconds after granting a role: if the
first `terraform apply` still fails because the identity can't read a secret or pull the image, run it again.

### Pull the image from a private registry

Without `container.registry`, the container app pulls `container.image` anonymously. To pull it from a private
registry, set `container.registry`: `server`, the registry's host with no scheme or path, and one way to sign in:

- `id`, the resource ID of an Azure Container Registry. The module grants its identity the role AcrPull on the
  registry, and the app pulls with that identity. Granting the role needs the right to create role assignments on the
  registry. A registry in the RBAC Registry + ABAC Repository Permissions mode doesn't honor AcrPull: grant the
  identity the Container Registry Repository Reader role on it yourself, as the module's README describes.
- For any other registry, `username` and `password_secret_id`, the Key Vault secret ID of the password, in the
  `key_vault_id` vault. The app reads it with the module's identity.

For example, with an Azure Container Registry:

```hcl
container = {
  image = "<Registry>.azurecr.io/gatling-ai-server:<version>"
  registry = {
    server = "<Registry>.azurecr.io"
    id     = "/subscriptions/<SubscriptionId>/resourceGroups/<ResourceGroup>/providers/Microsoft.ContainerRegistry/registries/<Registry>"
  }
}
```

The module refuses a `server` with a scheme or a path, and any sign-in other than `id` alone, or a non-empty
`username` with `password_secret_id`. The module's
[README](https://github.com/gatling/terraform-azure-ai-server#private-registry) gives an example of each, and the
registry settings that let the identity pull.

### Use the outputs on Azure

The module has two outputs:

| **Output** | **Value** |
| --- | --- |
| `url` | The server's HTTPS URL, its `GATLING_SERVER_PUBLIC_BASE_URL`. MCP clients connect to it followed by `/mcp`. |
| `container_app_id` | The resource ID of the container app. |

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
- **One replica**: when a change creates a new revision, such as a new image, setting, CPU, or memory, Container Apps
  keeps the old replica until the new one is ready. For a few seconds, two replicas run and share the requests, and a
  request that reaches the replica without its conversion, upload, or download fails. Apply such changes when nobody
  converts.
- **Secrets**: with a secret ID without a version, as above, Container Apps reads a new version of the secret within 30
  minutes and restarts the server. A secret ID with a version pins that version.
- **URL**: the server's URL is the container app's own address. `container.environment` can't change it, nor the other
  settings the module sets.

## Install on Google Cloud

The `gatling/ai-server/gcp` module runs the server on Cloud Run, on the service's `run.app` URL. Its repository,
[gatling/terraform-gcp-ai-server](https://github.com/gatling/terraform-gcp-ai-server), describes every input.

The following diagram shows how the components connect on Google Cloud:

{{< img src="gcp.png" alt="Your users' AI assistant reaches Gatling AI Server in your Google Cloud project through Cloud Run's HTTPS run.app URL, which ends a request after 630 s; Cloud Run runs the single server instance as a service account that can only read the secrets you created in Secret Manager; the server's data folder is on Cloud Run's in-memory filesystem; the server sends code snippets to your model endpoint and validates tokens against Gatling's sign-in service, where users sign in from their browser" >}}

### Know what the module creates

The module creates the following resources, each named after the `name` input, `gatling-ai-server` by default:

- A Cloud Run service with exactly one instance, public on its `run.app` URL. It keeps the CPU allocated between
  requests, so a conversion keeps running, and gives a request 630 seconds.
- A service account that can read the secrets you name, and nothing else.
- For an image on Docker Hub, the default, an Artifact Registry remote repository of Docker Hub, named
  `<name>-docker-hub`, in the service's region. Cloud Run fetches Docker Hub images through Google's cache of Docker
  Hub, which keeps frequently requested images only, with no guarantee, so it pulls the server's image through this
  repository. The repository keeps the last 2 releases.

The service has no `allUsers` IAM binding: the module turns off Cloud Run's invoker IAM check, and the server itself
requires a Gatling Enterprise sign-in.

### Gather what you supply

The module creates no project and no secret, and enables no API. Enable the Cloud Run Admin, Secret Manager, IAM, Cloud
Resource Manager, and Artifact Registry APIs in your project:

```console
gcloud services enable run.googleapis.com secretmanager.googleapis.com iam.googleapis.com \
  cloudresourcemanager.googleapis.com artifactregistry.googleapis.com --project=<ProjectId>
```

Run Terraform with a Google Cloud identity allowed to create, read, update, and delete what the module manages: the
Cloud Run service, with `run.services.setIamPolicy` to turn off its invoker IAM check; the service account, with
`iam.serviceAccounts.actAs` on it; an IAM binding on each secret you name; and, for an image on Docker Hub, the
Artifact Registry repository. It also reads the project (`resourcemanager.projects.get`). Owner allows all of it.
Editor doesn't: only Owner and Cloud Run Admin can turn off the invoker IAM check.

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

module "ai_server" {
  source  = "gatling/ai-server/gcp"
  version = "~> 0.1"

  project_id = "<ProjectId>"
  region     = "europe-west1"

  server = {
    model = {
      base_url       = "https://api.anthropic.com/v1/"
      name           = "<ModelName>"
      api_key_secret = "gatling-ai-model-api-key"
    }
    request_state_key_secret = "gatling-ai-request-state-key"
  }

  container = {
    image = "docker.io/gatlingcorp/gatling-ai-server:<version>"
  }
}

output "url" {
  value = module.ai_server.url
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
| `service_account_email` | The email of the service account the server runs as. |

Check that the server is ready, and that the MCP endpoint answers `401 Unauthorized`:

```console
curl -fsS "$(terraform output -raw url)/q/health/ready"
curl -i "$(terraform output -raw url)/mcp"
```

### Know the limits {#google-cloud-limits}

- **Memory**: Cloud Run's filesystem is in memory, so what the server writes to disk counts against the memory limit.
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

Every other setting of the [configuration reference]({{< ref "/ai/server/configuration/overview" >}}) goes in the module's
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
On Google Cloud, with `deletion_protection = true`, set it to `false` and apply before you destroy.

## Next steps

- [Review the configuration]({{< ref "/ai/server/configuration/overview" >}}).
- [Connect your AI assistant]({{< ref "/ai/server/connect" >}}).
