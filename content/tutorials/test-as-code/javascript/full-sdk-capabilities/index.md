---
title: Explore the JavaScript SDK — From Template to Production-Ready Simulations
menutitle: Full SDK Capabilities
description: Learn how to create a Gatling simulation using the JavaScript SDK, run it locally, and deploy it to Gatling Enterprise Edition.
---

## Why this guide exists
Build on the quick-start information from the [Installation Guide]({{< ref "tutorials/test-as-code/javascript/installation-guide/" >}}) and learn how to assemble production-ready scripts. This page collects the core building blocks—scenarios, feeders, checks, workload models, and operational tips—without forcing you through every editor action. If you need a slower pace, fall back to [Create your first JavaScript-based simulation]({{< ref "tutorials/test-as-code/javascript/running-your-first-simulation/" >}}).

## What you will cover
- Anatomy of a maintainable simulation module.
- Feeding data and correlating dynamic responses.
- Choosing injection profiles that mimic real traffic.
- Running, troubleshooting, and governing JavaScript-based load tests.

When we skip implementation details, we link to the relevant reference material so you can dive deeper on demand.

## Prerequisites
- Node.js 18 or newer (LTS releases recommended) with npm 8+.
- A TypeScript-friendly editor (VS Code, WebStorm, etc.).
- A non-production system you are allowed to exercise with load tests.
- Optional: a Gatling Enterprise account for distributed execution.

