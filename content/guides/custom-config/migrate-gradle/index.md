---
title: Migrate to the Gradle build tool
menutitle: Migate to Gradle
seotitle: Learn how to migrate to the Gradle build tool
description: Learn how to migrate to the Gradle build tool
lead: Learn how to migrate to the Gradle build tool
date: 2025-02-28T09:30:56+02:00
---

{{< alert info>}}
The following guide helps you migrate from the Gatling bundle versions 3.9.5 and earlier to the Gradle build tool. It is not useful for Gatling versions >3.10.   
{{< /alert >}}

## Introduction

The Gatling bundle is a self-contained project that lets you quickly start load testing. The bundle avoids manual configuration by including all the files and dependencies needed to run Gatling. Some benefits of the bundle are:

Straight forward installation
Faster initial setup due to predefined files
Quick set-up for feature experimentation
Sufficient for many small projects

## Build tool advantages 

For larger projects or deeper integration into the development process, the Gatling bundle becomes limited. In particular, integrating load tests into CI/CD pipelines is much easier with a build tool. If you have reached this point in your load testing, Gatling recommends migrating to a dependency management tool like Gradle for:

- Improved dependency management: Gradle automatically manages dependencies, ensuring you use the correct versions and avoid dependency conflicts.
- Source control management (SCM): Build tools isolate sources and libraries. As a result, your load tests become very easy to manage in an SCM such as git (which is not a library storage tool).
- Integrated IDEs: IDEs such as IntelliJ IDEA, Eclipse, and VSCode have built-in Gradle support. They let you import projects in just a few clicks. Then, you benefit from the IDE boons, including detailed logs and tracing, syntactic coloration, compile errors, automatic completion, and suggestions.
- Customization and extensibility: Gradle offers more advanced customization and extensibility possibilities, which is crucial when your testing needs increase in complexity.
- Scalability: Gradle offers better configuration management for large projects or complex load testing.

This guide details migrating Gatling tests to Gradle in 5 minutes or less.

## Bundle architecture

Before starting the migration process, it is useful to review the Gatling bundle file structure. The bundle contains the components required to run Gatling and has several key directories.bin: This directory stores the Gatling and the Recorder start scripts.

{{< img src="bundle-folders.webp">}}

_conf_: Housing the Gatling, Akka, and Logback configuration files; this directory is for the technical configuration of Gatling. You can configure the parameters of your tests and monitor the generated logs.

_lib_: This directory contains Gatling and its dependencies.

_results_: Stores your Gatling test results.

_user-files_: This directory centralizes your simulation scenarios. The simulations subfolder will receive the code for your Gatling tests. The resources subfolder hosts files other than the source code, such as feeder and templates for request bodies. You can also add dependencies in the lib subfolder.

## Migration prerequisites

- Upgrade Gatling to 3.9.5.
- Clone or download the Gatling-Gradle plugin demo
  - Java
  - Scala <!--add links-->

We recommend an IDE like IntelliJ for composing and managing load tests to benefit fully from Gradle’s functionality. To directly import the plugin demo with IntelliJ:

Go to File > New > Project from Version Control

<!--insert image-->

- Paste the repository URL,
- select your local directory,
- click Clone.

<!--insert image-->

## Check your Gatling version

You can identify the Gatling bundle version by the file name gatling-charts-highcharts-3.x.x.jar., where the 3.x.x indicates the version. For example, the current version is 3.9.5. The best practice is to have the same version in your Gradle project configuration and the bundle for a successful migration.

The demo projects on GitHub use the latest version of Gatling. If your version is older and you prefer to keep the existing one, you can adjust the version in your Gradle project configuration.

## Migrate your Java project to Gradle

