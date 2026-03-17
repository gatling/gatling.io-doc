---
title: Terraform Official Registry
seotitle: Gatling Control Plane Modules on the Terraform Official Registry
description: The Gatling Control Plane Terraform modules are now published on the official Terraform Registry for AWS, Azure, and GCP.
lead: The Control Plane Terraform modules are now available on the official Terraform Registry.
date: 2026-03-17T00:00:00+00:00
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

The Control Plane Terraform modules are now published on the [official Terraform Registry](https://registry.terraform.io/modules/gatling/control-plane), replacing the archived `gatling/gatling-enterprise-control-plane-deployment` repository.

You can now declare the source directly by registry address, without pointing to a Git repository:

{{< code-toggle lang="hcl" >}}
AWS: |
  module "control_plane" {
    source  = "gatling/control-plane/aws"
    version = "~> 0.0"
  }
Azure: |
  module "control_plane" {
    source  = "gatling/control-plane/azure"
    version = "~> 0.0"
  }
GCP: |
  module "control_plane" {
    source  = "gatling/control-plane/gcp"
    version = "~> 0.0"
  }
{{</ code-toggle >}}

The `~> 0.0` [version constraint](https://developer.hashicorp.com/terraform/language/expressions/version-constraints) follows semantic versioning: minor and patch versions increment freely (e.g. `0.0.1`, `0.1.0`, `0.5.3`), while `1.0.0` is reserved for breaking changes. Run `terraform init -upgrade` to pick up new compatible versions.

## Migrate from the archived repository

The former pattern used three separate modules (`location`, `private-package`, and `control-plane`) sourced via git URLs. 

These now collapse into a single registry module. Move the `location` and `private-package` arguments inline into
`module "control-plane"` as the `locations` list and `private-package` object.

Replace the old git-sourced modules:
```hcl
module "location" {
  source = "git::https://github.com/gatling/gatling-enterprise-control-plane-deployment//terraform/<provider>/location"
  # ...
}

module "private-package" {
  source = "git::https://github.com/gatling/gatling-enterprise-control-plane-deployment//terraform/<provider>/private-package"
  # ...
}

module "control-plane" {
  source = "git::https://github.com/gatling/gatling-enterprise-control-plane-deployment//terraform/<provider>/control-plane"
  # ...
}
```

With the single registry module:
```hcl
module "control-plane" {
  source  = "gatling/control-plane/<provider>"
  version = "~> 0.0"

  # ... existing configuration
  locations = [
    # previously module location
  ]

  private-package = {
    # previously module private-package
  }
}
```

Then reinitialize Terraform to download the module from the registry:
```console
terraform init -upgrade
```

See the full example configurations for all available options:
* AWS: [example/main.tf](https://github.com/gatling/terraform-aws-control-plane/blob/main/example/main.tf)
* Azure: [example/main.tf](https://github.com/gatling/terraform-azure-control-plane/blob/main/example/main.tf)
* GCP:  [example/main.tf](https://github.com/gatling/terraform-gcp-control-plane/blob/main/example/main.tf)
