---
menutitle: Testing gRPC
title: How to test the gRPC protocol
seotitle: Learn how to load test gRPC with Gatling
description: Learn how to load test gRPC with Gatling
lead: Learn how to load test gRPC with Gatling
date: 2025-02-28T8:30:56+02:00
aliases:
  - /guides/complex-use-cases/docker-app/
---

The Gatling gRPC SDK lets you create code-based load tests for your services that use the gRPC protocol. This guide provides an introduction to testing gRPC with Gatling. The SDK is additionally described in the [reference documentation]({{< ref="/reference/script/protocols/grpc/setup" >}}). 

## Setting up our environment

First, ensure you have Gatling installed and set up. If you haven't already, download it from the official documentation. Now [clone the project from GitHub](https://github.com/gatling/gatling-grpc-demo) and open the `GreetingSimulation` on your IDE.

## Explaining the simulation

### Proto

A proto (protocol buffer) in gRPC is a language-agnostic data serialization format that defines the structure of messages and services using an interface definition language. It allows you to specify how you want your data to be structured and typed, and gRPC uses this definition to automatically generate code in multiple languages. In our case, we will use the Greeting proto.

Our proto is composed of 2 services: Greet and GreetWithDeadline. The two functions take a first and last name and greet you. We will also test the deadline of gRPC using the second function and Gatling.

### Setup of libs and protocol

First, we need to import the libraries we need to launch our simulation. For the greetings simulation, we need these libraries:

```java
package io.gatling.grpc.demo;

import java.time.Duration;
import java.util.function.Function;

import io.gatling.grpc.demo.greeting.*;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.grpc.*;

import io.grpc.Status;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.grpc.GrpcDsl.*;
```

After that, we need to set up our base protocol for Gatling to connect to our gRPC application.

```java
public class GreetingSimulation extends Simulation {

  GrpcProtocolBuilder baseGrpcProtocol = grpc.forAddress("localhost", 50051)
    .channelCredentials("#{channelCredentials}")
    .overrideAuthority("gatling-grpc-demo-test-server");

  // ...
}
```

The gRPC Protocol Builder tells Gatling to connect to our gRPC application using channel credentials and by overriding the Authority. Now that we are connected, we need to define the user journey of our users.

### Set up the user journey

Now, we need to create a function that will take the first and last names from the session and pass them to the builder of our service.

```java
Function<Session, Greeting> greeting = session -> {
  String firstName = session.getString("firstName");
  String lastName = session.getString("lastName");
  return Greeting.newBuilder()
    .setFirstName(firstName)
    .setLastName(lastName)
    .build();
};
```

This function retrieves the first and last name from the session and creates the Greeting service using our first and last names as arguments. We will use it inside our two scenarios.

### Scenario greetings

So in our first scenario, we want to test if the service Greet works by checking its status code and the responses we get. To do this, here is the Gatling code:

```java
ScenarioBuilder unary = scenario("Greet Unary")
  .feed(Feeders.channelCredentials().circular())
  .feed(Feeders.randomNames())
  .exec(grpc("Greet")
    .unary(GreetingServiceGrpc.getGreetMethod())
    .send(session -> GreetRequest.newBuilder()
      .setGreeting(greeting.apply(session))
      .build())
    .check(
      statusCode().is(Status.Code.OK),
      response(GreetResponse::getResult).isEL("Hello #{firstName} #{lastName}")));
```

Let me explain it to you:

- The first feeder: This one permits getting random credentials to secure the channel.
- The second feeder: This one permits getting random first and last names.
- In the exec part:

  - The unary call allows testing unary methods; inside of it, we put the name of the service we want to test (if you want to understand more about unary, client/server stream, check the gRPC documentation)
  - In the send section, we create the object we will send to our server and we put the names coming from the second feeder.
  - In the check section, we verify that the server returns an OK status and the output string equals the one we provided inside the `isEL`.

### Scenario greetings with deadlines

So in our second scenario, we want to test the same thing but we add a deadline.

