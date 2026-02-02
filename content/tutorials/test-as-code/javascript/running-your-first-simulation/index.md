---
menutitle: Create your first simulation
title: Create your first JavaScript-based simulation
description: "Get started with Gatling and JavaScript: install, write your first load test, and execute it."
lead: Learn how to get started with Gatling and create a Gatling simulation with JavaScript.
date: 2023-12-16T18:30:56+02:00
aliases:
  - /tutorials/scripting-intro-js/
---

{{< alert warning >}}
This guide is intended for the Gatling JavaScript SDK version `{{< var gatlingJsVersion >}}` and later.
{{< /alert >}}

New to Gatling and the JavaScript SDK? This tutorial walks through every edit required to produce, run, and package your first simulation. If you already know the basics and just need configuration reminders, jump to the [Installation Guide]({{< ref "tutorials/test-as-code/javascript/installation-guide/index.md" >}}). For a broader tour of feeders, checks, and workload modelling, continue with [Full SDK Capabilities]({{< ref "tutorials/test-as-code/javascript/full-sdk-capabilities/index.md" >}}).

{{< alert tip >}}
Prefer Java or a JVM language? Follow the [Create your first Java-based simulation]({{< ref "tutorials/test-as-code/java-jvm/running-your-first-simulation/index.md" >}}) instead.
{{< /alert >}}

## Before you begin
1. Install Node.js v20 or later (LTS versions recommended) with npm v10 or later.
2. Install Git and a code editor. We use VS Code in the instructions, but any editor works.
3. (Optional) Create a [Gatling Enterprise trial account](https://cloud.gatling.io/) if you plan to run the script in the cloud.

Confirm your environment in a terminal:

```bash
node -v
npm -v
```

If either command fails, fix it before you continue. Visit [nodejs.org](https://nodejs.org/) to download Node.js, which includes npm.

## Step 1: Clone the tutorial project { #install-gatling }
1. Open a terminal and run:
   ```bash
   git clone https://github.com/gatling/se-ecommerce-demo-gatling-tests.git
   cd se-ecommerce-demo-gatling-tests/javascript
   ```
2. Prefer ZIP downloads? Grab the archive from GitHub, extract it, and open the `javascript` directory in your editor.
3. Install the project dependencies:
   ```bash
   npm install
   ```

{{< alert info >}}
The sample project targets the demo backend at [https://ecomm.gatling.io](https://ecomm.gatling.io). It is safe to use for practice.
{{< /alert >}}

## Step 2: Inspect the project layout
```
se-ecommerce-demo-gatling-tests/
└── javascript
    ├── src/
    │   └── basicSimulation.gatling.js
    ├── package.json
    └── node_modules/
```

- `basicSimulation.gatling.js` is the file you will edit.
- `package.json` already includes the Gatling JavaScript SDK—no additional setup needed.
- The `.gatling.js` extension tells Gatling this file contains a simulation.

## Step 3: Build the simulation incrementally { #simulation-construction }
Each subsection adds one concept. Keep your editor open with `basicSimulation.gatling.js` selected.

### 3.1 Clean up the starter file
Delete everything below line 2 (after `import { http } from "@gatling.io/http";`) so only the imports remain. The file should match:

{{< include-code "ScriptingIntro1Sample#setup-the-file" ts >}}

### 3.2 Define the simulation function
Gatling JavaScript simulations use the `simulation` function to define tests. Add the function with `setUp` as its parameter:

{{< include-code "ScriptingIntro1Sample#extend-the-simulation-function" ts >}}

### 3.3 Define the HTTP protocol
Configure the target base URL and headers so Gatling knows how to talk to your application:

{{< include-code "ScriptingIntro2Sample#define-the-protocol-class" ts >}}

Key callouts:
- `baseUrl: "https://api-ecomm.gatling.io"` sets the server you will exercise.
- Custom headers (accept) mimic a real browser. Adjust them when testing your own system.

### 3.4 Describe a scenario
Scenarios encode user journeys. Start with a single request:

{{< include-code "ScriptingIntro3Sample#write-the-scenario" ts >}}

Here you:
- Name the scenario (`"Scenario"`).
- Issue a GET request against `/session`.
- Leave room to add checks or additional steps later.

### 3.5 Choose an injection profile
Configure the arrival rate and duration for virtual users:

{{< include-code "ScriptingIntro4Sample#define-the-injection-profile" ts >}}

This configuration launches two users per second for one minute. Tweak the numbers once the script works.

You now have a complete simulation. The finished file should match:

{{< include-code "BasicSimulation#full-example" ts >}}

Need a refresher on what each SDK call does? Keep the [Installation Guide]({{< ref "tutorials/test-as-code/javascript/installation-guide/index.md" >}}) and the [JavaScript HTTP reference]({{< ref "reference/script/http/index.md" >}}) handy.

## Step 4: Run the simulation locally {#run-the-simulation-locally-for-debugging}
1. From the `javascript` directory, run:

   ```bash
   npx gatling run
   ```

2. When prompted, choose `[2] basicSimulation`.
3. After the run completes, open the HTML report printed in the terminal (`target/gatling/basicsimulation-<timestamp>/index.html`).

Troubleshooting tips:
- **Module not found:** ensure you ran `npm install` in the `javascript` directory.
- **Simulation not listed:** verify your file ends with `.gatling.js` and is in the `src/` directory.
- **Network errors:** verify you can open `https://api-ecomm.gatling.io/session` in a browser.

## Step 5: Package and run on Gatling Enterprise {#run-the-simulation-on-gatling-enterprise}
Upload your script manually or drive automated deployments.

### Manual packaging
1. From the same directory, run:

   ```bash
   npx gatling enterprise-package
   ```

2. The command produces a `.zip` under `target/`. Upload it under **Packages** in the Gatling Enterprise console.
3. Create a simulation, choose the uploaded package, select a managed location, and launch.

### Automated deployment (configuration as code)
1. Generate an [API token]({{< ref "/reference/administration/api-tokens" >}}) with the `Configure` permission.
2. Export it in your terminal session:

   {{< platform-toggle >}}
   Linux/MacOS: export GATLING_ENTERPRISE_API_TOKEN=<your-API-token>
   Windows: set GATLING_ENTERPRISE_API_TOKEN=<your-API-token>
   {{</ platform-toggle >}}

3. Run one of the following commands:
   - Deploy and start immediately:

     ```bash
     npx gatling enterprise-start --enterprise-simulation="<simulation name>"
     ```

   - Deploy only:

     ```bash
     npx gatling enterprise-deploy
     ```

Follow the run in the Enterprise UI for live metrics and historical reporting.

## Step 6: Keep learning
- Repeat the tutorial against your own API—replace the base URL and adjust requests.
- Enrich the scenario with checks (`.check(status().is(200))`) and pauses (`.pause(1)`), then re-run locally.
- Graduate to [Full SDK Capabilities]({{< ref "tutorials/test-as-code/javascript/full-sdk-capabilities/index.md" >}}) for feeders, correlation, and workload modelling.
- Revisit the [Installation Guide]({{< ref "tutorials/test-as-code/javascript/installation-guide/index.md" >}}) when you need npm configuration or project structure advice.
- Explore the [Recorder tutorial]({{< ref "tutorials/low-code/browser/recorder" >}}) to capture traffic and generate simulations automatically.

You have now installed Gatling, authored a JavaScript simulation, and executed it locally and (optionally) on Gatling Enterprise. Keep iterating!
