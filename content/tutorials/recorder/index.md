---
menutitle: Introduction to the Recorder
title: Introduction to the Gatling Recorder
aliases:
  - quickstart
description: "Learn the basics about Gatling: installing, using the Recorder to generate a basic raw test and how to execute it."
lead: Learn Gatling concepts and use the Recorder to create a runnable Gatling simulation.
date: 2021-04-20T18:30:56+02:00
---

## Introduction

{{< alert warning >}}
This tutorial is intended for Gatling versions `{{< var gatlingVersion >}}` and later. 
{{< /alert >}}

The Gatling Recorder allows you to capture browser-based actions to create a realistic user scenario for load testing. The Recorder application is launched from Gatling, using Maven, Gradle, sbt or the JavaScript CLI.  

In this tutorial, we use Gatling to load test a simple cloud-hosted web server and introduce you to the basic elements of the Recorder. We strongly recommend completing one of the following introductory guides according to your language of preference before starting to work with the Recorder:
- [Introduction to scripting tutorial with Java]({{< ref "/tutorials/scripting-intro" >}}). 
- [Introduction to scripting tutorial with Javascript]({{< ref "/tutorials/scripting-intro-js" >}}). 

This tutorial showcases the Gatling recorder using two options: **Java** SDK with the **Maven** plugin and the **JavaScript** SDK with **JavaScript CLI**.

