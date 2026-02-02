---
menutitle: TeamCity
title: TeamCity Plugin
seotitle: TeamCity Plugin for Gatling Enterprise Edition
description: Learn how to configure the Gatling Enterprise Edition TeamCity plugin and run your simulations.
lead: Run your Gatling Enterprise Edition simulations from your TeamCity CI.
aliases:
  - /reference/extensions/ci-cd/teamcity
  - /reference/integrations/ci-cd/teamcity
date: 2021-03-08T12:50:20+00:00
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Purpose of this plugin

This plugin allows you to start a Gatling Enterprise Edition simulation directly from your TeamCity platform. This plugin links a TeamCity plan with one and only one Gatling Enterprise Edition simulation.

This plugin doesn't create a new Gatling Enterprise Edition simulation, you have to create it manually before.

## Installation

To download the plugin, you need to get the ZIP file by clicking on the following button:

{{< button title="Download Teamcity Plugin" >}}
https://github.com/gatling/gatling-enterprise-ci-plugins/releases/download/v{{< var gatlingCiPluginsVersion >}}/gatling-enterprise-teamcity-plugin-{{< var gatlingCiPluginsVersion >}}.zip
{{< /button >}}

You need to be connected as an administrator of your TeamCity application to install it. Navigate to **Administration**, **Plugins**, **Upload plugin zip**, and choose the downloaded zip file.

{{< img src="upload-plugin.png" alt="Upload plugin" >}}

Once the plugin is uploaded, you need to enable it.

## Configuration

The plugin needs a global configuration. Go to **Administration**, then **gatling-enterprise-teamcity-plugin**:

- the **Gatling Enterprise Edition Address** is the address of Gatling Enterprise Edition (`https://cloud.gatling.io`).
- the **Gatling Enterprise Edition API Address** is for the public API (`https://api.gatling.io`).
- the **Gatling Enterprise Edition API Token** is needed to authenticate to Gatling Enterprise Edition:
  - the [API token]({{< ref "reference/administration/api-tokens" >}}) needs the **Start** permission.

{{< alert info >}}
If you specify the **Address** `https://cloud.gatling.io`, you can leave the **API Address** field blank as it will default to `https://api.gatling.io`. If you use an internal gateway to allow your Jenkins instance to call the Gatling Enterprise public API, you may need to specify your gateway address as the **API Address**.
{{< /alert >}}

{{< img src="administration.png" alt="" >}}

## Plan set-up

Add a new build step called **Gatling Enterprise Edition Launcher**. Choose in the Simulation list the simulation you want to monitor. You need to configure the global properties of the plugin, and create at least a simulation on Gatling Enterprise Edition to do this step.

{{< img src="configuration.png" alt="Configuration" >}}

This step regularly prints a summary of the run's current status to the build logs. By default, the summary is printed every 5 seconds the first 12 times (i.e. for the first 60 seconds), and then every 60 seconds. You can configure this behavior (or disable it completely) in the step configuration.

### JUnit reporting

You can display the results of the Gatling Enterprise Edition assertions as a JUnit Test.

Add a new build feature called **XML report processing**. Choose **Ant JUnit** as report type, and enter in the **Monitoring rules** input the following line:

`gatlingEnterpriseJunitResults/*.xml`

{{< img src="junit.png" alt="JUnit" >}}

## Usage

A new Gatling Enterprise Edition simulation will be started every time the job is run. Check the Console Log to see the advancement of the simulation. If the simulation ran successfully, it will look like the following:

{{< img src="log.png" alt="Console Log" >}}

When the job run is finished, you will be able to see on the **Gatling Enterprise Edition Results** tab, the summary of the Gatling Enterprise Edition simulation.

{{< img src="display-results.png" alt="Display results" >}}
