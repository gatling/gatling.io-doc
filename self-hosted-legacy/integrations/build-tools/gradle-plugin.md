---
menutitle: Gradle Plugin
title: Gatling Gradle Plugin
seotitle: Gradle Plugin for Gatling and Gatling Enterprise
description: How to use the Gradle plugin for Gatling to run tests and deploy them to Gatling Enterprise.
lead: The Gradle plugin allows you to run Gatling tests from the command line, without the bundle, as well as to package your simulations for Gatling Enterprise
date: 2021-04-20T18:30:56+02:00
lastmod: 2022-12-14T21:30:56+02:00
---

This Gradle plugin was initially contributed by [Ievgenii Shepeliuk](https://github.com/eshepelyuk) and
[Laszlo Kishalmi](https://github.com/lkishalmi).

This Gradle plugin integrates Gatling with Gradle, allowing to use Gatling as a testing framework.

## Versions

Check out available versions on [Gradle Plugins Portal](https://plugins.gradle.org/plugin/io.gatling.gradle).

## Compatibility

### Gradle version

{{< alert warning >}}
This plugin requires at least Gradle 7.1.
{{< /alert >}}

The latest version of this plugin is tested against Gradle versions ranging from 7.1 to 8.6.
Any version outside this range is not guaranteed to work.

## Setup

{{< alert tip >}}
Cloning or downloading one of our demo projects on GitHub is definitely the fastest way to get started:
* [for gradle and Java](https://github.com/gatling/gatling-gradle-plugin-demo-java)
* [for gradle and Kotlin](https://github.com/gatling/gatling-gradle-plugin-demo-kotlin)
* [for gradle and Scala](https://github.com/gatling/gatling-gradle-plugin-demo-scala)

They also come pre-configured with the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html), so you don't need to install Gradle yourself.
{{< /alert >}}

If you prefer to manually configure your Gradle project rather than clone one of our samples, you need to add the following to your `build.gradle`:

```groovy
 plugins {
   id 'io.gatling.gradle' version "MANUALLY_REPLACE_WITH_LATEST_VERSION"
 }
```

### Multi-project support

If you have a [multi-project build](https://docs.gradle.org/current/userguide/multi_project_builds.html), make sure to
only configure the subprojects which contain Gatling Simulations with the Gatling plugin as described above. Your
Gatling subproject can, however, depend on other subprojects.

## Source files layout

The plugin creates a dedicated [Gradle sourceSet](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.SourceSet.html)
named `gatling`. This source set is used for storing simulations and Gatling configurations. The following directories
are configured by default.

| Directory               | Purpose                                         |
| ----------------------- | ----------------------------------------------- |
| `src/gatling/java`      | Simulation sources (Java code)                  |
| `src/gatling/kotlin`    | Simulation sources (Kotlin code)                |
| `src/gatling/scala`     | Simulation sources (Scala code)                 |
| `src/gatling/resources` | Resources (feeders, configuration, bodies, etc) |

Using the Gradle API, file locations can be customized.

```groovy
sourceSets {
  gatling {
    scala.srcDir "folder1" <1>
    // or
    scala.srcDirs = ["folder1"] <2>

    resources.srcDir "folder2" <3>
    // or
    resources.srcDirs = ["folder2"] <4>
  }
}
```

1. append `folder1` as an extra simulations' folder.
2. use `folder1` as a single source of simulations.
3. append `folder2` as an extra `Gatling` resources folder.
4. use `folder2` as a single source of `Gatling` resources.

## Plugin configuration

The plugin defines the following extension properties in the `gatling` closure:

| Property name       | Type    | Default value                                                                                                                                                                                         | Description                                                                            |
|---------------------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| `gatlingVersion`    | String  | The first 3 digits of this plugin's version                                                                                                                                                           | Gatling version                                                                        |
| `includeMainOutput` | Boolean | `true`                                                                                                                                                                                                | `true`                                                                                 |
| `includeTestOutput` | Boolean | `true`                                                                                                                                                                                                | Include test source set output to gatlingImplementation                                |
| `scalaVersion`      | String  | `'2.13.8'`                                                                                                                                                                                            | Scala version that fits your Gatling version                                           |
| `jvmArgs`           | List    | <pre>[<br> '-server',<br> '-Xmx1G',<br> '-XX:+HeapDumpOnOutOfMemoryError',<br> '-XX:+UseG1GC',<br> '-XX:+ParallelRefProcEnabled',<br> '-XX:MaxInlineLevel=20',<br> '-XX:MaxTrivialSize=12'<br>]</pre> | Additional arguments passed to JVM when executing Gatling simulations                  |
| `systemProperties`  | Map     | `['java.net.preferIPv6Addresses': true]`                                                                                                                                                              | Additional systems properties passed to JVM together with caller JVM system properties |
| `simulation`        | String  | A fully qualified class name that extends a Gatling `Simulation`                                                                                                                                      | The simulation to run                                                                  |
| `apiToken`          | String  | `null`, optional                                                                                                                                                                                      | Your Gatling Enterprise api token                                                      |

How to override Gatling version, JVM arguments and system properties:

```groovy
gatling {
  gatlingVersion = '3.8.3'
  jvmArgs = ['-server', '-Xms512M', '-Xmx512M']
  systemProperties = ['file.encoding': 'UTF-8']
}
```

## Gatling configuration

### Override gatling.conf settings

To override Gatling's
[default parameters](https://github.com/gatling/gatling/blob/main/gatling-core/src/main/resources/gatling-defaults.conf),
put your own version of `gatling.conf` into `src/gatling/resources`.

### Logging management

Gatling uses [Logback](http://logback.qos.ch/documentation.html). To change the logging behaviour, put your
custom `logback.xml` configuration file in the resources folder, `src/gatling/resources`.

## Dependency management

This plugin defines three [Gradle configurations](https://docs.gradle.org/current/dsl/org.gradle.api.artifacts.Configuration.html):
`gatling`, `gatlingImplementation` and `gatlingRuntimeOnly`.

By default, the plugin adds Gatling libraries to `gatling` configuration.
Configurations `gatlingImplementation` and `gatlingRuntimeOnly` extend `gatling`,
i.e. all dependencies declared in `gatling` will be inherited. Dependencies added
to configurations other than these '`gatling`' configurations will not be available
within Gatling simulations.

Also, project classes (`src/main`) and tests classes (`src/test`) are added to
`gatlingImplementation` and `gatlingRuntimeOnly` classpath, so you can reuse
existing production and test code in your simulations.

If you do not need such behaviour, you can use flags. Manage test and main output:

```groovy
gatling {
  // do not include classes and resources from src/main
  includeMainOutput = false
  // do not include classes and resources from src/test
  includeTestOutput = false
}
```

Additional dependencies can be added to any of the configurations mentioned above. Add external libraries for `Gatling`
simulations:

```groovy
dependencies {
  gatling 'com.google.code.gson:gson:2.8.0' // <1>
  gatlingImplementation 'org.apache.commons:commons-lang3:3.4' // <2>
  gatlingRuntimeOnly 'cglib:cglib-nodep:3.2.0' // <3>
}
```

1. adding gson library, available both in compile and runtime classpath.
2. adding commons-lang3 to compile classpath for simulations.
3. adding cglib to runtime classpath for simulations.

## Tasks

### Running your simulations

Use the task `GatlingRunTask` to execute Gatling simulations. You can create your own instances of this task
to run particular simulations, or use the default `gatlingRun` task.

By default, the `gatlingRun` task runs in interactive mode and suggests the simulation class to launch unless:
* there's only one Simulation available,
* or the Simulation class is forced with the `--simulation=<FullyQualifiedClassName>` option,
* or the non-interactive mode is forced with the `--non-interactive` option, in which case the task will fail if there is more than 1 simulation available,
* or the `CI` environment variable is set to true, in which case the task will fail if there is more than 1 simulation available.

For example, to run a simulation:

{{< code-toggle console >}}
Linux/MacOS: ./gradlew gatlingRun
Windows: gradlew.bat gatlingRun
{{</ code-toggle >}}

Run a single simulation by its FQN (fully qualified class name):

{{< code-toggle console >}}
Linux/MacOS: ./gradlew gatlingRun --simulation com.project.simu.MySimulation
Windows: gradlew.bat gatlingRun --simulation com.project.simu.MySimulation
{{</ code-toggle >}}

You can run all simulations with the `--all` option:
{{< code-toggle console >}}
Linux/MacOS: ./gradlew gatlingRun --all
Windows: gradlew.bat gatlingRun --all
{{</ code-toggle >}}

The following configuration options are available. Those options are similar to
global `gatling` configurations. Options are used in a fallback manner, i.e. if
an option is not set the value from the `gatling` global config is taken.

| Property name      | Type | Default value | Description |
|--------------------| --- | --- | --- |
| `simulation`       | String  | The only class that extends a Gatling `Simulation` | The simulation to run |
| `jvmArgs`          | List<String>        | `null` | Additional arguments passed to JVM when executing Gatling simulations |
| `systemProperties` | Map<String, Object> | `null` | Additional systems properties passed to JVM together with caller JVM system properties |
| `environment`      | Map<String, Object> | `null` | Additional environment variables passed to the simulation |

### Running the Gatling Recorder

You can launch the [Gatling Recorder](https://docs.gatling.io/reference/script/protocols/http/recorder/):

{{< code-toggle console >}}
Linux/MacOS: ./gradlew gatlingRecorder
Windows: gradlew.bat gatlingRecorder
{{</ code-toggle >}}

### Running your simulations on Gatling Enterprise Self-Hosted

#### Build from sources

Once you have configured the Gradle plugin on your project, Gatling Enterprise Self-Hosted can build it from sources
without additional configuration.
[Add your source repository]({{< ref "/execute/repositories#downloading-from-sources" >}})
and configure your simulation to
[build from sources]({{< ref "/execute/simulations#option-1-build-from-sources" >}})
using Gradle or Gradle Wrapper.

To make sure your setup is correct, you can run the packaging command and check that you get a jar containing all the
classes and extra dependencies of your project in `build/libs/<artifactId>-<version>-tests.jar`:

{{< code-toggle console >}}
Linux/MacOS: ./gradlew gatlingEnterprisePackage
Windows: gradlew.bat gatlingEnterprisePackage
{{</ code-toggle >}}

#### Publish to a binary repository

Alternatively, you can package your simulations and publish them to a binary repository (JFrog Artifactory, Sonatype
Nexus or AWS S3).

{{< alert tip >}}
We use the official Maven Publish plugin for Gradle; please refer to the [official documentation](https://docs.gradle.org/current/userguide/publishing_maven.html)
for generic configuration options. Please also check the standards within your organization for the best way to configure
the credentials needed to access your binary repository.
{{< /alert >}}

Configure the `maven-publish` plugin to use the task named `gatlingEnterprisePackage`, then define the repository to
publish to:

```groovy
plugins {
  id "maven-publish"
}

publishing {
  publications {
    mavenJava(MavenPublication) {
      artifact gatlingEnterprisePackage
    }
  }
  repositories {
    maven {
      if (project.version.endsWith("-SNAPSHOT")) {
        url "REPLACE_WITH_YOUR_SNAPSHOTS_REPOSITORY_URL"
      } else {
        url "REPLACE_WITH_YOUR_RELEASES_REPOSITORY_URL"
      }
    }
  }
}
```


The packaged artifact will be deployed with the `tests` classifier when you publish it:

{{< code-toggle console >}}
Linux/MacOS: ./gradlew publish
Windows: gradlew.bat publish
{{</ code-toggle >}}


## Sources

If you're interested in contributing, you can find the [io.gatling.gradle plugin sources](https://github.com/gatling/gatling-gradle-plugin) on GitHub.
