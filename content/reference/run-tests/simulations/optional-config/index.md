---
title: Optional configurations for simulations
seotitle: Optional configurations for simulations in Gatling Enterprise
description: Learn about the optional configurations available for simulations in Gatling Enterprise.
lead: Optional configurations for simulations in Gatling Enterprise.
date: 2025-07-30T10:29:36+00:00
---

## Optional configurations for simulations

In addition to the standard configuration options, Gatling Enterprise provides several optional configurations for simulations. These options allow you to customize the behavior of your simulations to better suit your testing needs.

### Load generator parameters

You can specify load generator parameters in your simulation configuration. This is useful for scenarios where you need to customize the behavior of the load generator, such as adjusting the number of virtual users or the ramp-up time.

This step allows you to define the Java system properties or JS parameters and environment variables used when running this particular simulation. Properties/variables entered here are in addition to the default parameters, unless you ignore the defaults. 

If have the same key in your simulation configuration and the the default load generator parameters table <!--link here--> the simulation value is used and overrides the default.

{{< alert tip >}}
JVM options, Java System Properties or JS parameters and environment variables will be saved in a snapshot that will be available in the run. This information will be visible by anyone who has read access.
You can exclude some system properties from being copied if you prefix them with `sensitive.`, and environment variables if you prefix them with `SENSITIVE_`.
{{< /alert >}}

{{< alert tip >}}
You can configure the `gatling.enterprise.groupedDomains` Java System property to group connection stats from multiple subdomains and avoid memory issues when hitting a very large number of subdomains.

For example, setting this property as `.foo.com, .bar.com` will consolidate stats for `sub1.foo.com`, `sub2.foo.com`, `sub1.bar.com`, `sub2.bar.com` into `*****.foo.com` and `*****.bar.com`.
{{< /alert >}}

{{< alert tip >}}
System properties can be retrieved in your Gatling simulation with `System.getProperty("YOUR_PROPERTY_KEY")`.

Environment variables can be retrieved in your Gatling simulation with `System.getEnv("YOUR_ENV_VAR_KEY")`.
{{< /alert >}}

### Time window {#time-window}

You can configure ramp-up and ramp-down time windows to be excluded from computed assertions. This is typically useful when you know that at the beginning of your test run you expect higher response times than when your system is warm (JIT compiler has kicked in, autoscaling has done its work, caches are filled, etc.) and don’t want them the warm-up time to cause your assertions to fail.

- **Ramp Up**: the number of seconds you want to exclude at the beginning of the run.
- **Ramp Down**: the number of seconds you want to exclude at the end of the run.

{{< alert tip >}}
Ramp up/down parameters will only be applied if the run duration is longer than the sum of the two.
{{< /alert >}}

{{< alert tip >}}
Ramp up/down parameters can also be specified with the [public API]({{< ref "/reference/api/" >}}) and the [package descriptor]({{< ref "/reference/run-tests/configuration-as-code/" >}}). 
{{< /alert >}}

### Stop criteria

In this step, you can configure specific stop criteria to end the run earlier if certain thresholds are exceeded. 
This is particularly useful for terminating test runs once key performance metrics exceed acceptable limits.

Each stop criterion must include:

- **Metric**: The metric for which the stop criterion is evaluated. (Mean CPU, Global Error Ratio or Global Response Time)
- **Threshold**: The value that, when reached or exceeded, will trigger the stop condition. (eg: over 30% / 300ms on 99.9 percentile)
- **Timeframe (in seconds)**: The period during which the **metric** must exceed the **threshold** for the entire duration to trigger the stop (eg: last 60 seconds).

You can base stop criteria on the following metrics:

- **Mean CPU Usage**: The average CPU usage of the load generators, measured as a percentage.
- **Global Error Ratio**: The percentage of failed requests across all test scenarios.
- **Global Response Time**: The response time of all requests, measured at a specific percentile, in milliseconds.

{{< alert tip >}}
If you set a stop criterion to monitor the **mean CPU** over a **timeframe of 60 seconds** with a **threshold of 80%**, 
the **run will stop if the mean CPU exceeds 80% for the last 60 seconds**.
{{< /alert >}}

### Acceptance criteria (no-code simulations only)

Gatling Enterprise allows you to define acceptance criteria for your simulations. This includes specifying thresholds for key performance indicators (KPIs) such as response time, throughput, and error rate. By setting clear acceptance criteria, you can ensure that your application meets the desired performance standards before it is deployed. For test-as-code simulations, these criteria can be defined in the simulation code itself, while for no-code simulations, they can be configured through the user interface.
