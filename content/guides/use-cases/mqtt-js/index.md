---
menutitle: Testing MQTT with JS/TS
title: Test MQTT with the JS/TS SDK
seotitle: How to Test MQTT with the JS/TS SDK
description: Step-by-step guide to testing MQTT using the JavaScript/TypeScript SDK.
lead: Learn how to load test MQTT brokers with the JavaScript/TypeScript SDK using the mqtt-js fleet-tracking demo.
---

## Introduction

This guide walks through the **mqtt-js** reference project in the [Talks and tutorials repo](https://github.com/gatling/talks-and-tutorials/) to show how you can exercise an MQTT broker with Gatling's JavaScript/TypeScript SDK. The sample application simulates a fleet of delivery vehicles that publish telemetry over MQTT while a Node.js backend ingests the stream and exposes a live dashboard. Gatling wraps that workflow so you can rehearse realistic MQTT workloads and inspect detailed performance metrics.

{{< alert tip >}}
New to Gatling scripting in JavaScript or TypeScript? Start with the [Create a simulation with JavaScript/TypeScript tutorial]({{< ref "/tutorials/scripting-intro-js/" >}}) before diving into protocol-specific use cases.
{{< /alert >}}

## Why this example matters

- **MQTT in the real world:** IoT, mobility, and telemetry services often depend on MQTT for low-latency message delivery. Validating broker behavior under load is critical before connecting real devices.
- **Full-stack demo:** The project ships an MQTT broker, a backend processor, a WebSocket dashboard, an optional simulator, and Gatling load tests—everything you need to rehearse the end-to-end flow locally.
- **Reusable patterns:** The Gatling scripts show how to parameterize device profiles, publish structured payloads, and assert MQTT-specific success criteria using the `@gatling.io/mqtt` module.

## Prerequisites

- Node.js 20 or later (npm included)
- Docker (required for the bundled Mosquitto broker)
- A local Git client
- A [Gatling Enterprise Edition account](https://cloud.gatling.io/?utm_source=docs&utm_medium=js-guides) for the distributed testing

## Clone and configure the mqtt-js demo

1. **Fetch the repository and install dependencies.**

   ```bash
   git clone https://github.com/gatling/talks-and-tutorials.git
   cd articles/mqtt-js
   npm install
   ```

2. **Create a local environment file.** Copy the sample configuration and adjust hostnames or telemetry defaults if needed.

   ```bash
   cp example.env .env
   ```

   The important keys are the broker endpoint (`BROKER_HOST`, `BROKER_PORT`, `BROKER_TLS`) and the default telemetry topic (`SIM_TOPIC`).
3. **Start the MQTT broker.** Spin up Mosquitto with Docker Compose from the project root.

   ```bash
   docker compose up -d broker
   ```

4. **Launch the backend API.** It subscribes to MQTT topics and relays messages over WebSocket.

   ```bash
   npm run backend
   ```

   The service listens on `http://localhost:8080` and exposes `/readyz`, `/stats`, and `/stream` endpoints.
5. **Start the dashboard.** Run the Vite dev server to visualize vehicle locations.

   ```bash
   npm run dev
   ```

   Open `http://localhost:5173` (development) or run `npm run build && npm run start` for the production preview on port `4173`.
6. **Optional — seed background telemetry.** The simulator publishes MQTT messages independent of Gatling and is useful for validating the stack before load testing.

   ```bash
   npm run simulate -- --vehicles=25 --rate=1s --region=paris
   ```

   Cancel with `Ctrl+C` once you have enough baseline traffic.

At this stage you have a live MQTT pipeline: simulated vehicles → broker → backend → dashboard. You can now add Gatling virtual users to stress the broker and validate end-to-end behavior.

## Understand the Gatling simulation

Open `gatling/typescript/src/deliveryVehicleSimulation.gatling.ts` to inspect the TypeScript scenario (the JavaScript version lives in the companion folder). Key elements to note:

- **Parameter-driven profiles:** `getParameter()` exposes CLI overrides such as `loadProfile`, `deviceCount`, `telemetryIntervalMs`, or `topic`. The script catalogs ramp, spike, steady, soak, and smoke profiles so you can switch behaviors without editing code.
- **MQTT protocol definition:** The `mqtt` builder points at the broker (`brokerHost`, `brokerPort`), enables TLS when required, and correlates messages by `vehicleId` so request/response pairs appear in Gatling reports.
- **Stateful virtual users:** An `exec` block seeds each virtual user with coordinates, fuel level, and topic names. A `repeat` loop updates the session on every telemetry tick before publishing a JSON payload.
- **Checks and assertions:** The simulation asserts connection success and bounds the 95th percentile publish latency—use these to catch regressions automatically.

The excerpt below highlights how the MQTT DSL is used inside the scenario:

```ts
const mqttProtocol = mqtt
  .broker(brokerHost, brokerPort)
  .correlateBy(jmesPath("vehicleId"))
  .useTls(useTls);

const scn = scenario(`Telemetry-${profileKey}`)
  .exec(mqtt("Connect vehicle").connect())
  .repeat(updateIterations, "updateIndex")
  .on(
    exec(/* update session with new GPS and fuel values */)
      .exec(
        mqtt("Publish telemetry")
          .publish("#{topic}")
          .message(
            StringBody(
              '{"vehicleId":"#{vehicleId}","lat":#{lat},"lng":#{lng},"ts":"#{reportedAt}","fuelLevel":#{fuelLevel}}'
            )
          )
      )
      .pause(updateInterval)
  );

setUp(scn.injectOpen(...injectionSteps))
  .protocols(mqttProtocol)
  .assertions(
    details("Connect vehicle").successfulRequests().percent().gte(99),
    details("Publish telemetry").responseTime().percentile(95).lt(200)
  );
```

Use the sample as a template: replace the payload builder or correlation keys with ones that match your production topics.

## Run the MQTT load test (TypeScript)

1. Install Gatling dependencies the first time you run the project.

   ```bash
   cd gatling/typescript
   npm install
   ```
2. Launch a smoke test against your local stack. Adjust parameters as needed:

   ```bash
   npx gatling run --typescript --simulation deliveryVehicleSimulation \
     loadProfile=smoke \
     brokerHost=localhost \
     brokerPort=1883 \
     brokerTls=false \
     telemetryIntervalMs=1000 \
     telemetryDurationSeconds=120
   ```

   - `loadProfile` accepts `ramp`, `spike`, `steady`, `soak`, or `smoke`.
   - Tweak `deviceCount`, `topic`, `baseLat`, or `spawnRadius` to mirror your fleet distribution.
   - Use `telemetryDurationSeconds` together with `telemetryIntervalMs` to control how many messages each virtual user emits.
3. When the run completes, open the generated report under `gatling/typescript/target/gatling/<timestamp>/index.html`. The **Details** tab shows the MQTT "Connect vehicle" and "Publish telemetry" actions with latency and success metrics.

## Optional: run the JavaScript variant

Prefer plain JavaScript over TypeScript? The folder `gatling/javascript` mirrors the same scenario. Run it with:

```bash
cd gatling/javascript
npm install
npx gatling run --simulation deliveryVehicleSimulation loadProfile=smoke brokerHost=localhost brokerPort=1883 brokerTls=false
```

## Promote the test to Gatling Enterprise Edition

Gatling Enterprise lets you scale MQTT tests across distributed load generators and monitor KPIs in real time. It also removes the 5 virtual user limit of the open-source CLI. To deploy the mqtt-js simulation to Enterprise:

1. Create an API token in Gatling Enterprise with the `Configure` permission.
2. Package the simulation and deploy it from either the TypeScript or JavaScript project root.
   ```bash
   npx gatling enterprise-deploy --simulation deliveryVehicleSimulation --api-token <your_token>
   ```
3. Configure environment variables and/or JavaScript parameters (`BROKER_HOST`, credentials, TLS certificates) in the Enterprise Edition interface so remote load generators can reach your broker.
4. Use Enterprise Edition dashboards to watch connection counts, message throughput, and failure trends while the test executes.

Refer to the [JavaScript CLI guide]({{< ref "/integrations/build-tools/js-cli/#deploying-on-gatling-enterprise" >}}) for advanced packaging and deployment options.

## Troubleshooting tips

- **Broker connection refused:** Confirm `BROKER_HOST` and ports in `.env`, and expose the broker port (`1883` or `8883`) on Docker if you bind to a non-default interface.
- **TLS handshake failures:** Provide the proper certificate chain and set `brokerTls=true`. For self-signed certificates, mount the CA bundle and reference it via environment variables.
- **No data on the dashboard:** Verify the backend logs—if it cannot subscribe to the topic or the simulator is idle, the WebSocket feed remains empty.
- **Gatling run hangs at startup:** Delete any stale `.gatling` folder in the project root and rerun `npm install` to refresh the launcher cache.

## Next steps

- Dive deeper into the [`@gatling.io/mqtt` reference documentation]({{< ref "/reference/script/mqtt/protocol/" >}}) for advanced features such as retained messages, QoS tuning, and unmatched message processing.
- Combine this MQTT workload with HTTP or WebSocket checks to validate downstream services.
