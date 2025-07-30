---
title: Create simulations from packaged tests
seotitle: Create simulations from packaged tests in Gatling Enterprise
description: Learn how to create simulations from packaged tests in Gatling Enterprise.
lead: Create simulations from packaged tests in Gatling Enterprise.

---

## Creating a test-as-code simulation

Click the **Create a simulation** button on the Simulations view or the **Get started with test-as-code simulations** button if the Simulations view is empty. This opens the simulation creation modal:

{{< img src="simulation-creation-modal.png" alt="Simulation creation modal" >}}

Once you've selected your package, click the **Create** button to configure your simulation

{{< alert warning >}}
Gatling Enterprise has a hard limit for run durations of 7 days and will stop any test running for longer than that.
This limit exists for both performance reasons (to avoid data growing too large to be presented in the dashboard) and security
reasons (to avoid a forgotten test running forever).
{{< /alert >}}

### Set the general parameters

In this step, define the simulation's general parameters:

- **Name**: the name that will appear in the simulations table.
- **Package**: the actual package the simulation runs.
- **Simulation**: the simulation to run from this package.

### Configure the load generator locations {#locations-configuration}

Configure the Gatling Enterprise load generator location(s).

You can use: 
- managed locations provided by Gatling Enterprise
- [private locations]({{< ref "/reference/deploy/private-locations/introduction" >}})
- [dedicated IP addresses]({{< ref "dedicated-ips" >}}) for your managed load generators.

{{< alert info >}}
It is not currently possible to mix managed, private locations, and dedicated IPs in the same simulation.
{{< /alert >}}

Managed location load generators have the following specifications:

- 4 cores
- 8GB of RAM
- bandwidth up to 10 Gbit/s

Gatling Enterprise managed locations are available in the following regions:

- AP Pacific (Hong kong)
- AP Pacific (Tokyo)
- AP Pacific (Mumbai)
- AP SouthEast (Sydney)
- Europe (Dublin)
- Europe (London)
- Europe (Paris)
- SA East (São Paulo)
- US East (N. Virginia)
- US West (N. California)
- US West (Oregon)

If you want to use private locations, please refer to the [specific documentation]({{< ref "/reference/deploy/private-locations/introduction" >}}).

To get the best results from your simulation you should select the load generator locations that best match your user base.

{{< img src="step2.png" alt="Create simulation" >}}

- **Location**: defines the locations to be used when initiating the Gatling Enterprise load generators.
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
