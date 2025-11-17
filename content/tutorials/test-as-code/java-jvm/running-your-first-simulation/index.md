---
menutitle: Create a simulation in Java
title: Create your first Java-based simulation
description: "Get started with Gatling: install, write your first load test, and execute it."
lead: Learn how to get started with Gatling and create a Gatling simulation.
date: 2023-12-16T18:30:56+02:00
aliases:
  - /docs/tutorials/scripting-intro/
---

{{< alert warning >}}
This guide is intended for Gatling versions `{{< var gatlingVersion >}}` and later.
{{< /alert >}}

New to Gatling and the Java SDK? This tutorial walks through every edit required to produce, run, and package your first simulation. If you already know the basics and just need configuration reminders, jump to the [Installation Guide]({{< ref "tutorials/test-as-code/java-jvm/installation-guide/index.md" >}}). For a broader tour of feeders, checks, and workload modelling, continue with [Full SDK Capabilities]({{< ref "tutorials/test-as-code/java-jvm/full-sdk-capabilities/index.md" >}}).

{{< alert tip >}}
Prefer JavaScript or TypeScript? Follow the [introduction to JavaScript scripting]({{< ref "tutorials/test-as-code/javascript/running-your-first-simulation/index.md" >}}) instead.
{{< /alert >}}

