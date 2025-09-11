---
menutitle: JavaScript CLI
title: JavaScript tooling
seotitle: Tooling to work with Gatling in JavaScript and TypeScript
description: >
  Use our command-line (CLI) tool and a package manager such as npm to work with Gatling and Gatling Enterprise Edition, when
  using the JavaScript/TypeScript DSL to write your tests.
lead: >
  Run Gatling simulations written in JavaScript/TypeScript from the command line, and package them to run on Gatling
  Enterprise Edition
date: 2024-06-20T14:00:00+02:00
aliases:
  - /reference/integrations/build-tools/js-cli/
---

## Versions

Check out available versions on [the npm registry](https://www.npmjs.com/package/@gatling.io/cli).

## Pre-requisites and compatibility

You need:

- [Node.js](https://nodejs.org/) v18 or later (we only support using LTS versions)
- npm v8 or later, which comes pre-installed with Node.js

Using another compatible package manager (such as Yarn) should also work, but you need to adapt the equivalent npm
commands and configuration on your own.

## Setup

Cloning or downloading [our demo projects](https://github.com/gatling/gatling-js-demo) on GitHub is definitely the fastest way to get started. The repo
includes both a JavaScript and a TypeScript projects.

If you prefer to manually configure your npm project rather than clone one of our samples, you will need to add the
following dependencies:

```shell
npm install --save-dev "@gatling.io/cli"
npm install --save "@gatling.io/core"
npm install --save "@gatling.io/http"
```

The `@gatling.io/cli` package contains the `gatling` command-line interface (CLI) tool. Once added to your project's dev
dependencies, you can execute it with the command `npx gatling` without having to install it globally or add it to your
shell's path (see also [the `npx` documentation](https://docs.npmjs.com/cli/v10/commands/npx)).

You can explore all available commands with `npx gatling --help`, and all options for a given command with
`npx gatling command-name --help`. The most typical usages are explained below.

Remember that you can define aliases for commonly used commands in the `scripts` section of you `package.json` file, as
shown [in the npm documentation](https://docs.npmjs.com/cli/v10/using-npm/scripts).

### Project layout

By default, the `gatling` CLI tool expects that:

- Gatling simulation files are located in the `src` folder (can be overridden with the `--sources-folder` option).
  * The `gatling` tool will find simulations defined in files named with a `.gatling.js` or `.gatling.ts` extension at
    the root of this folder.
- Resource files accessible to Gatling (e.g. feeder files) are located in the `resources` folder (can be overridden with
  the `--resources-folder` option).
- When running locally, it will create test reports in the `target/gatling` folder (can be overridden with the
  `--results-folder` option).
- When running locally or packaging for Gatling Enterprise Edition, it will create a code bundle file at `target/bundle.js`
  (can be overridden with the `--bundle-file` option).
- When packaging for Gatling Enterprise Edition, it will create the package file at `target/package.zip` (can be overridden with
  the `--package-file` option).

### Dependency management

You can add other library dependencies from npm, as long as:

- they do not rely on native (non-JavaScript) binaries
- they do not use JavaScript APIs which are specific to Node.js

You can also use dev dependencies normally for other tools you might want to run on your code; e.g. our demo projects
include a [Prettier](https://prettier.io) configuration for automatic code formatting.

### Upgrade the JavaScript SDK version

Gatling periodically releases new versions of the JavaScript SDK to maintain compatibility with Gatling Enterprise Edition,
add new functionality, and improve performance. Be sure to check the
[Gatling upgrade guides]({{< ref "/release-notes/gatling/upgrading" >}}) for breaking changes.

Use the following procedure to upgrade your SDK version:

1. Update the following in the `package.json` to the latest version and save the file:

    * `version`
    * `@gatling.io/core`
    * `@gatling.io/http`
    * `@gatling.io/cli`

2. Run `npm install`

### Internet access

The `gatling` CLI tool requires Internet access to:

- Automatically download the Gatling runtime bundle, required to execute your Gatling simulations (from
  `https://github.com/gatling/gatling-js/releases/`).
  - By default, it is installed in the folder `~/.gatling` (Linux/macOS) or `%USERPROFILE%\.gatling` (Windows). You
    can override this with the `--gatling-home` option.
- Access the Gatling Enterprise Edition API (`https://api.gatling.io`) when executing commands such as
  `npx gatling enterprise-deploy` or `npx gatling enterprise-start`.
- Access your Control Plane when using the [Private Packages](#private-packages) feature with the same commands.

#### Configuring a proxy

If a proxy configuration is required to access the Internet, simply make sure it is configured for NPM. The `gatling`
CLI tool will inherit the NPM proxy settings when run with `npx gatling`. NPM proxy settings can be configured using
environment variables (`HTTP_PROXY`, `HTTPS_PROXY`, `NOPROXY`) or a `.npmrc` file, as shown in the official NPM
documentation for the following configuration keys:

- [`proxy`](https://docs.npmjs.com/cli/v11/using-npm/config#proxy)
- [`https-proxy`](https://docs.npmjs.com/cli/v11/using-npm/config#https-proxy)
- [`noproxy`](https://docs.npmjs.com/cli/v11/using-npm/config#noproxy)

#### Configuring a trust store

When using the [Private Packages](#private-packages) feature, the CLI tool needs to call your Control Plane to
automatically upload your packaged simulations. If your Control Plane uses an HTTPS certificate signed by an internal
Certificate Authority (CA), or a self-signed certificate, it will not be trusted by default. In that case, you can
configure a trust store containing the required certificate using the options:

- `--trust-store <path to trust store>` to point to a trust store in PKCS#12 or JKS format
- `--trust-store-password <password>` in case your trust store requires a password (usually not needed)

You can check out more details with `npx gatling enterprise-deploy --help` or `npx gatling enterprise-start --help`.

#### Manually downloading the Gatling runtime bundle

If you cannot give Internet access to the `gatling` CLI tool, you can still manually install the required Gatling
runtime bundle to be able to [run simulations locally](#running-your-simulations) or
[package them for Gatling Enterprise Edition](#packaging-your-simulations-for-gatling-enterprise-edition).

To do so, manually download the file from
[the releases page](https://github.com/gatling/gatling-js/releases/), being careful to choose the same version
configured in your `package.json` file and the correct variant for your system: we use the same names for the
[system type](https://nodejs.org/api/os.html#ostype) and [architecture](https://nodejs.org/api/os.html#osarch) as in the
Node `os` library. Then install it with the command:

```shell
npx gatling install <path-to-downloaded-file.zip>
```

## Commands

### Running your simulations

Use the `gatling run` command to execute Gatling simulations. For instance:

```shell
npx gatling run
```

Runs a simulation defined in a file matching the pattern `src/*.gatling.js` or `src/*.gatling.ts`, and writes the report
inside the folder `target/gatling`. If several simulations are found, you will be asked to choose one.

You can also specify which simulation to run with the `--simulation` option; for instance, to run a simulation defined
in `src/my-simulation.gatling.js` or `src/my-simulation.gatling.ts`:

```shell
npx gatling run --simulation "my-simulation"
```

You can pass options to you simulation using a `key=value` format, then read them using the `getParameter` function in your
code (see [the Passing parameters guide]({{< ref "/guides/optimize-scripts/passing-parameters#javascript" >}})):

```shell
npx gatling run key1=value1 key2=value2
```

You can check out other options with `npx gatling run --help`.

### Running the Gatling Recorder

You can launch the [Gatling Recorder]({{< ref "reference/script/http/recorder" >}}):

```shell
npx gatling recorder
```

You can check out other options with `npx gatling recorder --help`.

### Running your simulations on Gatling Enterprise Edition { #running-your-simulations-on-gatling-enterprise }

#### Prerequisites

You need to configure [an API token]({{< ref "/reference/collaborate/admin/api-tokens/" >}}) for most of the actions
between the CLI and Gatling Enterprise Edition.

{{< alert warning >}}
The API token needs the `Configure` role on expected teams.
{{< /alert >}}

Since you probably don't want to include you secret token in your source code, you can configure it using either:

- the `GATLING_ENTERPRISE_API_TOKEN` environment variable
- the `--api-token` option

{{< alert info >}}
Learn how to work with environment variables and JavaScript parameters in the [Configuration documentation]({{< ref "/concepts/configuration#manage-configuration-values" >}}).
{{< /alert >}}

#### Packaging your simulations for Gatling Enterprise Edition

Use the `enterprise-package` command to create a package of your simulations to deploy on Gatling Enterprise Edition.
For instance:

```shell
npx gatling enterprise-package
```

Will create a package `target/package.zip`, which contains all simulations matching the pattern `src/*.gatling.js` or
`src/*.gatling.ts`, and all resources files found in the `resources` folder.

You may want to specify the package file name:

```shell
npx gatling enterprise-package --package-file "target/my-package-file.zip"
```

You can check out other options with `npx gatling enterprise-package --help`.

#### Deploying on Gatling Enterprise Edition { #deploying-on-gatling-enterprise }

With the `enterprise-deploy` command, you can:

- Create, update and upload packages
- Create and update simulations

This command automatically checks your simulation project and performs the deployment according to your configuration.

By default, `enterprise-deploy` searches for the package descriptor in `.gatling/package.conf`.
However, you can target a different filename in `.gatling` by using the following command:
```shell
npx gatling enterprise-deploy --package-descriptor-filename="<file name>"
```

#### Private packages

To create, update, and upload private packages, you need to specify the `--control-plane-url` option on the `enterprise-deploy` command:
```shell
npx gatling enterprise-deploy --control-plane-url <your control plane URL>
```

You can check out other options with `npx gatling enterprise-deploy --help`.

{{< alert info >}}
You can run this command without any configuration to try it.

Check the [Configuration as Code documentation]({{< ref "/reference/run-tests/sources/configuration-as-code" >}}) for
the complete reference and advanced usage.
{{< /alert >}}

#### Start your simulations on Gatling Enterprise Edition { #start-your-simulations-on-gatling-enterprise }

You can, using the `enterprise-start` command:

- Automatically [deploy your package and associated simulations](#deploying-on-gatling-enterprise)
- Start a deployed simulation

By default, the Gatling plugin prompts the user to choose a simulation to start from amongst the deployed simulations.
However, users can also specify the simulation name directly to bypass the prompt using the following command:

```shell
npx gatling enterprise-start --enterprise-simulation="<simulation name>"
````

Replace `<simulation name>` with the desired name of the simulation you want to start.

If you are on a CI environment, you don't want to handle interaction with the plugin.
Most CI tools define the `CI` environment variable, used by the Gatling plugin to disable interactions and run in headless mode.

It's also possible to disable interactions by using the `--non-interactive` option.

Here are additional options for this command:

- `--wait-for-run-end`: Waits for the run to finish. The command will exit with an error status if any assertions fail.
- `--run-title <title>`: Sets a custom title for the run reports.
- `--run-description <description>`: Sets a custom description for the run summary.
- `--control-plane-url <URL>`: Specifies a control plane URL (essential for Private Packages).

You can check out other options with `npx gatling enterprise-start --help`.
