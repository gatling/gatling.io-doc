---
title: Override the injection profile
seotitle: Override the injection profile for a simulation in Gatling Enterprise
description: Learn how to override the injection profile of a simulation in Gatling Enterprise to adjust the load pattern without modifying or repackaging your simulation code.
lead: Override the injection profile of a test-as-code or build-from-source simulation to adjust your load pattern without modifying or repackaging your simulation code.
date: 2026-04-20T00:00:00+00:00
---

## Activate the override

The **Override your injection profile** step appears in the simulation creation and edit form (step 3). It is optional and collapsed by default.

When no override is configured, the banner indicates that the injection profile comes from your simulation code. Click **Override injection profile** to open the configuration form.

{{< alert warning >}}
The configured injection profile **completely replaces** the injection profile defined in your simulation code. Only the scenario execution logic from your code is preserved.
{{< /alert >}}

To revert to the code-defined profile, click **Use injection profile defined in code**.

{{< alert info >}}
This feature is available for test-as-code (packaged) and build-from-source simulations only.
{{< /alert >}}

## Select the injection model

Two injection models are available:

- **Open model**: you control the injection rate (users per second). Use this when you want to test how your system handles a specific arrival rate.
- **Closed model**: you control the number of concurrent users. Use this when you want to maintain a constant pool of active users. Don’t reason in terms of concurrent users if your system can’t push excess traffic into a queue.

For more information on the two models, see [Workload models]({{< ref "/testing-concepts/workload-models/" >}}).

## Define scenarios and injection steps

### Name your scenarios

You must define one injection profile per test scenario.

- **Single-scenario simulations**: the scenario name field is disabled. The profile applies automatically to the single scenario in your simulation.
- **Multi-scenario simulations**: you must enter a name for each scenario. Names must match the scenario names defined in your simulation code exactly, including case.

{{< alert warning >}}
Each profile must reference an existing scenario name. A mismatch in count or naming causes the run to fail with a **Broken** status. Check the run logs for the list of scenario names found in your simulation.
{{< /alert >}}

You can add up to **50 injection steps** per scenario. Click **Add another scenario** to define profiles for additional scenarios.

### Start from a template

For the open model, five templates are available to pre-populate a set of injection steps. No templates are available for the closed model.

After selecting a template, you can modify, add, or remove individual steps. The template is no longer applied once you change a step type or the number of steps.

**Ramp + plateau**: Gradual ramp then steady state

1. Ramp Users/sec: 0 → 10 over 60s
2. Constant Users/sec: 10 for 300s

**Staircase**: Stepped plateaus with ramps between

1. Ramp Users/sec: 0 → 5 over 30s
2. Constant Users/sec: 5 for 60s
3. Ramp Users/sec: 5 → 10 over 30s
4. Constant Users/sec: 10 for 60s
5. Ramp Users/sec: 10 → 20 over 30s
6. Constant Users/sec: 20 for 60s
7. Ramp Users/sec: 20 → 40 over 30s
8. Constant Users/sec: 40 for 60s

**Long plateau**: Ramp then hours-long steady state

1. Ramp Users/sec: 0 → 10 over 60s
2. Constant Users/sec: 10 for 14,400s (4h)

**Spike**: Steady baseline with a sudden burst

1. Constant Users/sec: 5 for 120s
2. Stress Peak Users: 500 over 30s
3. Constant Users/sec: 5 for 120s

**Single user**: One user, one pass

1. At Once Users: 1

### Available step types: open model

| Step type              | Description                                       | Parameters                                               |
|------------------------|---------------------------------------------------|----------------------------------------------------------|
| **At Once Users**      | Inject a fixed number of users simultaneously     | Users (int > 0)                                          |
| **Ramp Users**         | Inject users spread evenly over a duration        | Users (int > 0), Duration (s) (> 0)                      |
| **Constant Users/sec** | Inject users at a constant rate per second        | Rate (users/s > 0), Duration (s) (> 0)                   |
| **Ramp Users/sec**     | Ramp the injection rate from one value to another | From (users/s ≥ 0), To (users/s ≥ 0), Duration (s) (> 0) |
| **Stress Peak Users**  | Inject users following a Heaviside step function  | Users (int > 0), Duration (s) (> 0)                      |

### Available step types: closed model

| Step type                     | Description                                       | Parameters                                       |
|-------------------------------|---------------------------------------------------|--------------------------------------------------|
| **Constant Concurrent Users** | Maintain a constant number of concurrent users    | Users (int > 0), Duration (s) (> 0)              |
| **Ramp Concurrent Users**     | Ramp the concurrent user count between two values | From (int ≥ 0), To (int ≥ 0), Duration (s) (> 0) |

## View injection profile details

The injection profile summary appears in two places in the UI once the simulation is saved.

**Simulation details**: The **Injection profile** field in the header shows:
- `Defined in code`: no override is configured.
- `X scenario(s), Y steps (open)` or `X scenario(s), Y steps (closed)`: an override is active, where X is the number of configured scenarios and Y is the total number of injection steps.

**Run summary**: The injection profile shown in the run summary reflects the profile that was used for that specific run. It may differ from the current simulation configuration if the simulation was edited after the run.