## Before you begin
1. Install a 64-bit OpenJDK LTS version (11 through 25 supported, version 17 or 21 recommended). The sample project targets Java 17.
2. Install Git and an IDE. We use IntelliJ IDEA Community Edition in the instructions, but any Java IDE works.
3. (Optional) Create a [Gatling Enterprise trial account](https://cloud.gatling.io/) if you plan to run the script in the cloud.

Confirm your environment in a terminal:

```shell
java -version
mvn -version
```

If either command fails, fix it before you continue. Maven can come from your system installation or from the Maven Wrapper bundled with the project.

## Step 1: Clone the tutorial project { #install-gatling }
1. Open a terminal and run:
   ```shell
   git clone https://github.com/gatling/se-ecommerce-demo-gatling-tests.git
   cd se-ecommerce-demo-gatling-tests/java/maven
   ```
2. Prefer ZIP downloads? Grab the archive from GitHub, extract it, and open the `java/maven` directory in your IDE.
3. The module ships with a Maven Wrapper (`./mvnw` / `mvnw.cmd`). Use it unless your organisation mandates a system-wide Maven.

{{< alert info >}}
The sample project targets the demo backend at [https://ecomm.gatling.io](https://ecomm.gatling.io). It is safe to use for practice.
{{< /alert >}}

## Step 2: Inspect the project layout
```
se-ecommerce-demo-gatling-tests/
└── java/maven
    ├── src/test/java/example/BasicSimulation.java
    ├── src/test/resources/data/...
    ├── pom.xml
    └── mvnw / mvnw.cmd
```

- `BasicSimulation.java` is the file you will edit.
- `src/test/resources` contains data files you can feed into requests.
- `pom.xml` already references the Gatling Maven plugin—no additional setup needed.

## Step 3: Build the simulation incrementally { #simulation-construction }
Each subsection adds one concept. Keep IntelliJ (or your IDE) open with `BasicSimulation.java` selected.

### 3.1 Clean up the starter file
Delete everything below the import statements so only the package and imports remain. The file should match:

{{< include-code "ScriptingIntro1Sample#setup-the-file" java >}}

### 3.2 Extend the `Simulation` class
Gatling scripts extend `Simulation`. Add the class declaration:

{{< include-code "ScriptingIntro1Sample#extend-the-simulation-class" java >}}

### 3.3 Define the HTTP protocol
Configure the target base URL and headers so Gatling knows how to talk to your application:

{{< include-code "ScriptingIntro2Sample#define-the-protocol-class" java >}}

Key callouts:
- `http.baseUrl("https://api-ecomm.gatling.io")` sets the server you will exercise.
- Custom headers (user agent, accept) mimic a real browser. Adjust them when testing your own system.

### 3.4 Describe a scenario
Scenarios encode user journeys. Start with a single request:

{{< include-code "ScriptingIntro3Sample#write-the-scenario" java >}}

Here you:
- Name the scenario (`"Scenario"`).
- Issue a GET request against `/session`.
- Leave room to add checks or additional steps later.

### 3.5 Choose an injection profile
Configure the arrival rate and duration for virtual users:

{{< include-code "ScriptingIntro4Sample#define-the-injection-profile" java >}}

This configuration launches two users per second for one minute. Tweak the numbers once the script works.

You now have a complete simulation. The finished file should match:

{{< include-code "BasicSimulation#full-example" java >}}

Need a refresher on what each SDK call does? Keep the [Installation Guide]({{< ref "tutorials/test-as-code/java-jvm/installation-guide/index.md" >}}) and the [Java HTTP reference]({{< ref "reference/script/http/index.md" >}}) handy.

## Step 4: Run the simulation locally {#run-the-simulation-locally-for-debugging}
1. From the `java/maven` directory, run the Maven Wrapper:

   {{< platform-toggle >}}
   Linux/MacOS: ./mvnw gatling:test
   Windows: mvnw.cmd gatling:test
   {{</ platform-toggle >}}

2. When prompted, choose `[1] example.BasicSimulation`.
3. After the run completes, open the HTML report printed in the terminal (`target/gatling/basicsimulation-<timestamp>/index.html`).

Troubleshooting tips:
- **Command not allowed:** make the wrapper executable (`chmod +x mvnw`).
- **Compilation errors:** check that each code block above is in place—missing braces are the most common typo.
- **SSL or DNS failures:** verify you can open `https://api-ecomm.gatling.io/session` in a browser.

## Step 5: Package and run on Gatling Enterprise {#run-the-simulation-on-gatling-enterprise}
Upload your script manually or drive automated deployments.

### Manual packaging
1. From the same directory, run:

   {{< platform-toggle >}}
   Linux/MacOS: ./mvnw gatling:enterprisePackage
   Windows: mvnw.cmd gatling:enterprisePackage
   {{</ platform-toggle >}}

2. The command produces a `.jar` under `target/`. Upload it under **Packages** in the Gatling Enterprise console.
3. Create a simulation, choose the uploaded package, select a managed location, and launch.

### Automated deployment (configuration as code)
1. Generate an [API token]({{< ref "/reference/collaborate/admin/api-tokens" >}}) with the `Configure` permission.
2. Export it in your terminal session:

   {{< platform-toggle >}}
   Linux/MacOS: export GATLING_ENTERPRISE_API_TOKEN=<your-API-token>
   Windows: set GATLING_ENTERPRISE_API_TOKEN=<your-API-token>
   {{</ platform-toggle >}}

3. Run one of the following commands:
   - Deploy and start immediately:

     {{< platform-toggle >}}
     Linux/MacOS: ./mvnw gatling:enterpriseStart -Dgatling.enterprise.simulationName="<simulation name>"
     Windows: mvnw.cmd gatling:enterpriseStart -Dgatling.enterprise.simulationName="<simulation name>"
     {{</ platform-toggle >}}

   - Deploy only:

     {{< platform-toggle >}}
     Linux/MacOS: ./mvnw gatling:enterpriseDeploy
     Windows: mvnw.cmd gatling:enterpriseDeploy
     {{</ platform-toggle >}}

Follow the run in the Enterprise UI for live metrics and historical reporting.

## Step 6: Keep learning
- Repeat the tutorial against your own API—replace the base URL and adjust requests.
- Enrich the scenario with checks (`.check(status().is(200))`) and pauses (`.pause(1)`), then re-run locally.
- Graduate to [Full SDK Capabilities]({{< ref "tutorials/test-as-code/java-jvm/full-sdk-capabilities/index.md" >}}) for feeders, correlation, and workload modelling.
- Revisit the [Installation Guide]({{< ref "tutorials/test-as-code/java-jvm/installation-guide/index.md" >}}) when you need Maven configuration snippets or project structure advice.
- Explore the [Recorder tutorial]({{< ref "tutorials/low-code/recorder" >}}) to capture traffic and generate simulations automatically.

You have now installed Gatling, authored a Java simulation, and executed it locally and (optionally) on Gatling Enterprise. Keep iterating!
