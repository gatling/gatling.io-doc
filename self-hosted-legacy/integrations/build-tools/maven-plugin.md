---
menutitle: Maven Plugin
title: Gatling Maven Plugin
seotitle: Maven Plugin for Gatling and Gatling Enterprise Edition
description: How to use the Maven plugin for Gatling to run tests and deploy them to Gatling Enterprise Edition.
lead: The Maven plugin allows you to run Gatling tests from the command line, without the bundle, as well as to package your simulations for Gatling Enterprise Edition.
date: 2021-04-20T18:30:56+02:00
---

Using this plugin, Gatling can be launched when building your project, for example with your favorite Continuous Integration (CI) solution.
This plugin can also be used to package your Gatling project to run it on [Gatling Enterprise Edition](https://gatling.io/enterprise/).

## Versions

Check out available versions on [Maven Central](https://central.sonatype.com/search?q=gatling-maven-plugin&namespace=io.gatling).

Beware that milestones (M versions) are not documented for Community Edition users and are only released for [Gatling Enterprise Edition](https://gatling.io/enterprise/) customers.

## Setup

{{< alert tip >}}
Cloning or downloading one of our demo projects on GitHub is definitely the fastest way to get started:
* [for maven and Java](https://github.com/gatling/gatling-maven-plugin-demo-java)
* [for maven and Kotlin](https://github.com/gatling/gatling-maven-plugin-demo-kotlin)
* [for maven and Scala](https://github.com/gatling/gatling-maven-plugin-demo-scala)
{{< /alert >}}

If you prefer to manually configure your Maven project rather than clone one of our samples, you need to add the following to your `pom.xml`:

```xml
<dependencies>
  <dependency>
    <groupId>io.gatling.highcharts</groupId>
    <artifactId>gatling-charts-highcharts</artifactId>
    <version>MANUALLY_REPLACE_WITH_LATEST_VERSION</version>
    <scope>test</scope>
  </dependency>
</dependencies>

<plugin>
  <groupId>io.gatling</groupId>
  <artifactId>gatling-maven-plugin</artifactId>
  <version>MANUALLY_REPLACE_WITH_LATEST_VERSION</version>
</plugin>
```

{{< alert warning >}}
**For Scala users only**: starting from version 4, this plugin no longer compiles Scala code and requires to use the `scala-maven-plugin` when using Gatling with Simulations written in Scala. Please check the `pom.xml` of the demo project for Maven and Scala mentioned above for complete configuration.
{{< /alert >}}

## Configuration

The plugin supports many configuration options, eg:

```xml  
<plugin>
  <groupId>io.gatling</groupId>
  <artifactId>gatling-maven-plugin</artifactId>
  <version>MANUALLY_REPLACE_WITH_LATEST_VERSION</version>
  <configuration>
    <simulationClass>foo.Bar</simulationClass>
  </configuration>
</plugin>
```

See each goal's section below for the relevant configuration options.

## Usage

### Running your simulations

You can directly launch the `gatling-maven-plugin` with the `test` goal:

{{< code-toggle console >}}
Linux/MacOS: ./mvnw gatling:test
Windows: mvnw.cmd gatling:test
{{</ code-toggle >}}

Use `mvn gatling:help -Ddetail=true -Dgoal=test` to print the description of all the available configuration options on
the `test` goal.

The `gatling:test` goal runs in interactive mode and suggests the simulation class to launch unless:
* there's only one simulation available,
* or the Simulation class is forced with the `-Dgatling.simulationClass=<FullyQualifiedClassName>` Java System Property,
* or the non-interactive mode is forced, in which case the task will fail if there is more than 1 simulation available,
* or it's in batch mode (`-B` Maven option), in which case the task will fail if there is more than 1 simulation available,
* or the `CI` env var is set to `true`, in which case the task will fail if there is more than 1 simulation available.

### Running the Gatling Recorder

You can launch the [Gatling Recorder](https://docs.gatling.io/reference/script/protocols/http/recorder/):

{{< code-toggle console >}}
Linux/MacOS: ./mvnw gatling:recorder
Windows: mvnw.cmd gatling:recorder
{{</ code-toggle >}}

Use `gatling:help -Ddetail=true -Dgoal=recorder` to print the description of all the available configuration options
on the `recorder` goal.

### Running your simulations on Gatling Enterprise Edition Self-Hosted { #running-your-simulations-on-gatling-enterprise-self-hosted }

#### Build from sources

Once you have configured the Maven plugin on your project, Gatling Enterprise Edition Self-Hosted can build it from sources
without additional configuration.
[Add your source repository]({{< ref "/execute/repositories#downloading-from-sources" >}})
and configure your simulation to
[build from sources]({{< ref "/execute/simulations#option-1-build-from-sources" >}})
using Maven.

To make sure your setup is correct, you can run the packaging command and check that you get a jar containing all the
classes and extra dependencies of your project in `target/<artifactId>-<version>-shaded.jar`:

{{< code-toggle console >}}
Linux/MacOS: ./mvnw gatling:enterprisePackage
Windows: mvnw.cmd gatling:enterprisePackage
{{</ code-toggle >}}

#### Publish to a binary repository

Alternatively, you can package your simulations and publish them to a binary repository (JFrog Artifactory, Sonatype
Nexus or AWS S3).

{{< alert tip >}}
We use the standard Maven Deploy plugin; please refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/)
for generic configuration options. Please also check the standards within your organization for the best way to configure
the credentials needed to access your binary repository.
{{< /alert >}}

Configure the `repository` and/or `snapshotRepository` block, depending on whether you want to deploy releases or snapshots.

```xml
<distributionManagement>
  <repository>
    <id>your.releases.repository.id</id>
    <url>REPLACE_WITH_YOUR_RELEASES_REPOSITORY_URL</url>
  </repository>
  <snapshotRepository>
    <id>your.snapshots.repository.id</id>
    <url>REPLACE_WITH_YOUR_SNAPSHOTS_REPOSITORY_URL</url>
  </snapshotRepository>
</distributionManagement>
```

Bind the `gatling:enterprisePackage` goal to the Maven lifecycle in the plugin configuration:

```xml
<plugin>
  <groupId>io.gatling</groupId>
  <artifactId>gatling-maven-plugin</artifactId>
  <version>${gatling-maven-plugin.version}</version>
  <executions>
    <execution>
      <goals>
        <goal>enterprisePackage</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

The packaged artifact will be automatically attached to your project and deployed with the `shaded` classifier when you publish it:

{{< code-toggle console >}}
Linux/MacOS: ./mvnw deploy
Windows: mvnw.cmd deploy
{{</ code-toggle >}}

## Integrating with the Maven lifecycle

The plugin's goals can also be bound to the Maven lifecycle phases by configuring an execution block in the plugin configuration:

```xml
<plugin>
  <groupId>io.gatling</groupId>
  <artifactId>gatling-maven-plugin</artifactId>
  <version>MANUALLY_REPLACE_WITH_LATEST_VERSION</version>
  <executions>
    <execution>
      <goals>
        <goal>test</goal>
        <goal>enterprisePackage</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

By default:

- `test` will be bound to the `integration-test` phase, e.g. it will be triggered by `mvn integration-test` or `mvn verify`
- `enterprisePackage` will be bound to the `package` phase, e.g. it will be triggered by `mvn package`

## Overriding the logback.xml file

You can either have a `logback-test.xml` that has precedence over the embedded `logback.xml` file, or add a JVM option `-Dlogback.configurationFile=myFilePath`.

## Sources

If you're interested in contributing, you can find the [gatling-maven-plugin sources](https://github.com/gatling/gatling-maven-plugin) on GitHub.
