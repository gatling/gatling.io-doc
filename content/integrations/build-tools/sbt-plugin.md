---
menutitle: sbt Plugin
title: Gatling sbt Plugin
seotitle: sbt Plugin for Gatling and Gatling Enterprise Edition
description: How to use the sbt plugin for Gatling to run tests and deploy them to Gatling Enterprise Edition.
lead: The sbt plugin allows you to run Gatling tests from the command line, without the bundle, as well as to package your simulations for Gatling Enterprise Edition.
aliases:
  - /reference/extensions/build-tools/sbt-plugin
  - /reference/integrations/build-tools/sbt-plugin/
date: 2021-04-20T18:30:56+02:00
---

This sbt plugin integrates Gatling with sbt, allowing to use Gatling as a testing framework. It can also be used to
package your Gatling project to run it on [Gatling Enterprise Edition](https://gatling.io/products/).

## Versions

Check out available versions on [Maven Central](https://central.sonatype.com/search?q=gatling-sbt&namespace=io.gatling).

Beware that milestones (M versions) are not documented for Community Edition users and are only released for [Gatling Enterprise Edition](https://gatling.io/products/) customers.

## Setup

{{< alert warning >}}
This plugin only supports Simulations written in Scala. If you want to write your Simulations in Java or Kotlin, please
use [Maven]({{< ref "maven-plugin" >}}) or [Gradle]({{< ref "gradle-plugin" >}}).
{{< /alert >}}

{{< alert warning >}}
This plugin requires using sbt 1.x or 2.x (sbt 0.13 is not supported). All code examples on this page use the
[unified slash syntax](https://www.scala-sbt.org/1.x/docs/Migrating-from-sbt-013x.html#Migrating+to+slash+syntax)
introduced in sbt 1.1.
{{< /alert >}}

{{< alert tip >}}
Cloning or downloading our demo project on GitHub is definitely the fastest way to get started:
* [for sbt and Scala](https://github.com/gatling/gatling-sbt-plugin-demo)
{{< /alert >}}

If you prefer to manually configure your sbt project rather than clone our sample, you need to add the Gatling plugin dependency to your `project/plugins.sbt`:

```scala
addSbtPlugin("io.gatling" % "gatling-sbt" % "MANUALLY_REPLACE_WITH_LATEST_VERSION")
```

And then add the Gatling library dependencies and enable the Gatling plugin in your `build.sbt`:

```scala
enablePlugins(GatlingPlugin)
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "MANUALLY_REPLACE_WITH_LATEST_VERSION" % "test"
libraryDependencies += "io.gatling"            % "gatling-test-framework"    % "MANUALLY_REPLACE_WITH_LATEST_VERSION" % "test"
```

### Default settings

This plugin offers a custom sbt configurations named `Gatling`. With this configuration:

* By default, Gatling simulations must be in `src/test/scala`, configurable using the `Gatling / scalaSource` setting.
* By default, Gatling reports are written to `target/gatling`, configurable using the `Gatling / target` setting.

If you override the default settings, you need to reset them on the project, eg:

```scala
Gatling / scalaSource := sourceDirectory.value / "gatling" / "scala"
lazy val root = (project in file(".")).settings(inConfig(Gatling)(Defaults.testSettings): _*)
```

### Multi-project support

If you have a [multi-project build](https://www.scala-sbt.org/1.x/docs/Multi-Project.html), make sure to only configure
the subprojects which contain Gatling Simulations with the Gatling plugin and dependencies as described above. Your
Gatling subproject can, however, depend on other subprojects.

## Usage

### Running your simulations

As with any sbt testing framework, you'll be able to run Gatling simulations using sbt standard `test`, `testOnly`,
`testFull`, etc... tasks. However, since the sbt Plugin introduces many customizations that we don't want to interfere
with unit tests, those commands are integrated into custom configurations, meaning you'll need to prefix them with
`Gatling/`.

For example, run all Gatling simulations from the `testFull` configuration:

```bash
sbt Gatling/testFull
```

Or run a single simulation, by its FQCN (fully qualified class name):

```bash
sbt 'Gatling/testOnly com.project.simu.MySimulation'
```

{{< alert warning >}}
The semantic of the standard test tasks has changed between sbt 1.x and sbt 2.x:

| Task                                                  | sbt 1.x              | sbt 2.x              |
|-------------------------------------------------------|----------------------|----------------------|
| run all tests                                         | `test`               | `testFull`           |
| run only new, modified or previously failing tests    | `testQuick`          | `test`               |
| run test(s) matching the pattern provided as argument | `testOnly <pattern>` | `testOnly <pattern>` |
{{< /alert >}}

{{< alert tip >}}
This behavior differs from what was previously possible, eg. calling `test` without prefixing started Gatling simulations.
However, this caused many interferences with other testing libraries and forcing the use of a prefix solves those issues.
{{< /alert >}}

### Running your simulations on Gatling Enterprise Edition { #running-your-simulations-on-gatling-enterprise }

#### Prerequisites

You need to configure an [an API token]({{< ref "reference/administration/api-tokens" >}}) for most
of the actions between the CLI and Gatling Enterprise Edition.

{{< alert warning >}}
The API token needs the `Configure` role on expected teams.
{{< /alert >}}

Since you probably don’t want to include you secret token in your source code, you can configure it using either:

- the `GATLING_ENTERPRISE_API_TOKEN` environment variable
- the `gatling.enterprise.apiToken` [Java System property](https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html)

{{< alert info >}}
Learn how to work with environment variables and Java system properties in the [Configuration docummentation]({{< ref "/concepts/configuration#manage-configuration-values" >}}). 
{{< /alert >}}

If really needed, you can also configure it in your build.sbt:
```scala
Gatling / enterpriseApiToken := "YOUR_API_TOKEN"
```

#### Deploying on Gatling Enterprise Edition { #deploying-on-gatling-enterprise }

With `Gatling/enterpriseDeploy` command, you can:
- Create, update and upload packages
- Create and update simulations

This command automatically checks your simulation project and performs the deployment according to your configuration.

By default, `enterpriseDeploy` searches for the package descriptor in `.gatling/package.conf`.
However, you can target a different filename in `.gatling` by using the following command:
```shell
sbt 'Gatling/enterpriseDeploy --package-descriptor-filename "<file name>"'
```

{{< alert info >}}
You can run this command without any configuration to try it.

Check the [Configuration as Code documentation]({{< ref "/reference/run-tests/sources/configuration-as-code" >}}) for the complete reference and advanced usage.
{{< /alert >}}

#### Start your simulations on Gatling Enterprise Edition { #start-your-simulations-on-gatling-enterprise }

You can, using the `gatling:enterpriseStart` command:
- Automatically [deploy your package and associated simulations](#deploying-on-gatling-enterprise)
- Start a deployed simulation

By default, the Gatling plugin prompts the user to choose a simulation to start from among the deployed simulations.
However, users can also specify the simulation name directly to bypass the prompt using the following command:
```shell
sbt 'Gatling/enterpriseStart "<simulation name>"'
```
Replace `<simulation name>` with the desired name of the simulation you want to start.

If you are on a CI environment, you don't want to handle interaction with the plugin.
Most CI tools define the `CI` environment variable, used by the Gatling plugin to disable interactions and run in headless mode.

If you need the command to wait until the run completes and to fail in case of assertion failures, you can enable:
```scala
Gatling / waitForRunEnd := true
```

Here are additional options for this command:
- `--run-title <title>`: Allows setting a title for your run reports.
- `--run-description <description>`: Allows setting a description for your run reports summary.

#### Upload a package manually

##### Packaging

You can directly package your simulations for Gatling Enterprise Edition using:

```shell
sbt Gatling/enterprisePackage
```

This will generate the `target/gatling/<artifactId>-gatling-enterprise-<version>.jar` package which you can then
[upload to the Cloud]({{< ref "reference/run-tests/sources/package-conf" >}}).

#### Private packages

Configure the [Control Plane URL]({{< ref "/reference/deploy/private-locations/introduction/#control-plane-server" >}}):

```scala
Gatling / enterpriseControlPlaneUrl := Some(URI.create("YOUR_CONTROL_PLANE_URL").toURL)
```

Once configured, your private package can be created and uploaded using the [deploy command]({{< ref "#deploying-on-gatling-enterprise" >}}).

### Additional tasks

Gatling's sbt plugin also offers four additional tasks:

* `Gatling/startRecorder`: starts the Recorder, configured to save recorded simulations to the location specified by `Gatling/scalaSource` (by default, `src/test/scala`).
* `Gatling/generateReport`: generates reports for a specified report folder.
* `Gatling/lastReport`: opens by the last generated report in your web browser. A simulation name can be specified to open the last report for that simulation.
* `Gatling/copyConfigFiles`: copies Gatling's configuration files (gatling.conf & recorder.conf) from the bundle into your project resources if they're missing.
* `Gatling/copyLogbackXml`: copies Gatling's default logback.xml.

## Overriding JVM options

Gatling's sbt plugin uses the same default JVM options as the bundle launchers or the Maven plugin, which should be sufficient for most simulations.
However, should you need to tweak them, you can use `overrideDefaultJavaOptions` to only override those default options, without replacing them completely.

E.g., if you want to tweak Xms/Xmx to give more memory to Gatling

```scala
Gatling / javaOptions := overrideDefaultJavaOptions("-Xms1024m", "-Xmx2048m")
```

## Sources

If you're interested in contributing, you can find the [gatling-sbt plugin sources](https://github.com/gatling/gatling-sbt) on GitHub.