| Element                | Location in the bundle   | Location in Gradle                     | Mandatory |
|------------------------|--------------------------|----------------------------------------|-----------|
| Test Case              | user-files/simulations/* | src/gatling/java/                      | Yes       |
| Resources              | user-files/resources/*   | src/gatling/resources/                 | Yes       |
| Gatling configuration  | conf/gatling.conf        | src/gatling/resources/gatling.conf     | No        |
| Logback configuration  | conf/logback.xml         | src/gatling/resources/logback-test.xml | No        |
| Recorder configuration | conf/recorder.conf       | src/gatling/resources/recorder.conf    | No        |

1. Copy all the files from `user-files/simulations/` in the Gatling bundle to `src/test/java/` in your Gradle project.
2. Copy all files from `user-files/resources/` in the Gatling bundle to `src/test/resources/` in your Gradle project.
3. (optional) Copy the Gatling configuration file gatling.conf from `/conf` in the Gatling bundle to `src/test/resources/` in your Gradle project.
4. (optional) Copy the logback configuration file logback.conf from `/conf` in the Gatling bundle to `src/test/resources/` in your Gradle project. Rename `logback.conf` to `logback-test.xml` to align with Gradle’s context.
(optional) Copy the recorder configuration file `recorder.conf` from `/conf` in the Gatling bundle to `src/test/resources/` in your Gradle project.

To configure a specific version of Gatling, modify the build.gradle file.

```xml
plugins {
    id 'java'
    // The following line allows to load io.gatling.gradle plugin and directly apply it
    id 'io.gatling.gradle' version '3.9.5.5'
}
```

### Migrate your Scala project to Gradle

| Element                | Location in the bundle   | Location in Gradle                     | Mandatory |
|------------------------|--------------------------|----------------------------------------|-----------|
| Test Case              | user-files/simulations/* | src/gatling/scala/                     | Yes       |
| Resources              | user-files/resources/*   | src/gatling/resources/                 | Yes       |
| Gatling configuration  | conf/gatling.conf        | src/gatling/resources/gatling.conf     | No        |
| Logback configuration  | conf/logback.xml         | src/gatling/resources/logback-test.xml | No        |
| Recorder configuration | conf/recorder.conf       | src/gatling/resources/recorder.conf    | No        |

1. Copy all files from `user-files/simulations/` of the Gatling bundle to `src/test/scala/` in your Gradle project.
2. Copy all files from `user-files/resources/` of the Gatling bundle to `src/test/resources/` in your Gradle project.
3. (optional) Copy the Gatling configuration file `gatling.conf` from `/conf` in the Gatling bundle to `src/test/resources/` in your Gradle project.
4. (optional) Copy the logback configuration file logback.conf from `/conf` in the Gatling bundle to `src/test/resources/` in your Gradle project. Rename `logback.conf` to `logback-test.xml` to align with Gradle’s context.
5. (optional) Copy the recorder configuration file `recorder.conf` from `/conf` in the Gatling bundle to `src/test/resources/` in your Gradle project.
 

To configure a specific version of Gatling, modify the build.gradle file.

```xml
plugins {
    id 'scala'
    // The following line allows to load io.gatling.gradle plugin and directly apply it
    id 'io.gatling.gradle' version '3.9.5.5'
}
```

### Verifying migration success

Once you have completed migrating files from the Gatling bundle to your Gradle project, you should validate the migration. You can use the Gradle wrapper or locally install Gradle to validate the migration.

The Gradle wrapper, built by Gatling, provides sufficient flexibility to run your load tests for most projects. Use the built-in wrapper by running the following command in the root directory of your Gradle project.

{{< platform-toggle >}}
Linux/MacOS: ./gradlew gatlingRun
Windows: gradlew.bat gatlingRun
{{</ platform-toggle >}}

Some reasons you might not want to use the wrapper include corporate network restrictions or the need for more granular settings. For these cases, installing Gradle is preferable. See the official Gradle website for installation instructions.

Run your Gatling test with a local Gradle installation with the following command in the root directory of your project.

```console
gradle gatlingRun
```
If your test ran successfully, you have successfully migrated! You can learn more about using Gradle with Gatling from the following resources:

<!-- add links-->
- The Gatling Documentation has more details on test configuration with the Gradle plugin.
- The Community Forum is a free resource for asking questions and helping other community members learn to load tests with Gatling.
- The Gatling Customer Success team (customersuccess@gatling.io) is available to customers for assistance in using Gradle or any other Gatling-supported tools and plugins.
- The Gradle plugin works with Gatling Enterprise. Gatling Enterprise offers a variety of features to elevate your testing, collaboration, and security. Try Gatling and Gradle today with our free trial.
