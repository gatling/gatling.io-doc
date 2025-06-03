---
title: GCP locations configuration
menutitle: Configuration
seotitle: Configure GCP locations in Gatling Enterprise
description: Load Generators on your private GCP account.
lead: Private Locations on your GCP account.
date: 2023-10-02T15:29:00+00:00
aliases:
  - /reference/install/cloud/private-locations/gcp/configuration/
---

## Instance specifications

We recommend that you use for your own load generator instances with at least 4 cores.

As Gatling load tests tend to be CPU bound, we recommend using instances of the "Compute Optimized" family.

As a result, we recommend using `c3-highcpu-4` instances or larger.

You might want to tune the `Xmx` JVM options to half of the physical memory.
See `jvm-options` configuration below.
If you don't, the JVM will use a max heap size of 1/4th of the physical memory.

{{<alert tip >}}
Accelerate deployment and simplify configuration with Gatling's pre-built [<span style="text-decoration: underline;">infrastructure-as-code configurations</span>]({{< ref "infrastructure-as-code/#gcp" >}}).
{{</alert>}}

## Permissions

GCP private locations require the control plane to have GCP access rights configured in order to instantiate virtual machines.

GCP private locations require the control plane to have credentials configured in order to instantiate virtual machines and associated resources.

Access rights can be set through a service account associated with your control plane.

