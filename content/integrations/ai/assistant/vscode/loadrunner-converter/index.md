---
title: LoadRunner Script Migration
description: Migrate LoadRunner C scripts to Gatling Java simulations using an AI agent workflow
lead: Migrate from LoadRunner to Gatling with an AI-powered agentic workflow
---

Migrate existing LoadRunner C scripts into Gatling Java simulations directly from VS Code. The AI agent automatically parses, analyzes, transforms, and generates Gatling code through a multi-step workflow with full visibility into each stage.

{{<  alert info >}}
This is an experimental feature. Review the generated simulations carefully before use.
{{< /alert >}}

## When to use the migration tool

- **Migrating from LoadRunner**: Migrate your entire LoadRunner test suite to Gatling
- **Consolidating tools**: Unify performance testing across teams
- **Cost optimization**: Reduce licensing costs while maintaining test coverage
- **Enhanced CI/CD**: Integrate with Gatling Cloud or on-premises deployments

## How it works

The migration uses an AI agent workflow that processes your LoadRunner scripts through multiple steps:

1. **Parse**: The agent reads and parses the LoadRunner C script, identifying requests, headers, parameters, and correlation directives
2. **Analyze**: Script structure is analyzed to determine request flows, shared headers, query parameters, and data extraction patterns
3. **Transform**: LoadRunner functions are mapped to their Gatling Java equivalents, with intelligent grouping of global headers into the HTTP protocol configuration
4. **Generate**: A complete Gatling Java simulation is generated with proper structure and conventions

A progress UI displays each step as the agent works, giving you full visibility into the migration process.

## Migration workflow

### Single script migration

#### 1. Right-click the LoadRunner script

In the VS Code Explorer, right-click any `.c` file and select **"Migrate LoadRunner Script to Gatling"**.

#### 2. Provide the base URL

Enter the base URL for your target application (e.g., `https://api.example.com`). The agent uses this to generate accurate request paths in the output simulation.

#### 3. Follow the agent's progress

The progress UI shows each step of the migration in real time. The agent automatically handles:
- Parsing headers (`web_add_header`, `web_add_auto_header`) and query parameters
- Identifying correlation directives and generating extraction code
- Grouping global headers into the HTTP protocol configuration
- Generating per-request headers and query parameters where needed

If a transient error occurs, the agent automatically retries before reporting a failure.

#### 4. Review the generated code

The agent produces a diff-based output so you can review exactly what was generated. Inspect the output for:
- Correct endpoint URLs and base URL
- Proper request parameters and payloads
- Accurate think times
- Appropriate checks and assertions
- Correct correlation code

#### 5. Validate in your project

Copy the generated Java simulation file to your Gatling project and test it locally before deploying.

### Batch migration

Migrate multiple LoadRunner scripts at once:

1. Open the Command Palette (`Ctrl+Shift+P` / `Cmd+Shift+P`)
2. Run **"Migrate LoadRunner Scripts to Gatling"**
3. Provide the base URL
4. The agent processes all `.c` files in your workspace

## What gets migrated

### Automatically mapped

| LoadRunner | Gatling Java | Notes |
|-----------|-------------|-------|
| `web_url()` | `http(...).get()` | Migrated to GET request |
| `web_submit_form()` | `http(...).post()` | Form data preserved as body |
| `web_submit_data()` | `http(...).post()` | Request body migrated to form-encoded or JSON |
| `web_custom_request()` | `http(...).method()` | Preserves custom method and headers |
| `web_add_header()` | `.header()` | Per-request headers applied to the appropriate request |
| `web_add_auto_header()` | `httpProtocol.header()` | Global headers added to the HTTP protocol configuration |
| `lr_think_time()` | `pause()` | Think times become pause durations |
| `lr_start_transaction()` / `lr_end_transaction()` | Named groups | Transaction boundaries preserved as groups |
| Comments | `//` comments | Context preserved in output |

### Correlation and data extraction

| LoadRunner | Gatling Java | Notes |
|-----------|-------------|-------|
| `web_reg_save_param()` | `check(regex()).saveAs()` | Extracts values using left/right boundary regex |
| `web_reg_save_param()` (JSON responses) | `check(jmesPath()).saveAs()` | Automatically inferred for JSON response bodies |
| `web_reg_find()` | `check(bodyString().is())` | Migrates to body validation check |
| Form parameters with `{PARAM}` | `"#{PARAM}"` in Gatling EL | Parameter references migrated to Gatling expression language |

### Requires manual review

| LoadRunner | Reason | Action |
|-----------|--------|--------|
| `lr_load_dll()` | External C libraries | Not migrated - implement in Java |
| `lr_save_string()` | String manipulation | Not migrated - use feeders or session vars |
| `lr_eval_string()` | String evaluation | Not migrated - use Gatling EL instead |
| `web_submit_form()` with `Ordinal` | Implicit form detection | TODO comment added - specify form fields manually |
| Complex C logic | Custom functions/conditionals | Review and reimplement in Java |

## Migration examples

### Example 1: Simple web flow

**LoadRunner C script:**
```c
Action()
{
    lr_think_time(5);

    web_url("Home",
        "URL=https://api.example.com/",
        "Resource=0",
        "RecContentType=text/html",
        LAST);

    lr_think_time(3);

    web_submit_form("Login",
        "Snapshot=t1.inf",
        "Action=Login",
        ITEMDATA,
        "Name=email", "Value=user@example.com",
        "Name=password", "Value=testpass123",
        LAST);

    lr_think_time(2);

    web_url("Dashboard",
        "URL=https://api.example.com/dashboard",
        LAST);

    return 0;
}
```

