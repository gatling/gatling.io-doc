---
title: Installation Guide
menutitle: Installation Guide
seotitle: Gatling Java and JDK Language SDK Installation Guide
description: Step-by-step guide to installing the Gatling Java SDK.
---

## What the SDK delivers
The Gatling Java SDK lets you script load tests with Java, Kotlin, or Scala while using the Gatling engine for execution and reporting. It fits teams that already work with JVM tooling and want reusable, version-controlled performance tests.

## When to choose it
- You prefer writing load tests in Java, Kotlin, or Scala.
- You need to run tests locally first and keep the option to scale with Gatling Enterprise.
- You want Gatling's HTML reports, assertions, and workload models without changing ecosystems.

## Requirements
- 64-bit OpenJDK LTS versions 11 through 25 installed on macOS, Linux, or Windows. We recommend the [Azul JDK](https://www.azul.com/downloads/?package=jdk#zulu).
- Maven 3.6.3+ or Gradle 7.6+ (or use the wrapper supplied with the starter project).
- Git or download access to the Gatling Java demo project.
- A Gatling Enterprise Edition account for distributed runs.

Verify the prerequisites before continuing:

```shell
java -version
mvn -version
```

{{< alert warning >}}
Gatling launch scripts and the Maven plugin honor `JAVA_HOME`. If this environment variable points at the wrong runtime you might see `Unsupported major.minor version` errors. Double-check it before running test suites.
{{< /alert >}}

## Download the starter project {#zip-install}
Bootstrap your workspace from the official demo repository:

{{< button title="Download Gatling for Java" >}}
https://github.com/gatling/gatling-maven-plugin-demo-java/archive/refs/heads/main.zip{{< /button >}}

1. Unzip the archive and open it in your IDE or editor.
2. The project uses Maven with the Maven Wrapper (`./mvnw` or `mvnw.cmd` on Windows) so you don't need Maven installed system-wide.
3. Install dependencies with Maven:

```shell
./mvnw clean install
```

Prefer cloning? Use:

```shell
git clone https://github.com/gatling/gatling-maven-plugin-demo-java.git
cd gatling-maven-plugin-demo-java
./mvnw clean install
```

If you prefer Gradle, download the [Gradle starter project](https://github.com/gatling/gatling-gradle-plugin-demo-java) instead and use `./gradlew` commands.

### Alternative: Kotlin and Scala starters
If you would rather write simulations in another JVM language, start from the language-specific templates:

- [Maven + Kotlin demo](https://github.com/gatling/gatling-maven-plugin-demo-kotlin)
- [Gradle + Kotlin demo](https://github.com/gatling/gatling-gradle-plugin-demo-kotlin)
- [Maven + Scala demo](https://github.com/gatling/gatling-maven-plugin-demo-scala)
- [Gradle + Scala demo](https://github.com/gatling/gatling-gradle-plugin-demo-scala)
- [Gradle + Scala demo](https://github.com/gatling/gatling-gradle-plugin-demo-scala)
- [sbt + Scala demo](https://github.com/gatling/gatling-sbt-plugin-demo)

## Run the demo simulation
Confirm everything works by running the bundled sample scenario:

```shell
./mvnw gatling:test
```

The Maven plugin will prompt you to select a simulation if multiple exist, or run the only available one automatically. The HTML report lands under `target/gatling/`. Open the most recent folder in your browser to inspect the results.

To run a specific simulation without the interactive prompt:

```shell
./mvnw gatling:test -Dgatling.simulationClass=computerdatabase.ComputerDatabaseSimulation
```

## Use the standalone bundle

The Gatling bundle is primarily intended for users who don't have internet access (e.g., behind a corporate firewall). Otherwise, we strongly recommend using the Maven plugin, which is lighter and easier to push to Git.

From Gatling 3.11, the bundle is based on a Maven wrapper, and we recommend using it with an IDE such as IntelliJ.

{{< button title="Download Gatling bundle" >}}
https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/{{< var gatlingVersion >}}/gatling-charts-highcharts-bundle-{{< var gatlingVersion >}}-bundle.zip
{{< /button >}}

{{< alert warning >}}
The bundle only supports Java, not Scala and Kotlin. To use Kotlin or Scala, you need a [Maven, Gradle, or sbt]({{< ref "#zip-install" >}}) project.
{{< /alert >}}

{{< alert warning >}}
Windows users:
- We recommend that you do not place Gatling in the *Programs* folder as there may be permission and path issues.
- The standard Windows zip tool will not work to extract the bundle. We recommend using [7-zip](https://www.7-zip.org/) instead.
{{< /alert >}}

The bundle structure is as follows:

* `src/test/java`: where to place your Simulations code. You must respect the package folder hierarchy.
* `src/test/resources`: non-source code files such as feeder files and templates for request bodies and configuration files for Gatling, Akka and Logback.
* `pom.xml`: Maven information about the project.
* `target`: where test results are generated.

For all details regarding the installation and tuning of the operating system (OS), please refer to the [operations]({{< ref "/concepts/operations" >}}) section.

## Start the Gatling Recorder

The [Gatling Recorder]({{< ref "/reference/script/http/recorder/" >}}) allows you to capture browser-based actions and convert them into a script. Use the following command to launch the Recorder:

{{< platform-toggle >}}
Linux/MacOS: ./mvnw gatling:recorder
Windows: mvnw.cmd gatling:recorder
{{</ platform-toggle >}}

For Gradle users:

{{< platform-toggle >}}
Linux/MacOS: ./gradlew gatlingRecorder
Windows: gradlew.cmd gatlingRecorder
{{</ platform-toggle >}}

## IDE setup

You can edit your Simulation classes with any text editor, but using an IDE provides better code completion, refactoring, and debugging capabilities.

### IntelliJ IDEA

IntelliJ IDEA Community Edition comes with Java, Kotlin, Maven, and Gradle support enabled by default.

If you want to use Scala and possibly sbt, you'll have to install the Scala plugin, which is available in the Community Edition. You'll most likely have to increase the stack size for the Scala compiler so you don't suffer from StackOverflowErrors. We recommend setting `Xss` to `100M`.

To configure this in IntelliJ:
1. Go to **Settings** → **Build, Execution, Deployment** → **Compiler** → **Scala Compiler**
2. In the **Scala Compiler** section, add `-Xss100M` to the compiler options

### VS Code

We recommend that you have a look at the official documentation for setting up VS Code:
* [with Java](https://code.visualstudio.com/docs/java/java-build)
* [with Kotlin](https://kotlinlang.org/docs/jvm-get-started.html)
* [with Scala](https://scalameta.org/metals/)

## Where to go next
- Walk through your first end-to-end run in [Your First Simulation]({{< ref "tutorials/test-as-code/java-jvm/running-your-first-simulation/" >}}).
- Learn the broader SDK surface in [Explore the SDK]({{< ref "tutorials/test-as-code/java-jvm/full-sdk-capabilities/" >}}).
- Explore all of the Maven plugin options in the [Maven Plugin Reference]({{< ref "integrations/build-tools/maven-plugin/" >}}).
- Explore all of the Gradle plugin options in the [Gradle Plugin Reference]({{< ref "integrations/build-tools/gradle-plugin/" >}}).