## Setup recap
Clone the [se-ecommerce-demo-gatling-tests](https://github.com/gatling/se-ecommerce-demo-gatling-tests) repository, install dependencies, and confirm that Gatling runs locally:

```bash
git clone https://github.com/gatling/se-ecommerce-demo-gatling-tests.git
cd se-ecommerce-demo-gatling-tests/javascript
npm install
npm run gatling:test -- --simulation src/basicSimulation.gatling
```

Need help with IDE configuration or project layout? Revisit the [Installation Guide]({{< ref "tutorials/test-as-code/javascript/installation-guide/" >}}) before continuing.

## Understand the core concepts
| Concept | Description | Deep dive |
| --- | --- | --- |
| Simulation | Default export of a `.gatling.ts` file that wires scenarios and injection. | [Simulation concepts]({{< ref "/concepts/simulation" >}}) |
| Protocol | Shared configuration (e.g., base URL, headers) applied to one or more scenarios. | [HTTP protocol reference]({{< ref "/reference/script/http/protocol" >}}) |
| Scenario | Virtual user behavior—chains of requests, pauses, and logic. | [Scenario reference]({{< ref "/concepts/scenario" >}}) |
| Feeder | Data source (arrays, CSV, custom functions) injected into sessions. | [Feeders reference]({{< ref "/concepts/session/feeders" >}}) |
| Checks & Assertions | Response validations and pass/fail thresholds. | [Checks reference]({{< ref "/reference/script/http/checks" >}}), [Assertions]({{< ref "/concepts/assertions" >}}) |
| Injection Profile | Defines arrival rate and ramp-up strategy. | [Injection reference]({{< ref "/concepts/injection" >}}) |

**Mental model:** translate your business journey into scenarios, feed them contextual data, drive them with an injection profile, and guard the outcome with assertions.

## Assemble a baseline simulation
Start from a single module, then break logic into helpers as the script grows.

### Baseline example
{{< include-code "GetStartedSimulation#full-example" ts >}}

Key points:
- Keep protocol builders immutable and share them across scenarios with `.protocols()`.
- Use environment variables (or typed configuration helpers) for quick parameterisation.
- Export the simulation as the module default so the Gatling CLI can pick it up automatically.

## Enrich scenarios with data and behavior

### Feeders: avoid hot-cache artifacts
```ts
import { exec, scenario } from "@gatling.io/core";
import { http, status } from "@gatling.io/http";
import products from "./resources/products.json" assert { type: "json" };

const productFeeder = () =>
  products[Math.floor(Math.random() * products.length)];

const browse = scenario("Browse with data").exec(
  (session) => session.set("product", productFeeder()),
  http("View product")
    .get((session) => `/items/${session.get<string>("product").sku}`)
    .check(status().is(200))
);
```

- Store CSV/JSON under `src/resources` so they are bundled.
- Pick a strategy (`random`, `queue`, `circular`) that balances uniqueness and repeatability. See the [Feeders reference]({{< ref "/concepts/session/feeders" >}}) for additional helpers.

### Correlation: capture dynamic values
```ts
import { scenario } from "@gatling.io/core";
import { css, http, status } from "@gatling.io/http";

const addWithCsrf = scenario("Add with CSRF")
  .exec(
    http("Get account")
      .get("/account")
      .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
  )
  .pause(1)
  .exec(
    http("Submit form")
      .post("/account")
      .formParam("csrfToken", (session) => session.get<string>("csrf"))
      .check(status().in(200, 302))
  );
```

- Pair extractors with a status/content check so failures surface immediately.
- Choose the extractor that matches the response format (`jsonPath`, `css`, `regex`, etc.). The [check reference]({{< ref "reference/script/http/checks" >}}) lists every option.

### Compose journeys
Break long user journeys into reusable chains:

```ts
import { exec, scenario } from "@gatling.io/core";
import { http, status } from "@gatling.io/http";

const search = exec(
  http("Search")
    .get("/search")
    .queryParam("q", (session) => session.get<string>("term"))
    .check(status().is(200))
);

const view = exec(
  http("View item")
    .get((session) => `/items/${session.get<string>("sku")}`)
    .check(status().in(200, 304))
);

const browse = scenario("Browse").repeat(3).on(search, view);
```

Extract common chains or helpers into separate modules (e.g., `src/utils/protocols.ts`) and import them where needed.

## Model realistic workloads
Use injection profiles to express how traffic arrives. Combine warm-up, steady state, and cool-down phases to mimic production.

```ts
setUp(
  browse.injectOpen(
    nothingFor(5),
    rampUsers(50).during(30),
    constantUsersPerSec(10).during(60)
  ),
  addWithCsrf.injectOpen(constantUsersPerSec(2).during(90))
).assertions(
  global().successfulRequests().percent().gt(99),
  details("Browse", "Search").responseTime().percentile3().lt(1500)
);

// Import nothingFor, rampUsers, constantUsersPerSec, global, and details from "@gatling.io/core".
```

Need a refresher on each injection helper? Review the [injection reference]({{< ref "/concepts/injection" >}}) and the [workload modelling guide]({{< ref "/testing-concepts/workload-models" >}}).

## Run and inspect results
- Run locally with `npm run gatling:test -- --simulation <path>` or in interactive mode with `--interactive`.
- After each run, open `target/gatling/<simulation>-<timestamp>/index.html` to review percentiles, throughput, and errors.
- Automate the same commands in CI, or evaluate [Gatling Enterprise]({{< ref "/evaluate-enterprise/trial-plan/" >}}) for distributed load and real-time dashboards.

## Operational hygiene
- Commit simulations alongside application code and review them like any other change.
- Externalize secrets via environment variables or Gatling Enterprise [configuration-as-code]({{< ref "/reference/run-tests/sources/configuration-as-code/" >}}).
- Encode SLOs with assertions so builds fail fast when thresholds are breached.
- Document scenario intent, metrics, and known caveats in README files or ADRs.

## Where to go next
- Want a slower, instructional pace? Follow [Create your first JavaScript-based simulation]({{< ref "tutorials/test-as-code/javascript/running-your-first-simulation/" >}}).
- Need setup or packaging reminders? Revisit the [Installation Guide]({{< ref "tutorials/test-as-code/javascript/installation-guide/" >}}).
- Expand beyond HTTP with the [protocol guides]({{< ref "reference/" >}}) and the [HTTP scripting reference]({{< ref "reference/script/http/" >}}).
- Ready for team-wide load infrastructure? Scale out and automate with [Gatling Enterprise]({{< ref "evaluate-enterprise/trial-plan/" >}}).
