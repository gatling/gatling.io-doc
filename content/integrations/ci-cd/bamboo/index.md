---
menutitle: Bamboo
title: Bamboo Plugin
seotitle: Bamboo Plugin for Gatling Enterprise
description: Learn how to configure the Gatling Enterprise Bamboo plugin and run your simulations.
lead: Run your Gatling Enterprise simulations from your Bamboo CI.
aliases:
  - /reference/extensions/ci-cd/bamboo
  - /reference/integrations/ci-cd/bamboo
date: 2021-03-08T12:49:49+00:00
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Purpose of this plugin

This plugin enables you to start a Gatling Enterprise simulation directly from your Bamboo platform. This plugin links a Bamboo job with one Gatling Enterprise simulation.

This plugin doesn't create a new Gatling Enterprise simulation, you have to create it using the Gatling Enterprise Dashboard before.

## Installation

To download the plugin, you need to get the JAR file by clicking on the following button:

{{< button title="Download Bamboo Plugin" >}}
https://downloads.gatling.io/releases/frontline-bamboo-plugin/{{< var ciPluginsVersion >}}/frontline-bamboo-plugin-{{< var ciPluginsVersion >}}.jar
{{< /button >}}

You need to be connected as an administrator of your Bamboo application to install it. Go to *Bamboo Administration*, *Manage Apps*, *Upload app*, and choose the jar file.

{{< img src="installation.png" alt="Installation" >}}

## Configuration

The plugin needs some global configuration. Go to **Administration**, then **Global variables**.

Add new variables:

- `frontline.address` corresponds to the address of Gatling Enterprise (`https://cloud.gatling.io`).
- `frontline.apiAddress` corresponds to the public API (`https://api.gatling.io`).
- `frontline.apiTokenPassword` corresponds to the API token needed to authenticate to Gatling Enterprise:
  - the [API token]({{< ref "reference/collaborate/admin/api-tokens" >}}) needs the **Start** permission.

{{< alert info >}}
If you specify `https://cloud.gatling.io` for ``frontline.address``, you can leave out `frontline.apiAddress` as it will default to `https://api.gatling.io`. If you use an internal gateway to allow your Jenkins instance to call the Gatling Enterprise public API, you may need to specify your gateway address for `frontline.apiAddress`.
{{< /alert >}}

{{< img src="global-variable.png" alt="Global variable" >}}

## Job set-up

### Job configuration

Add a new build task called **Gatling Enterprise**. Choose in the Gatling Enterprise Simulation list the simulation you want to use.

{{< img src="configuration-task.png" alt="Task configuration" >}}

This job regularly prints a summary of the run's current status to the build logs. By default, the summary is printed every 5 seconds the first 12 times (i.e. for the first 60 seconds), and then every 60 seconds. You can configure this behavior (or disable it completely) in the job configuration.

### JUnit reporting

You can display the results of the Gatling Enterprise assertions with the JUnit Parser plugin.

Add a new build task called **JUnit Parser** and fill the **Specify custom results directories** input with the following line:

```
**/gatlingFrontLineJunitResults/*.xml
```

{{< alert danger >}}
Be sure to place this task always after the **Gatling Enterprise** task, or it won't read the results of the new run.
{{< /alert >}}

{{< alert danger >}}
If you don't have any assertions in your Gatling simulation, the JUnit task will fail.
{{< /alert >}}

{{< img src="configuration-junit.png" alt="JUnit configuration" >}}

## Usage

A new Gatling Enterprise simulation will be started every time the job is run. Check the logs to see the simulation progress. If the simulation ran successfully, it will look like the following:

If the Gatling Enterprise deployment fails (i.e. because of a shortage of available hosts), the plugin will retry 3 times to redeploy the simulation.

{{< img src="console-output.png" alt="Console output" >}}

Live metrics will be displayed in the console, and in the build summary.

{{< img src="results.png" alt="Results" >}}
