---
menutitle: Testing SSE with JS/TS
title: How to test the SSE protocol with JavaScript/TypeScript SDK
seotitle: Learn how to load test SSE with Gatling and the JavaScript/TypeScript SDK
description: Learn how to load test SSE with Gatling and the JavaScript/TypeScript SDK
lead: Learn how to load test SSE with Gatling and the JavaScript/TypeScript SDK
date: 2025-09-10T13:35:00+02:00
---

## Introduction

This guide walks through setting up a Server-Sent Events (SSE) demo service and writing a Gatling simulation in JavaScript or TypeScript. It assumes familiarity with modern JavaScript but not with Gatling. Server-Sent Events (SSE) is a unidirectional protocol where servers can push real-time updates to clients over HTTP. It's commonly used for live feeds, notifications, and real-time data streaming.

Performance testing SSE endpoints is crucial to ensure they can handle high loads and deliver timely updates without degradation. Gatling's JavaScript/TypeScript SDK provides a powerful way to create and run load tests for SSE services, allowing developers to simulate realistic user behavior and measure performance metrics effectively.

{{< alert info >}}
This guide showcases the JavaScript/TypeScript SDK, but the same process applies to the Java, Kotlin, and Scala SDKs.

 If you haven't already, we recommend starting with the [Create a simulation with JavaScript/TypeScript tutorial]({{<ref "/tutorials/test-as-code/javascript/running-your-first-simulation/" >}}) to set up your environment and understand the basics of writing simulations.
{{< /alert >}}

## Price streaming as an SSE use case

A common use case for SSE is streaming real-time price updates in financial applications. Clients subscribe to a price feed and receive continuous updates as prices change. This ensures users always have the latest information without needing to refresh or poll the server. In our case, we are going to simulate a price feed that sends random cryptocurrency price updates for multiple currencies. 

The project consists of 3 parts:

- A NodeJS backend that streams price updates using SSE
- A frontend that lets users visualize the price updates in real-time
- A performance test to validate the SSE implementation

Additionally there is a `docker-compose.yml` file to launch the backend and frontend together and (optionally) an ngrok tunnel to expose the services so they can be tested using Gatling Enterprise Edition.

