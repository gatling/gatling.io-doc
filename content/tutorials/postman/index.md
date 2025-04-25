---
title: Create a simulation from Postman
seotitle: Gatling Postman getting started
description: Learn how to use your Postman collections in Gatling load tests.
lead: Use your Postman collections in Gatling load tests
date: 2024-12-15T15:22:00+02:00
---

{{< alert enterprise >}}
Enhanced usage of this feature is available with Gatling Enterprise. [Explore our plans to learn more](https://gatling.io/pricing?utm_source=docs).
{{< /alert >}}

{{< alert warning >}}
This guide is intended for Gatling versions `{{< var gatlingVersion >}}` and later.
{{< /alert >}}

## Introduction

The Gatling Postman component allows Postman users to use their preexisting Postman Collections, environments, and global variables to construct Gatling load tests. This feature: 

- reduces test authoring time, 
- increases consistency between functional and non-functional tests,
- improves collaboration between developers, operations, and QA teams.

To learn more about [Postman](https://www.postman.com), visit the [Postman official documentation](https://learning.postman.com/docs/). 

## Scope of Postman support

Postman support is only available for the Gatling JavaScript/TypeScript SDK; it is not available in Java, Kotlin, or Scala.

The Gatling Postman component is designed to import Postman collections and run straightforward requests. While it already supports most core functionalities, we're continuously enhancing its capabilities to meet your evolving needs. Here's a quick look at features we’re actively considering but are limited or not currently available:

- Postman Cloud Integration: Currently, collections and environments must be exported as JSON files for use. We’re exploring deeper integration possibilities.
- Script Execution: Support for pre-request and post-response scripts is not currently supported.
- Dynamic Variable Handling: Enhancements for modifying variable values during execution and injecting them from data files is not currently supported.
- Randomly Generated Data: Postman's dynamic variables used to generate sample data is not currently supported. 
- Request Authorization & Settings: Features like Basic Auth, Bearer Token handling, and advanced settings (e.g., disabling follow redirects). 
- File Uploads: For now, file uploads are supported by placing files in your Gatling project's `resources` folder.

More information about the current functionalities and limitations is available in the [reference documentation]({{< ref "/integrations/postman/" >}}).

## Installation

To get started with the Gatling Postman integration, either:

- download a demo project
- add the Postman dependency to an existing project

Both methods are detailed in the following sections. 

### Start with the demo project {#demo-project}

A [demo project](https://github.com/gatling/se-ecommerce-demo-gatling-tests/tree/main/postman/) is available with JavaScript and TypeScript test examples:

- [JavaScript](https://github.com/gatling/se-ecommerce-demo-gatling-tests/tree/main/postman/javascript)
- [TypeScript](https://github.com/gatling/se-ecommerce-demo-gatling-tests/tree/main/postman/typescript)

You can download the demo project and open it in your IDE to start working with the Gatling Postman integration. 

### Add the Gatling Postman dependency to an existing project {#dependency}

You can also add Gatling Postman to an existing Gatling project written in JavaScript or TypeScript. In that case, add the Gatling Postman dependency to set up your project:

```shell
npm install --save "@gatling.io/postman"
```

For more details on how to create a JavaScript or TypeScript project, check the
[dedicated tutorial]({{< ref "/tutorials/scripting-intro-js/" >}}).

## Import your Postman assets

To use your Postman assets in a Gatling project, copy the exported Postman collection, optionally environment, and global variables to the `resources` folder. 

## Write your simulation

The simulation consists of 3 parts. In the simplest form: 

1. Import statements that bring the core Gatling functionalities and the Postman component:

    ```javascript
    import { simulation, scenario, atOnceUsers } from "@gatling.io/core";
    import { http } from "@gatling.io/http";
    import { postman } from "@gatling.io/postman";
    ```

2. The virtual user scenario contained in a simulation function:
  
    ```javascript
    export default simulation((setUp) => {
    const httpProtocol = http;

    const collection = postman
      .fromResource("myCollection.postman_collection.json");

    const scn = collection.scenario("My Scenario");
    //...
    });
    ```

3. The injection profile that defines how virtual users are added to your scenario:

    ```javascript
    setUp(
      scn.injectOpen(
          constantUsersPerSec(0.1).during(50)
      ).protocols(httpProtocol)
   );
    ```
  
The following code example combines the 3 preceeding parts and is a complete Gatling simulation using the Postman component. The code example is annotated to explain how it works with the following numbers:

1. Import a Postman Collection.
2. Use the Postman Collection as a virtual user scenario.
3. Create an injection profile that adds 1 new user every 10 seconds for 50 seconds.

```javascript
import { simulation, constantUsersPerSec } from "@gatling.io/core";
import { http } from "@gatling.io/http"; 
import { postman } from "@gatling.io/postman";

export default simulation((setUp) => {
  const httpProtocol = http;

  const collection = postman
    .fromResource("myCollection.postman_collection.json"); //1

  const scn = collection.scenario("My Scenario", { pauses: 1 }); //2
  
  setUp(
    scn.injectOpen(
        constantUsersPerSec(0.1).during(50)
    ).protocols(httpProtocol) //3
 ); 
});
```
{{< alert tip >}}
You can develop more complex scenarios that, for example, blend Postman Collections and Gatling requests. To learn more about the complete SDK functionality, see the [reference documentation]({{< ref "/integrations/postman/" >}}). 
{{< /alert >}}

## Test execution

### Run the Simulation on Gatling Enterprise

You can package, deploy, and run your simulation using one of two approaches, depending on whether you prefer a manual or automated process.

#### Simple Manual Use Case { #test }

1. Manually generate the package by executing the following command locally on your developer’s workstation:

   ```console
   npx gatling enterprise-package
   ```

2. The above command will create a packaged **zip** file in your project's **target** directory.

3. From your Gatling Enterprise console, go to **Packages**. Create a new package specifying its name, team that owns it, select your packaged zip file for upload then click **Save**.

4. Go to **Simulations** > **Create a simulation** > **Test as code**. Under **Select a package**, choose the newly created package, then click **Create**.

5. Configure your simulation parameters:
   - Simulation name.
   - Under **Select your package and simulation** > **Simulation**, select your simulation class.
   - Under **Configure your locations**, choose the _Managed_ type and select a location based on your preference.
   - Click **Save and launch**.

#### Advanced Use Case with Automated Deployments (Configuration-as-Code)

Gatling Enterprise is a feature-rich SaaS platform that is designed for teams and organizations to get the most
out of load testing. With the [trial account](https://cloud.gatling.io/), you can upload and run your test with advanced configuration, reporting, and collaboration features.

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

### Run the Simulation locally for debugging {{% badge info "Optional" /%}} {#run-the-simulation-locally-for-debugging}

The open-source version of Gatling allows you to run simulations locally, generating load from your computer. Running a
new or modified simulation locally is often useful to ensure it works before launching it on Gatling Enterprise.
Using the JavaScript CLI, you can launch your test in interactive mode using the following approach:

1. Run the following command:

   ```console
   npx gatling run
   ```

2. Choose your postman simulation.

When the test has finished, there is an HTML link in the terminal that you can use to access the static report.

## License and limitations {#license}

**The Gatling Postman component is distributed under the
[Gatling Enterprise Component License]({{< ref "/project/licenses/enterprise-component" >}}).**

Gatling Postman can be used with both the [Open Source](https://gatling.io/products/) and
[Enterprise](https://gatling.io/products/) versions of Gatling.

Its usage is unlimited when running on [Gatling Enterprise](https://gatling.io/products/). When used with
[Gatling Open Source](https://gatling.io/products/), usage is limited to:

- 5 users maximum
- 5 minute duration tests

Limits after which the test stops.
