---
menutitle: Testing gRPC with JS/TS
title: Test gRPC with the JS/TS SDK
seotitle: How to Test gRPC with the JS/TS SDK
description: Step-by-step guide to testing gRPC using the JavaScript/TypeScript SDK.
lead: Learn how to load test gRPC services with the JavaScript/TypeScript SDK using the grpc-js fleet-tracking demo.
---

## Introduction
This guide walks you through creating comprehensive load tests for gRPC services using Gatling's TypeScript SDK. We'll build tests for a real-world vehicle telemetry system that demonstrates both unary RPCs and server streaming.

### What You'll Learn

By the end of this guide, you'll be able to:
- Set up Gatling for gRPC load testing with TypeScript
- Configure protocol buffers for code generation
- Test unary RPC endpoints
- Test server streaming endpoints
- Create multiple load profiles for different scenarios
- Deploy and run tests on Gatling Enterprise

### Prerequisites

Before starting, you'll need:
- **Node.js v20 or later** installed
- **Basic understanding of gRPC** and protocol buffers
- **A gRPC service to test** (we'll use a vehicle telemetry service as our example)

If you haven't worked with gRPC before, we recommend reviewing the [gRPC documentation](https://grpc.io/docs/) to understand the core concepts of services, RPCs, and protocol buffers.

### Understanding the Example Service

Our example tests a Vehicle Telemetry Service that provides four gRPC endpoints:

1. **GetFleetSnapshot** (unary) - Retrieves current state of all vehicles
2. **QueryTelemetryHistory** (server streaming) - Streams historical telemetry data
3. **StreamVehicleSnapshots** (server streaming) - Real-time vehicle data stream
4. **GetHistoricalAggregates** (unary) - Returns time-bucketed analytics

This mix of unary and streaming RPCs represents common patterns you'll encounter in production gRPC services.

## Project Setup

### Initialize Your Gatling Project

First, create a new directory and initialize the Gatling TypeScript project:

```bash
mkdir gatling-grpc-tests
cd gatling-grpc-tests
npm init -y
npm install @gatling.io/cli @gatling.io/core @gatling.io/grpc
```

### Configure TypeScript

Create a `tsconfig.json`:

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "ES2022",
    "moduleResolution": "bundler",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "resolveJsonModule": true
  },
  "include": ["src/**/*"]
}
```

### Set Up Protocol Buffers

Create the directory structure for your proto files:

```bash
mkdir -p protobuf/google/protobuf
mkdir -p src
mkdir -p resources
```

Gatling's gRPC plugin automatically generates TypeScript code from your `.proto` files. Place your service definition in `protobuf/telemetry.proto`:

```protobuf
syntax = "proto3";

package telemetry.v1;

import "google/protobuf/timestamp.proto";

service TelemetryService {
  rpc GetFleetSnapshot(FleetSnapshotRequest) returns (FleetSnapshotResponse);
  rpc QueryTelemetryHistory(HistoryRequest) returns (stream TelemetrySnapshot);
  rpc StreamVehicleSnapshots(StreamRequest) returns (stream VehicleSnapshot);
  rpc GetHistoricalAggregates(AggregatesRequest) returns (AggregatesResponse);
}

message FleetSnapshotRequest {
  repeated string vehicle_ids = 1;
  bool include_metrics = 2;
}

message FleetSnapshotResponse {
  repeated VehicleSnapshot snapshots = 1;
  FleetMetrics metrics = 2;
}

// Additional message definitions...
```

**Important**: If your proto files import Google's common types (like `google/protobuf/timestamp.proto`), you must include them in your `protobuf/` directory. Download the official definitions from the [protocolbuffers/protobuf repository](https://github.com/protocolbuffers/protobuf/tree/main/src/google/protobuf) and place them in `protobuf/google/protobuf/`.

### Configure Gatling

Create `.gatling/package.conf` to tell Gatling where to find your proto files:

```hocon
gatling {
  package {
    outputDirectory = "target/bundle"
  }
  grpc {
    protocPlugin {
      path = "protobuf"
    }
  }
}
```

## Creating Your First gRPC Test

Let's start with a simple test for the unary `GetFleetSnapshot` endpoint. Create `src/fleetTest.gatling.ts`:

```typescript
import { simulation, scenario, atOnceUsers } from "@gatling.io/core";
import { grpc, statusCode } from "@gatling.io/grpc";

