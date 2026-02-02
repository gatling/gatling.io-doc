---
menutitle: GitHub Actions
title: GitHub Actions integration
seotitle: GitHub Actions integration for Gatling Enterprise Edition
description: Learn how to configure the Gatling Enterprise Edition GitHub Action and run your simulations.
lead: Run your Gatling Enterprise Edition simulations from GitHub Actions.
aliases:
  - /reference/extensions/ci-cd/github-actions
  - /reference/integrations/ci-cd/github-actions/
date: 2022-01-04T15:00:00+00:00
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Purpose of this GitHub Action

This Action enables you to start a Gatling Enterprise Edition simulation directly from your GitHub Actions workflows. This plugin links a workflow with one and only one Gatling Enterprise Edition simulation.

This plugin doesn't create a new Gatling Enterprise Edition simulation, you have to create it using the Gatling Enterprise Edition Dashboard before.

On Gatling Enterprise Edition, you can do it using the options provided by our build tools plugins:

- [Maven]({{< ref "../build-tools/maven-plugin#running-your-simulations-on-gatling-enterprise" >}})
- [Gradle]({{< ref "../build-tools/gradle-plugin#running-your-simulations-on-gatling-enterprise" >}})
- [sbt]({{< ref "../build-tools/sbt-plugin#running-your-simulations-on-gatling-enterprise" >}})

