---
title: AWS locations configuration
menutitle: Configuration
seotitle: Configure AWS locations in Gatling Enterprise Edition
description: AWS locations on your private AWS account.
lead: Private Locations on your AWS account.
date: 2023-01-12T16:46:04+00:00
aliases:
  - /reference/install/cloud/private-locations/aws/configuration/
---

## Instance specifications

The follow recommendations help you to select the best instances and tune them for maximum performance. 

- We recommend instances with at least 4 cores for your load generators.
- As Gatling load tests tend to be CPU bound, we recommend using instances from the "Compute Optimized" family.
- In our experience, it seems that Intel CPUs perform better than AMD and ARM Graviton ones when it comes to TLS. As a result, we recommend using `c7i.xlarge` instances or larger.
- You might want to tune the `Xmx` JVM options to half of the physical memory. See `jvm-options` configuration below. If you don't, the JVM will use a max heap size of 1/4th of the physical memory.

{{<alert tip >}}
Accelerate deployment and simplify configuration with Gatling's pre-built [<span style="text-decoration: underline;">infrastructure-as-code configurations</span>]({{< ref "infrastructure-as-code/#aws" >}}).
{{</alert>}}

## Permissions

AWS private locations requires the control plane to have access to AWS credentials from the default credential provider chain.

See [the AWS documentation for the Default Credential Provider Chain](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-default).

## System requirements

AWS EC2 private locations rely on some dependencies.

So when using a custom image, make sure following are available:

- [cloud-init](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/user-data.html) integration.
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
  # Locations configurations
  # Server configuration (optional)
  # server {
    # port = 8080 # (optional, default: 8080)
    # bind-address = "0.0.0.0" # (optional, default: 0.0.0.0)

    # # PKCS#12 certificate (optional)
    # certificate {
    # path = "/path/to/certificate.p12"
    #  password = ${CERTIFICATE_PASSWORD} # (optional)
    # }
  # }
  locations = [
    {
      # Private location ID, must be prefixed by prl_, only consist of numbers 0-9, 
      # lowercase letters a-z, and underscores, with a max length of 30 characters
      id = "prl_private_location_example"
      # Private location description (optional)
      description = "Private Location on AWS"
      # Private location type
      type = "aws"
      # Configuration specific to AWS type configuration
      region = "eu-west-1"
      # Engine (optional, default classic)
      engine = "classic" # Possible values: classic or javascript
      # Certified AMI configuration
      ami {
        type = "certified"
        # java = latest # See engine section
      }
      # Custom AMI configuration (alternative to certified AMI)
      # ami = {
      #   type = custom
      #   id = "ami-00000000000000000"
      # }
      # Absolute path of the directory where executable binaries will be stored
      # executable-dir = "/tmp"
      # Security groups
      security-groups = ["sg-mysecuritygroup"]
      # Instance type
      instance-type = "c7i.xlarge"
      # Spot instances (optional, default: false)
      # spot = true
      # Subnets
      subnets = ["subnet-a", "subnet-b"]
      # Automatically associate a public IPv4 (optional, default true)
      # auto-associate-public-ipv4 = true
      # Elastic IP addresses (optional, not compatible with auto-associate-public-ipv4)
      # You will only be able to deploy a number of load generators up to the number of Elastic IP addresses you have configured.
      # elastic-ips = ["203.0.113.3", "203.0.113.4"]
      # Profile name (optional)
      # profile-name = ""
      # IAM Instance profile (optional)
      # iam-instance-profile = ""
      # Custom tags (optional)
      tags {
       # ExampleKey = "ExampleValue"
      }
      # Custom tags for each AWS resource type (optional)
      # Only resources types mentioned further are managed
      tags-for {
        instance {
          # ExampleKey = "ExampleValue"
        }
        volume {
          # ExampleKey = "ExampleValue"
        }
        network-interface {
          # ExampleKey = "ExampleValue"
        }
      }
      
      # Java configuration (following configuration properties are optional)
      # System properties (optional)
      system-properties {
        # ExampleKey = "ExampleValue"
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
| classic     | 25 or latest            |
| javascript  | latest                  |

{{< alert info >}}
For the `javascript` engine, only the latest Java version is supported, which corresponds to the GraalVM version used to run Gatling with JavaScript.
{{< /alert >}}