Check GCP and Gatling documentation pages for more details:
* [GCP Service account](https://cloud.google.com/iam/docs/service-account-overview)
* [Gatling installation guide]({{< ref "installation/#service-account" >}})

## System requirements

GCP private locations rely on some dependencies.

So when using a custom image, make sure following are available:

- [cloud-init](https://cloud.google.com/compute/docs/instances/startup-scripts/linux) integration.
- [jq](https://jqlang.github.io/jq/download/) a lightweight and flexible command-line JSON processor.
- [curl](https://curl.se/download.html) a command line tool and library for transferring data with URLs
- [Java runtime environment](https://openjdk.org/install/): OpenJDK 64bits LTS versions: 11, 17 or 21 (see [Gatling prerequisites]({{< ref "/tutorials/oss#java-version" >}}))

{{< alert tip >}}
Learn how to tune the OS for more performance, configure the open files limit, the kernel and the network [here]({{< ref "/concepts/operations#os-tuning" >}}).
{{< /alert >}}

## Control plane configuration file

```bash
control-plane {
  # Control plane token
  token = "cpt_example_c7oze5djp3u14a5xqjanh..."
  # Control plane token with an environment variable
  token = ${?CONTROL_PLANE_TOKEN}
  # Control plane description (optional)
  description = "my control plane description"
  # Server configuration (optional)
  # server {
    # port = 8080 # (optional, default: 8080)
    # bindAddress = "0.0.0.0" # (optional, default: 0.0.0.0)

    # # PKCS#12 certificate (optional)
    # certificate {
    # path = "/path/to/certificate.p12"
    #  password = ${CERTIFICATE_PASSWORD} # (optional)
    # }
  # }
  # Locations configurations
  locations = [
    {
      # Private location ID, must be prefixed by prl_, only consist of numbers 0-9, 
      # lowercase letters a-z, and underscores, with a max length of 30 characters
      id = "prl_private_location_example"
      # Private location description (optional)
      description = "Private Location on GCP"
      # Private location type
      type = "gcp"
      # GCP location name, as listed by GCP CLI:
      # gcloud compute zones list
      zone = "europe-west3-a"
      # Instance template (alternative to machine)
      # instance-template = "example-template"
      # Machine configuration (alternative to instance template)
      machine {
        # Virtual machine type, as listed by GCP CLI:
        # gcloud compute machine-types list --filter="zone:( europe-west3-a )"
        type = "c3-highcpu-4"
        # Configure load generators instances as preemptible or not. (optional, default: false)
        # preemptible = true
        # Engine (optional, default classic)
        engine = "classic" # Possible values: classic or javascript
        # Certified image configuration
        image {
          type = "certified"
          java = "latest" # See engine section
        }
        # Custom image configuration (alternative to certified image)
        # Base your location on a custom image from its name
        # image {
        #   type = "custom"
        #   project = "my-project"
        #   id = "image-name"
        # }
        # Custom image configuration (alternative to certified image)
        # Base your location on a custom image from its image family
        # image {
        #   type = "custom"
        #   project = "my-project"
        #   family = "image-family"
        # }
        # Storage configuration
        disk {
          # Disk size in Gb (mininum 20Gb)
          sizeGb = 20
        }
        # Network interface (optional)
        network-interface {
          # Specific project for the network interface (optional)
          # project = "my-network-interface-project-id"
          # Network name on your project (optional)
          # Not needed if subnetwork is configured
          # network = "gatling-network"
          # Subnetwork name on your project (optional)
          # subnetwork = "gatling-subnetwork-europe-west3"
          # Associate external IP to instance (optional, default to true)
          # See Cloud NAT when set to false
          # with-external-ip = true
        }
      }
      # GCP project id as returned by GCP CLI:
      # gcloud projects list
      project = "my-project-id"
      # Java configuration (the following configuration properties are optional)
      # System properties (optional)
      system-properties {
        "java.net.preferIPv6Addresses" = "true"
      }
      # Overwrite JAVA_HOME definition (optional)
      # java-home = "/usr/lib/jvm/zulu"
      # JVM Options (optional)
      # Default ones, that can be overridden with precedence:
      # [
      #   "-XX:MaxInlineLevel=20", 
      #   "-XX:MaxTrivialSize=12", 
      #   "-XX:+IgnoreUnrecognizedVMOptions", 
      #   "--add-opens=java.base/java.nio=ALL-UNNAMED", 
      #   "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED"
      # ]
      #  Based on your instance configuration, you may want to update Xmx and Xms values.
      # jvm-options = ["-Xmx4G", "-Xms512M"]
    }
  ]
}
```

### Engine

The engine specified for a location determines the compatible package formats (JavaScript or JVM) for Gatling packages.

Each engine (`classic` or `javascript`) supports specific Java versions, where `latest` is defaulted.

The table below outlines the supported Java versions for certified Gatling images:

| Engine      | Supported Java Versions |
|-------------|-------------------------|
| classic     | 21 or latest            |
| javascript  | latest                  |

{{< alert info >}}
For the `javascript` engine, only the latest Java version is supported, which corresponds to the GraalVM version used to run Gatling with JavaScript.
{{< /alert >}}

## Internet access for your Load Generators instances

Cloud NAT (Network Address Translation) lets certain resources in Google Cloud create outbound connections to the internet or to other Virtual Private Cloud (VPC) networks. 
Cloud NAT supports address translation for established inbound response packets only. **It does not allow unsolicited inbound connections.**

More info on [Cloud NAT overview](https://cloud.google.com/nat/docs/overview).

### Instances with no external IP

An instance without an external IP cannot access to internet without a Cloud NAT configured on the network, for the region.
Load generators must have access to some outbound domains in order to run (see [Private locations introduction]({{< ref "../introduction" >}})).

In the GCP management console, open the [Cloud NAT](https://console.cloud.google.com/net-services/nat) (or search for "Cloud NAT" in the search bar).

Set the Gateway:
- name
- region base on your location zone
- create a router

{{< img src="cloud-nat-gateway.png" alt="Gateway configuration" >}}
{{< img src="cloud-nat-router.png" alt="Router configuration" >}}

###  Set static IPs

Cloud NAT gateway can be configured with static IP addresses.
See limits of concurrent connections with Cloud NAT on [Cloud NAT port reservation](https://cloud.google.com/nat/docs/ports-and-addresses#examples) and 
make sure to provide enough static IP addresses based on the load you need to generate.

{{< img src="cloud-nat-static-ip.png" alt="Static IP configuration" >}}