Don't forget to check out [GitHub's official documentation](https://docs.github.com/en/actions) to learn how to write CI/CD workflows with GitHub Actions.

## Action coordinates

The Action is published with the following coordinates: `gatling/enterprise-action@v1`.

You can check out the latest releases available [from the GitHub project](https://github.com/gatling/enterprise-action/releases). You generally only need to specify the major version you want to use, currently `v1`.

## Pre-requisites

You must first create an API token. It will be used to authenticate with Gatling Enterprise Edition.

We recommend storing the API Token [in a GitHub encrypted secret](https://docs.github.com/en/actions/security-guides/encrypted-secrets#using-encrypted-secrets-in-a-workflow).
In the following examples, we assume the **API Token** is stored in a secret called `GATLING_ENTERPRISE_API_TOKEN`.

For Gatling Enterprise Edition, the [API token]({{< ref "reference/administration/api-tokens" >}}) needs the **Start** permission.

We also assume that you have already configured a simulation on Gatling Enterprise Edition. You can copy the simulation ID from the simulations list view. In the following examples, we will show the simulation ID as `test_00000000000000000000000000`.

See [Gatling Enterprise Edition documentation]({{< ref "reference/run-tests/simulations" >}}).

## Quickstart (minimal job configuration)

In this example, we configure a workflow which will only start a simulation as already configured and uploaded on Gatling Enterprise Edition. We use the `workflow_dispatch` trigger event, so that we can [run it manually](https://docs.github.com/en/actions/managing-workflow-runs/manually-running-a-workflow), but feel free to use what works for your use case.

```yaml
name: Run Gatling Enterprise Simulation

on:
  workflow_dispatch:
    inputs:
      simulation_id:
        type: string
        required: true

jobs:
  run:
    runs-on: ubuntu-latest
    steps:
      - name: Gatling Enterprise Action
        uses: gatling/enterprise-action@v1
        with:
          api_token: ${{ secrets.GATLING_ENTERPRISE_API_TOKEN }}
          simulation_id: ${{ inputs.simulation_id }}
```

Push this to your repository's default branch (otherwise, new `workflow_dispatch` workflows don't get detected). You can then run the workflow from you GitHub repository's Actions tab. Select the workflow's name from the menu on the left, and click on Run workflow.

{{< img src="quickstart_run_workflow.png" alt="Run workflow menu" >}}

## Configuration reference

Several configuration options are available as Action inputs. This Action also provides several outputs which you can access in the following steps of your workflow.

### Inputs

Example:

```yaml
steps:
  - uses: gatling/enterprise-action@v1
    with:
      api_token: ${{ secrets.GATLING_ENTERPRISE_API_TOKEN }}
      simulation_id: 'test_00000000000000000000000000'
      title: 'My run title'
      description: 'My run description'
      extra_system_properties: >
        {
          "sys_prop_1":"value 1",
          "sys_prop_2":42,
          "sys_prop_3":true
        }
      extra_environment_variables: >
        {
          "ENV_VAR_1":"value 1",
          "ENV_VAR_2":42,
          "ENV_VAR_3":true
        }
      fail_action_on_run_failure: true
      wait_for_run_end: true
      run_summary_enabled: true
      run_summary_initial_refresh_interval: 5
      run_summary_initial_refresh_count: 12
      run_summary_refresh_interval: 60
```

- `api_token` {{< badge danger >}}required{{< /badge >}} (unless using an environment variable named `GATLING_ENTERPRISE_API_TOKEN` instead): The API token used by the Action to authenticate with Gatling Enterprise Edition.

- `simulation_id` {{< badge danger >}}required{{< /badge >}}: The ID of the simulation as configured on Gatling Enterprise Edition.

- `title` {{< badge info >}}optional{{< /badge >}}: Specify a title for the new simulation run.

- `description` {{< badge info >}}optional{{< /badge >}}: Specify a description for the new simulation run.

- `extra_system_properties` {{< badge info >}}optional{{< /badge >}}: Additional Java system properties, will be merged with the simulation's configured system properties. Must be formatted as a JSON object containing the desired key/value pairs. Values can be strings, numbers or booleans.

- `extra_environment_variables` {{< badge info >}}optional{{< /badge >}}: Additional environment variables, will be merged with the simulation's configured environment variables. Must be formatted as a JSON object containing the desired key/value pairs. Values can be strings, numbers or booleans.

- `fail_action_on_run_failure` {{< badge info >}}optional{{< /badge >}} (defaults to `true`): If `true`, the Action will fail if the simulation run ends in an error (including failed assertions). Note: if set to `false` and the simulation ends in an error, some of the outputs may be missing (e.g. there will be no assertion results if the simulation crashed before the end).

- `wait_for_run_end` {{< badge info >}}optional{{< /badge >}} (defaults to `true`): If `true`, the Action will wait for the end of te simulation run on Gatling Enterprise Edition before terminating. Note: if set to `false`, some of the outputs may be missing (there will be no status nor assertion results).

- `run_summary_enabled` {{< badge info >}}optional{{< /badge >}} (defaults to `true`): Assuming `wait_for_run_end` is also true, will regularly log a summary of the ongoing run to the console until it finishes. See also the [logs section]({{< ref "#logs" >}}).

- `run_summary_initial_refresh_interval` {{< badge info >}}optional{{< /badge >}} (defaults to `5`): Initial interval before displaying a new summary of the ongoing run in the console, in seconds. Should be a multiple of 5 (otherwise it will be rounded up). Only used a limited number of times (set by `run_summary_initial_refresh_count`) before switching to the interval set by run_summary_refresh_interval. See also the [logs section]({{< ref "#logs" >}}).

- `run_summary_initial_refresh_count` {{< badge info >}}optional{{< /badge >}} (defaults to `12`): Number of times to use `run_summary_initial_refresh_interval` as the interval before displaying a new summary of the ongoing run in the console. After that, `run_summary_refresh_interval` will be used. This allows to avoid spamming the log output once the test run is well underway. See also the [logs section]({{< ref "#logs" >}}).

- `run_summary_refresh_interval` {{< badge info >}}optional{{< /badge >}} (defaults to `60`): Interval before displaying a new summary of the ongoing run in the console, in seconds. Should be a multiple of 5 (otherwise it will be rounded up). See also the [logs section]({{< ref "#logs" >}}).


### Outputs

Example:

```yaml
steps:
  - id: gatling-enterprise-action
    uses: gatling/enterprise-action@v1
    with:
      api_token: ${{ secrets.GATLING_ENTERPRISE_API_TOKEN }}
      simulation_id: 'test_00000000000000000000000000'
  - run: |
      echo "run_id=${{ steps.enterprise-action.outputs.run_id }}"
      echo "reports_url=${{ steps.enterprise-action.outputs.reports_url }}"
      echo "runs_url=${{ steps.enterprise-action.outputs.runs_url }}"
      echo "run_status_code=${{ steps.enterprise-action.outputs.run_status_code }}"
      echo "run_status_name=${{ steps.enterprise-action.outputs.run_status_name }}"
      echo "run_assertions=${{ steps.enterprise-action.outputs.run_assertions }}"
```

- `run_id`: The ID of the run started by this action.

- `reports_url`: The URL of the reports page for this run.

- `runs_url`: The URL of the runs history page for this simulation.

- `run_status_name`: The name of the run's final status (e.g. `Successful`, `AssertionsSuccessful`, `AssertionsFailed`, etc.).

- `run_status_code`: The code of the run's final status.

- `run_assertions`: The results of the run's assertions, as a JSON array.

### Logs

The action regularly logs a summary of the run's current status to the GitHub Action console. When the run ends, the Action logs the status of the run and the results of any assertions. Here's a very short duration example:

{{< img src="reference_logs.png" alt="A run's logs in the GitHub Actions console" >}}

By default, logs are printed every 5 seconds the first 12 times (i.e. during 60 seconds), then every 60 seconds. This can be adjusted using the inputs `run_summary_initial_refresh_interval`, `run_summary_initial_refresh_count`, and `run_summary_refresh_interval`. The ongoing logs can also be completely disabled using the input `run_summary_enabled: false`: in this case, only the final results will be printed.

### Cancellation

When the Action starts, it registers a post-execution, clean-up task in the workflow. If the Action fails or gets cancelled by a user, and if the simulation is still running on Gatling Enterprise Edition, this clean-up task will attempt to cancel the execution on Gatling Enterprise Edition.

## Sample use cases {{% badge cloud "Cloud" /%}} {#cloud-sample-use-cases}

### Build and run simulation

This workflow is defined in the GitHub repository which contains your Gatling simulation script built with one of our build tools plugins. In this example, every time the code on the `main` branch gets updated, we:

- build, package, and upload to Gatling Enterprise Edition the current version of the simulation script
- run the updated simulation on Gatling Enterprise Edition

Feel free to use different trigger events or to configure the other inputs and outputs for the Action as documented above, according to your own use case.

{{< include-file >}}
Maven: includes/use-case-build-and-run.maven.md
Maven Wrapper: includes/use-case-build-and-run.mavenw.md
Gradle: includes/use-case-build-and-run.gradle.md
Gradle Wrapper: includes/use-case-build-and-run.gradlew.md
sbt: includes/use-case-build-and-run.sbt.md
{{< /include-file >}}

### Build and update on every push, run weekly

This first workflow is defined in the GitHub repository which contains your Gatling simulation script built with one of our build tools plugins. In this example, every time the code on the `main` branch gets updated, we build, package, and upload to Gatling Enterprise Edition the current version of the simulation script.

{{< include-file >}}
Maven: includes/use-case-separate-build-run-1.maven.md
Maven Wrapper: includes/use-case-separate-build-run-1.mavenw.md
Gradle: includes/use-case-separate-build-run-1.gradle.md
Gradle Wrapper: includes/use-case-separate-build-run-1.gradlew.md
sbt: includes/use-case-separate-build-run-1.sbt.md
{{< /include-file >}}

This second workflow may be defined in the same repository or another one. Once a week (based on a CRON expression), we run the simulation on Gatling Enterprise Edition.

```yaml
name: Run Gatling Enterprise Simulation

# Execute the workflow every Sunday at 2 AM UTC, using POSIX CRON syntax. See:
# https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#schedule
on:
  schedule:
    - '0 2 * * 0'

jobs:
  run:
      # Run the simulation on Gatling Enterprise
      - name: Gatling Enterprise Action
        uses: gatling/enterprise-action@v1
        with:
          api_token: ${{ secrets.GATLING_ENTERPRISE_API_TOKEN }}
          simulation_id: 'test_00000000000000000000000000'
```
