---
title: Infrastructure-as-code
seotitle: Gatling Private Locations & Packages Infrastructure-as-Code
description: Learn how to automate your Gatling Private Locations & Packages deployment.
date: 2021-11-07T14:29:04+00:00
aliases:
  - /reference/install/cloud/private-locations/infrastructure-as-code/
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

This guide provides several infrastructure-as-code options for Private Locations & Packages deployment:
* Terraform scripts for AWS, Azure, and GCP
* a Helm chart for Kubernetes and OpenShift

{{< alert warning >}}
These scripts are intended to help you bootstrap your deployment.
Feel free to fork and adapt them to suit your needs.
{{< /alert >}}

## Prerequisites
- Control Plane Token: You must have a valid organization with Private Locations activated and store your [control plane token]({{< ref "/reference/deploy/private-locations/introduction/#cp-token" >}}) in a supported secret manager, such as AWS Secrets Manager, Azure Key Vault, GCP Secret Manager, or Kubernetes Secrets.
- Deployment environment: A cloud account or a Kubernetes or OpenShift cluster with the necessary administrative permissions.
- IaC Tools: Depending on your chosen provider and approach, install the relevant tools (e.g., Terraform, Cloud Provider CLI, Helm, Kubectl, etc.).

## Helm chart for Kubernetes and OpenShift {#kubernetes}

Deploys the Control Plane container as a Kubernetes or OpenShift deployment, along with the required Role, ServiceAccount, and RoleBinding, enabling it to spawn batch jobs that create one or more pods as load generators with defined resource limits and requests.

Helm charts versions are available on the Gatling Helm subdomain [https://helm.gatling.io](https://helm.gatling.io).

- Follow installation steps available [here](https://github.com/gatling/helm-control-plane).
- [Values file](https://github.com/gatling/helm-control-plane/blob/main/values.yaml) provides 3 sets of values—controlPlane, privateLocations, and an optional privatePackage—with fully configurable parameters for customizing the deployment. Optionally supports storing Private Packages in an existing S3 bucket, Azure Blob Storage, GCP Cloud Storage, or Control Plane's volume.

## Terraform for AWS {#aws}

Deploys the Control Plane container on Elastic Container Service (ECS) using Fargate and creates the necessary IAM permissions to spawn EC2 instances as load generators in your VPC. Optionally supports storing Private Packages in an existing S3 bucket.

[AWS Terraform Control Plane Module](https://github.com/gatling/terraform-aws-control-plane): `gatling/control-plane/aws`
- See [Sample Configuration](https://github.com/gatling/terraform-aws-control-plane/blob/main/example/main.tf)

## Terraform for Azure {#azure}

Deploys the Control Plane container on a Container App using a Container App Environment, utilizes an existing storage account as a volume, and creates the necessary role assignments to spawn EC2 instances as load generators in your VPC. Optionally, it supports storing private packages in an existing storage account.

[Azure Terraform Control Plane Module](https://github.com/gatling/terraform-azure-control-plane): `gatling/control-plane/azure`
- See [Sample Configuration](https://github.com/gatling/terraform-azure-control-plane/blob/main/example/main.tf)

## Terraform for GCP {#gcp}

Deploys the Control Plane container on a Container App using a Container App Environment, utilizes an existing storage account as a volume, and creates the necessary role assignments to spawn EC2 instances as load generators in your VPC. Optionally, it supports storing private packages in an existing storage account.

[GCP Terraform Control Plane Module](https://github.com/gatling/terraform-gcp-control-plane): `gatling/control-plane/gcp`
- See [Sample Configuration](https://github.com/gatling/terraform-gcp-control-plane/blob/main/example/main.tf)