export default simulation((setUp) => {
  // --- Server Configuration ---
  // Configure the gRPC server connection
  const telemetryServer = grpc
    .serverConfiguration("telemetry")
    .forAddress("127.0.0.1", 50051)
    .usePlaintext();  // Use for non-TLS connections

  const testProtocol = grpc.serverConfigurations(telemetryServer);

  // --- Scenario Definition ---
  // Test the GetFleetSnapshot endpoint
  const fleetScenario = scenario("Fleet Snapshot Test")
    .exec(
      grpc("GetFleetSnapshot - All Vehicles")
        .unary("telemetry.v1.TelemetryService/GetFleetSnapshot")
        .send({ vehicle_ids: [], include_metrics: true })
        .check(statusCode().is("OK"))
    )
    .exec(
      grpc("GetFleetSnapshot - Specific Vehicle")
        .unary("telemetry.v1.TelemetryService/GetFleetSnapshot")
        .send({ vehicle_ids: ["vehicle-1"], include_metrics: false })
        .check(statusCode().is("OK"))
    );

  // --- Load Injection ---
  // Run with 2 virtual users executing once
  setUp(fleetScenario.injectOpen(atOnceUsers(2))).protocols(testProtocol);
});
```

### Understanding the Code

Let's break down the key components:

**Server Configuration**: The `serverConfiguration()` method creates a connection profile for your gRPC service. The `.usePlaintext()` method is crucial for non-TLS connections - without it, Gatling defaults to expecting TLS/SSL.

**RPC Invocation**: For unary RPCs, use the `.unary()` method with the fully qualified service path (`package.service/Method`). The `.send()` method accepts a JavaScript object matching your protobuf message structure.

**Field Naming**: Use snake_case field names as defined in your `.proto` files. Gatling's code generator handles the mapping automatically.

**Checks**: The `.check()` method validates responses. At minimum, verify the status code is "OK" to ensure the RPC succeeded.

### Running Your First Test

Build and run the simulation:

```bash
npx gatling run --typescript --simulation fleetTest
```

You should see output showing successful requests:

```
---- Global Information --------------------------------------------------------
> request count                                                      4      (OK=4      KO=0     )
> min response time                                                 45      (OK=45     KO=-     )
> max response time                                                156      (OK=156    KO=-     )
================================================================================
```

## Testing Server Streaming

Server streaming RPCs require different handling since they return multiple messages over time. Create `src/streamingTest.gatling.ts`:

```typescript
import {
  Session,
  simulation,
  scenario,
  atOnceUsers
} from "@gatling.io/core";
import { grpc, statusCode } from "@gatling.io/grpc";

