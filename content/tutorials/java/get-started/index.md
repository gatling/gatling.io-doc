---
title: Get started with the Java SDK
---

## Why this guide exists
Use this page when you already understand the basics from the [Java introduction]({{< ref "tutorials/java/intro/index.md" >}}) and want a fuller view of Gatling’s Java tooling. It gathers the essential building blocks—scenarios, feeders, checks, workload models, and project hygiene—without forcing you through a step-by-step tutorial. If you prefer copy-and-paste instructions, switch to [Create your first Java-based simulation]({{< ref "tutorials/java/scripting-intro/index.md" >}}).

## Prerequisites
- Java 17 (or newer) LTS runtime.
- A JVM build tool such as Maven or Gradle (examples below use Maven Wrapper commands).
- A non-production system you are allowed to exercise with load tests.
- Optional: a Gatling Enterprise account for distributed execution.

## Setup recap
Clone the [gatling-java-demo](https://github.com/gatling/gatling-java-demo) project or adapt an existing Maven test module. Confirm you can run:

```shell
./mvnw gatling:test
```

For IDE configuration, wrapper details, and project anatomy, defer to the [introduction page]({{< ref "tutorials/java/intro/index.md" >}}).

## Understand the core concepts
| Concept | Description |
| --- | --- |
| Simulation | Executable performance test class that orchestrates your scenarios and injection profiles. |
| Protocol | Shared configuration (e.g., base URL, headers) applied to one or more scenarios. |
| Scenario | Virtual user behaviour—a sequence of actions that represents a workflow. |
| Feeder | Test data source (CSV, JSON, JDBC, custom code). |
| Checks & Assertions | Response validations and pass/fail thresholds for your run. |
| Injection Profile | Defines the arrival rate and ramp-up strategy for virtual users. |

**Mental model:** translate your business journey into scenarios, feed them data, run users through an injection profile, and guard the outcome with assertions.

## Assemble a baseline simulation
Start from a single-scenario template, then branch into helper classes as the script grows.

### Baseline example
{{< include-code "GetStartedSimulation#full-example" java >}}

Key points:
- Keep protocol builders immutable, then share them across scenarios with `.protocols()`.
- Use `System.getProperty` for quick parameterization (`-Dusers=100`). For complex configuration, graduate to Typesafe Config or dedicated Java classes.

## Enrich scenarios with data and business behaviour

### Feeders: avoid hot-cache artifacts
{{< include-code "FeederExample#full-example" java >}}

- Store CSV/JSON files under `src/test/resources/data` so they ride the classpath.
- Pick the right strategy (`.circular()`, `.queue()`, `.random()`) to balance uniqueness and repeatability.

### Correlation: capture dynamic values
{{< include-code "AddWithCsrf#full-example" java >}}

- Add a check that extracts the token (`saveAs`).
- Reuse it in later requests with `#{csrf}` (string interpolation) or `.formParam("csrf", session("csrf"))` if you prefer method references.
- Keep a `check(status().is(200))` near every extractor to fail fast when the app changes.

### Compose journeys
Break long journeys into smaller chains and reuse them:

```java
ChainBuilder search = exec(http("Search").get("/search").queryParam("q", "#{term}").check(status().is(200)));
ChainBuilder view   = exec(http("View").get("/items/#{sku}").check(status().in(200, 304)));

ScenarioBuilder browse = scenario("Browse").feed(productFeeder).exec(search, view);
```

Declare shared helpers in `src/test/java/.../utils` and import them from your simulations to keep logic tidy.

## Model realistic workloads
Use injection profiles to express how traffic arrives. Combine warm-up, steady state, and cool-down phases to mimic production.

{{< include-code "DualJourneySimulation#full-example" java >}}

Highlights:
- Mix scenarios with distinct arrival rates to mirror different user personas.
- Detect regressions with assertions on `global()` and `details("scenario", "request")`.
- Keep human-readable names (`"01 Browse"`) so reports stay sorted and readable.

Common injection shortcuts:
- `atOnceUsers(x)` for smoke tests or spikes.
- `rampUsers(x).during(t)` to smooth into load.
- `constantUsersPerSec(rate).during(t)` when you care about arrival rate more than concurrent sessions.
- `heavisideUsers(x).during(t)` for S-curve ramps that avoid sudden jumps.

## Run and inspect results
- Run locally with `./mvnw -Dgatling.simulationClass=… gatling:test` or interactive mode (`./mvnw gatling:test`).
- After each run, open `target/gatling/<simulation>-<timestamp>/index.html`.
- Focus on p95/p99 latency, throughput, per-request errors, and response time distribution. Correlate regressions with application metrics.

## Troubleshooting checklist
- **Connection failures:** verify base URL, DNS, VPN/proxy rules, and SSL trust stores.
- **Server 429/503 responses:** coordinate with ops and honour rate limits—reduce load or widen ramp.
- **Too-perfect results:** add `pause()` and realistic think times; confirm you are requesting business-critical endpoints, not static assets only.
- **Data collisions:** switch feeders to `.queue()` or generate unique IDs per user; reset test data between runs.

## Operational hygiene
- Version your tests alongside application code and review them like any other pull request.
- Externalize secrets via environment variables or the Gatling Enterprise console—never hard-code credentials.
- Document target SLOs in code using assertions so CI builds surface performance regressions immediately.

## Where to go next
- Want a slower, instructional pace? Follow [Create your first Java-based simulation]({{< ref "tutorials/java/scripting-intro/index.md" >}}).
- Need IDE, packaging, or Maven plugin help? Revisit the [Java introduction]({{< ref "tutorials/java/intro/index.md" >}}) or the [gatling-maven-plugin guide]({{< ref "integrations/build-tools/maven-plugin/index.md" >}}).
- Move beyond HTTP with the [protocol guides]({{< ref "reference/index.md" >}}) and expand checks using the [Java DSL reference]({{< ref "reference/script/http/index.md" >}}).
- Scale out, share dashboards, and automate governance with [Gatling Enterprise]({{< ref "evaluate-enterprise/trial-plan/index.md" >}}).
