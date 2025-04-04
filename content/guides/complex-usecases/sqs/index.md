---
title: Load testing AWS SQS with Gatling
menutitle: How to test AWS SQS
seotitle: Learn how to load test AWS SQS with Gatling
description: Learn how to load test AWS SQS with Gatling
lead: Learn how to load test AWS SQS with Gatling
date: 2025-02-28T09:30:56+02:00
private: true
---

## Introduction (write this) <!--add links-->

Some of our users asked about load testing AWS SQS with Gatling. Here is a step-by-step guide!

Amazon Simple Queue Service (SQS) is AWS's managed message queuing service.

Currently, Gatling doesn't have native SQS support, but still, it's fairly easy to use Gatling to load test SQS.

AWS provides a library that implements part of the Java Message Service (JMS) API.
This library is just a thin adapter layer on top of the AWS Java SDK library, so it doesn't introduce any significant overhead.
You can then plug into Gatling's native JMS support.

## Importing the SQS JMS adapter library

The first thing you have to do is add the amazon-sqs-java-messaging-lib jar to your classpath.
For example, here are the maven coordinates you'd have to add to your pom.xml.

```xml
<dependency>
 <groupId>com.amazonaws</groupId>
 <artifactId>amazon-sqs-java-messaging-lib</artifactId>
 <version>1.0.4</version> <!-- latest version at the time of this post -->
</dependency>
```
We also recommend that you upgrade the version of AWS Java SDK as the one that's being pulled transitively is a bit old:

```xml
<dependency>
 <groupId>com.amazonaws</groupId>
 <artifactId>aws-java-sdk-sqs</artifactId>
 <version>1.11.490</version> <!-- latest version at the time of this post -->
</dependency>
```

Note that only the v1 of the AWS Java SQK is currently supported.

## Creating a connection factory

### Adding imports

You must add imports for SQS, the JMS SQS wrapper, and Gatling JMS DSL.

```scala
val sqsClient = {
 val builder = AmazonSQSAsyncClientBuilder.standard
 // configure the credentials strategy of your choice
 builder.setCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("myKey", "myToken")))
 builder.setRegion(Regions.EU_WEST_3.getName)
 builder.build
}
```

Make sure that you properly close the client and its underlying resources once the test is done, thanks to the after hook:

```scala
after {
 sqsClient.shutdown()
}
```

### Configuring the JMS Protocol with the ConnectionFactory

Gatling JMS support lets you either pass a JNDI configuration to look up a ConnectionFactory or directly pass one you'd create programmatically. Only the latter works for SQS.

```scala
al jmsProtocol = jms.connectionFactory(new SQSConnectionFactory(new ProviderConfiguration(), sqsClient))
```

## Writing a JMS scenario and configuring the JMS Protocol on the `SetUp`

This step is pure Gatling JMS, please refer to the official documentation.

```scala
val scn = scenario("SQS test")
 .exec(jms("SendMessage").send
  .queue("MyQueue")
  .textMessage("SomeText"))
setUp(scn.inject(atOnceUsers(1))).protocols(jmsProtocol)
```

Note that SQS doesn't support temporary queues, so if you want to use requestReply, you must explicitly configure the replyQueue.

### Using FIFO queues

SQS FIFO queues have the notions of Message Group ID (required) and Message Deduplication ID (optional). Those can be configured with the respective JMSXGroupID and JMS_SQS_DeduplicationId JMS properties.

```scala
jms("SendMessage").send
 .queue("MyFIFOQueue")
 .textMessage("SomeText")
 .property("JMSXGroupID", "MyMessageGroupId")
```
