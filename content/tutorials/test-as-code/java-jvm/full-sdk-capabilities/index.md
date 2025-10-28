---
title: Gatling Java SDK — From Template to Production-Ready Simulations
menutitle: Full SDK Capabilities
seotitle: Learn how to create a Gatling simulation using the Java SDK, run it locally, and deploy it to Gatling Enterprise Edition  
description: Learn how to create a Gatling simulation using the Java SDK, run it locally, and deploy it to Gatling Enterprise Edition.
---

## Why this guide exists
Build on the fast-start information from the [Installation Guide]({{< ref "tutorials/test-as-code/java-jvm/installation-guide/index.md" >}}) and learn how to assemble production-ready scripts. This page collects the core building blocks—scenarios, feeders, checks, workload models, and workflow hygiene—without walking through every editor action. If you need a slower pace, fall back to [Create your first Java-based simulation]({{< ref "tutorials/test-as-code/java-jvm/running-your-first-simulation/index.md" >}}).

## What you will cover
- Anatomy of a maintainable simulation file.
- Feeding data and correlating dynamic responses.
- Choosing injection profiles that mimic real traffic.
- Operating tips: reports, troubleshooting, and governance.

We link to the appropriate reference material whenever we skip implementation details, so you can go deeper on demand.

## Prerequisites
- Java LTS runtime (versions 11 through 25 supported, 17 or newer recommended).
- A JVM build tool such as Maven or Gradle (examples below use Maven Wrapper commands).
- A non-production system you are allowed to exercise with load tests.
- Optional: a Gatling Enterprise account for distributed execution.

## Setup recap
Clone the [gatling-java-demo](https://github.com/gatling/gatling-java-demo) project or adapt an existing Maven test module. Confirm you can run:

```shell
./mvnw gatling:test
```

Need help with IDE configuration or directory layout? Revisit the [Installation Guide]({{< ref "tutorials/test-as-code/java-jvm/installation-guide/index.md" >}}) before continuing.

## Understand the core concepts
| Concept | Description | Dig deeper |
| --- | --- | --- |
| Simulation | Executable performance test class that orchestrates your scenarios and injection profiles. | [Simulation concepts]({{< ref "/concepts/simulation" >}}) |
| Protocol | Shared configuration (e.g., base URL, headers) applied to one or more scenarios. | [HTTP protocol reference]({{< ref "/reference/script/http/protocol" >}}) |
| Scenario | Virtual user behaviour—a sequence of actions that represents a workflow. | [Scenario SDK reference]({{< ref "/concepts/scenario" >}}) |
| Feeder | Test data source (CSV, JSON, JDBC, custom code). | [Feeder reference]({{< ref "/concepts/session/feeders" >}}) |
| Checks & Assertions | Response validations and pass/fail thresholds for your run. | [Checks]({{< ref "/concepts/checks" >}}), [Assertions]({{< ref "/concepts/assertions" >}}) |
| Injection Profile | Defines the arrival rate and ramp-up strategy for virtual users. | [Injection SDK reference]({{< ref "/concepts/injection" >}}) |

**Mental model:** translate your business journey into scenarios, feed them data, run users through an injection profile, and guard the outcome with assertions.

## Assemble a baseline simulation
Start from a single-scenario template, then break logic into helpers as the script grows.

### Baseline example
{{< include-code "GetStartedSimulation#full-example" java >}}

Key points:
- Keep protocol builders immutable, then share them across scenarios with `.protocols()`.
- Use `System.getProperty` for quick parameterization (`-Dusers=100`). For richer configuration, graduate to Typesafe Config or dedicated Java classes (see the [configuration guide]({{< ref "guides/optimize-scripts/passing-parameters/" >}})).

## Enrich scenarios with data and business behavior

### Feeders: avoid hot-cache artifacts
{{< include-code "FeederExample#full-example" java >}}

- Store CSV/JSON files under `src/test/resources/data` so they ride the classpath.
- Pick the right strategy (`.circular()`, `.queue()`, `.random()`) to balance uniqueness and repeatability. More strategies live in the [feeder reference]({{< ref "concepts/session/feeders" >}}).

### Correlation: capture dynamic values
{{< include-code "AddWithCsrf#full-example" java >}}

- Add a check that extracts the token (`saveAs`).
- Reuse it in later requests with `#{csrf}` (string interpolation) or `.formParam("csrf", session("csrf"))` if you prefer method references.
- Keep a `check(status().is(200))` near every extractor to fail fast when the app changes. For advanced extractors, see the [check builders]({{< ref "reference/script/http/checks" >}}).

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

Prefer pacing by arrival rate or closed workload models? Study the [injection SDK reference]({{< ref "/concepts/injection" >}}) and [closed models guide]({{< ref "/testing-concepts/workload-models/" >}}).

## Run and inspect results
- Run locally with `./mvnw -Dgatling.simulationClass=… gatling:test` or interactive mode (`./mvnw gatling:test`).
- After each run, open `target/gatling/<simulation>-<timestamp>/index.html` and focus on p95/p99 latency, throughput, per-request errors, and response time distribution.
- Need to automate? Wire the same command into CI, or explore [Gatling Enterprise]({{< ref "/evaluate-enterprise/trial-plan/" >}}) for distributed runs and real-time dashboards.

## Troubleshooting checklist
- **Connection failures:** verify base URL, DNS, VPN/proxy rules, and SSL trust stores.
- **Server 429/503 responses:** coordinate with ops and honour rate limits—reduce load or widen ramp.
- **Too-perfect results:** add `pause()` and realistic think times; confirm you are exercising business-critical endpoints, not just static assets.
- **Data collisions:** switch feeders to `.queue()` or generate unique IDs per user; reset test data between runs.

## Operational hygiene
- Version your tests alongside application code and review them like any other pull request.
- Externalize secrets via environment variables or the Gatling Enterprise console—never hard-code credentials.
- Document target SLOs in code using assertions so CI builds surface performance regressions immediately.
- Share knowledge: keep README notes or ADRs that explain scenarios, target metrics, and known limitations.

## Where to go next
- Want a slower, instructional pace? Follow [Create your first Java-based simulation]({{< ref "tutorials/test-as-code/java-jvm/running-your-first-simulation/index.md" >}}).
- Need IDE, packaging, or Maven plugin help? Revisit the [Installation Guide]({{< ref "tutorials/test-as-code/java-jvm/installation-guide/index.md" >}}) or the [gatling-maven-plugin guide]({{< ref "integrations/build-tools/maven-plugin/index.md" >}}).
- Move beyond HTTP with the [protocol guides]({{< ref "reference/index.md" >}}) and expand checks using the [Java SDK reference]({{< ref "reference/script/http/index.md" >}}).
- Ready for team-wide load infrastructure? Scale out, share dashboards, and automate governance with [Gatling Enterprise]({{< ref "evaluate-enterprise/trial-plan/index.md" >}}).
