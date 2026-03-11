---
title: Explain Code
description: Get contextual explanations of Gatling code directly in VS Code
lead: Understand Gatling simulations and patterns with AI analysis
---

Select any portion of your Gatling code and ask the AI Assistant to explain it. This is useful for understanding existing simulations, learning Gatling patterns, and getting clarity on specific configuration options.

## How to use Explain Code

### 1. Select code

Highlight any section of your Gatling simulation in VS Code.

### 2. Open context menu

Right-click on the selection and choose **"Explain Code"** from the context menu.

### 3. Read the explanation

The AI Assistant opens in the sidebar with a detailed explanation of your selected code.

## What it explains

The assistant can explain:

- **Request definitions**: HTTP methods, headers, authentication, body content
- **Checks and assertions**: Validation rules, response parsing, status checks
- **User behavior**: Think times, pauses, scenario flow
- **Load injection**: Ramp-up strategies, user ramping, duration patterns
- **Gatling concepts**: Sessions, feeders, chainable builders, DSL usage
- **Performance implications**: How code affects test behavior and metrics
- **Best practices**: Why code is written a certain way

## Follow-up questions

After getting an explanation, you can ask follow-up questions in the chat:

**After an explanation:**
> "Why use `injectOpen` instead of `injectClosed`?"

**About performance:**
> "Will this request pattern cause connection pooling issues?"

**For alternatives:**
> "Is there a better way to handle authentication in Gatling?"

**For best practices:**
> "What's the recommended way to load test data for this scenario?"

## Common questions to ask

### Understanding patterns

- "Why is a `pause` used here instead of a `wait`?"
- "What's the difference between `exec` and `feed` in this context?"
- "Why extract this value with `saveAs` instead of discarding it?"

### Performance implications

- "How does this load injection pattern affect CPU usage?"
- "Will adding more assertions slow down the test?"
- "What's the performance cost of this think time strategy?"

### Best practices

- "Is this the recommended way to structure scenarios in Gatling?"
- "Should I use `exec` or chain requests differently?"
- "What's the best practice for handling authentication tokens?"

### Debugging

- "Why is this check failing?"
- "What does this error message mean?"
- "How can I debug this response data extraction?"

## Tips for effective explanations

### Select meaningful chunks

❌ **Too small:** Single line like `pause(2)`

✅ **Good size:** Complete request with checks, or a scenario block

### Include context

Selecting a larger block provides better context:
```typescript
// Include the scenario name and setup
scenario("User Journey")
  .exec(http("Search").get("/search"))
  .pause(2)
  .exec(http("ViewResult").get("/product/${id}"))
  .pause(3)
```

### Explain variations

Select different implementations of the same concept to compare:
```typescript
// Request 1: Using static values
http("Search").get("/search?q=laptop")

// Request 2: Using parameterized values
http("Search").get("/search").queryParam("q", "${searchTerm}")
```

Then ask: "What's the difference between these two approaches?"
