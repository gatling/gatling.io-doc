---
menutitle: Testing gRPC with JS/TS
title: Test gRPC with the JS/TS SDK
seotitle: How to Test gRPC with the JS/TS SDK
description: Step-by-step guide to testing gRPC using the JavaScript/TypeScript SDK.
lead: Learn how to load test gRPC services with the JavaScript/TypeScript SDK using the official Gatling gRPC demo.
---

## Introduction

This guide walks through the official [Gatling gRPC demo](https://github.com/gatling/gatling-grpc-demo) to show how you can load test gRPC services with Gatling's JavaScript/TypeScript SDK. The demo provides a complete environment with example gRPC servers (a greeting service and calculator service) and pre-built load tests that demonstrate both unary and streaming RPC patterns.

gRPC is increasingly common in microservices architectures, mobile backends, and IoT platforms. Load testing gRPC endpoints ensures they can handle production traffic patterns before deployment. Gatling's JavaScript/TypeScript SDK provides a straightforward way to create realistic gRPC workloads and measure performance under load.

{{< alert tip >}}
New to Gatling scripting in JavaScript or TypeScript? Start with the [Create a simulation with JavaScript/TypeScript tutorial]({{< ref "/tutorials/test-as-code/javascript/running-your-first-simulation/" >}}) before diving into protocol-specific use cases.
{{< /alert >}}

## Why this example matters

- **Real-world gRPC patterns:** The demo covers both unary RPCs (simple request-response) and streaming RPCs (continuous data flows), which are common in production gRPC services.
- **Complete working environment:** The project includes a demo gRPC server, working simulations, and all necessary configuration—everything you need to understand gRPC load testing locally.
- **Reusable patterns:** The Gatling scripts demonstrate how to configure gRPC connections, generate protocol buffer code, send messages, validate responses, and handle streaming data using the `@gatling.io/grpc` module.

## Prerequisites

- Node.js 20 or later (npm included)
- Docker (required for the demo gRPC servers)
- A local Git client
- A [Gatling Enterprise Edition account](https://cloud.gatling.io/?utm_source=docs&utm_medium=js-guides) for distributed testing (optional)

## Clone and start the demo

1. **Fetch the repository and navigate to the TypeScript demo.**

   ```bash
   git clone https://github.com/gatling/gatling-grpc-demo.git
   cd gatling-grpc-demo/typescript
   npm install
   ```

2. **Start the demo gRPC servers.** From the repository root, launch the greeting and calculator services via Docker Compose.

   ```bash
   cd ..  # Navigate back to repository root
   docker compose up -d
   ```

   This starts:
   - **Greeting service** on `localhost:50051`
   - **Calculator service** on `localhost:50052`

   Both servers run without TLS for local testing.

At this point you have a complete gRPC test environment. The servers are running, and the TypeScript project already includes the proto files in the `protobuf/` directory (`greeting.proto` and `calculator.proto`). Gatling's gRPC plugin will automatically generate TypeScript client code from these `.proto` files when you run the simulation.

## Understand the gRPC simulation structure

Gatling gRPC tests in JavaScript/TypeScript follow a test-as-code approach consisting of three main steps:

1. **Configure the gRPC protocol** (server address, TLS settings)
2. **Define the scenario** (the RPC calls virtual users will make)
3. **Shape the load** (how many users, injection patterns)

Let's break down how these work for gRPC services.

## Create a simple gRPC load test

Open `src/GreetingSimulation.gatling.ts` to inspect a basic unary RPC test. The simulation demonstrates the core pattern for testing gRPC services:

### Set up the gRPC protocol

First, configure the connection to your gRPC service:

```typescript
import { simulation, scenario, atOnceUsers } from "@gatling.io/core";
import { grpc, statusCode } from "@gatling.io/grpc";

export default simulation((setUp) => {
  // Configure the gRPC server connection
  const grpcProtocol = grpc
    .serverConfiguration("greeting-server")
    .forAddress("127.0.0.1", 50051)
    .usePlaintext();  // Required for non-TLS connections

  const protocol = grpc.serverConfigurations(grpcProtocol);
```

**Important details:**
- `.forAddress()` specifies the gRPC server host and port
- `.usePlaintext()` disables TLS encryption (required for local testing without certificates)
- Use `127.0.0.1` instead of `localhost` to avoid IPv4/IPv6 resolution issues

### Define the scenario with unary RPCs

For unary RPCs (single request, single response), use the `.unary()` method:

```typescript
  // Define the user journey
  const greetingScenario = scenario("Greeting Service Test")
    .exec(
      grpc("Greet Alice")
        .unary("helloworld.Greeter/SayHello")
        .send({ name: "Alice" })
        .check(statusCode().is("OK"))
    )
    .exec(
      grpc("Greet Bob")
        .unary("helloworld.Greeter/SayHello")
        .send({ name: "Bob" })
        .check(statusCode().is("OK"))
    );
```

**Key elements:**
- **Service path format:** `package.ServiceName/MethodName` (from your `.proto` file)
- **Request payload:** Use JavaScript objects with field names matching your protobuf definition (snake_case)
- **Checks:** Validate responses with `.check()` (at minimum, verify status code is "OK")

### Shape the load

Finally, inject virtual users to generate load:

```typescript
  // Inject 5 users at once
  setUp(greetingScenario.injectOpen(atOnceUsers(5)))
    .protocols(protocol);
});
```

## Run your first gRPC load test

Execute the greeting simulation against the local server:

```bash
npx gatling run --typescript --simulation GreetingSimulation
```

Gatling will:
1. Generate TypeScript client code from the proto files in `protobuf/`
2. Compile the simulation
3. Execute 5 virtual users making greeting requests
4. Generate an HTML report

You should see output showing successful requests:

```
---- Global Information --------------------------------------------------------
> request count                                                     10 (OK=10     KO=0     )
> min response time                                                 12 (OK=12     KO=-     )
> max response time                                                 89 (OK=89     KO=-     )
================================================================================
```

Open the generated report at `target/gatling/<timestamp>/index.html` to view detailed metrics, response time distributions, and request counts.

## Test server streaming RPCs

Server streaming RPCs return multiple messages over time and require different handling. Open `src/CalculatorSimulation.gatling.ts` to see how streaming works.

### Define a server stream

```typescript
import { Session, simulation, scenario, atOnceUsers } from "@gatling.io/core";
import { grpc, statusCode } from "@gatling.io/grpc";

export default simulation((setUp) => {
  // Configure protocol
  const grpcProtocol = grpc
    .serverConfiguration("calculator-server")
    .forAddress("127.0.0.1", 50052)
    .usePlaintext();

  const protocol = grpc.serverConfigurations(grpcProtocol);

  // Define the server stream separately
  const fibonacciStream = grpc("Fibonacci Stream")
    .serverStream("calculator.Calculator/Fibonacci")
    .check(statusCode().is("OK"));

  // Use the stream in a scenario
  const calculatorScenario = scenario("Calculator Stream Test")
    .exec(
      fibonacciStream.send({ count: 10 }),
      fibonacciStream.awaitStreamEnd()
    );

  setUp(calculatorScenario.injectOpen(atOnceUsers(3)))
    .protocols(protocol);
});
```

**Critical differences for streaming:**
1. **Create stream objects separately** using `.serverStream()` before calling `.send()`
2. **Always call `.awaitStreamEnd()`** after `.send()` to wait for all messages
3. **Never reuse stream objects**—create a fresh stream for each request

### Run the streaming test

```bash
npx gatling run --typescript --simulation CalculatorSimulation
```

The report will show the stream request duration (total time to receive all messages) and validate that the stream completed successfully.

## Working with protocol buffers

### Proto file requirements

Gatling automatically generates client code from `.proto` files in the `protobuf/` directory. The directory structure must match the import paths in your proto files.

For example, if your proto file imports Google's common types:

```protobuf
syntax = "proto3";

import "google/protobuf/timestamp.proto";

service MyService {
  rpc GetData(Request) returns (Response);
}
```

You must include the imported files:

```
protobuf/
├── google/
│   └── protobuf/
│       └── timestamp.proto
└── myservice.proto
```

Download Google's proto definitions from the [protocolbuffers/protobuf repository](https://github.com/protocolbuffers/protobuf/tree/main/src/google/protobuf) and place them in `protobuf/google/protobuf/`.

### Field naming conventions

Use **snake_case** field names in your `.send()` objects to match your protobuf definitions:

```typescript
// If your proto defines: message Request { string user_id = 1; }
.send({ user_id: "12345" })  // Correct

// Not: .send({ userId: "12345" })  // Wrong
```

## Common configuration patterns

### Testing with TLS

For production gRPC services using TLS, remove `.usePlaintext()`:

```typescript
const grpcProtocol = grpc
  .serverConfiguration("secure-service")
  .forAddress("api.example.com", 443);
  // TLS is enabled by default
```

### Runtime parameters

Make your simulations configurable with runtime parameters:

```typescript
import { getParameter } from "@gatling.io/core";

const grpcHost = getParameter("grpcHost") || "127.0.0.1";
const grpcPort = parseInt(getParameter("grpcPort") || "50051");

const grpcProtocol = grpc
  .serverConfiguration("service")
  .forAddress(grpcHost, grpcPort)
  .usePlaintext();
```

Run with custom parameters:

```bash
npx gatling run --typescript --simulation GreetingSimulation \
  grpcHost=staging.example.com \
  grpcPort=443
```

### Advanced response validation

Beyond status codes, validate response content:

```typescript
import { grpc, statusCode, response } from "@gatling.io/grpc";

grpc("Greet")
  .unary("helloworld.Greeter/SayHello")
  .send({ name: "Alice" })
  .check(
    statusCode().is("OK"),
    response((r: any) => r.message.includes("Alice"), "Contains name")
  );
```

## Create realistic load profiles

For production-like testing, combine multiple scenarios with different injection patterns:

```typescript
import {
  simulation,
  scenario,
  atOnceUsers,
  rampUsers,
  constantUsersPerSec,
  getParameter
} from "@gatling.io/core";
import { grpc, statusCode } from "@gatling.io/grpc";

export default simulation((setUp) => {
  // Configure gRPC protocol
  const grpcProtocol = grpc
    .serverConfiguration("service")
    .forAddress("127.0.0.1", 50051)
    .usePlaintext();

  const protocol = grpc.serverConfigurations(grpcProtocol);

  const loadProfile = getParameter("loadProfile") || "smoke";

  // Define multiple scenarios
  const quickUsers = scenario("Quick Checks")
    .exec(
      grpc("Quick RPC")
        .unary("service.Service/Method")
        .send({ /* request */ })
        .check(statusCode().is("OK"))
    );

  const streamingUsers = scenario("Long Streams")
    .exec(
      grpc("Long Stream")
        .serverStream("service.Service/StreamMethod")
        .send({ /* request */ })
        .check(statusCode().is("OK"))
    );

  // Shape different load profiles
  const loadProfiles = {
    smoke: () => {
      setUp(
        quickUsers.injectOpen(atOnceUsers(1)),
        streamingUsers.injectOpen(atOnceUsers(1))
      ).protocols(protocol);
    },

    load: () => {
      setUp(
        quickUsers.injectOpen(
          rampUsers(50).during(60),
          constantUsersPerSec(5).during(300)
        ),
        streamingUsers.injectOpen(
          rampUsers(10).during(120)
        )
      ).protocols(protocol);
    }
  };

  // Execute selected profile
  loadProfiles[loadProfile]();
});
```

Run with:

```bash
npx gatling run --typescript --simulation MySimulation loadProfile=load
```

## Deploy to Gatling Enterprise Edition

To run gRPC tests on Gatling Enterprise (for unlimited virtual users and distributed load generation):

1. **Ensure your gRPC service is publicly accessible.** For local testing, use a tool like ngrok:

   ```bash
   ngrok tcp 50051
   ```

   Note the forwarding address (e.g., `0.tcp.ngrok.io:12345`).

2. **Package and deploy the simulation.**

   ```bash
   npx gatling enterprise-deploy \
     --simulation GreetingSimulation \
     --api-token <your_token>
   ```

3. **Configure the target server** in the Gatling Enterprise UI using JavaScript parameters:
   - `grpcHost`: Your ngrok or production hostname
   - `grpcPort`: The exposed port

4. **Run and monitor** the test through the Enterprise dashboard to view real-time metrics, concurrent connections, and throughput.

Refer to the [JavaScript CLI guide]({{< ref "/integrations/build-tools/js-cli/#deploying-on-gatling-enterprise" >}}) for advanced deployment options.

## Best practices

### 1. Never reuse stream objects

Always create new stream objects for each request:

```typescript
// ✅ Correct
const stream1 = grpc("Query1").serverStream(...);
const stream2 = grpc("Query2").serverStream(...);

.exec(stream1.send(...), stream1.awaitStreamEnd())
.exec(stream2.send(...), stream2.awaitStreamEnd())

// ❌ Wrong - causes errors
const stream = grpc("Query").serverStream(...);

.exec(stream.send(...), stream.awaitStreamEnd())
.exec(stream.send(...), stream.awaitStreamEnd())  // Will fail!
```

### 2. Start with smoke tests

Validate your simulation with minimal load before running high-intensity tests:

```bash
npx gatling run --typescript --simulation MySimulation loadProfile=smoke
```

This catches configuration errors without overwhelming your service.

### 3. Use session variables for dynamic data

For dynamic request data, use Gatling's session:

```typescript
import { Session } from "@gatling.io/core";

.exec((session: Session) => {
  const timestamp = Date.now();
  return session.set("requestTime", timestamp);
})
.exec(
  grpc("TimedQuery")
    .unary("service/Method")
    .send((session: Session) => ({
      timestamp: session.get("requestTime")
    }))
)
```

### 4. Prefer IPv4 for local testing

When testing against local Docker containers, use `127.0.0.1` instead of `localhost`:

```typescript
// ✅ Correct - forces IPv4
.forAddress("127.0.0.1", 50051)

// ❌ May fail - could resolve to IPv6
.forAddress("localhost", 50051)
```

This is especially important on macOS where `localhost` often resolves to IPv6 (`::1`) by default, which may not match your Docker container's network binding.

## Troubleshooting

### "UNAVAILABLE" status errors

**Symptom:** All requests return `UNAVAILABLE` status.

**Common causes:**
1. **Missing `.usePlaintext()`** - Add this for non-TLS services
2. **Wrong service path** - Verify format: `package.ServiceName/MethodName`
3. **IPv4/IPv6 mismatch** - Use `127.0.0.1` instead of `localhost`
4. **Service not running** - Verify with `docker ps` or check server logs

### Proto import errors

**Symptom:** Build fails with "cannot find import" errors.

**Solution:** Download required proto files (like `google/protobuf/timestamp.proto`) from the [official repository](https://github.com/protocolbuffers/protobuf/tree/main/src/google/protobuf) and place them in your `protobuf/` directory matching the import path structure.

### Stream state errors

**Symptom:** `IllegalStateException: Cannot send message on gRPC stream; current state is ClosedStream`

**Solution:** Create a new stream object for each request instead of reusing the same one. Each stream can only be used once.

### Proto code generation fails

**Symptom:** Build errors about missing proto files or syntax errors.

**Solution:**
1. Verify all proto files are in the `protobuf/` directory
2. Check that directory structure matches import paths
3. Ensure proto files have valid `syntax = "proto3";` declarations
4. Delete `target/` directory and rebuild: `rm -rf target && npx gatling run ...`

## Next steps

You now have a working gRPC load testing setup with Gatling's JavaScript/TypeScript SDK. From here, you can:

- Adapt the demo simulations to test your own gRPC services
- Add custom checks to validate response content
- Create sophisticated load patterns with multiple scenarios
- Integrate tests into CI/CD pipelines
- Scale testing with Gatling Enterprise Edition

For more advanced scenarios and configuration options, see the [Gatling gRPC documentation]({{< ref "/reference/script/grpc/" >}}).

## Reference

- [Official Gatling gRPC demo repository](https://github.com/gatling/gatling-grpc-demo)
- [Gatling gRPC protocol reference]({{< ref "/reference/script/grpc/" >}})
- [JavaScript/TypeScript SDK documentation]({{< ref "/concepts/simulation/" >}})
