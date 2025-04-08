---
title: Migrate to the Maven build tool
menutitle: Migrate to Maven
seotitle: Learn how to migrate to the Maven build tool.
description: Learn how to migrate to the Maven build tool.
lead: Learn how to migrate to the Maven build tool.
date: 2025-02-27T09:30:56+02:00
---

## Introduction

{{< alert info>}}
The following guide helps you migrate from the Gatling bundle versions 3.9.5 and earlier to the Maven build tool. It is not useful for Gatling versions >3.10.
{{< /alert >}}

The Gatling bundle is an all-in-one project to get started quickly with Gatling. This bundle includes all the files and dependencies needed to run Gatling without having to configure dependencies manually. Its benefits include:

- Simple installation
- Reduced initial setup due to predefined files
- Quick set-up for experimenting with features
- Convenience for small projects

## The advantages of a build tool

The Gatling bundle, however, has limitations for larger projects or deeper integration into the development process. These limitations are particularly true if you want to integrate load tests into a CI/CD pipeline. We recommend migrating to a dependency management tool like Maven at this stage for:

- Improved dependency management: Maven automatically manages dependencies, ensuring you use the correct versions and avoid dependency conflicts.
- Source control management (SCM): Build tools isolate sources and libraries. As a result, your load tests become very easy to manage in an SCM such as git (which is not a library storage tool).
- Integrated IDEs: IDEs such as IntelliJ IDEA, Eclipse, and VSCode have built-in Maven support. They let you import projects in just a few clicks. Then, you benefit from the IDE boons, including detailed logs and tracing, syntactic coloration, compile errors, automatic completion, and suggestions.
- Customization and extensibility: Maven offers more advanced customization and extensibility possibilities, which is crucial when your testing needs increase in complexity.
- Scalability: Maven offers better configuration management for large projects or complex load testing.

This guide details how to migrate your Gatling tests to Maven in less than 5 minutes.

## Bundle architecture

To better understand the migration process, examining the operation and architecture of the Gatling bundle is helpful. The bundle combines the components necessary to run Gatling and has several important directories.

{{< img src="bundle-folders.webp">}}

_conf_: Housing the Gatling, Akka, and Logback configuration files; this directory is for the technical configuration of Gatling. You can configure the parameters of your tests and monitor the generated logs.

_lib_: This directory contains Gatling and its dependencies.

_results_: Stores your Gatling test results.

_user-files_: This directory centralizes your simulation scenarios. The simulations subfolder will receive the code for your Gatling tests. The resources subfolder hosts files other than the source code, such as feeder and templates for request bodies. You can also add dependencies in the lib subfolder.

## Migration prerequisites

- Upgrade Gatling to the latest release.
- Clone or download the [Gatling-Maven plugin demo]({{< ref "/reference/integrations/build-tools/maven-plugin" >}})

Gatling recommends using an IDE like IntelliJ for writing and managing load tests. With IntelliJ, you can directly clone the plugin demo:

Go to File > New > Project from Version Control

- Paste the repository URL,
- select your local directory,
- click Clone.

### Note on Gatling versions

To determine your Gatling bundle version, navigate to the dependencies folder lib and read the file name: gatling-charts-highcharts-3.x.x.jar. The 3.x.x indicates the Gatling version. For example, the current version is 3.9.5. Align the Gatling version in your Maven project configuration and the bundle to ensure successful synchronization.

Note that demo projects on GitHub use the latest version of Gatling. If your version is older and you prefer to keep the existing one, you must adjust the one in your Maven project.

## Migrate your Java project to Maven

