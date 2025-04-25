---
menutitle: Create a simulation with JavaScript
title: Create your first JavaScript-based simulation
description: "Get started with Gatling and JavaScript: install, write your first load test, and execute it."
lead: Learn how to get started with Gatling and create a Gatling simulation with JavaScript.
date: 2023-12-16T18:30:56+02:00
---

{{< alert warning >}}
This guide is only intended for the Gatling JavaScript SDK version `{{< var gatlingJsVersion >}}`.
{{< /alert >}}

Gatling is a highly flexible load-testing platform. You can write load tests in Java, Kotlin, Scala, JavaScript, and TypeScript, or use our [no-code feature](https://gatling.io/features/no-code-generator/) with Gatling Enterprise.

In this guide, we cover a "Hello world"-style example for JavaScript of how to:

- [install and setup your local dev environment]({{< ref "#install-gatling" >}}),
- [write your first simulation]({{< ref "#simulation-construction" >}}),
- [Package and upload your simulation to Gatling Enterprise]({{< ref "#package" >}}),
- [Create and run a new test on Gatling Enterprise]({{< ref "#test" >}})
- [test your simulation locally]({{< ref "#run-local" >}}).

{{< alert tip >}}
Join the [Gatling Community Forum](https://community.gatling.io) to discuss load testing with other users. Please try to find answers in the Documentation before asking for help.
{{< /alert >}}

## Setup

This section guides you through installation and setting up your developer environment. This guide uses JavaScript and the gatling demo project. The JavaScript SDK is currently available for the `HTTP` protocol only.

### Sign up for Gatling Enterprise

Gatling Enterprise is a fully managed SaaS solution for load testing. Sign up for a [trial account](https://cloud.gatling.io/) to run your first test on Gatling Enterprise. The [Gatling website](https://gatling.io/features) has a full list of Enterprise features.

### Clone Gatling demo repository { #install-gatling }

{{< alert info >}}
**Prerequisites**

- [Node.js](https://nodejs.org/) v18 or later (LTS versions only) and npm v8 or later.
  {{< /alert >}}

Then, use the following procedure to install Gatling:

1. Clone the following [repository](https://github.com/gatling/se-ecommerce-demo-gatling-tests).

2. Open the project in your IDE or terminal.
3. Navigate to the `/javascript` folder.
4. Run `npm install` to install the packages and dependencies including the `gatling` command.

## Simulation construction

This guide introduces the basic Gatling HTTP features. Gatling provides a cloud-hosted web application
[https://ecomm.gatling.io](https://ecomm.gatling.io) for running sample simulations. You'll learn how to construct simulations
using the JavaScript SDK.

### Learn the simulation components

A Gatling simulation consists of the following:

- Importing Gatling functions.
- Configuring the protocol (commonly HTTP).
- Describing a scenario.
- Setting up the injection profile (virtual users profile).

The following procedure teaches you to develop the simulation from each constituent component. If you want to skip ahead
and copy the final simulation, jump to [Test execution]({{< ref "#test-execution" >}}). Learn more about simulations in the
[Documentation]({{< ref "/concepts/simulation" >}}).

#### Set up the file

To set up the test file use the following procedure:

1. Navigate to and open `javascript/src/basicSimulation.gatling.js`.
2. Modify the simulation by deleting everything below line 2 `import { http } from "@gatling.io/http";`.
3. The simulation should now look like the following:

{{< include-code "ScriptingIntro1Sample#setup-the-file" ts >}}

#### Define the `Simulation` function

The `simulation` function takes the `setUp` function as an argument, which is used to write a script. To add the `simulation` function, after the import statements, add:

{{< include-code "ScriptingIntro1Sample#extend-the-simulation-function" ts >}}

#### Define an HTTP protocol

Inside the `simulation` function, define an HTTP protocol. Learn about all of the
`HttpProtocolBuilder` options in the [Documentation]({{< ref "/reference/script/http/protocol" >}}). For
this example, the `baseUrl` property is hardcoded as the Gatling e-commerce test site, and the `acceptHeader` is set to `application/json`. Add the HTTP protocol:

{{< include-code "ScriptingIntro2Sample#define-the-protocol-class" ts >}}

#### Write the scenario

The next step is to describe the user journey. For a web application, this usually consists of a user arriving at the application and then a series of interactions with the application. The following scenario performs a get request to `https://api-ecomm.gatling.io/session` to retrieve the current user session. Add the scenario:

{{< include-code "ScriptingIntro3Sample#write-the-scenario" ts >}}

See the [Documentation]({{< ref "/concepts/scenario" >}}) for the available scenario
components.

#### Define the injection profile

The final component of a Gatling simulation is the injection profile. In your simulation you must call the `setUp` function exactly once to configure the injection profile. If you have several scenarios, each needs its own injection profile.

The following example adds 2 users per second for 60 seconds and each user executes the scenario we defined in [Write the Scenario]({{< ref="#write-the-scenario" >}}). See the [Documentation]({{< ref "/concepts/injection" >}}) for all of the injection profile options.

{{< include-code "ScriptingIntro4Sample#define-the-injection-profile" ts >}}

Congrats! You have written your first Gatling simulation. The next step is to learn how to run the simulation.

## Test execution

Now, you should have a completed simulation that looks like the following:

{{< include-code "BasicSimulation#full-example" ts >}}

### Package, upload and run your simulation to Gatling Enterprise { #package }

You can package, deploy, and run your simulation using one of two approaches, depending on whether you prefer a manual or automated process.

#### Simple Manual Use Case { #test }

1. Manually generate the package by executing the following command locally on your developer’s workstation:

   ```console
   npx gatling enterprise-package
   ```

2. The above command will create a packaged `zip` file in your project's **target** directory.

3. From your Gatling Enterprise console, go to **Packages**. Create a new package specifying its name, team that owns it, select your packaged zip file for upload then click **Save**.

4. Go to **Simulations** > **Create a simulation** > **Test as code**. Under **Select a package**, choose the newly created package, then click **Create**.

5. Configure your simulation parameters:
   - Simulation name.
   - Under **Select your package and simulation** > **Simulation**, select your simulation class.
   - Under **Configure your locations**, choose the _Managed_ type and select a location based on your preference.
   - Click **Save and launch**.

#### Advanced Use Case with Automated Deployments (Configuration-as-Code)

Gatling Enterprise is a feature-rich SaaS platform that is designed for teams and organizations to get the most
out of load testing. With the trial account, you created in the [Prerequisites section]({{< ref "#install-gatling" >}}), you can upload and run your test with advanced configuration, reporting, and collaboration features.

From Gatling 3.11 packaging and running simulations on Gatling Enterprise is simplified by using [configuration as code]({{< ref "reference/run-tests/configuration-as-code" >}}). In this tutorial, we only use the default configuration to demonstrate deploying your project. You can learn more about customizing your configuration with our [configuration-as-code guide]({{< ref "guides/ci-cd-automations/config-as-code" >}}).

To deploy and run your simulation on Gatling Enterprise, use the following procedure:

1. Generate an [API token]({{< ref "/reference/collaborate/admin/api-tokens" >}}) with the `Configure` permission in your Gatling Enterprise account.
2. Add the API token to your current terminal session by replacing `<your-API-token>` with the API token generated in step 1 and running the following command:

   {{< platform-toggle >}}
   Linux/MacOS: export GATLING_ENTERPRISE_API_TOKEN=<your-API-token>
   Windows: set GATLING_ENTERPRISE_API_TOKEN=<your-API-token>
   {{</ platform-toggle >}}

3. Run one of the following two commands according to your needs:

   - To deploy your package **and** start the simulation, run:

     ```console
     npx gatling enterprise-start --enterprise-simulation="<simulation name>"
     ```

   - To deploy your package without starting a run:

     ```console
     npx gatling enterprise-deploy
     ```

Watch the Simulation deploy automatically and generate real-time reports.

### Test the simulation locally {{% badge info "Optional" /%}} {#run-local}

The open-source version of Gatling allows you to run simulations locally, generating load from your computer. Running a
new or modified simulation locally is often useful to ensure it works before launching it on Gatling Enterprise.
Using the JavaScript CLI, you can launch your test in interactive mode using the following approach:

Using the terminal, you can launch your test with the following command in the `javascript` project directory:

1. In the `javascript` directory, run the following command:

   ```console
   npx gatling run
   ```

2. Choose `[2] basicSimulation`.

When the test has finished, there is an HTML link in the terminal that you can use to access the static report.
