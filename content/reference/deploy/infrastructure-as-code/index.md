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

This guide provides several infrastructure-as-code options for AWS, Azure, and GCP, as well as a Kubernetes Helm chart for Private Locations & Packages deployment. By leveraging the configurations & charts from [gatling/gatling-enterprise-control-plane-deployment](https://github.com/gatling/gatling-enterprise-control-plane-deployment), you can quickly spin up Private Locations & Packages.

{{< alert warning >}}
These scripts are intended to help you bootstrap your deployment.
They may change frequently and in ways that are not backward compatible.
Feel free to fork and adapt them to suit your needs.
{{< /alert >}}

## Prerequisites
- Control Plane Token: You must have a valid organization with Private Locations activated and store your [control plane token]({{< ref "/reference/deploy/private-locations/introduction/#cp-token" >}}) in a supported secret manager, such as AWS Secrets Manager, Azure Key Vault, GCP Secret Manager, or Kubernetes Secrets.
- Deployment environment: A cloud account or a Kubernetes cluster with the necessary administrative permissions.
- IaC Tools: Depending on your chosen provider and approach, install the relevant tools (e.g., Terraform, Cloud Provider CLI, Helm, Kubectl, etc.).

## Kubernetes {#kubernetes}

Deploys the Control Plane container as a Kubernetes deployment, along with the required Role, ServiceAccount, and RoleBinding, enabling it to spawn batch jobs that create one or more pods as load generators with defined resource limits and requests.

### Helm

Helm charts versions are available on the Gatling Helm subdomain [https://helm.gatling.io](https://helm.gatling.io).

- Follow installation steps available [here](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/helm-chart/README.md).
- [Values file](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/helm-chart/values.yaml) provides 3 sets of values—controlPlane, privateLocations, and an optional privatePackage—with fully configurable parameters for customizing the deployment. Optionally supports storing Private Packages in an existing S3 bucket, Azure Blob Storage, GCP Cloud Storage, or Control Plane's volume.

## AWS {#aws}

Deploys the Control Plane container on Elastic Container Service (ECS) using Fargate and creates the necessary IAM permissions to spawn EC2 instances as load generators in your VPC. Optionally supports storing Private Packages in an existing S3 bucket.

### CloudFormation

These templates contain nested stacks for Control Plane, Location, and optional Private Package resources.

- [Private Locations sample template](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/aws/cloudformation/samples/private-location)
- [Private Locations & Packages sample template](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/aws/cloudformation/samples/private-location-package)

### Cloud Development Kit (CDK)

#### TypeScript

This CDK application includes constructs for Control Plane, Location, and Private Package. Installation guide available [here](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/aws/cdk/typescript/README.md).

- [Private Locations sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/aws/cdk/typescript/bin/private-location.ts)
- [Private Locations & Packages sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/aws/cdk/typescript/bin/private-location-package.ts)

#### Java

This CDK application includes constructs for Control Plane, Location, and Private Package. Installation guide available [here](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/aws/cdk/java/README.md).

- [Private Locations sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/aws/cdk/java/src/main/java/com/gatlingenterprise/PrivateLocation.java)
- [Private Locations & Packages sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/blob/main/aws/cdk/java/src/main/java/com/gatlingenterprise/PrivateLocationPackage.java)

### Terraform

The configuration consists of three modules: one for deploying the control plane, another for specifying the location, and a third for defining an optional private package.

- [Private Locations sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/tree/main/terraform/examples/AWS-private-location)
- [Private Locations & Packages sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/tree/main/terraform/examples/AWS-private-package)

## Azure {#azure}

Deploys the Control Plane container on a Container App using a Container App Environment, utilizes an existing storage account as a volume, and creates the necessary role assignments to spawn EC2 instances as load generators in your VPC. Optionally, it supports storing private packages in an existing storage account.

### Terraform

The configuration consists of three modules: one for deploying the control plane, another for specifying the location, and a third for defining an optional private package.

- [Private Locations sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/tree/main/terraform/examples/AZURE-private-location)
- [Private Locations & Packages sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/tree/main/terraform/examples/AZURE-private-package)

## GCP {#gcp}

Deploys the Control Plane container on a Container App using a Container App Environment, utilizes an existing storage account as a volume, and creates the necessary role assignments to spawn EC2 instances as load generators in your VPC. Optionally, it supports storing private packages in an existing storage account.

### Terraform  

The configuration consists of three modules: one for deploying the control plane, another for specifying the location, and a third for defining an optional private package.

- [Private Locations sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/tree/main/terraform/examples/GCP-private-location)
- [Private Locations & Packages sample configuration](https://github.com/gatling/gatling-enterprise-control-plane-deployment/tree/main/terraform/examples/GCP-private-package)
