---
title: Create no-code simulations
seotitle: Create no-code simulations in Gatling Enterprise Edition
description: Learn how to create no-code simulations in Gatling Enterprise Edition.
lead: Create no-code simulations in Gatling Enterprise Edition.
date: 2025-07-30T10:29:36+00:00
---

## Creating a no-code simulation

From the Simulations view, click **Create a simulation** to open the modal, then select **No-code** and click **Create**. This opens the no-code simulation creation modal, where you can configure your simulation without writing any code. The no-code test builder allows you to create HTTP requests, define user scenarios, and set up injection profiles using a user-friendly interface.

### Name your simulation (optional)

Enter a name for your simulation to help identify it later. This is optional, and if you leave it blank, a default name is generated.

### Create a no-code user scenario

In this step, you can create a user scenario by adding requests. You can:

- add a request by clicking the **Add request** button and entering the request details in the form. 
- select a request type (GET, POST, etc.)
- add request bodies as needed
- set inter-request pauses by clicking the **Optional: configure pauses** accordion and entering the pause duration in seconds
- modify a request
- delete a request

### Create a no-code injection profile

Select from the 3 available injection profiles:

- Capacity test: simulates a large number of users to determine the system's maximum capacity.
- Stress test: pushes the system beyond its limits to identify breaking points.
- Soak test: evaluates system performance under sustained load over an extended period.

Set the time parameters for the injection profile:

- Test duration: the total time the test will run.
- Initial user arrival rate: the number of users that will start the test at the beginning (Capacity test only).
- Final user arrival rate: the number of users that will be added at the end of the test (Capacity test only).
- Total injected users: the total number of users that will be injected during the test (Stress test only).
- Constant user arrival rate: the number of users that will be injected at a constant rate throughout the test (Soak test only).

### Configure the no-code simulation

#### Select the load generator location(s)

You can either use the managed locations provided by Gatling Enterprise Edition, your own [private locations]({{< ref "/reference/deploy/private-locations/introduction" >}}), or [dedicated IP addresses]({{< ref "dedicated-ips" >}}) for your load generators. The no-code test builder defaults to 1 load generator deployed in the Paris region.

- **Location**: defines the locations to be used when initiating the Gatling Enterprise Edition load generators.
- **Number of load generators**: number of load generators for this location.
- **Weight distribution**: by default, every load generator will produce the same load. If enabled, you must set the weight in % for each location (e.g. the first location does 20% of the requests, and the second does 80%). The sum of all weights must be 100%.

Managed location load generators have the following specifications:

- 4 cores
- 8GB of RAM
- bandwidth up to 10 Gbit/s

Gatling Enterprise Edition managed locations are available in the following regions:

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

{{< alert info >}}
When using private locations or dedicated IP addresses, ensure that your load generators can communicate with the target application.
{{< /alert >}}

#### Apply optional configurations

The following configurations options can be used to customize your simulation, additional details are available in the [Optional configurations for simulations]({{< ref "/reference/run-tests/simulations/optional-config">}}) documentation.

- Set acceptance criteria
- Specify a time window
- Add stop criteria

### Save and launch your simulation

Once you've configured your no-code simulation, click the **Save and Launch** button to save your simulation and start the test.
