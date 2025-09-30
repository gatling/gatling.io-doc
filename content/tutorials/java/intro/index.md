---
title: Quickstart Load Testing with the Gatling Java SDK
menutitle: Java Quickstart
description: Set up a Gatling project on the JVM, run your first simulation with Maven, and explore next steps for JVM teams.
---

## Why start here
Use this page when you already know your way around Java build tooling and want the fastest route to a working Gatling project. You will get the minimum setup guidance, a ready-to-run simulation skeleton, and pointers to deeper resources.

### Choose the right companion guides
- Need a beginner-friendly walkthrough? Follow [Create your first Java-based simulation]({{< ref "tutorials/java/scripting-intro/index.md" >}}).
- Want a fuller catalogue of feeders, checks, and workload patterns? Continue with [Get started with the Java SDK]({{< ref "tutorials/java/get-started/index.md" >}}).
- Looking for SDK syntax or API details? Jump straight to the [reference documentation]({{< ref "reference/index.md" >}}).

## Quick checklist for experienced teams
| Question | Fast answer |
| --- | --- |
| Which JDK? | Use 17+ for local development; 11 is the minimal supported LTS for the SDK. |
| Which build tool? | Stay on Maven (or the Maven Wrapper) for the quickest path; see the Gradle guide if you prefer Gradle. |
| How do we parameterize runs? | Pass system properties (`-Dusers=...`) or rely on environment variables for secrets. |
| Where do reports land? | `target/gatling/<simulation>-<timestamp>/index.html` after every run. |

## Minimal prerequisites
- JDK 11 or newer (Gatling itself targets 17+).
- Maven 3.6.3+ or the Maven Wrapper supplied with the starter project.
- Optional: a Gatling Enterprise account if you need managed, distributed load generation.

Verify the toolchain in your terminal:

```shell
java -version
mvn -version
echo $JAVA_HOME
```

If `JAVA_HOME` is unset, follow your JDK vendor's instructions so Maven and IDEs resolve the JDK correctly.

## Bootstrap a Java project quickly
1. Fetch the starter project (clone or download a ZIP):
   ```shell
   git clone https://github.com/gatling/gatling-java-demo.git
   # or
   curl -L -o gatling-java-demo.zip https://github.com/gatling/gatling-java-demo/archive/refs/heads/main.zip
   unzip gatling-java-demo.zip
   ```
2. Open the project in your IDE (IntelliJ IDEA, Eclipse, or VS Code with the Java extension pack).
3. Stick with the Maven Wrapper (`./mvnw` or `mvnw.cmd`) if you do not install Maven system-wide. Every command below works with the wrapper.

Prefer a slower tutorial that shows every edit? Switch to [Create your first Java-based simulation]({{< ref "tutorials/java/scripting-intro/index.md" >}}).

## Recommended project layout (Maven + Java)

```text
gatling-java-demo/
├─ src/
│  ├─ test/
│  │  ├─ java/                     # Simulation classes live here
│  │  └─ resources/                # Feeders, bodies, gatling.conf, logback-test.xml
├─ pom.xml                         # Maven coordinates + gatling-maven-plugin
├─ .mvn/                           # Maven Wrapper scripts and settings
└─ README.md
```

- Keep simulation classes under `src/test/java` so the Gatling Maven plugin discovers them automatically.
- Drop CSV/JSON feeders and configuration files into `src/test/resources`; they are on the classpath at runtime.
- Add shared helpers (protocol builders, headers, checks) under `src/test/java/.../utils` and import them into simulations as needed.

## Maven essentials for Gatling
Pin the Gatling runtime and plugin versions in `pom.xml` to keep them aligned:

```xml
<properties>
  <gatling.version>3.9.5</gatling.version>
  <gatling.plugin.version>4.4.8</gatling.plugin.version>
</properties>
```

Add the Gatling Java SDK dependencies and plugin goals:

```xml
<dependencies>
  <dependency>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-core-java</artifactId>
    <version>${gatling.version}</version>
  </dependency>
  <dependency>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-http-java</artifactId>
    <version>${gatling.version}</version>
  </dependency>
</dependencies>

<build>
  <plugins>
    <plugin>
      <groupId>io.gatling</groupId>
      <artifactId>gatling-maven-plugin</artifactId>
      <version>${gatling.plugin.version}</version>
      <executions>
        <execution>
          <goals>
            <goal>test</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

Need more options (packaging, Enterprise automation, Gradle)? Continue with the [build-tool integrations]({{< ref "integrations/build-tools/maven-plugin/index.md" >}}).

## Base simulation template

```java
// src/test/java/com/example/simulations/BasicSimulation.java
package com.example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class BasicSimulation extends Simulation {
  HttpProtocolBuilder httpProtocol = http.baseUrl(System.getProperty("baseUrl", "http://localhost:8080"));

  ScenarioBuilder scn = scenario("Basic Scenario")
    .exec(http("GET root").get("/").check(status().is(200)));

  {
    setUp(
      scn.injectOpen(atOnceUsers(Integer.parseInt(System.getProperty("users", "1"))))
    ).protocols(httpProtocol);
  }
}
```

Run the simulation locally from the project root:

```shell
./mvnw -Dgatling.simulationClass=com.example.simulations.BasicSimulation gatling:test
```

Omit `-Dgatling.simulationClass=…` to choose a simulation interactively.

## Parameterize with properties or environment variables

| Setting | System property | Default |
| --- | --- | --- |
| Target base URL | `baseUrl` | `http://localhost:8080` |
| Concurrent users | `users` | `1` |
| Ramp duration (seconds) | `ramp` | `0` |

Resolve values in Java with `System.getProperty("users", "1")`. For secrets or environment-provided credentials, switch to `System.getenv()`—especially when promoting the same simulations to Gatling Enterprise.

## What to explore next
- Deep dive into scenarios, feeders, checks, and workload modelling in [Get started with the Java SDK]({{< ref "tutorials/java/get-started/index.md" >}}).
- Prefer a copy-and-paste walkthrough? Follow [Create your first Java-based simulation]({{< ref "tutorials/java/scripting-intro/index.md" >}}).
- Browse the [Java SDK reference]({{< ref "reference/script/http/index.md" >}}) and [protocol guides]({{< ref "reference/index.md" >}}) as you expand your tests.
- Evaluate distributed runs and real-time reporting with [Gatling Enterprise]({{< ref "evaluate-enterprise/trial-plan/index.md" >}}).