{{< alert tip >}}
Join the [Gatling Community Forum](https://community.gatling.io) to discuss load testing with other users. Please try to find answers in the documentation before asking for help.
{{< /alert >}}

## Plan the user scenario

This tutorial uses a sample _e-commerce_ website, which is deployed at the URL: [https://ecomm.gatling.io](https://ecomm.gatling.io). This application is for demonstration purposes and is read-only. Please be kind and only run small proof of concept load tests against the site.

To test the performance of the _e-commerce_ application, create scenarios representative of what happens when users navigate the site.

The following is an example of what a real user might do with the application. 

1. The user arrives at the application.
2. The user clicks on the login button.
3. The user goes to the login page.
4. The user logs in.
5. The user adds the first product to his cart.

### Clone Gatling demo repository { #install-gatling }

{{< alert info >}}
**Sample project prerequisites**

- **For Java**: Java 17 or 21 64-bit OpenJDK LTS (Long Term Support) version installed on your local machine. The sample project leverages features introduced in Java 17, making it the minimum required version. We recommend the [Azul JDK](https://www.azul.com/downloads/?package=jdk#zulu).
- **For JavaScript**: [Node.js](https://nodejs.org/) v18 or later (LTS versions only) and npm v8 or later.
  {{< /alert >}}

{{< alert info >}}
**Gatling SDK prerequisites**

- **For Java**: Java 11, 17 or 21 64-bit OpenJDK LTS (Long Term Support) version installed on your local machine. While the sample project requires Java 17, The Gatling SDK support extends back to Java 11. We recommend the [Azul JDK](https://www.azul.com/downloads/?package=jdk#zulu).
- **For JavaScript**: [Node.js](https://nodejs.org/) v18 or later (LTS versions only) and npm v8 or later.
  {{< /alert >}}

{{< alert info >}}
Additionally, the tutorial uses the Mozilla FireFox browser to create the Gatling Script and Gatling Enterprise to run tests with dedicated load generators and enhanced data reporting features. Kindly check the following prerequisites:

- [Create a Gatling Enterprise trial account](https://cloud.gatling.io/)
- [Configure your web browser]({{< ref "/reference/script/protocols/http/recorder/#configuration" >}})

  {{< /alert >}}

1. Clone the sample project [repository](https://github.com/gatling/se-ecommerce-demo-gatling-tests).

2. Open the project in your IDE or terminal.

3. For Java, navigate to `/java/maven`. For JavaScript, navigate go to `/javascript`.

## Launch the Recorder

Launch the recorder using the following command:

Maven:
{{< platform-toggle >}}
Linux/MacOS: ./mvnw gatling:recorder
Windows: mvnw.cmd gatling:recorder
{{</ platform-toggle >}}

JavaScript CLI:
```console
npm run recorder
```

Once launched, the Recorder application opens, allowing you to configure the settings before recording a web browser session.

Set it up with the following options:

* *Recorder Mode* set to *HTTP Proxy*
* *computerdatabase* package
* *RecordedSimulation* name
* *Follow Redirects?* checked
* *Infer HTML resources?* checked
* *Automatic Referers?* checked
* *Remove cache headers?* checked
* *No static resources* clicked
* Select the desired `format`. This tutorial assumes "Java 17" 

After configuring the recorder, all you have to do is click **Start!**. 

{{< alert tip >}}
For more information regarding Recorder and browser configuration, please check out the [Recorder reference documentation]({{< ref "/reference/script/protocols/http/recorder" >}}).
{{< /alert >}}


## Record a website session

Once the Recorder is launched, there are 4 buttons to control the session recording:
- **Add** - adds a tag to organize actions in your session.
- **Clear** - clears the _Executed events_.
- **Cancel** - cancels the Recorder session.
- **Stop & Save** - stops and saves the current Recorder session. 



Based on the scenario described in [Launch the Recorder](#launch-the-recorder) perform the following actions in your configured web browser. Try to act as a real user would, don't immediately jump from one page to another without taking the time to read. This makes your scenario similar to how a real user would behave.

1. Enter a 'Homepage' tag in the Recorder application and click **Add**.
2. Go to the website: [https://ecomm.gatling.io](https://ecomm.gatling.io).
3. Return to the Recorder application.
4. Enter a 'Authentication' tag and click **Add**.
5. Click on the 'Login' button in the top right.
5. Click on 'Submit'.
3. Return to the Recorder application.
6. Enter a 'Cart' tag and click **Add**.
7. Click on 'Add to cart' for the 'Pink Throwback Hip Bag' product.

The simulation is generated in the folder:

{{< code-toggle >}}
Java: src/test/java/
JavaScript: src/
{{</ code-toggle >}}

{{< alert tip >}}
The scenario components and their functionality are described in the [Intro to Scripting]({{< ref "/tutorials/scripting-intro" >}}) tutorial. For more details regarding the Simulation structure, please check out the [Simulation reference page]({{< ref "/reference/script/core/simulation" >}}).
{{< /alert >}}

## Test execution

### Run the Simulation on Gatling Enterprise

You can package, deploy, and run your simulation using one of two approaches, depending on whether you prefer a manual or automated process.

#### Simple Manual Use Case

1. Manually generate the package by executing the following command locally on your developer’s workstation:

   Maven:
   {{< platform-toggle >}}
   Linux/MacOS: ./mvnw gatling:enterprisePackage
   Windows: mvnw.cmd gatling:enterprisePackage
   {{</ platform-toggle >}}

   JavaScript CLI:

   ```console
   npx gatling enterprise-package
   ```

2. The above command will create a packaged `jar` or `zip` file in your project's **target** directory.

3. From your Gatling Enterprise console, go to **Packages**. Create a new package specifying its name, team that owns it, select your packaged jar/zip file for upload then click **Save**.

4. Go to **Simulations** > **Create a simulation** > **Test as code**. Under **Select a package**, choose the newly created package, then click **Create**.

5. Configure your simulation parameters:
   - Simulation name.
   - Under **Select your package and simulation** > **Simulation**, select your simulation class.
   - Under **Configure your locations**, choose the _Managed_ type and select a location based on your preference.
   - Click **Save and launch**.

#### Advanced Use Case with Automated Deployments (Configuration-as-Code)

Gatling Enterprise is a feature-rich SaaS platform that is designed for teams and organizations to get the most
out of load testing. With the trial account, you created in the [Prerequisites section]({{< ref "#install-gatling" >}}), you can upload and run your test with advanced configuration, reporting, and collaboration features.

From Gatling 3.11 packaging and running simulations on Gatling Enterprise is simplified by using [configuration as code]({{< ref "reference/execute/cloud/user/configuration-as-code" >}}). In this tutorial, we only use the default configuration to demonstrate deploying your project. You can learn more about customizing your configuration with our [configuration-as-code guide]({{< ref "guides/custom-config/config-as-code" >}}). 

To deploy and run your simulation on Gatling Enterprise, use the following procedure:

1. Generate an [API token]({{< ref "/reference/execute/cloud/admin/api-tokens" >}}) with the `Configure` permission in your Gatling Enterprise account.
2. Add the API token to your current terminal session by replacing `<your-API-token>` with the API token generated in step 1 and running the following command:

   {{< platform-toggle >}}
   Linux/MacOS: export GATLING_ENTERPRISE_API_TOKEN=<your-API-token>
   Windows: set GATLING_ENTERPRISE_API_TOKEN=<your-API-token>
   {{</ platform-toggle >}}

3. Run one of the following two commands according to your needs:

   - To deploy your package **and** start the simulation, run:

     Maven:
     {{< platform-toggle >}}
     Linux/MacOS: ./mvnw gatling:enterpriseStart -Dgatling.enterprise.simulationName="<simulation name>"
     Windows: mvnw.cmd gatling:enterpriseStart -Dgatling.enterprise.simulationName="<simulation name>"
     {{</ platform-toggle >}}

     JavaScript CLI:

     ```console
     npx gatling enterprise-start --enterprise-simulation="<simulation name>"
     ```

   - To deploy your package without starting a run:

     Maven:
     {{< platform-toggle >}}
     Linux/MacOS: ./mvnw gatling:enterpriseDeploy
     Windows: mvnw.cmd gatling:enterpriseDeploy
     {{</ platform-toggle >}}

     JavaScript CLI:

     ```console
     npx gatling enterprise-deploy
     ```

Watch the Simulation deploy automatically and generate real-time reports.

### Run the Simulation locally for debugging {{% badge info "Optional" /%}} {#run-the-simulation-locally-for-debugging}

The open-source version of Gatling allows you to run simulations locally, generating load from your computer. Running a
new or modified simulation locally is often useful to ensure it works before launching it on Gatling Enterprise Cloud.
Using the Java SDK or the Javascript CLI, you can launch your test in interactive mode using the following approach:

1. Run the following command:

   Maven:
   {{< platform-toggle>}}
   Linux/MacOS: ./mvnw gatling:test
   Windows: mvnw.cmd gatling:test
   {{</ platform-toggle >}}

   JavaScript CLI:

   ```console
   npx gatling run
   ```

2. Choose your recorded simulation.

When the test has finished, there is an HTML link in the terminal that you can use to access the static report.


## Keep learning

Now that you have completed the Introduction to scripting and Introduction to the Recorder tutorials, you have a solid foundation of Gatling and load testing knowlege. We strongly recommend you complete the Writing realistic tests tutorial to learn the essential skills for writing clean and concise tests. 

 - [Writing realistic tests]({{< ref "writing-realistic-tests" >}})
