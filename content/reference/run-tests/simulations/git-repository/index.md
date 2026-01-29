---
title: Create simulations from a git repository
menutitle: Create simulations from git repositories
seotitle: Create simulations from git repositories in Gatling Enterprise Edition
description: Learn how to create simulations from git repositories in Gatling Enterprise Edition.
lead: Create simulations from git repositories in Gatling Enterprise Edition.
date: 2025-07-30T10:29:36+00:00
---

## Requirements

A control-plane that supports [Private Locations - Build From Git]({{< ref "reference/deploy/private-locations/introduction" >}}) is required.

## Create simulations from a git repository

Use the **Create a simulation** button either coming from the Simulations page or the Getting started guide to open the simulation creation modal.

To create a simulation from a git repository:

1. Select **Build from source** in the modal.
2. Select your git repository or import a new one.
3. Click **+ Create**.

### Import a new git repository

To import a new git repository, click on the **Create** button in the modal and provide:

- a **name** for the repository,
- a team to associate it with,
- the **repository URL**.

Click **Create** to import the repository.

{{< alert warning >}}
Gatling Enterprise Edition has a hard limit for run durations of 7 days and will stop any test running for longer than that.
This limit exists for both performance reasons (to avoid data growing too large to be presented in the dashboard) and security
reasons (to avoid a forgotten test running forever).
{{< /alert >}}

### Configure the general parameters

You need to configure the general parameters:
- **Name**: the name that will appear in the simulations table.
- **Source repository**: the git repository to use as the source for the simulation.
- (optional) **Branch**: the branch to use (defaults to the repository default branch).
- (optional) **Working directory**: the directory to use as the working directory (defaults to the repository root).
- **Build tool**: Your project build tool.
- **Simulation class**: the class to use as the simulation.
  - JVM projects: Enter the fully qualified name (example: `io.gatling.DemoSimulation`)
  - JavaScript projects: Use the simulation name (example: `demoSimulation` for `demoSimulation.gatling.js`)


#### Custom Build Command

Custom build commands must be enabled by a global administrator from [organization settings]({{< ref "reference/collaborate/user/organization#custom-build-command" >}}).

{{< alert warning >}}
Custom build commands require a minimum control-plane version [2026.4.1-builder](https://hub.docker.com/layers/gatlingcorp/control-plane/2026.4.1-builder),
though we recommend using the [latest-builder](https://hub.docker.com/layers/gatlingcorp/control-plane/latest-builder) version.
{{< /alert >}}

You need to configure:
- **Command**: the command used to package your simulations for Gatling Enterprise Edition. 
(see packaging section of your [build tool]({{< ref "integrations/build-tools">}}))
- **Package format**: the format of the package, either `JVM` (for Maven, Gradle and sbt) or `JS` (for JavaScript).

### Configure the load generator locations

Configure the Gatling Enterprise Edition load generator locations. Build from a git repository is only compatible with Private locations.

- **Location**: defines the locations to be used when initiating the Gatling Enterprise Edition load generators.
- **Number of load generators**: number of load generators for this location.
- **Weight distribution**: by default, every load generator will produce the same load. If enabled, you must set the weight in % for each location (e.g. the first location does 20% of the requests, and the second does 80%). The sum of all weights must be 100%.

You can add several locations with different numbers of load generators to run your simulation.

After this step, you can save the simulation, or continue with optional configurations.
 
### Apply optional configurations

The following configurations are optional, further details about the available options and how configure each option are available in the [Optional configurations for simulations]({{< ref "/reference/run-tests/simulations/optional-config">}}) documentation.

- Set load generator parameters
- Set acceptance criteria
- Specify a time window
- Add stop criteria
