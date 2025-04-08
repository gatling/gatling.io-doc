---
title: Using SSE to load test an LLM API  
menutitle: Test server-sent events
seotitle: Using server sent events to load test an LLM API
description: Learn how to use server sent events to load test an LLM API
lead: Learn how to use server sent events to load test an LLM API
date: 2025-02-27T09:30:56+02:00
---

Server-sent events is a server push technology, which allows clients to receive updates from a server using an HTTP connection. It instructs servers how to initiate data transmission to clients after the initial client connection is established.

Gatling allows you to to test server-sent events as an extension of the HTTP DSL. 

<div style="position: relative; overflow: hidden; max-width: 100%; padding-bottom: 56.25%; margin: 0px;"><iframe width="560" height="315" src="https://www.youtube.com/embed/dK9_73FHj8w?si=6VCZCP4aM2wZznE3" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen="" style="position: absolute; top: 0px; left: 0px; width: 100%; height: 100%; border: none;"></iframe></div>

## Step 1: Setting up the project

First, ensure you have Gatling installed and set up. If you don't have Gatling installed, you can download it on the documentation and [clone this project](https://github.com/gatling/devrel-projects) from Github and open the `articles/load-test-llm-sse` folder.

## Step 2: Configuring the HTTP protocol

Picture yourself at a grand theater in Paris, comfortably seated and admiring the set and ambiance. In Gatling, just as the theater environment shapes the audience experience, the HTTP protocol provides the framework for your test scenarios. The baseUrl defines where the performance takes place, guiding all interactions to the correct destination.

In your Gatling project, configure the HTTP protocol to specify the base URL of ChatGPT (OpenAI) API. We use `sseUnmatchedInboundMessageBufferSize` in order to buffer the inbound message

```java
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class SSELLM extends Simulation {
   String api_key = System.getenv("api_key");
   HttpProtocolBuilder httpProtocol =
      http.baseUrl("https://api.openai.com/v1/chat")
          .sseUnmatchedInboundMessageBufferSize(100);
```

## Step 3: Defining the scenario

Now the piece has started, the actors enter the scene and follow their scripts. At Gatling, we call this a scenario, and it defines the steps your test will take (connecting, parsing messages, user interaction, etc.,).

In our case, our scenario is pretty small. People will:

- connect to the completion endpoint of Open AI,
- send a prompt using SSE,
- process all the messages until ChatGPT sends us {"data":"[DONE]"},
- close the SSE connection.

```java
 ScenarioBuilder prompt = scenario("Scenario").exec(
      sse("Connect to LLM and get Answer")
          .post("/completions")
          .header("Authorization", "Bearer "+api_key)
          .body(StringBody("{\"model\": \"gpt-3.5-turbo\",\"stream\":true,\"messages\":[{\"role\":\"user\",\"content\":\"Just say HI\"}]}"))
          .asJson(),
      asLongAs("#{stop.isUndefined()}").on(
          sse.processUnmatchedMessages((messages, session) -> {
            return messages.stream()
            .anyMatch(message -> message.message().contains("{\"data\":\"[DONE]\"}")) ? session.set("stop", true) : session;        
          }) 
      ),
      sse("close").close()
  );
```

The `processUnmatchedMessages` method allows us to process the inbound messages. This function catches all the messages that ChatGPT sent us and when we receive {"data":"[DONE]"}, we set a stop variable to true in order to exit the loop.

## Step 4: Injecting users

As the audience arrives and fills their seats, the theater comes alive. In Gatling, this is the injection profile. It permits you to choose how and when users enter your test, whether gradually, all at once, or in waves.

In our guide, we will simulate a low number of users (i.e. 10 users) arriving at once on our website. Do you want to use different user arrival profiles? Check out our various [injection profiles](/reference/script/core/injection/#open-model).

```java
  {
    setUp(
        prompt.injectOpen(atOnceUsers(10))
    ).protocols(httpProtocol);
  }
```

## Step 5: Running the simulation

Run the simulation to see how the LLM handles the load. Use the following command to execute the test:

Set the API token environment variable:

{{< platform-toggle >}}
Linux/MacOS: export api_key=<API-token-value>
Windows: set api_key=<API-token-value>
{{</ platform-toggle >}}
 
Then launch the test:

{{< platform-toggle >}}
Linux/MacOS: ./mvnw gatling:test
Windows: mvnw.cmd gatling:test
{{</ platform-toggle >}}

## Step 6: Analyzing the results

After the simulation is complete, Gatling generates an HTML link in the terminal that you can use to access your report. Review metrics like response times, the number of successful and failed connections, and other metrics to spot potential issues with your service.

## Conclusion

By updating SSE support to add the post method, Gatling enables load testing for applications using this method like LLMs, and many more. This practical example using the OpenAI API demonstrates how you can use Gatling to ensure your applications effectively manage user demands. So, don't streSSE about it and use Gatling to keep your servers and users happy.
