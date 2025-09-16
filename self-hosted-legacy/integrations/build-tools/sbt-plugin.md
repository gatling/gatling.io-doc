---
menutitle: sbt Plugin
title: Gatling sbt Plugin
seotitle: sbt Plugin for Gatling and Gatling Enterprise Edition
description: How to use the sbt plugin for Gatling to run tests and deploy them to Gatling Enterprise Edition.
lead: The sbt plugin allows you to run Gatling tests from the command line, without the bundle, as well as to package your simulations for Gatling Enterprise Edition.
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
This plugin requires using sbt 1 (sbt 0.13 is not supported). All code examples on this page use the
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

### 'Test' vs 'Integration Tests' configurations

This plugin offers two different custom sbt configurations, named `Gatling` and `GatlingIt`.
They are tied to different source directories (see next section for more details) and therefore allow to separate your simulations according to your needs, should you desire it.

Ideally:

* Your simulations with low injection profiles, which may serve as functional tests, should live in `src/test` (the default source directory for the `Gatling` configuration), and run along your unit tests, since they would complete quickly
* Longer, more complex simulations with high injection profiles, should live in `src/it` (the default source directory for the `GatlingIt` configuration) and be run on an as-needed basis.

Also, since they're tied to separate sbt configurations, your sbt settings can then be customized per configuration.
You can expect a relatively short simulation to run easily with the default JVM settings, but, for example, simulations with much higher load could require an increase of the max heap memory.

{{< alert tip >}}
When using the `GatlingIt` configuration, you must use the `GatlingIt/` prefix, e.g. `Gatling/test` becomes `GatlingIt/test`, etc...
{{< /alert >}}

### Default settings

For the `Gatling` configuration:

* By default, Gatling simulations must be in `src/test/scala`, configurable using the `Gatling / scalaSource` setting.
* By default, Gatling reports are written to `target/gatling`, configurable using the `Gatling / target` setting.

For the `GatlingIt` configuration:

* By default, Gatling simulations must be in `src/it/scala`, configurable using the `GatlingIt / scalaSource` setting.
* By default, Gatling reports are written to `target/gatling-it`, configurable using the `GatlingIt / target` setting.

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
`testQuick`, etc... tasks. However, since the sbt Plugin introduces many customizations that we don't want to interfere
with unit tests, those commands are integrated into custom configurations, meaning you'll need to prefix them with
`Gatling/` or `GatlingIt/`.

For example, run all Gatling simulations from the `test` configuration:

```bash
sbt Gatling/test
```

Or run a single simulation, by its FQN (fully qualified class name), from the `it` configuration:

```bash
sbt 'GatlingIt/testOnly com.project.simu.MySimulation'
```

{{< alert tip >}}
This behavior differs from what was previously possible, eg. calling `test` without prefixing started Gatling simulations.
However, this caused many interferences with other testing libraries and forcing the use of a prefix solves those issues.
{{< /alert >}}

### Running your simulations on Gatling Enterprise Self-Hosted { #running-your-simulations-on-gatling-enterprise-self-hosted }

#### Build from sources

Once you have configured the sbt plugin on your project, Gatling Enterprise Edition Self-Hosted can build it from sources
without additional configuration. [Add your source repository]({{< ref "/execute/repositories#downloading-from-sources" >}})
and configure your simulation to [build from sources]({{< ref "/execute/simulations#option-1-build-from-sources" >}})
using sbt.

To make sure your setup is correct, you can run the packaging command and check that you get a jar containing all the
classes and extra dependencies of your project in `target/gatling/<artifactId>-gatling-enterprise-<version>.jar`:

```shell
sbt Gatling/enterprisePackage
```

{{< alert warning >}}
If you use the `it` configuration, you will need to configure a custom build command in Gatling Enterprise Edition Self-Hosted, as the
default one is for the `test` configuration:
``sbt -J-Xss100M ;clean;GatlingIt/enterprisePackage -batch --error``
{{< /alert >}}

#### Publish to a binary repository

Alternatively, you can package your simulations and publish them to a binary repository (JFrog Artifactory, Sonatype
Nexus or AWS S3).

{{< alert tip >}}
Please refer to the [official documentation](https://www.scala-sbt.org/1.x/docs/Publishing.html) for generic
configuration options. Please also check the standards within your organization for the best way to configure the
credentials needed to access your binary repository.
{{< /alert >}}

Enable publishing the Gatling test artifact, then define the repository:

```scala
Gatling / publishArtifact := true
publishTo := (
  if (isSnapshot.value)
    Some("private repo" at "REPLACE_WITH_YOUR_SNAPSHOTS_REPOSITORY_URL")
  else
    Some("private repo" at "REPLACE_WITH_YOUR_RELEASES_REPOSITORY_URL")
)
```

The packaged artifact will be automatically attached to your project and deployed with the `tests` classifier when you publish it:

```shell
sbt publish
```

You can also set:
- `GatlingIt / publishArtifact := true` to publish Gatling simulations from the `it` configuration, this artifact will be
published with the `it` qualifier
- `Compile / publishArtifact := false` e.g. if your project only contains Gatling simulations and you don't need to
publish code from `src/main`.

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
