---
title: Configuration as code
seotitle: Gatling Enterprise Deployment with Configuration as Code
description: Guides you through deploying your Gatling project on Gatling Enterprise.
date: 2024-03-10T14:29:04+00:00
aliases:
  - /reference/execute/cloud/user/configuration-as-code/
---

## Introduction

Helps you quickly create and maintain your simulations on Gatling Enterprise directly from your
project configuration through a simple command of your Gatling build tool plugin.

## Pre-requisites

On Gatling Enterprise:

- [Create an account]({{< ref "reference/collaborate/user/login/#create-your-own-account" >}})
- [Create or join an organization]({{< ref "reference/collaborate/user/login/#login" >}})
- Create an [API Token]({{< ref "reference/collaborate/admin/api-tokens" >}}) with **Configure** permission

For a better understanding, see references for underlying concepts:
- [Package Generation]({{< ref "reference/run-tests/sources/package-gen" >}})
- [Package Configuration]({{< ref "reference/run-tests/sources/package-conf" >}})
- [Simulations]({{< ref "reference/run-tests/simulations/intro" >}})

## Usage

To deploy your Gatling project on Gatling Enterprise, follow these steps:

1. Configure the Gatling Enterprise [API Token]({{< ref "reference/collaborate/admin/api-tokens" >}}) within your Gatling Build Plugin:
   - [Gatling Plugin with Maven]({{< ref "/integrations/build-tools/maven-plugin/#prerequisites" >}})
   - [Gatling Plugin with Gradle]({{< ref "/integrations/build-tools/gradle-plugin/#prerequisites" >}})
   - [Gatling Plugin with sbt]({{< ref "/integrations/build-tools/sbt-plugin/#prerequisites" >}})
   - [JavaScript or TypeScript with npm]({{< ref "/integrations/build-tools/js-cli/#running-your-simulations-on-gatling-enterprise" >}})
2. Use the following command for deployment:
    - [Maven]({{< ref "/integrations/build-tools/maven-plugin/#deploying-on-gatling-enterprise" >}}): `mvn gatling:enterpriseDeploy`
    - [Gradle]({{< ref "/integrations/build-tools/gradle-plugin/#deploying-on-gatling-enterprise" >}}): `gradle gatlingEnterpriseDeploy`
    - [sbt]({{< ref "/integrations/build-tools/sbt-plugin/#deploying-on-gatling-enterprise" >}}): `sbt Gatling/enterpriseDeploy`
    - [JavaScript or TypeScript with npm]({{< ref "/integrations/build-tools/js-cli/#deploying-on-gatling-enterprise" >}}): `npx gatling enterprise-deploy`
    
