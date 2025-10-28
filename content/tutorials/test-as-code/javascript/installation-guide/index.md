---
title: Installation Guide
menutitle: Installation Guide
seotitle: Gatling JavaScript SDK Installation Guide
description: Step-by-step guide to installing the Gatling JavaScript SDK.
---

## What the SDK delivers
The Gatling JavaScript SDK lets you script load tests with modern JavaScript or TypeScript while using the Gatling engine for execution and reporting. It fits teams that already automate with Node.js tooling and want reusable, version-controlled performance tests.

## When to choose it
- You prefer writing load tests in JavaScript or TypeScript.
- You need to run tests locally first and keep the option to scale with Gatling Enterprise.
- You want Gatling's HTML reports, assertions, and workload models without changing ecosystems.

## Requirements
- Node.js LTS versions >18 with npm 8 or newer installed on macOS, Linux, or Windows.
- Git or download access to the Gatling JavaScript demo project.
- A Gatling Enterprise Edition account for distributed runs.

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
2. Move into the language folder you want to use:
   - `javascript/` for JavaScript
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

## Start the Gatling Recorder

The [Gatling Recorder]({{< ref "/reference/script/http/recorder/" >}}) allows you to capture browser-based actions and convert them into a script. Use the following command to launch the Recorder:

```bash
npx gatling recorder
```

## IDE setup

You can edit your simulation files with any text editor, but using an IDE provides better code completion, refactoring, and debugging capabilities.

### Visual Studio Code

VS Code is a popular choice for JavaScript and TypeScript development. For the best experience:

1. Install the recommended extensions:
   - **ESLint** for code linting
   - **Prettier** for code formatting
   - **JavaScript and TypeScript Nightly** for enhanced language support

2. Configure your workspace settings in `.vscode/settings.json`:

```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  }
}
```

### IntelliJ IDEA / WebStorm

JetBrains IDEs provide excellent JavaScript and TypeScript support out of the box. Simply open the project folder and the IDE will automatically detect the Node.js project structure.

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
- Explore all of the JavaScript CLI options in the [CLI Reference]({{< ref "integrations/build-tools/js-cli" >}}).
