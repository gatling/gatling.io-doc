---
title: Get started with Gatling for JavaScript/TypeScript
menutitle: Get started for JavaScript/TypeScript
seotitle: Gatling JavaScript/TypeScript Load Testing Tutorial & Quickstart Guide
description: Step-by-step introduction to Gatling for JavaScript/TypeScript.
---
## Intro
Welcome to the Gatling JavaScript & TypeScript Developer Hub. This page is your starting point and central resource for running Gatling performance tests with JavaScript or TypeScript. Here you'll find:

- quick setup instructions, 
- ready-to-use helper functions and code recipes, 
- project structure recommendations, and 

Whether you're new to load testing or migrating from other tools, use this hub to get oriented and access everything you need to begin writing and running Gatling JS/TS simulations.

## Who this is for

JavaScript/TypeScript developers who want to write Gatling simulations in JS/TS and run them locally or with Gatling Enterprise Edition.

## Prerequisites

- Node.js LTS >=18
- npm >=8
- [Gatling Enterprise Edition](https://cloud.gatling.io) account for distributed test execution

## Quickstart

1. Download the Gatling JS/TS SDK:

  {{< button title="Download Gatling for JavaScript" >}}
  https://github.com/gatling/gatling-js-demo/archive/refs/heads/main.zip
  {{< /button >}}   

2. Unzip the folder.
3. Open the folder in your IDE.
4. Install dependencies in the `/javascript` folder for JavaScript projects or the `/typescript` folder for TypeScript projects by running `npm install` in your terminal.
5. Run a sample simulation with `npx gatling run` (JavaScript) or `npx gatling run --typescript` (TypeScript) in the respective folder.

## Project structure

```
gatling-js-demo/
├─ .github/
│  └─ (workflows/) — optional CI config for PR builds, etc.
│
├─ javascript/                      # JS demo project
│  ├─ package.json                  # npm scripts (clean, format, build, run, recorder)
│  ├─ src/
│  │  └─ basicSimulation.gatling.js # sample simulation you can run/modify
│  ├─ resources/                    # place CSV/JSON test data here (create as needed)
│  └─ (formatting config, e.g., Prettier) 
│
├─ typescript/                      # TS demo project
│  ├─ package.json                  # npm scripts (incl. "check" for type-checking)
│  ├─ tsconfig.json                 # TypeScript compiler options
│  ├─ src/
│  │  └─ basicSimulation.gatling.ts # sample TS simulation
│  ├─ resources/                    # place CSV/JSON test data here (create as needed)
│  └─ (formatting config, e.g., Prettier)
│
├─ .gitignore
└─ README.md  
```

- Simulation names must end with `.gatling.js` or `.gatling.ts` to be recognized by the Gatling CLI.
- Place test data files (CSV, JSON) in the `resources/` folder. They are packaged automatically and available at runtime.
- Use the provided `package.json` scripts to run, build, and check your simulations. 

### TypeScript preset (optional)

```json
// tsconfig.json
{
  "compilerOptions": {
    "baseUrl": "./",
    "outDir": "target",
    "lib": ["es2021", "dom"],
    "target": "es2021",
    "module": "es2020",
    "moduleResolution": "Node",
    "strict": true,
    "forceConsistentCasingInFileNames": true
  },
  "include": ["src/**/*.ts"],
  "exclude": ["**/*.test.ts"]
}
```

## Dedicated JS/TS documentation

JavaScript/TypeScript code examples can be found throughout the [reference documentation]({{<ref "/reference" >}}), however the following pages are particularly relevant, and solely focused on the JS/TS SDK:

- [Create a simulation with JavaScript]({{<ref "/tutorials/scripting-intro-js" >}})
- [JavaScript CLI reference]({{<ref "/integrations/build-tools/js-cli" >}})
- [Postman]({{<ref "/integrations/postman" >}})
- [Test SSE with the JavaScript/TypeScript SDK]({{<ref "/guides/use-cases/sse-js" >}})

## Protocols supported in the JS/TS SDK

| Protocol | Support |
| --- | --- |
| HTTP/HTTPS | ✅ |
| WebSocket | ✅ |
| SSE | ✅ |
| gRPC | coming soon |
| MQTT |  ✅ |

{{< alert tip>}}
Need protocol support not listed here? Add a [feature request](https://gatling.io/roadmap/) or upvote existing ones on our public roadmap.
{{< /alert >}}

## Import Gatling modules

To write your simulations, import the Gatling modules required for your use case. The tables below list the most commonly used modules, their packages, and links to relevant reference documentation. Use these imports as building blocks for your own load testing scripts.


### `@gatling.io/core` package

| Module                | Import example                           | Description              |
| --------------------- | ---------------------------------------- | ------------------------ |
| `simulation`                                         | `import { simulation } from "@gatling.io/core";`                                  | Top-level wrapper that takes `setUp` and builds the [Simulation]({{<ref "/concepts/simulation/" >}}).                 |
| `scenario`                                         | `import { scenario } from "@gatling.io/core";`                                    | [Defines a user journey]({{<ref "/concepts/scenario/" >}}).                                                         |
| `atOnceUsers`, `rampUsers`, `constantUsersPerSec`    | `import { atOnceUsers, rampUsers, constantUsersPerSec } from "@gatling.io/core";` | [Open/closed workload injection profiles]({{<ref "/concepts/injection/" >}}).                                        |
| `pause`                                            | `import { pause } from "@gatling.io/core";`                                       | [Think times / pacing between steps]({{<ref "/concepts/scenario#pause" >}}).                                              |
| `jmesPath`, `jsonPath`                                | `import { jmesPath, jsonPath } from "@gatling.io/core";`                          | [Generic checks for JSON payloads (validate/extract)]({{<ref "/concepts/checks#jsonpath" >}}).                        |

### `@gatling.io` protocol packages

| Module           | Import example                                                    | Description                        |
| ------------------------------------------------- | ---------------------------------------------------------- | --------------------------------------------------------------------------------- |
| `http`                                            | `import { http } from "@gatling.io/http";`                                        | [HTTP protocol setup + request builder]({{<ref "/reference/script/http/protocol" >}}) `http("name").get(...)`.                                |
| `status`                                       | `import { status } from "@gatling.io/http";`                                         | [HTTP-specific check for response status codes]({{<ref "/reference/script/http/checks" >}}), e.g. `.check(status().is(200))`.           |
| `header`, `headerRegex`                            | `import { header, headerRegex } from "@gatling.io/http";`                         |[ Check header values or capture via regex]({{<ref "/reference/script/http/checks#http-header" >}}). |
| `sse`                                             | `import { sse } from "@gatling.io/http";`                                         | [SSE entry point]({{<ref "/reference/script/sse" >}}) (connect/await/close) — extension of the HTTP DSL.                   |
| `ws`                                               | `import { ws } from "@gatling.io/http";`                                         | [WebSocket entry point]({{<ref "/reference/script/websocket" >}}) (connect/send/await/close) — extension of HTTP DSL. |
| `mqtt`                                             | `import { mqtt } from "@gatling.io/mqtt";`                                         | [MQTT entry point]({{<ref "/reference/script/mqtt" >}}) (connect/publish/subscribe/await/disconnect).                   |

### `@gatling.io/postman` package

| Module                                            |  Import example                                | Description                       |
| ------------------------------------------------- | --------------------- | --------------------------------------------------------------------------------- |
| `postman`  | `import { postman } from "@gatling.io/postman";`       | [Load a Postman collection]({{<ref "/integrations/postman" >}}) and use its generated requests/scenarios. |


## Bring-your-own-helpers pattern

Most pieces of your tests can live in reusable helpers so your simulation files stay clean and easy to read. Extract code like:

 - protocols, 
 - headers, 
 - checks, and
 - injection profiles

to src/utils/* and import them in each simulation.

Example of a shared helper for HTTP protocol configuration:

```ts
// src/utils/protocols.ts
import { http } from "@gatling.io/http";
import { getParameter } from "@gatling.io/core";

export const httpProtocol = http.baseUrl(
  getParameter("baseUrl", "http://localhost:3000")
);
```

## Parameters, environment variables & secrets

When running your simulations, you often need to[ pass parameters]({{<ref "/guides/optimize-scripts/passing-parameters" >}}) (e.g., number of users, ramp-up time), environment variables (e.g., API endpoints), or secrets (e.g., API keys, passwords).

The Gatling command-line tool allows you to pass options to your simulation using a `key=value` format:

```shell
npx gatling run users=500 ramp=3600
```

You can resolve these options directly in your code with the `getParameter` function:

```ts
import { getParameter } from "@gatling.io/core";

const nbUsers = parseInt(getParameter(
  "users", // Key used to identify the option
  "1" // Default value (optional)
));
const myRamp = parseInt(getParameter("ramp", "0"));

setUp(scn.injectOpen(rampUsers(nbUsers).during(myRamp)));
```

We also provide a `getEnvironmentVariable` function to read environment variables:

```ts
import { getEnvironmentVariable } from "@gatling.io/core";

const mySecret = getEnvironmentVariable(
  "MY_SECRET", // Name of the environment variable
  "FOO"// Default value (optional)
);
```