export default simulation((setUp) => {
  // --- Server Configuration ---
  const telemetryServer = grpc
    .serverConfiguration("telemetry")
    .forAddress("127.0.0.1", 50051)
    .usePlaintext();

  const testProtocol = grpc.serverConfigurations(telemetryServer);

  // --- Helper Function ---
  // Build a time range for historical queries
  const buildTimeRange = () => {
    const endSeconds = Math.floor(Date.now() / 1000);
    const startSeconds = endSeconds - 3600; // Last hour
    return {
      start: { seconds: startSeconds, nanos: 0 },
      end: { seconds: endSeconds, nanos: 0 }
    };
  };

  // --- Stream Definition ---
  // Define the server stream separately from execution
  const historyStream = grpc("QueryTelemetryHistory")
    .serverStream("telemetry.v1.TelemetryService/QueryTelemetryHistory")
    .check(statusCode().is("OK"));

  // --- Scenario Definition ---
  const streamingScenario = scenario("Historical Data Stream")
    // Store the time range in session for use in the request
    .exec((session: Session) => {
      const timeRange = buildTimeRange();
      return session.set("timeRange", timeRange);
    })
    // Send the stream request and wait for completion
    .exec(
      historyStream.send((session: Session) => ({
        vehicle_ids: [],
        range: session.get("timeRange"),
        limit: 100
      })),
      historyStream.awaitStreamEnd()
    );

  // --- Load Injection ---
  setUp(streamingScenario.injectOpen(atOnceUsers(1))).protocols(testProtocol);
});
```

### Key Differences for Streaming

**Stream Object Lifecycle**: Create the stream object using `.serverStream()` separately from the `.send()` and `.awaitStreamEnd()` calls. This allows proper stream state management.

**Session Variables**: Use Gatling's session to store dynamic values needed for request construction. The `.exec()` method with a function parameter lets you manipulate session state.

**Awaiting Completion**: Always call `.awaitStreamEnd()` after `.send()` to ensure Gatling waits for all streamed messages before considering the request complete.

**Important**: Never reuse the same stream object for multiple requests. Each `.exec()` block should use a fresh stream definition if you need multiple streaming calls in one scenario.

## Building a Comprehensive Load Test

Now let's create a full simulation with multiple scenarios and configurable load profiles. Create `src/telemetrySimulation.gatling.ts`:

```typescript
import {
  Session,
  simulation,
  scenario,
  atOnceUsers,
  rampUsers,
  constantUsersPerSec,
  getParameter
} from "@gatling.io/core";
import { grpc, statusCode } from "@gatling.io/grpc";

export default simulation((setUp) => {
  // --- Configuration Parameters ---
  // Allow runtime configuration via CLI parameters
  const grpcHost = getParameter("grpcHost") || "127.0.0.1";
  const grpcPort = parseInt(getParameter("grpcPort") || "50051");
  const loadProfile = getParameter("loadProfile") || "mixed";
  const testDurationSeconds = parseInt(getParameter("testDurationSeconds") || "60");

  // --- Server Configuration ---
  const telemetryServer = grpc
    .serverConfiguration("telemetry")
    .forAddress(grpcHost, grpcPort)
    .usePlaintext();

  const baseGrpcProtocol = grpc.serverConfigurations(telemetryServer);

  // --- Helper Functions ---
  const buildTimeRange = (durationSeconds: number = 3600) => {
    const endSeconds = Math.floor(Date.now() / 1000);
    const startSeconds = endSeconds - durationSeconds;
    return {
      start: { seconds: startSeconds, nanos: 0 },
      end: { seconds: endSeconds, nanos: 0 }
    };
  };

  // --- Scenario 1: Fleet Snapshots ---
  const fleetScenario = scenario("Fleet Queries")
    .exec(
      grpc("GetFleetSnapshot - All")
        .unary("telemetry.v1.TelemetryService/GetFleetSnapshot")
        .send({ vehicle_ids: [], include_metrics: true })
        .check(statusCode().is("OK"))
    )
    .pause(1)
    .exec(
      grpc("GetFleetSnapshot - Filtered")
        .unary("telemetry.v1.TelemetryService/GetFleetSnapshot")
        .send({ vehicle_ids: ["vehicle-1", "vehicle-2"], include_metrics: false })
        .check(statusCode().is("OK"))
    );

  // --- Scenario 2: Historical Queries ---
  const historyStream = grpc("QueryHistory")
    .serverStream("telemetry.v1.TelemetryService/QueryTelemetryHistory")
    .check(statusCode().is("OK"));

  const historyScenario = scenario("Historical Data")
    .exec((session: Session) => session.set("timeRange", buildTimeRange(3600)))
    .exec(
      historyStream.send((session: Session) => ({
        vehicle_ids: [],
        range: session.get("timeRange"),
        limit: 100
      })),
      historyStream.awaitStreamEnd()
    );

  // --- Scenario 3: Aggregates ---
  const aggregatesScenario = scenario("Analytics Queries")
    .exec((session: Session) => session.set("timeRange", buildTimeRange(3600)))
    .exec(
      grpc("GetAggregates - Speed")
        .unary("telemetry.v1.TelemetryService/GetHistoricalAggregates")
        .send((session: Session) => ({
          vehicle_ids: [],
          range: session.get("timeRange"),
          aggregate_type: "SPEED",
          window_seconds: 300
        }))
        .check(statusCode().is("OK"))
    );

  // --- Load Profile Configuration ---
  // Define different load patterns based on the loadProfile parameter
  const loadProfiles = {
    smoke: () => {
      setUp(
        fleetScenario.injectOpen(atOnceUsers(1)),
        historyScenario.injectOpen(atOnceUsers(1)),
        aggregatesScenario.injectOpen(atOnceUsers(1))
      ).protocols(baseGrpcProtocol);
    },

    fleet: () => {
      setUp(
        fleetScenario.injectOpen(
          rampUsers(10).during(30),
          constantUsersPerSec(5).during(testDurationSeconds)
        )
      ).protocols(baseGrpcProtocol);
    },

    mixed: () => {
      setUp(
        fleetScenario.injectOpen(rampUsers(5).during(20)),
        historyScenario.injectOpen(atOnceUsers(2)),
        aggregatesScenario.injectOpen(rampUsers(3).during(15))
      ).protocols(baseGrpcProtocol);
    }
  };

  // Execute the selected load profile
  const profileFunction = loadProfiles[loadProfile as keyof typeof loadProfiles];
  if (profileFunction) {
    profileFunction();
  } else {
    throw new Error(`Unknown load profile: ${loadProfile}`);
  }
});
```

### Running Different Load Profiles

Execute specific load profiles using CLI parameters:

```bash
# Quick smoke test
npx gatling run --typescript --simulation telemetrySimulation \
  loadProfile=smoke