| Element                | Location in the bundle   | Location in Maven                  | Mandatory |
|------------------------|--------------------------|-------------------------------------|-----------|
| Test Case              | user-files/simulations/* | src/test/java/                      | Yes       |
| Resources              | user-files/resources/*   | src/test/resources/                 | Yes       |
| Gatling configuration  | conf/gatling.conf        | src/test/resources/gatling.conf     | No        |
| Logback configuration  | conf/logback.xml         | src/test/resources/logback-test.xml | No        |
| Recorder configuration | conf/recorder.conf       | src/test/resources/recorder.conf    | No        |

1. Copy all files from `user-files/simulations/` of the Gatling bundle to `src/test/java/` in your Maven project.
2. Copy all files from `user-files/resources/` of the Gatling bundle to `src/test/resources/` in your Maven project.
3. (optional) Copy the Gatling configuration file gatling.conf from ``/conf`` in the Gatling bundle to `src/test/resources/` in your Maven project.
4. (optional) Copy the logback configuration file `logback.conf` from ``/conf`` in the Gatling bundle to `src/test/resources/` in your Maven project. Rename `logback.conf` to `logback-test.xml` to align with Maven’s context.
(optional) Copy the recorder configuration file `recorder.conf` from ``/conf`` in the Gatling bundle to `src/test/resources/` in your Maven project.
Open `pom.xml` in the Maven project and modify it to compile with your version of Java:

```xml
<!-- use the following if you're compiling with JDK 8-->
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <!-- comment the 2 lines above and uncomment the line bellow if you're compiling with JDK 11 or 17 -->
    <!--    <maven.compiler.release>11</maven.compiler.release>-->
```

Or to configure the Gatling version:

```xml
<version>3.9.5</version>
```

## Migrate your Scala project to Maven

| Element                | Location in the bundle   | Location in Maven                  | Mandatory |
|------------------------|--------------------------|-------------------------------------|-----------|
| Test Case              | user-files/simulations/* | src/test/scala/                     | Yes       |
| Resources              | user-files/resources/*   | src/test/resources/                 | Yes       |
| Gatling configuration  | conf/gatling.conf        | src/test/resources/gatling.conf     | No        |
| Logback configuration  | conf/logback.xml         | src/test/resources/logback-test.xml | No        |
| Recorder configuration | conf/recorder.conf       | src/test/resources/recorder.conf    | No        |

1. Copy all files from user-files/simulations/ of the Gatling bundle to src/test/scala/ in your Maven project.
2. Copy all files from user-files/resources/ of the Gatling bundle to src/test/resources/ in your Maven project.
3. (optional) Copy the Gatling configuration file gatling.conf from `/conf` in the Gatling bundle to src/test/resources/ in your Maven project.
4. (optional) Copy the logback configuration file logback.conf from `/conf` in the Gatling bundle to src/test/resources/ in your Maven project. Rename logback.conf to logback-test.xml to align with Maven’s context.
5. (optional) Copy the recorder configuration file recorder.conf from `/conf` in the Gatling bundle to src/test/resources/ in your Maven project.
6. Modify pom.xml from the Maven project to configure a specific version of Gatling.

## Verifying migration success

After migrating your files from the Gatling bundle to your Maven project, it is time to validate the migration. For this, you can either use the Maven wrapper or locally install Maven.

For most projects, the Maven wrapper, built by Gatling, provides sufficient flexibility to run your load tests. To use the built-in wrapper, run the following command in the root directory of your Maven project.

{{< platform-toggle >}}
Linux/MacOS: ./mvnw gatling:test
Windows: mvnw.cmd gatling:test
{{</ platform-toggle >}}

Some projects can’t use the wrapper due to corporate network restrictions or needing more fine-grained control of settings. In this case, installing Maven is a better choice. See the official Maven website for installation instructions.

To run your Gatling test with a local Maven installation, run the following command in the root directory of your project.

```console
mvn gatling:test
```

Assuming your test ran successfully, you are finished! To learn more about using Maven and Gatling, we offer multiple resources:

- The [Gatling Maven plugin documentation]({{< ref"/reference/integrations/build-tools/maven-plugin" >}}) contains additional information for configuring tests using the Maven plugin.
- [The Gatling Community Forum](https://community.gatling.io/) is a great place to ask questions about using Maven and Gatling.