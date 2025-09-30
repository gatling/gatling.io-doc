---
title: Intro to Load Testing with the JavaScript SDK
description: Spin up a Gatling JavaScript project quickly, run a first simulation with Node.js, and plan your next steps.
---

## Why start here

Already comfortable with Node.js tooling and TypeScript? Use this page as your fast track to a working Gatling JavaScript project. You will find the minimal setup, the project skeleton, and the commands needed to execute simulations locally or in CI.

### Choose the companion that fits your journey

- Need a line-by-line walkthrough? Follow [Create your first JavaScript-based simulation]({{< ref "tutorials/javascript/scripting-intro-js/" >}}).
- Want a deeper catalogue of feeders, checks, and workload ideas? Continue with [Get started with the JavaScript SDK]({{< ref "tutorials/javascript/get-started/" >}}).
- Looking for SDK syntax or API details? Go straight to the [HTTP scripting reference]({{< ref "reference/script/http/" >}}).

## Quick checklist for experienced teams

| Question | Fast answer |
| --- | --- |
| Which Node.js? | Use LTS v18+ (v20 recommended). Ensure `npm` is v8+ or use `pnpm`/`yarn` if preferred. |
| How do dependencies install? | Use `npm install` inside the project; the Gatling CLI is provided via the `gatling` npm package. |
| How do we parameterize runs? | Pass environment variables (e.g., `BASE_URL`) or leverage `.env` files loaded by your launcher script. |
| Where do HTML reports land? | `target/gatling/<simulation>-<timestamp>/index.html` after each run. |

## Minimal prerequisites

- Node.js 18+ with npm 8+ (LTS releases recommended).
- A code editor with TypeScript support.
- Optional: a Gatling Enterprise account if you need managed, distributed load generation.

Confirm your toolchain:

```bash
node -v
npm -v
```

## Bootstrap a JavaScript project quickly

1. Fetch the demo project (clone or download a ZIP):
   ```bash
   git clone https://github.com/gatling/se-ecommerce-demo-gatling-tests.git
   cd se-ecommerce-demo-gatling-tests/javascript
   npm install
   ```
2. Open the folder in your editor (VS Code, WebStorm, or similar).
3. Prefer yarn or pnpm? Generate a lockfile with your tool of choice and update scripts accordingly.

If you would rather follow a step-by-step tutorial, switch to [Create your first JavaScript-based simulation]({{< ref "tutorials/javascript/scripting-intro-js/" >}}).

## Recommended project layout (JavaScript)

```text
javascript/
├─ src/
│  ├─ basicSimulation.gatling.ts      # Simulation entry point
│  └─ data/                           # Feeders and payloads (optional)
├─ package.json                       # Dependencies + Gatling CLI commands
├─ tsconfig.json                      # TypeScript configuration for simulations
├─ gatling.conf                       # Optional overrides
└─ target/                            # Build artifacts + reports (created after runs)
```

- Keep simulation sources under `src/` with the `.gatling.ts` (or `.js`) suffix so the Gatling bundler picks them up.
- Place CSV/JSON feeders under `src/data` or any directory bundled by your build step.
- Use `package.json` scripts to codify how you launch Gatling locally and in CI.

## NPM essentials for Gatling

Add helpful scripts to `package.json` so teammates do not memorize long commands:

```json
{
  "scripts": {
    "gatling:test": "gatling test",
    "gatling:select": "gatling test --interactive",
    "gatling:enterprise": "gatling enterprise-package"
  },
  "dependencies": {
    "@gatling.io/core": "{{< var gatlingJsVersion >}}",
    "@gatling.io/http": "{{< var gatlingJsVersion >}}"
  }
}
```

Check the [HTTP scripting reference]({{< ref "reference/script/http/" >}}) for optional protocol modules or utility packages.

## Base simulation template

```ts
// src/basicSimulation.gatling.ts
import { constantUsersPerSec, scenario, simulation } from "@gatling.io/core";
import { http } from "@gatling.io/http";

export default simulation((setUp) => {
  const httpProtocol = http
    .baseUrl(process.env.BASE_URL ?? "http://localhost:8080")
    .acceptHeader("application/json");

  const scn = scenario("Smoke").exec(http("GET /").get("/"));

  setUp(scn.injectOpen(constantUsersPerSec(1).during(30))).protocols(httpProtocol);
});
```

Run the simulation locally from the project root:

```bash
npm run gatling:test -- --simulation src/basicSimulation.gatling
```

Drop the `--simulation` flag to choose interactively or run `npm run gatling:select` if you added the script above.

## Parameterize with environment variables

| Setting | How to pass | Default |
| --- | --- | --- |
| Target base URL | `BASE_URL=https://api.example.com npm run gatling:test` | `http://localhost:8080` |
| Virtual users per second | `USERS_PER_SEC=5` via env or `.env` | `1` |
| Ramp duration (seconds) | `RAMP_DURATION=30` | use in custom scripts |

Read values in TypeScript with `process.env.MY_VAR ?? "fallback"`. For secrets, rely on CI secrets or Gatling Enterprise environment management rather than committing them to source control.

## What to explore next

- Need practical patterns (feeders, correlation, workload models)? Continue with [Get started with the JavaScript SDK]({{< ref "tutorials/javascript/get-started/" >}}).
- Prefer a guided tutorial? Start with [Create your first JavaScript-based simulation]({{< ref "tutorials/javascript/scripting-intro-js/" >}}).
- Browse the [HTTP scripting reference]({{< ref "reference/script/http/" >}}) and [protocol guides]({{< ref "reference/" >}}) as you harden your tests.
- Evaluate distributed runs, real-time dashboards, and governance with [Gatling Enterprise]({{< ref "evaluate-enterprise/trial-plan/" >}}).