```java
ScenarioBuilder deadlines = scenario("Greet w/ Deadlines")
  .feed(Feeders.channelCredentials().circular())
  .feed(Feeders.randomNames())
  .exec(grpc("Greet w/ Deadlines")
    .unary(GreetingServiceGrpc.getGreetWithDeadlineMethod())
    .send(session -> GreetRequest.newBuilder()
      .setGreeting(greeting.apply(session))
      .build())
    .deadlineAfter(Duration.ofMillis(100))
    .check(statusCode().is(Status.Code.DEADLINE_EXCEEDED)));
```

You can see that it's almost the same scenario, as the only thing changing is:

```java
.deadlineAfter(Duration.ofMillis(100))
.check(statusCode().is(Status.Code.DEADLINE_EXCEEDED)));
```

In some applications, you need to set a deadline. If the server takes more time to answer, the request will be terminated and you will get an error. In our case, we will wait 100ms and then check if the server returns the error code "DEADLINE_EXCEEDED".

The general purpose of this is to prevent requests from hanging indefinitely and ensure system responsiveness by enforcing strict time constraints, allowing applications to fail fast and handle timeouts gracefully rather than leaving users waiting.

### Injection profile

Now we need to tell Gatling what pattern the users will follow. In our case, here is the injection profile:

```java
{
  String name = System.getProperty("grpc.scenario");
  ScenarioBuilder scn;
  if ("deadlines".equals(name)) {
    scn = deadlines;
  } else {
    scn = unary;
  }

  setUp(scn.injectOpen(atOnceUsers(5))).protocols(baseGrpcProtocol);
}
```

## Launching the simulation

### Setup

To launch the server, open a terminal and run the following command in the root folder:

```console
./certificates
cd server 
./gradlew -PmainClass=io.gatling.grpc.demo.server.greeting.GreetingServer run
```

### Community Edition version

To launch the load test, open a terminal and run the following command in the `java/maven` folder:

```console
./mvnw gatling:test -Dgrpc.scenario=unary \
  -Dgatling.simulationClass=io.gatling.grpc.demo.GreetingSimulation
```

or
```
./mvnw gatling:test -Dgrpc.scenario=deadlines \
  -Dgatling.simulationClass=io.gatling.grpc.demo.GreetingSimulation
```

After the simulation is complete, Gatling generates an HTML link in the terminal to access your report. Review metrics such as response times, successful and failed connections, and other indicators to identify potential issues with your service.

### Enterprise Edition version

Let's see how to run a simulation using Gatling Enterprise Edition. First, you need to create an API key on Gatling Enterprise Edition. To do this, you can check our documentation. Now you need to run the following commands:

```console
export GATLING_ENTERPRISE_API_TOKEN=<your_token>
./mvnw gatling:enterpriseDeploy
```

Now on Gatling Enterprise Edition → Sources, you will see a new package:

{{< img src="package-demo.png" >}}

Next, create a simulation by navigating to the "Simulations" tab and clicking "Create New." Select your package from the dropdown list, then click "Create" and configure your simulation settings. (You can set the gRPC.scenario environment variable in the load generator parameters if you want to run the scenario with deadlines)

{{< img src="simulation-runs.png" >}} <!--update for new UI-->

Now click on the #1 to get the data of our scenario

{{< img src="scenario-demo.png" >}} <!--update for new UI? -->

Now we see a small summary of the report with 5 users. If you want to view the whole report, you can click on the report tab. Also, we offer integrations with many different tools if you want to streamline your load testing process.

## Conclusion

We've explored how to load test gRPC applications using Gatling. We also walked through setting up a practical example that demonstrates both basic gRPC testing and deadline handling, two critical aspects of modern micro-service architectures.

If you want to go deeper, you can check our documentation and also try the Gatling Enterprise Edition. You will get access to CI/CD integration, detailed reporting, private locations, and many more features to ensure your gRPC services perform reliably under load.

The example we've explored, though simple, showcases essential testing patterns that can be adapted for more complex scenarios. Whether you're using the Community Edition version for initial testing or the Enterprise Edition for comprehensive load testing, Gatling provides the necessary tools to verify the performance and reliability of your gRPC services in real-world conditions.