# Heavy fleet queries
npx gatling run --typescript --simulation telemetrySimulation \
  loadProfile=fleet \
  testDurationSeconds=120

# Test against a remote server
npx gatling run --typescript --simulation telemetrySimulation \
  loadProfile=mixed \
  grpcHost=telemetry.example.com \
  grpcPort=443
```

## Common Configuration Patterns

### Testing with TLS

For production gRPC services using TLS, remove the `.usePlaintext()` call:

```typescript
const telemetryServer = grpc
  .serverConfiguration("telemetry")
  .forAddress("telemetry.example.com", 443);
  // TLS is enabled by default when .usePlaintext() is omitted
```

If you need custom certificates:

```typescript
const telemetryServer = grpc
  .serverConfiguration("telemetry")
  .forAddress("telemetry.example.com", 443)
  .useTls();  // Explicitly enable TLS

// Place your certificates in resources/certs/
// Gatling will automatically use them for the connection
```

### IPv4 vs IPv6 Considerations

When testing against local Docker containers or services that only bind to IPv4, use `127.0.0.1` instead of `localhost`:

```typescript
// ✅ Correct - forces IPv4
.forAddress("127.0.0.1", 50051)

// ❌ May fail - could resolve to IPv6
.forAddress("localhost", 50051)
```

This is especially important on macOS where `localhost` often resolves to IPv6 (`::1`) by default.

### Advanced Response Validation

Beyond status code checks, you can validate response content:

```typescript
import { grpc, statusCode, response } from "@gatling.io/grpc";

grpc("GetFleetSnapshot")
  .unary("telemetry.v1.TelemetryService/GetFleetSnapshot")
  .send({ vehicle_ids: [], include_metrics: true })
  .check(
    statusCode().is("OK"),
    response((r: any) => r.snapshots.length > 0, "Has snapshots"),
    response((r: any) => r.metrics !== undefined, "Has metrics")
  );
