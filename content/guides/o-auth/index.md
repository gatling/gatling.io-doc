---
title: Testing OAuth Applications with Gatling
menutitle: OAuth
---

## Overview
This guide will show you how to load test applications that use OAuth authentication using Gatling. We'll focus on the Authorization Code grant type, which is the most common OAuth flow.

## Implementation

Here's a basic Gatling simulation for testing an OAuth-protected API:

```java
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class OAuthSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
        .baseUrl("https://api.example.com")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    // OAuth configuration
    private static final String CLIENT_ID = "your-client-id";
    private static final String CLIENT_SECRET = "your-client-secret";
    private static final String AUTH_URL = "https://auth.example.com/oauth/authorize";
    private static final String TOKEN_URL = "https://auth.example.com/oauth/token";

    // Scenario to obtain OAuth token
    private ChainBuilder getOAuthToken = exec(
        http("Get OAuth Token")
            .post(TOKEN_URL)
            .formParam("grant_type", "client_credentials")
            .formParam("client_id", CLIENT_ID)
            .formParam("client_secret", CLIENT_SECRET)
            .check(jsonPath("$.access_token").saveAs("accessToken"))
    );

    // Example API call using the token
    private ChainBuilder callProtectedApi = exec(
        http("Call Protected API")
            .get("/api/protected-resource")
            .header("Authorization", "Bearer #{accessToken}")
            .check(status().is(200))
    );

    // Define the test scenario
    private ScenarioBuilder scn = scenario("OAuth API Test")
        .exec(getOAuthToken)
        .pause(1)
        .exec(callProtectedApi);

    {
        setUp(
            scn.injectOpen(
                rampUsers(50).during(30)
            )
        ).protocols(httpProtocol);
    }
}
```

## Key Components Explained

1. **OAuth Configuration**: Define your OAuth credentials and endpoints at the top of the simulation.

2. **Token Acquisition**: The `getOAuthToken` chain obtains an OAuth token and saves it to the session.

3. **Protected API Call**: The `callProtectedApi` chain demonstrates how to use the token in subsequent API calls.

4. **Load Profile**: The setup uses a simple ramp-up of 50 users over 30 seconds.

## Running the Test

To run the simulation:

1. Place the simulation file in your project's test directory
2. Run using Maven:
```bash
mvn gatling:test -Dgatling.simulationClass=OAuthSimulation
```

## Best Practices

1. Store OAuth credentials in configuration files, not in code
2. Consider token caching for better performance
3. Add appropriate pauses between requests to simulate real user behavior
4. Include proper error handling and assertions

This basic implementation should get you started with load testing OAuth-protected applications. Adjust the parameters and scenarios based on your specific needs.