{{< alert tip >}}
Demo projects are available with a fully configured [Package Descriptor example]({{< ref "#package-descriptor" >}}) for each Build Plugin: [Maven](https://github.com/gatling/gatling-maven-plugin-demo-java/tree/main/.gatling/example.package.conf), [Gradle](https://github.com/gatling/gatling-gradle-plugin-demo-java/tree/main/.gatling/example.package.conf), and [sbt](https://github.com/gatling/gatling-sbt-plugin-demo/tree/main/.gatling/example.package.conf)
{{< /alert >}}

## Default Behavior

When no additional configuration is provided, the deployment process follows these inferred settings:

1. Creation of a package within the organization:
    1. The package is named after the artifact ID of your project.
    2. The team assigned to the package is either:
        1. The only team specified in the API Token.
        2. The only team in the organization if the API Token has a global role.
2. Creation of Simulations within the package:
    1. Each Simulation is named after its simulation class.
    2. The team assigned to the simulation is inherited from the package.
    3. Locations are defaulted by Gatling Enterprise.

{{< alert info >}}
During subsequent deployments without additional configuration, inferred values will be sourced from existing simulations on Gatling Enterprise based on their names.

This means that if the simulation name remains unchanged but other properties have been modified in the Cloud UI, those modifications will remain.
{{< /alert >}}

## Package Descriptor

You can use a package descriptor file to specify the deployment configuration and enhance control over the deployment process.

Let's proceed step by step to create a package descriptor configuration and understand the process involved, from a minimal configuration to a fully qualified one.

### Create a configuration file

Add a new directory at the root of your project: `.gatling` and create a new file `.gatling/package.conf`.
This file will be in [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md) format.

```console
.
├── .gatling/
│   └── package.conf
└── src/
    ├── main/
    └── test/
```

No configuration file or an empty one is valid and will have the [Default Behavior]({{< ref "#default-behavior" >}}) described above.

### Configure the name of the package.

```hocon
gatling.enterprise.package {
  name = "My package name"
}
```

{{< alert warning >}}
See [Consistent deployment]({{< ref "#consistent-deployment-with-id" >}}) to change the name **without deploying a new package**.
{{< /alert >}}

### Assign the package to a team

As described in [Default Behavior]({{< ref "#default-behavior" >}}), the team is inferred from the [API Token]({{< ref "reference/collaborate/admin/api-tokens" >}}) when not specified.

You can configure the [Team]({{< ref "reference/collaborate/admin/teams" >}}), however, the [API Token]({{< ref "reference/collaborate/admin/api-tokens" >}}) must have the Configure permission for the specified team.

```hocon
gatling.enterprise.package {
  name = "My package name"
  team = "My team name" # or ID with team = "00000000-0000-0000-0000-000000000000"
}
```

### Deploy only selected simulations

To deploy only some of the simulations in your Gatling project, you can configure `simulations` for a given Package.

Only configured simulation classes will be deployed to Gatling Enterprise.

For example, a project with multiple simulations `com.example.SimulationA` and `io.gatling.SimulationB` , where you only want to deploy `com.example.SimulationA`.

```hocon
gatling.enterprise.package {
  name = "My package name"
  team = "My team name"
  simulations = [
    {
      simulation = "com.example.SimulationA"
    }
  ]
}
```

`simulation` is the only mandatory field of `simulations` when specified, other fields (see below) can still be inferred by [Default Behavior]({{< ref "#default-behavior" >}}).

### Consistent deployment with ID

{{< alert warning >}}
Packages and simulations are deployed according to their names. Therefore, any updates of the names made from the Web UI will result in a new deployment.
{{< /alert >}}

To avoid this, you can set the package and simulation IDs in the configuration.

These IDs will be logged during your first deployment, such as after your initial [Usage]({{< ref "#usage" >}}).

```
Package 'My package name' (id='00000000-0000-0000-0000-000000000000') deployed
Simulation 'com.example.SimulationA' (id='00000000-0000-0000-0000-000000000001') deployed
```

Then, you can freely update names without having new packages or simulations created:

```hocon
gatling.enterprise.package {
  id = "00000000-0000-0000-0000-000000000000"
  name = "My new package name"
  team = "My team name"
  simulations = [
    {
      id = "00000000-0000-0000-0000-000000000001"
      name = "My new simulation name"
      simulation = "com.example.SimulationA"
    }
  ]
}
```

### Simulation configuration {#simulation-config}

As we mention in the [Default Behavior]({{< ref "#default-behavior" >}}) section, a [Simulation]({{< ref "/reference/run-tests/simulations/test-as-code" >}}) includes more than just a simulation.

Each property of a [Simulation]({{< ref "/reference/run-tests/simulations/test-as-code" >}}), can be configured individually or left to default settings, allowing for customization either through configuration or via the Web UI.

```hocon
gatling.enterprise.package {
  # id = "00000000-0000-0000-0000-000000000000"
  name = "My package name"
  team = "My team name"
  simulations = [
    {
      # id = "00000000-0000-0000-0000-000000000001"
      name = "My simulation name"
      simulation = "com.example.SimulationA"
      locations = [
        {
          name: "Europe - Paris",
          size: 2,
          weight: 30
        },
        {
          name: "AP Pacific - Mumbai",
          size: 2,
          weight: 70
        }
      ]
      parameters {
        ignoreDefaults = false
        systemProperties {
          "com.example.prop.1" = "system prop 1"
          "com.example.prop.2" = "system prop 2"
        }
        environmentVariables {
          SIMULATION_ENV_VAR_1 = "environment 1"
          SIMULATION_ENV_VAR_2 = "environment 2"
        }
      }
      timeWindow {
        rampUp = 10
        rampDown = 10
      }
      stopCriteria = [
        {
          type = "meanCpu",
          timeFrameInSeconds = 10,
          threshold = {
            maxPercentage = 90
          }
        },
        {
          type = "globalResponseTime",
          timeFrameInSeconds = 10,
          threshold = {
            maxMilliseconds = 90,
            percentile = 50
          }
        },
        {
          type = "globalErrorRatio",
          timeFrameInSeconds = 10,
          threshold = {
            maxPercentage = 90
          }
        }
      ]
    }
  ]
}
```

**Properties:**

`locations` *(optional)* :

- `name` : Location name is one of the following:
    - `AP - Hong Kong`
    - `AP - Tokyo`
    - `AP Pacific - Mumbai`
    - `AP SouthEast - Sydney`
    - `Europe - Paris`
    - `Europe - Dublin`
    - `Europe - London`
    - `US East - N. Virginia`
    - `US West - N. California`
    - `US West - Oregon`
    - An existing [Private Location ID]({{< ref "/reference/deploy/private-locations/introduction" >}})
- `size` *(optional)* : The number of load generators to deploy for the location *(default: 1)*
- `weight` *(optional)* : The % of virtual users handled by the load generators of the location *(default: even weight)*

{{< alert warning >}}
Locations weights sum must be equal to 100
{{< /alert >}}

`useDedicatedIps` (optional) : A Boolean property controlling whether to use dedicated IPs.
- For a new simulation (i.e., first deployment): If useDedicatedIps is omitted, it defaults to false.
- For an already deployed simulation: If useDedicatedIps is omitted, the existing dedicated IP configuration remains unchanged.

`parameters` *(optional)* :

- `ignoreDefaults` *(optional)* : Ignore or not [Default Load Generator Parameters]({{< ref "/reference/deploy/private-locations/introduction" >}}) *(default: false, or existing)*
- `systemProperties` *(optional)* : [Java system properties]({{ < ref "/reference/run-tests/simulations/optional-config/load-generator-parameters" >}}) for the simulation *(default: empty, or existing)*
- `environmentVariables` *(optional)* : Environment variables for the simulation *(default: empty, or existing)*

{{< alert info >}}
System properties prefix with `sensitive.` will not be displayed on dashboard.
Environment variables prefix with `SENSITIVE_` will not be displayed on dashboard.
{{< /alert >}}

`timeWindow` *(optional)* :

- `rampUp` *(optional)* : number of second at the beginning of the test to ignore *(default: 0, or existing)*
- `rampDown` *(optional)* : number of second at the end of the test to ignore *(default: 0, or existing)*

`stopCriteria` *(optional)*
- `type`: `meanCpu`, `globalResponseTime` or `globalErrorRatio`
- `timeFrameInSeconds`: the timeframe during which the threshold must be reached to stop the simulation.
- `threshold`, if `globalResponseTime`:
  - `percentile`: defines the percentile from which you want to check the response time.
  - `maxMilliseconds`: the max response time, in milliseconds. Run stops if exceeded.
- `threshold`, if `meanCpu` or `globalErrorRation`
  - `maxPercentage`: percentage of mean Load Generators CPU usage or requests error rate at which you want the test to stop.


### Common settings for simulations

If you need to set up multiple simulations with similar settings, doing so individually can become time-consuming.

To simplify the process, a `default` section is available for `simulation`.

Consequently, both example simulations **`com.example.SimulationA`** and **`com.example.SimulationB`** can share the same basic settings, saving you time and effort.

```hocon
gatling.enterprise.package {
  # id = "00000000-0000-0000-0000-000000000000"
  name = "My package name"
  team = "My team name" # or ID with team = "00000000-0000-0000-0000-000000000000"
  default {
    simulation {
      locations = [
        {
          name: "Europe - Paris",
          size: 1
        }
      ]
      parameters {
        ignoreDefaults = false
        systemProperties {
          "com.example.prop.1" = "default value for system prop 1"
          "com.example.prop.2" = "default value for system prop 2"
        }
        environmentVariables {
          MY_SIMULATION_ENV_VAR_1 = "default value from environment 1"
          MY_SIMULATION_ENV_VAR_2 = "default value from environment 2"
        }
      }
      timeWindow {
        rampUp = 10
        rampDown = 10
      }
      stopCriteria = [
        {
          type = "meanCpu",
          timeFrameInSeconds = 10,
          threshold = {
            maxPercentage = 90
          }
        },
        {
          type = "globalResponseTime",
          timeFrameInSeconds = 10,
          threshold = {
            maxMilliseconds = 90,
            percentile = 50
          }
        },
        {
          type = "globalErrorRatio",
          timeFrameInSeconds = 10,
          threshold = {
            maxPercentage = 90
          }
        }
      ]
    }
  }
  simulations = [
    {
      # id = "00000000-0000-0000-0000-000000000001"
      simulation = "com.example.SimulationA"
    },
    {
      # id = "00000000-0000-0000-0000-000000000002"
      simulation = "com.example.SimulationB"
      parameters {
        ignoreDefaults = true
        systemProperties {
          "com.example.prop.1" = "override value from system prop 1"
        }
        environmentVariables {
          MY_SIMULATION_ENV_VAR_1 = "override value from environment 1"
        }
      }
      timeWindow {
        rampDown = 20
      }
      stopCriteria = [
        {
          type = "meanCpu",
          timeFrameInSeconds = 15,
          threshold = {
            maxPercentage = 95
          }
        }
      ]
    }
  ]
}
```

{{< alert info >}}
`ignoreDefaults` refer to [Default Load Generator Parameters]({{< ref "reference/deploy/private-locations/introduction" >}}), not to the `default` configuration block of `package.conf` file. 
{{< /alert >}}

For **`com.example.SimulationB`** in the example, its properties are combined with the default settings. However, priority is given to the settings specific to **`SimulationB`**

```hocon
{
  # id = "00000000-0000-0000-0000-000000000002"
  simulation = "com.example.SimulationB"
  parameters {
    ignoreDefaults = true
    systemProperties {
      "com.example.prop.1" = "override value for system prop 1"
      "com.example.prop.2" = "default value for system prop 2"
    }
    environmentVariables {
      MY_SIMULATION_ENV_VAR_1 = "override value from environment 1"
      MY_SIMULATION_ENV_VAR_2 = "default value from environment 2"
    }
  }
  timeWindow {
    rampUp = 10
    rampDown = 20
  }
  stopCriteria = [
    {
      type = "meanCpu",
      timeFrameInSeconds = 15,
      threshold = {
        maxPercentage = 95
      }
    }
  ]
}
```
