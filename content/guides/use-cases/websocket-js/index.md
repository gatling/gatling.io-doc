---
menutitle: Testing websocket with JS/TS
title: How to test the websocket protocol with JavaScript/TypeScript SDK
seotitle: Master websocket load testing with Gatling JavaScript/TypeScript SDK a step-by-step guide
lead: Learn how to load test websocket with Gatling and the JavaScript/TypeScript SDK
date: 2025-09-10T13:35:00+02:00
---

## Introduction

Websocket protocol is widely used for real-time applications such as chat, gaming, and live updates. The protocol enables persistent, full-duplex communication between client and server. Testing websocket endpoints is crucial to ensure your system can handle concurrent connections and real-time data exchange. This guide demonstrates how to use Gatling's JavaScript/TypeScript SDK to load test a websocket server, helping you validate performance and reliability before going to production.

The following example uses a simple chatbot websocket server available from the Gatling [Talks and Tutorials Repository]( https://github.com/gatling/talks-and-tutorials/articles/websocket-chatbot-js). The application:

- Echoes messages, simulating a chatbot response.
- Listens on `ws://localhost:3000` by default.
- Includes a Docker setup and ngrok configuration for public access, which enables testing from Gatling Enterprise Edition.

{{< alert tip >}} 
This guide assumes basic familiarity with JavaScript and command-line tools. If you're new to Gatling, check out the [Create a simulation with JavaScript]({{<ref "/tutorials/scripting-intro-js" >}}) tutorial first.
{{< /alert >}}

## Key concepts

- **Websocket**: A protocol for two-way communication over a single TCP connection.
- **Gatling JS/TS SDK**: Enables writing load tests in JavaScript or TypeScript.
- **Simulation**: A script that defines virtual user behavior and test scenarios.

## Quick start

1. **Clone and start the demo server**

  ```bash
  git clone https://github.com/gatling/talks-and-tutorials/articles/websocket-chatbot-js.git
  cd websocket-chatbot-js
  npm install
  npm start
  ```

  The server listens on `ws://localhost:3000`.

2. **Install Gatling JS/TS SDK**

  Navigate to the `gatling/javascript` or `gatling/typescript` directory and install dependencies:

  ```bash
  npm install
  ```

3. **Review the websocket simulation**

  Navigate to the `src` folder and open `chatbotSimulation.gatling.ts`:

  The example simulation is annotated with comments to explain each part.

4. **Run the simulation**

  ```bash
  # Example from the gatling/typescript folder

  npx gatling run --typescript --simulation chatbotSimulation
  ```

  Gatling executes the scenario and generates an HTML report.

## Result analysis

After the test, open the generated Gatling report and select the **Details** tab:

- Click to view the **Chatbot Response** request. This is the websocket message sent by the server. The server has a random timer that varies the response time, to mimic real-world conditions. The response time graph shows how long it took to receive the response after sending a message. 

## Running with Gatling Enterprise Edition

Gatling Enterprise Edition offers additional features for websocket testing, including:

- **Distributed load testing**: Simulate thousands of concurrent users across multiple machines.
- **Advanced reporting**: Gain deeper insights into performance metrics and user behavior.
- **Integration with CI/CD**: Automate performance testing as part of your development pipeline.

To run the websocket simulation with Enterprise Edition:

1. Create an [Enterprise Edition trial](https://cloud.gatling.io/) account if you don't have one.
2. Create an ngrok account and copy your auth token.
3. From the `websocket-chatbot-js` folder copy the `example.env` file to `.env` and update the `NGROK_AUTH_TOKEN` values.
4. Start the demo websocket server in a Docker container:

  ```bash
  docker compose up
  ```
5. From the Enterprise Edition web interface, create an API token with the `Configure` permission and copy it to the clipboard.
6. From the `gatling/typescript` folder, upload the  the simulation with:
  ```bash
  npx gatling enterpriseStart --typescript --simulation chatbotSimulation --api-token <your_token>
  ```
7. The simulation is packaged, uploaded, and started. You can monitor its progress from the Enterprise Edition web interface.

## Troubleshooting

- **Connection errors**: Ensure the server is running and accessible.
- **Timeouts**: Increase await timeouts if responses are slow.
- **Protocol mismatches**: Verify message formats and expected responses.

## Summary

This guide covered the essentials of load testing websocket servers using Gatling's JavaScript/TypeScript SDK. You learned how to set up a demo websocket server, configure and run a basic simulation, and analyze the resulting performance reports. 

Keep learning about Gatling and websocket testing in the [protocol documentation]({{<ref "/reference/script/websocket/" >}}).
