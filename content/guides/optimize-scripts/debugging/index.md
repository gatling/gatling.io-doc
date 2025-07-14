---
menutitle: Debugging 
title: Debugging guide
seotitle: Debugging guide for Gatling scripts
description: Debug Gatling scripts by printing session values or with logback.
lead: Debug Gatling scripts by printing session values or with logback.
aliases:
  - /guides/debugging
---

## Printing Session Values

Print a session value.

{{< alert warning >}}
Only use `println` for debugging, not under load.
sysout is a slow blocking output, massively writing in here will freeze Gatling's engine and break your test.
{{< /alert >}}

{{< include-code "print-session-value" >}}

## Logback

There's a logback.xml file in the Gatling conf directory.
You can either set the log-level to TRACE to log all HTTP requests and responses or DEBUG to log failed HTTP request and responses.

```xml
<!-- uncomment and set to DEBUG to log all failing HTTP requests -->
<!-- uncomment and set to TRACE to log all HTTP requests -->
<!--<logger name="io.gatling.http.engine.response" level="TRACE" />-->
```

It will by default print debugging information to the console, but you can add a file appender:

```xml
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
  <file>PATH_TO_LOG_FILE</file>
  <append>true</append>
  <encoder>
    <pattern>%d{HH:mm:ss.SSS} [%-5level] %logger{15} - %msg%n%rEx</pattern>
  </encoder>
</appender>
```

And reference that appender:

```xml
<root level="WARN">
  <appender-ref ref="FILE" />
</root>
```

This can be useful if you run at one user and remove all logging apart from the HTML, and open the file in your browser.

## Using a Java debugger

{{< alert warning >}}
Requires at least:

* Gatling 3.13.4
* gatling-maven-plugin 4.14.0
* gatling-gradle-plugin 3.13.4

{{< /alert >}}

When launching Gatling from Maven or Gradle, you can attach a debugger so you can execute step-by-step.

### Maven with IntelliJ IDEA

Please check the official [IntelliJ documentation](https://www.jetbrains.com/help/idea/run-debug-configuration-maven.html).

Here are the mandatory steps:

1. Click on `Add Configuration` or `Edit Configurations` to the left of the `Run` and `Debug` buttons on the right side of the top bar.
2. Click on the `+` sign and add a new `Maven` configuration
3. Enter `gatling:test -Dgatling.sameProcess=true` in the `Command line` field
4. Click on `Modify` Java Options, select `Add JVM options` and enter `--add-opens=java.base/java.lang=ALL-UNNAMED`
5. Click on the `Debug` button to launch

{{< img src="intellij-maven-debug.png" alt="IntelliJ Maven Debug Configuration" >}}

### Gradle with IntelliJ IDEA

Please check the official [IntelliJ documentation](https://www.jetbrains.com/help/idea/run-debug-gradle.html).

Here are the mandatory steps:

1. Click on `Add Configuration` or `Edit Configurations` to the left of the `Run` and `Debug` buttons on the right side of the top bar.
2. Click on the `+` sign and add a new `Gradle` configuration
3. Enter `gatlingRun --same-process` in the `Task and arguments` field
4. Click on `Modify options`, select `Add VM options` and enter `--add-opens=java.base/java.lang=ALL-UNNAMED`
5. Click on the `Debug` button to launch

{{< img src="intellij-gradle-debug.png" alt="IntelliJ Gradle Debug Configuration" >}}