**Migrated to Gatling Java:**
```java
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class MigratedSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
        .baseUrl("https://api.example.com")
        .acceptHeader("text/html,application/json")
        .acceptEncodingHeader("gzip, deflate")
        .userAgentHeader("Mozilla/5.0");

    ScenarioBuilder userFlow = scenario("User Flow")
        .pause(5)
        .exec(http("Home")
            .get("/"))
        .pause(3)
        .exec(http("Login")
            .post("/Login")
            .formParam("email", "user@example.com")
            .formParam("password", "testpass123")
            .check(status().is(200)))
        .pause(2)
        .exec(http("Dashboard")
            .get("/dashboard"));

    {
        setUp(userFlow.injectOpen(constantUsersPerSec(1).during(60)))
            .protocols(httpProtocol);
    }
}
```

### Example 2: Request with data extraction

**LoadRunner C script:**
```c
web_reg_save_param("ProductID",
    "LB=product_id=",
    "RB=&",
    "Ord=1",
    LAST);

web_url("Search",
    "URL=https://store.example.com/search?q=laptop",
    LAST);

web_url("View Product",
    "URL=https://store.example.com/product/{ProductID}",
    LAST);
```

**Migrated to Gatling Java:**
```java
scenario("Product Search")
    .exec(http("Search")
        .get("/search?q=laptop")
        .check(regex("product_id=(.*?)&").saveAs("ProductID")))
    .exec(http("View Product")
        .get("/product/#{ProductID}"))
```

### Example 3: Global and per-request headers

**LoadRunner C script:**
```c
web_add_auto_header("Accept", "application/json");
web_add_auto_header("X-Api-Version", "2");

web_add_header("X-Request-Id", "abc123");

web_url("Get Items",
    "URL=https://api.example.com/items",
    LAST);
```

**Migrated to Gatling Java:**
```java
HttpProtocolBuilder httpProtocol = http
    .baseUrl("https://api.example.com")
    .header("Accept", "application/json")
    .header("X-Api-Version", "2");

ScenarioBuilder scn = scenario("Items Flow")
    .exec(http("Get Items")
        .get("/items")
        .header("X-Request-Id", "abc123"));
```

## Post-migration review checklist

After migration, review the generated simulation:

### Endpoints and URLs
- [ ] Base URL is correct
- [ ] All endpoints are properly mapped
- [ ] Query parameters are preserved
- [ ] Path variables are correct

### Authentication
- [ ] Auth headers are present
- [ ] API keys or tokens are properly placed
- [ ] Session handling is correct
- [ ] Credentials are parameterized (not hardcoded)

### Request data
- [ ] Form data is converted to proper format (JSON, form-encoded)
- [ ] Request payloads are accurate
- [ ] Custom headers are preserved
- [ ] Content-Type headers are correct

### Think times and pauses
- [ ] Think times converted to appropriate pause durations
- [ ] Realistic pauses between requests (not too short/long)
- [ ] Transaction boundaries are clear

### Validations and checks
- [ ] Assertions match original LoadRunner validations
- [ ] Data extraction (saveAs) works correctly
- [ ] Error handling is appropriate

### Load injection
- [ ] Manually configure based on your test goals
- [ ] Default is single user—adjust for your load pattern
- [ ] Consider ramp-up/ramp-down strategies

## Common post-migration adjustments

### Add realistic load injection

The migration generates single-user simulations. Add your load pattern:

```java
setUp(
    userFlow.injectOpen(
        rampUsers(100).during(60),
        constantUsersPerSec(50).during(300)
    )
).protocols(httpProtocol);
```

### Update hardcoded values

Replace hardcoded credentials and data with feeders:

```java
// Before
.post("/login")
.body(StringBody("{\"email\":\"user@example.com\",\"password\":\"pass123\"}"))

// After
.post("/login")
.body(StringBody("{\"email\":\"#{email}\",\"password\":\"#{password}\"}"))
```

### Improve data extraction

The migration may use simple regex. For complex JSON responses, refine extractions:

```java
// Before (migrated from LoadRunner)
.check(regex("token=([\\w]+)").saveAs("token"))

// After (more reliable)
.check(jmesPath("auth.token").saveAs("token"))
```

### Adjust think times

Review and adjust pauses to match expected user behavior:

```java
// Before (exact conversion)
.pause(5)

// After (realistic variation)
.pause(3, 7) // Random pause between 3-7 seconds
```

### Add transaction grouping

Group related requests for better reporting:

```java
.exec(http("Login").post("/login"))
.pause(2)
.exec(http("Get Profile").get("/profile"))
.exec(http("Get Settings").get("/settings"))
```

## Limitations and workarounds

### External C libraries

LoadRunner scripts sometimes call external C DLLs. These don't migrate automatically.

**Solution**: Implement equivalent logic in Java.

### Complex string operations

LoadRunner's C language allows arbitrary string manipulation.

**Solution**: Implement custom Java functions or use `exec()` blocks.

### Advanced correlation

Complex LoadRunner correlation rules may not migrate directly.

**Solution**: Review and manually adjust `jmesPath()` or `regex()` checks.

### Custom LoadRunner functions

Your LoadRunner scripts may use custom functions.

**Solution**: Map these to equivalent Gatling patterns or reimplement them in Java.

## Validation and testing

After migration:

1. **Test locally**: Run the generated simulation in your dev environment
2. **Verify assertions**: Confirm all checks pass against your target system
3. **Compare metrics**: Run both LoadRunner and Gatling versions to compare results
4. **Review performance**: Ensure the migrated simulation generates similar load profiles
