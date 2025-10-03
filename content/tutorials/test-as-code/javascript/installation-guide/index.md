---
title: Installation Guide
menutitle: Installation Guide
seotitle: Gatling JavaScript SDK Installation Guide
description: Step-by-step guide to installing the Gatling JavaScript SDK.
---

## What the SDK delivers
The Gatling JavaScript SDK lets you script load tests with modern JavaScript or TypeScript while using the Gatling engine for execution and reporting. It fits teams that already automate with Node.js tooling and want reusable, version-controlled performance tests.

{{< alert info >}}
The JavaScript/TypeScript SDK currently supports the HTTP protocol. Other Gatling protocols remain JVM-only.
{{< /alert >}}

## When to choose it
- You prefer writing load tests in JavaScript or TypeScript instead of Scala-based DSLs.
- You need to run tests locally first and keep the option to scale with Gatling Enterprise.
- You want Gatling's HTML reports, assertions, and workload models without changing ecosystems.

## Requirements
- Node.js 18 LTS or 20 LTS with npm 8 or newer installed on macOS, Linux, or Windows.
- Permission to exercise a non-production environment.
- Git or download access to the Gatling JavaScript demo project.
- Optional: a Gatling Enterprise account for distributed runs.

Verify the prerequisites before continuing:

```bash
node -v
npm -v
```

## Download the starter project
Bootstrap your workspace from the official demo repository:

{{< button title="Download Gatling for JavaScript" >}}
https://github.com/gatling/gatling-js-demo/archive/refs/heads/main.zip{{< /button >}}

1. Unzip the archive and open it in your IDE or editor.
2. Move into the language folder you need:
   - `javascript/` for vanilla JavaScript
   - `typescript/` for TypeScript projects
3. Install dependencies with npm (or your preferred package manager):

```bash
npm install
```

Prefer cloning? Use:

```bash
git clone https://github.com/gatling/gatling-js-demo.git
cd gatling-js-demo/javascript
npm install
```

If you rely on pnpm or yarn, swap the install command accordingly. Ensure your lockfile is committed so teammates reproduce the same dependency graph.

## Run the demo simulation
Confirm everything works by running the bundled sample scenario:

{{< code-toggle >}}
JavaScript: npx gatling run --simulation basicSimulation
TypeScript: npx gatling run --typescript --simulation basicSimulation
{{</ code-toggle >}}

The HTML report lands under `target/gatling/`. Open the most recent folder in your browser to inspect the results.

## Automate with package scripts
Add convenient scripts to `package.json` when you want shorthand commands:

```json
{
  "scripts": {
    "gatling:test": "npx gatling run --simulation basicSimulation",
    "gatling:recorder": "npx gatling recorder"
  }
}
```

Then run:

```bash
npm run gatling:test
```

## Where to go next
- Walk through your first end-to-end run in [Your First Simulation]({{< ref "tutorials/test-as-code/javascript/running-your-first-simulation/" >}}).
- Learn the broader SDK surface in [Explore the SDK]({{< ref "tutorials/test-as-code/javascript/full-sdk-capabilities/" >}}).
- Grab ready-made commands and snippets in [Cheatsheet]({{< ref "tutorials/anything-left-over/javascript/cheatsheet/" >}}).
- Explore all of the JavaScript CLI options in the [CLI Reference]({{< ref "integrations/build-tools/js-cli" >}}).