You can find the complete code in the [Gatling devrel repository](https://github.com/gatling/talks-and-tutorials).

Let's get started! 

## Create a Gatling SSE load test with JavaScript/TypeScript

Gatling is a test-as-code tool, meaning you can write performance tests in code, using the same best practices as your application code. This makes it easy to version, review, and maintain your tests. Load tests essentially consist of 3 steps:

1. Define the user journey, often called a scenario.
2. Set up the environment, including defining the protocols, endpoints, and libraries to use.
3. Shape the load by defining how many users to simulate over how much time and how they arrive and exit the system. 

{{< alert info >}}
If you are new to load testing, we recommend reading the [workload models guide]({{<ref "/testing-concepts/workload-models/" >}}) for a conceptual overview of load testing and how to design realistic workloads.
{{< /alert >}}

Let's go through each step conceptually, then build the simulation using our SSE price feed example.

### Set up the environment

First, we need to set up the environment for our load test. This involves defining constants for:

- user profiles,
- the base URL of the SSE service,
- a reusable check to validate incoming messages, and
- configuring the HTTP protocol. 

The following code snippet shows how to set up the environment using Gatling's JavaScript/TypeScript SDK:

```ts
// --- Parameters & Constants ---
const BASE_URL = getParameter("baseUrl", "http://localhost:3000");
const PRICE_MIN = 0;
const PRICE_MAX = 1_000_000;

// User profile parameters
const QUICK_AT_ONCE = parseInt(getParameter("quickAtOnce", "10"), 10);
const QUICK_RAMP = parseInt(getParameter("quickRamp", "50"), 10);
const QUICK_RAMP_DURATION = parseInt(getParameter("quickRampDuration", "60"), 10);
const QUICK_CONSTANT_RATE = parseFloat(getParameter("quickConstantRate", "2"));
const QUICK_CONSTANT_DURATION = parseInt(getParameter("quickConstantDuration", "300"), 10);

const ACTIVE_RAMP = parseInt(getParameter("activeRamp", "20"), 10);
const ACTIVE_RAMP_DURATION = parseInt(getParameter("activeRampDuration", "120"), 10);
const ACTIVE_CONSTANT_RATE = parseFloat(getParameter("activeConstantRate", "0.5"));
const ACTIVE_CONSTANT_DURATION = parseInt(getParameter("activeConstantDuration", "300"), 10);

const MONITOR_AT_ONCE = parseInt(getParameter("monitorAtOnce", "5"), 10);
const MONITOR_RAMP = parseInt(getParameter("monitorRamp", "5"), 10);
const MONITOR_RAMP_DURATION = parseInt(getParameter("monitorRampDuration", "300"), 10);

// --- Protocol ---
const httpProtocol = http.baseUrl(BASE_URL);

// --- Reusable SSE Check ---
const priceUpdateCheck = sse.checkMessage("price-update")
  .matching(jmesPath("event").is("price-update"))
  .check(
    jmesPath("data").exists(),
    jmesPath("data").transform(raw => {
      const price = JSON.parse(raw).price;
      return price > PRICE_MIN && price < PRICE_MAX;
    }),
  );
```
We also need to import the necessary Gatling modules at the top of the file. These consist of core functions for defining simulations and scenarios, as well as the HTTP and SSE modules for handling requests and connections:

```ts
import {
  simulation,
  scenario,
  constantUsersPerSec,
  atOnceUsers,
  rampUsers,
  pause,
  jmesPath,
  getParameter,
} from "@gatling.io/core";
import { http, sse } from "@gatling.io/http";

```

Don't worry if some of the code is unfamiliar as this point. It should make more since as we progress through the guide.

### Define the user journey

In our SSE price feed example, the user journey consists of connecting to the SSE endpoint, receiving price updates, and validating the data. We want to simulate multiple users subscribing to the price feed and reacting to updates in real-time. In this case, we define three user profiles:

- **Quick Price Checker**: Connects to the price feed, waits for a few updates, then disconnects.
- **Active Trader**: Connects to the price feed, stays connected for a longer period, and periodically checks for updates.
- **Long-term Monitor**: Connects to the price feed and remains connected for an extended duration, simulating a user who relies heavily on real-time updates.

These 3 profiles connect to the endpoint for different durations, allowing us to simulate a realistic mix of user behaviors. Because this example is focused on SSE, the user journeys are relatively simple. In a real-world scenario, users might perform additional actions such as placing orders or navigating through different parts of an application.

The following code snippet shows how to define the user journeys using Gatling's JavaScript/TypeScript SDK:

```ts
// --- Scenarios ---
const quickChecker = scenario("QuickPriceChecker")
  .exec(
    sse("Prices").get("/prices")
      .await(10).on(priceUpdateCheck),
    pause(2, 8),
    sse("Prices").close()
  );

const activeTrader = scenario("ActiveTrader")
  .exec(
    sse("Prices").get("/prices")
      .await(30).on(priceUpdateCheck),
    pause(25, 35),
    sse("Prices").close()
  );

const longTermMonitor = scenario("LongTermMonitor")
  .exec(
    sse("Prices").get("/prices")
      .await(300).on(priceUpdateCheck),
    pause(280, 320),
    sse("Prices").close()
  );
  ```

### Shape the load

Finally, we need to shape the load by defining how many users to simulate for each profile and how they arrive over time. We can use different injection profiles to model realistic traffic patterns. The first choice is whether we are testing a open or closed system. A simple question to determine the correct choice: Is user arrival independent (open) or dependent (closed) on the system state? Most web applications are open systems, meaning users arrive independently of the system state. In contrast, a closed system has a fixed number of users cycling through the application, sometimes controlled by queuing systems. In open systems, like our demo app, we must reason in terms of arrival rates (e.g., users per second). In closed systems, we reason in terms of concurrent users. This means in open systems we have no direct control over the number of concurrent users, as it depends on how long each user stays connected.

We are going to use an injection profile for each of our user scenarios, consisting of different arrival patterns. For example, we can use `atOnceUsers` to simulate a sudden spike in users, `rampUsers` to gradually increase the load, and `constantUsersPerSec` to maintain a steady rate of new users injected into the system (not concurrently in the system!).

The following code snippet shows how to shape the load for our three user profiles:

```ts
// --- Simulation Setup ---
export default simulation((setUp) => {
  setUp(
    quickChecker.injectOpen(
      atOnceUsers(QUICK_AT_ONCE),
      rampUsers(QUICK_RAMP).during(QUICK_RAMP_DURATION),
      constantUsersPerSec(QUICK_CONSTANT_RATE).during(QUICK_CONSTANT_DURATION)
    ),
    activeTrader.injectOpen(
      rampUsers(ACTIVE_RAMP).during(ACTIVE_RAMP_DURATION),
      constantUsersPerSec(ACTIVE_CONSTANT_RATE).during(ACTIVE_CONSTANT_DURATION)
    ),
    longTermMonitor.injectOpen(
      atOnceUsers(MONITOR_AT_ONCE),
      rampUsers(MONITOR_RAMP).during(MONITOR_RAMP_DURATION)
    )
  ).protocols(httpProtocol);
});
```

Let's break down one of the user profiles to understand how the load is shaped:

```ts
quickChecker.injectOpen(
      atOnceUsers(QUICK_AT_ONCE),
      rampUsers(QUICK_RAMP).during(QUICK_RAMP_DURATION),
      constantUsersPerSec(QUICK_CONSTANT_RATE).during(QUICK_CONSTANT_DURATION)
    ),
```
This profile simulates a "Quick Price Checker" user who:
- Starts with a burst of `QUICK_AT_ONCE` users connecting simultaneously.
- Then ramps up an additional `QUICK_RAMP` users over `QUICK_RAMP_DURATION` seconds.
- Finally, maintains a steady arrival rate of `QUICK_CONSTANT_RATE` users per second for `QUICK_CONSTANT_DURATION` seconds.
The other two profiles follow a similar pattern but with different parameters to reflect their unique behaviors.

In the [Set up the environment]({{<ref "#set-up-the-environment" >}}) section, we defined preceding parameters for each user profile that can be adjusted at runtime. This allows us to easily tune the load test without modifying the code. You can do this by passing parameters via the command line when running the simulation, but also via Javascript parameters in the Gatling Enterprise Edition UI.

### Complete simulation code

The following is the complete simulation code, combining all the parts we've discussed:

```ts
import {
  simulation,
  scenario,
  constantUsersPerSec,
  atOnceUsers,
  rampUsers,
  pause,
  jmesPath,
  getParameter,
} from "@gatling.io/core";
import { http, sse } from "@gatling.io/http";

// --- Parameters & Constants ---
const BASE_URL = getParameter("baseUrl", "http://localhost:3000");
const PRICE_MIN = 0;
const PRICE_MAX = 1_000_000;

// User profile parameters
const QUICK_AT_ONCE = parseInt(getParameter("quickAtOnce", "10"), 10);
const QUICK_RAMP = parseInt(getParameter("quickRamp", "50"), 10);
const QUICK_RAMP_DURATION = parseInt(getParameter("quickRampDuration", "60"), 10);
const QUICK_CONSTANT_RATE = parseFloat(getParameter("quickConstantRate", "2"));
const QUICK_CONSTANT_DURATION = parseInt(getParameter("quickConstantDuration", "300"), 10);

const ACTIVE_RAMP = parseInt(getParameter("activeRamp", "20"), 10);
const ACTIVE_RAMP_DURATION = parseInt(getParameter("activeRampDuration", "120"), 10);
const ACTIVE_CONSTANT_RATE = parseFloat(getParameter("activeConstantRate", "0.5"));
const ACTIVE_CONSTANT_DURATION = parseInt(getParameter("activeConstantDuration", "300"), 10);

const MONITOR_AT_ONCE = parseInt(getParameter("monitorAtOnce", "5"), 10);
const MONITOR_RAMP = parseInt(getParameter("monitorRamp", "5"), 10);
const MONITOR_RAMP_DURATION = parseInt(getParameter("monitorRampDuration", "300"), 10);

// --- Protocol ---
const httpProtocol = http.baseUrl(BASE_URL);

// --- Reusable SSE Check ---
const priceUpdateCheck = sse.checkMessage("price-update")
  .matching(jmesPath("event").is("price-update"))
  .check(
    jmesPath("data").exists(),
    jmesPath("data").transform(raw => {
      const price = JSON.parse(raw).price;
      return price > PRICE_MIN && price < PRICE_MAX;
    }),
  );

// --- Scenarios ---
const quickChecker = scenario("QuickPriceChecker")
  .exec(
    sse("Prices").get("/prices")
      .await(10).on(priceUpdateCheck),
    pause(2, 8),
    sse("Prices").close()
  );

const activeTrader = scenario("ActiveTrader")
  .exec(
    sse("Prices").get("/prices")
      .await(30).on(priceUpdateCheck),
    pause(25, 35),
    sse("Prices").close()
  );

const longTermMonitor = scenario("LongTermMonitor")
  .exec(
    sse("Prices").get("/prices")
      .await(300).on(priceUpdateCheck),
    pause(280, 320),
    sse("Prices").close()
  );

// --- Simulation Setup ---
export default simulation((setUp) => {
  setUp(
    quickChecker.injectOpen(
      atOnceUsers(QUICK_AT_ONCE),
      rampUsers(QUICK_RAMP).during(QUICK_RAMP_DURATION),
      constantUsersPerSec(QUICK_CONSTANT_RATE).during(QUICK_CONSTANT_DURATION)
    ),
    activeTrader.injectOpen(
      rampUsers(ACTIVE_RAMP).during(ACTIVE_RAMP_DURATION),
      constantUsersPerSec(ACTIVE_CONSTANT_RATE).during(ACTIVE_CONSTANT_DURATION)
    ),
    longTermMonitor.injectOpen(
      atOnceUsers(MONITOR_AT_ONCE),
      rampUsers(MONITOR_RAMP).during(MONITOR_RAMP_DURATION)
    )
  ).protocols(httpProtocol);
});

```

## Testing the application locally

Now that we have our simulation ready, we can run it against our SSE price feed application. If you haven't already, start the backend and frontend services using Docker Compose:

```bash
docker-compose up --build
``` 
This launches the backend on `http://localhost:3000` and the frontend on `http://localhost:8080`. You can open the frontend in your browser to visualize the price updates in real-time.
Next, run the Gatling simulation using the command line. Make sure to adjust the `baseUrl` parameter if your services are running on a different host or port:

```bash
npx gatling run <path/to/your/simulation.ts> 
``` 

## Test the application using Gatling Enterprise Edition

If you are using Gatling Enterprise Edition, you can easily run the simulation through the web interface. If you are new to Gatling Enterprise Edition, create a [free trial account](https://cloud.gatling.io) to get started.

Ensure your backend and frontend services are accessible from the Gatling Enterprise server. You can use a tool like ngrok to expose your local services to the internet. The repository includes a sample ngrok configuration in the `docker-compose.yml` file. Create a `.env` file at the project root and add your ngrok auth token. An `example.env` file  is provided to illustrate the required format.

```bash
NGROK_AUTHTOKEN=your_ngrok_auth_token
```

Then, start the services with Docker Compose:

```bash
docker-compose up --build
```     
This exposes the backend and frontend services via ngrok, and you can find the public URL in the terminal output. Use this URL as the `baseUrl` parameter when configuring the simulation in Gatling Enterprise Edition.

Next, [package and upload the simulation]({{<ref "/integrations/build-tools/js-cli/#running-your-simulations-on-gatling-enterprise" >}}) to Gatling Enterprise Edition.

Once your simulation is uploaded to Gatling Enterprise Edition:

-  [create a test]({{<ref "/reference/run-tests/simulations/intro/" >}}), and
-  (optionally) configure the runtime parameters under the [Load generator parameters]({{<ref "/reference/run-tests/simulations/optional-config/" >}}) as needed. You can adjust the user profile parameters to simulate different load scenarios without modifying the code.
-  Finally, run the simulation and monitor the results in real-time through the Gatling Enterprise Edition dashboard.

That is it! You have successfully created and run a load test for an SSE service using Gatling's JavaScript/TypeScript SDK. You can now analyze the results to identify performance bottlenecks and ensure your SSE implementation can handle the expected load.
