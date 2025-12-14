---
title: Contextual Chat
description: Ask questions about Gatling concepts, best practices, and implementation details
lead: Get instant answers about Gatling performance testing
---

Use the Gatling AI Assistant chat to ask questions about your simulations, Gatling concepts, best practices, and implementation patterns. The assistant understands your project context and provides relevant, practical advice.

## How to use contextual chat

### 1. Open the Gatling panel

Click the Gatling icon in the Activity Bar to open the chat.

### 2. Ask a question

Type your question directly in the chat. You can ask about:
- Gatling concepts and features
- How to implement specific scenarios
- Best practices and patterns
- Optimization and performance
- Troubleshooting and debugging

### 3. Get answers with context

The assistant automatically includes context from your project:
- Currently open files
- File structure and project layout
- Gatling-related dependencies
- Selected code (if any)

This means you don't need to paste code—just ask naturally.

## Example questions and answers

### Conceptual questions

**Q: What's the difference between a scenario and a simulation in Gatling?**

> **A:** A **scenario** defines a sequence of requests that simulates a user's actions (e.g., login → browse → checkout). A **simulation** is the complete test definition that includes:
> - One or more scenarios
> - Load injection strategy (how users ramp up)
> - Protocol configuration (base URL, headers, etc.)
> - Assertions and checks
>
> Think of a scenario as the "user journey" and a simulation as the "complete test blueprint."

**Q: When should I use pauses vs think times?**

> **A:** In Gatling, `pause()` and `thinkTime()` achieve similar goals but differ in flexibility:
>
> - **pause(min, max)**: Fixed wait between requests. Simple and predictable. Use for basic timing control.
> - **thinkTime(duration)**: Configurable think time that respects simulation parameters. Better for production-like behavior.
> - **Custom logic**: Conditionally pause based on response content or session data.
>
> For realistic simulations, use pauses that vary (e.g., `pause(2, 5)`) to simulate real user behavior.

### Implementation patterns

**Q: How do I extract a value from a JSON response and use it in the next request?**

> **A:** Use the `saveAs` check to extract and store the value:
>
> ```typescript
> exec(http("Login")
>   .post("/api/login")
>   .body(StringBody('{"email":"${email}","password":"${password}"}'))
>   .check(jsonPath("$.token").saveAs("authToken")))
> .exec(http("Get Profile")
>   .get("/api/profile")
>   .header("Authorization", "Bearer ${authToken}"))
> ```
>
> The `saveAs("authToken")` extracts the token from the login response and stores it in the session. The next request uses `${authToken}` to reference it.

**Q: How do I test an API that requires authentication tokens that expire?**

> **A:** Implement a token refresh flow:
>
> ```typescript
> scenario("Authenticated Requests")
>   .exec(http("Login").post("/login").check(...))
>   .exec(http("Request 1").get("/data").header("Authorization", "Bearer ${token}"))
>   .pause(300) // Token expires after 5 minutes
>   .exec(http("Refresh Token").post("/refresh").check(...))
>   .exec(http("Request 2").get("/data").header("Authorization", "Bearer ${newToken}"))
> ```
>
> Or use a during-condition to refresh periodically:
>
> ```typescript
> .during(600) { // Run for 10 minutes
>   exec(http("API Call").get("/data"))
>   .pause(2)
>   .asLongAs(tokenNotExpired) {
>     // Check and refresh token if needed
>   }
> }
> ```

### Load injection strategies

**Q: How do I simulate a traffic spike?**

> **A:** Use a jump or ramp-up with a steep slope:
>
> ```typescript
> setUp(
>   scenario.injectOpen(
>     atOnceUsers(500),           // Jump to 500 users instantly
>     nothingFor(60),             // Hold for 60 seconds
>     rampUsers(0).during(30)   // Cool down
>   )
> )
> ```
>
> Or combine patterns:
>
> ```typescript
> rampUsers(100).during(30),      // Gradual ramp to 100
> constantUsersPerSec(50).during(60), // Spike in request rate
> rampUsers(0).during(30)   // Cool down
> ```

### Optimization and performance

**Q: My simulation is using too much memory. How do I optimize it?**

> **A:** Common optimizations:
>
> - **Reduce feeder data**: Load only necessary test data into memory
> - **Stream feeders**: Use circular queues instead of loading full datasets
> - **Disable assertions**: Remove non-critical checks during load testing
> - **Optimize session data**: Don't store large objects in the session
> - **Adjust connection pools**: Tune `maxConnectionsPerHost` if needed
>
> Example:
> ```typescript
> const httpProtocol = http.maxConnectionsPerHost(6)
> ```

**Q: How many users can I simulate on my machine?**

> **A:** Depends on:
> - Scenario complexity (number of requests, size of responses)
> - Available CPU and memory
> - Network bandwidth
> - Target system capacity
>
> Start with 100 users and gradually increase until you hit resource limits. Monitor CPU, memory, and garbage collection. For enterprise load testing, use distributed testing with Gatling Enterprise.

### Troubleshooting

**Q: My assertions are failing. How do I debug?**

> **A:** Add logging to see extracted values:
>
> ```typescript
> .check(
>   status().is(200),
>   jsonPath("$.name").saveAs("productName")
> )
> .exec((session) => {
>   console.log(`Product name: ${session.get("productName")}`);
>   return session;
> })
> ```
>
> Or review the Gatling reports after running your simulation to see detailed request/response information and identify where assertions fail.

**Q: Why is my simulation timing out?**

> **A:** Check:
> - **Request timeout**: Increase `requestTimeout` if the target is slow
> - **Think times**: Verify pauses aren't unreasonably long
> - **Connection issues**: Test endpoints with `curl` or Postman
> - **Rate limiting**: The target might be throttling requests
>
> Example timeout configuration:
>
> ```typescript
> const httpProtocol = http
>   .baseUrl("https://api.example.com")
>   .connectionTimeout(5000)
>   .requestTimeout(10000)
> ```

## Context awareness

The assistant automatically includes:

- **Current file content**: The simulation you're working on
- **Project structure**: Layout and organization of your test files
- **Open files**: Names of other files in your workspace
- **Dependencies**: Gatling versions and related libraries
- **Configuration**: gatling.conf and build files

This means you can reference your code naturally:

✅ **Good:** "Why is this request failing in my simulation?"

✅ **Good:** "How would I add OAuth authentication to my checkout flow?"

❌ **Requires more context:** "How do I configure timeouts?" (Better to ask about a specific simulation)

## Follow-up questions

Build on previous answers by asking:

- "Can you show me an example?"
- "How would I implement this in Java?"
- "What's the performance impact?"
- "Is there a better way?"
- "How do I handle errors?"

## Best practices for asking

### Be specific about your scenario

❌ **Vague:** "How do I load test an API?"

✅ **Specific:** "How do I simulate realistic user behavior for an e-commerce API, with login, product browse, and checkout flows?"

### Include relevant details

- Your target API/application type
- Authentication method
- Expected user flows
- Performance goals
- Constraints (budget, time, resources)

### Ask about patterns

- "What's the best practice for...?"
- "How should I structure...?"
- "What's the difference between...?"

### Request examples

- "Show me an example of..."
- "How would this look in TypeScript?"
- "Can you provide a code snippet?"