```

## Deploying Tests to Gatling Enterprise Edition

To run your tests on Enterprise Edition, you need a publicly accessible gRPC endpoint.

### Option 1: Using Railway or similar PaaS

Railway provides a simple way to deploy your gRPC service with a persistent endpoint:

1. **Prepare your service** with a `Dockerfile` or let Railway auto-detect your runtime
2. **Deploy to Railway** via their GitHub integration
3. **Configure environment variables** for your gRPC settings
4. **Use the Railway endpoint** in your Gatling simulation

```bash
# Run against your Railway deployment
npx gatling run --typescript --simulation telemetrySimulation \
  grpcHost=your-app.up.railway.app \
  grpcPort=50051 \
  loadProfile=mixed
```

### Option 2: Using ngrok for Quick Testing

For temporary testing without deployment:

```bash
# Start your local service
npm run start-grpc-service

# In another terminal, expose it via ngrok
ngrok tcp 50051

# Use the ngrok address (e.g., 0.tcp.ngrok.io:12345)
npx gatling run --typescript --simulation telemetrySimulation \
  grpcHost=0.tcp.ngrok.io \
  grpcPort=12345 \
  loadProfile=smoke
```

Note that ngrok's free tier provides ephemeral URLs that change each session, making Railway a better choice for repeated or scheduled tests.

## Best Practices

### 1. Separate Stream Objects

Always create new stream objects for each request. Never reuse a stream:

```typescript
// ✅ Correct - separate streams
const stream1 = grpc("Query1").serverStream(...);
const stream2 = grpc("Query2").serverStream(...);

.exec(stream1.send(...), stream1.awaitStreamEnd())
.exec(stream2.send(...), stream2.awaitStreamEnd())

// ❌ Wrong - reusing stream causes errors
const stream = grpc("Query").serverStream(...);

.exec(stream.send(...), stream.awaitStreamEnd())
.exec(stream.send(...), stream.awaitStreamEnd())  // Will fail!
```

### 2. Use Session Variables for Dynamic Data

For dynamic request data, use session variables:

```typescript
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

### 3. Configure Reasonable Timeouts

For streaming RPCs that may run long, configure appropriate timeouts in your Gatling configuration:

```hocon
gatling {
  http {
    requestTimeout = 120000  // 2 minutes for long streams
  }
}
```

### 4. Start with Smoke Tests

Always validate your simulation with a smoke test (1 user per scenario) before running high-load tests:

```bash
npx gatling run --typescript --simulation telemetrySimulation \
  loadProfile=smoke
```

This quickly identifies configuration issues without generating excessive load.

## Troubleshooting

### "UNAVAILABLE" Status Errors

**Symptom**: All requests return `UNAVAILABLE` status.

**Common causes**:
1. **Missing `.usePlaintext()`** - Add this for non-TLS services
2. **Wrong service path** - Verify the format: `package.ServiceName/MethodName`
3. **IPv4/IPv6 mismatch** - Use `127.0.0.1` instead of `localhost`
4. **Service not running** - Verify your gRPC service is listening

### Proto Import Errors

**Symptom**: Build fails with "cannot find import" errors.

**Solution**: Download the required proto files (like `google/protobuf/timestamp.proto`) from the [official repository](https://github.com/protocolbuffers/protobuf/tree/main/src/google/protobuf) and place them in your `protobuf/` directory matching the import path.

### Stream State Errors

**Symptom**: `IllegalStateException: Cannot send message on gRPC stream; current state is ClosedStream`

**Solution**: Create a new stream object for each request instead of reusing the same one.

## Next Steps

You now have a complete gRPC load testing setup with Gatling. From here, you can:

- Add custom checks to validate response content
- Create sophisticated load patterns with Gatling's injection profiles
- Integrate tests into CI/CD pipelines
- Scale testing with Gatling Enterprise

For more advanced scenarios and configuration options, see the [Gatling gRPC documentation](https://docs.gatling.io/reference/script/protocols/grpc/).

## Example Repository

A complete working example implementing all patterns from this guide is available at:
https://github.com/brownshaun/mqtt-js/tree/main/gatling

The repository includes:
- Full TypeScript simulations for unary and streaming RPCs
- Protocol buffer definitions with proper imports
- Multiple load profiles
- Deployment configurations for Railway and ngrok
- A complete vehicle telemetry backend for testing
