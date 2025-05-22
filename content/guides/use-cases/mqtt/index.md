---
menutitle: Testing MQTT
title: How to test the MQTT protocol
seotitle: Learn how to test the MQTT protocol with Gatling
description: Learn how to test the MQTT protocol with Gatling
lead: Learn how to test the MQTT protocol with Gatling
date: 2025-02-27T09:30:56+02:00
aliases:
  - /guides/complex-use-cases/mqtt/
---

## Introduction 

MQTT is a lightweight, publish-subscribe, network protocol that runs between machines. It provides a message queue and is particularly suited for remote locations and IoT devices. For example, MQTT is used in chemical plants to communicate between control rooms and specific sensors in the production process. Gatling has a dedicated MQTT SDK, which allows you to load test the MQTT protcol in your applications. 

The Gatling MQTT SDK lets you create code-based load tests for your sevices that use the MQTT protocol. This guide provides an introduction to testing MQTT with Gatling. The SDK is additionally described in the [reference documentation]({{< ref="/reference/script/mqtt/setup" >}}). 

This guide helps you set up and test an MQTT broker.  

{{< img src="MQTT-gatling.webp" >}}

## Configuring Gatling for performance testing

In the rest of this article, we’ll go over setting up Gatling for performance evaluation of an MQTT broker in an IoT system.

Gatling simulates connections from numerous clients and transmits data messages to the broker in real time while measuring the response time and providing other key metrics to measure performance.

Let’s walk through the steps to start performance testing with Gatling from scratch.

### Create a Gatling project from Maven

The easiest way to start is to clone one of our MQTT demo projects from the [GitHub repository](https://github.com/gatling/gatling-mqtt-demo). For the rest of this post, we will use the Maven demo project written in Java. 

If you already have a Maven project you prefer, you can check the Gatling dependencies imported in the pom.xml of the same project and add those to your Java project.

### Add dependencies

Gatling does not, by default, import the plugin dependencies unless you start from the demo project. If you are working from an existing project, add the following code to the top of your Gatling script: 

```java
import io.gatling.javaapi.mqtt.*;
import static io.gatling.javaapi.mqtt.MqttDsl.*;
```

### Write a Gatling script

We’ll use the MQTT SDK to write our Gatling script with the MQTT dependencies added. Our MQTT performance test script will use two scenarios connecting to the broker. One will subscribe to a topic and continue listening for messages, while the other will publish messages on the topic.

For this project, we will use the publicly accessible MQTT broker hosted at https://test.mosquitto.org. Our load test will simulate a single client connection from a subscriber alongside 1000 different client connections from publishers, ramping for 30 seconds.

Our Gatling code for the performance test looks like this:

```java
public class MqttSimulation extends Simulation {
 MqttProtocolBuilder mqttProtocol = mqtt
  .broker("test.mosquitto.org", 1883)
  .qosAtLeastOnce();

 ChainBuilder connect = exec(mqtt("Connecting").connect());

 ScenarioBuilder subscriberScenario = scenario("MQTT Subscriber")
  .exec(connect)
  .exec(mqtt("Subscribing_Topic")
   .subscribe("gatling/demo/topic")
    .qosExactlyOnce() // Override the default QoS level
    .expect(Duration.ofMillis(500)));

 ScenarioBuilder publisherScenario = scenario("MQTT Publisher")
  .exec(connect)
  .exec(mqtt("Publishing_Topic")
   .publish("gatling/demo/topic")
    .message(StringBody("Gatling test message"))
    .expect(Duration.ofMillis(500))
     .check(jsonPath("$.error").notExists()));

 {
  setUp(
   subscriberScenario.injectOpen(atOnceUsers(1)),
   publisherScenario.injectOpen(rampUsers(1000).during(30))
  ).protocols(mqttProtocol);
 }

}
```

{{< alert tip >}}
Consider adding assertions to your setup block. [Assertions]({{< ref "/concepts/assertions/#scope" >}}) allow you to specify the acceptable performance criteria for your test, and are returned on a pass/fail basis so you know instantly if your application is meeting the most important KPIs.
{{< /alert >}}

### Execute a performance test on Gatling Enterprise

We can now upload the simulation to Gatling Enterprise and execute a load test while watching the real-time results.

Remember that the MQTT protocol differs from the HTTP protocol commonly evaluated in Gatling and other performance test tools. HTTP is a request-response protocol where a client waits for a response from the server. The MQTT protocol, on the other hand, is a publish-subscribe protocol that doesn’t block threads waiting for a response. Thus, Gatling does not record response times unless a check is configured to block and await a message response.

Relevant metrics to monitor for MQTT testing, such as TCP connections, can be found in the Connections tab of the Gatling Enterprise test execution page.

{{< img src="MQTT-report.webp" >}}

## Conclusion

In this article, we’ve seen how Java, Scala, and Kotlin developers can start utilizing Gatling Enterprise to conduct performance tests of MQTT brokers at scale while enduring minimal cost or development time.

The ability to simulate realistic MQTT performance test scenarios with high levels of traffic using the expressive Gatling SDK, combined with widely distributed geographic load test infrastructure deployable on demand in the cloud with a single click, makes Gatling Enterprise an outstanding choice for MQTT performance evaluation of your IoT application.
