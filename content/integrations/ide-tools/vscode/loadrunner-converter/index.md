---
title: LoadRunner Script Converter
description: Automatically convert LoadRunner C scripts to Gatling simulations
lead: Migrate from LoadRunner to Gatling with AI-powered conversion
---

Convert existing LoadRunner C scripts into Gatling simulations directly from VS Code. The converter automatically maps LoadRunner functions to their Gatling equivalents while preserving your test logic.

{{<  alert info >}}
This is an experimental feature. Review the generated simulations carefully before use.
{{< /alert >}}

## When to use the converter

- **Migrating from LoadRunner**: Convert your entire LoadRunner test suite to Gatling
- **Consolidating tools**: Unify performance testing across teams
- **Cost optimization**: Reduce licensing costs while maintaining test coverage
- **Enhanced CI/CD**: Integrate with Gatling Cloud or on-premises deployments

## Conversion workflow

### Single script conversion

#### 1. Right-click the LoadRunner script

In the VS Code Explorer, right-click any `.c` file and select **"Convert LoadRunner Script to Gatling"**.

#### 2. Choose output language

Select your target Gatling language:
- JavaScript
- TypeScript (recommended)
- Java
- Scala
- Kotlin

#### 3. Review and customize

The converter generates a Gatling simulation file. Review it for:
- Correct endpoint URLs
- Proper request parameters and payloads
- Accurate think times
- Appropriate checks and assertions

#### 4. Validate in your project

Copy the generated file to your Gatling project and test it locally before deploying.

### Batch conversion

Convert multiple LoadRunner scripts at once:

1. Open the Command Palette (`Ctrl+Shift+P` / `Cmd+Shift+P`)
2. Run **"Convert LoadRunner Scripts to Gatling"**
3. Select the output language
4. The converter processes all `.c` files in your workspace

## What gets converted

### Automatically mapped

| LoadRunner | Gatling | Notes |
|-----------|---------|-------|
| `web_url()` | `http(...).get()` | Converted to GET request |
| `web_submit_form()` | `http(...).post()` | Form data preserved as body |
| `web_submit_data()` | `http(...).post()` | Request body converted to form-encoded or JSON |
| `web_custom_request()` | `http(...).method()` | Preserves custom method and headers |
| `lr_think_time()` | `pause()` | Think times become pause durations |
| `lr_start_transaction()` / `lr_end_transaction()` | Named groups | Transaction boundaries preserved as groups |
| Comments | `//` comments | Context preserved in output |

### Partially handled

| LoadRunner | Gatling | Notes |
|-----------|---------|-------|
| `web_reg_save_param()` | `check(regex()).saveAs()` | Extracts values using left/right boundary regex |
| `web_reg_find()` | `check(bodyString.is())` | Converts to body validation check |
| Form parameters with `{PARAM}` | `#{PARAM}` in Gatling EL | Parameter references converted to Gatling expression language |

### Requires manual review

| LoadRunner | Reason | Action |
|-----------|--------|--------|
| `lr_load_dll()` | External C libraries | Not converted - implement in target language |
| `lr_save_string()` | String manipulation | Not converted - use feeders or session vars |
| `lr_eval_string()` | String evaluation | Not converted - use Gatling EL instead |
| `web_submit_form()` with `Ordinal` | Implicit form detection | TODO comment added - specify form fields manually |
| Complex C logic | Custom functions/conditionals | Review and reimplement in target language |

## Conversion examples

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

**Converted to TypeScript:**
```typescript
import { simulation, scenario, exec, pause, constantUsersPerSec } from "@gatling.io/core";
import { http, status, bodyString, regex } from "@gatling.io/http";

export default simulation((setUp) => {
  const httpProtocol = http
    .baseUrl("https://api.example.com")
    .acceptHeader("text/html,application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0");

  const userFlow = scenario("User Flow")
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

  setUp(userFlow.injectOpen(constantUsersPerSec(1).during(60)))
    .protocols(httpProtocol);
});
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

**Converted to TypeScript:**
```typescript
scenario("Product Search")
  .exec(http("Search")
    .get("/search?q=laptop")
    .check(regex("product_id=(.*?)&").saveAs("ProductID")))
  .exec(http("View Product")
    .get("/product/#{ProductID}"))
```

## Post-conversion review checklist

After conversion, review the generated simulation:

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

## Common post-conversion adjustments

### Add realistic load injection

The converter generates single-user simulations. Add your load pattern:

```typescript
setUp(
  userFlow.injectOpen(
    rampUsers(100).during(60),
    constantUsersPerSec(50).during(300)
  )
).protocols(httpProtocol);
```

### Update hardcoded values

Replace hardcoded credentials and data with feeders:

```typescript
// Before
.post("/login")
.body(StringBody('{"email":"user@example.com","password":"pass123"}'))

// After
.post("/login")
.body(StringBody('{"email":"${email}","password":"${password}"}'))
```

### Improve data extraction

The converter may use simple regex. For complex JSON/XML responses, refine extractions:

```typescript
// Before (converted from LoadRunner)
.check(regex("token=([\\w]+)").saveAs("token"))

// After (more reliable)
.check(jsonPath("$.auth.token").saveAs("token"))
```

### Adjust think times

Review and adjust pauses to match expected user behavior:

```typescript
// Before (exact conversion)
.pause(5)

// After (realistic variation)
.pause(3, 7) // Random pause between 3-7 seconds
```

### Add transaction grouping

Group related requests for better reporting:

```typescript
.exec(http("Login").post("/login"))
.pause(2)
.exec(http("Get Profile").get("/profile"))
.exec(http("Get Settings").get("/settings"))
```

## Limitations and workarounds

### External C libraries

LoadRunner scripts sometimes call external C DLLs. These don't convert automatically.

**Solution**: Implement equivalent logic in Gatling.

### Complex string operations

LoadRunner's C language allows arbitrary string manipulation.

**Solution**: Implement custom Gatling functions or use `exec()` blocks with JavaScript/Scala.

### Advanced correlation

Complex LoadRunner correlation rules may not convert directly.

**Solution**: Review and manually adjust `jsonPath()` or `xpath()` checks.

### Custom LoadRunner functions

Your LoadRunner scripts may use custom functions.

**Solution**: Map these to equivalent Gatling patterns or reimplement them.

## Validation and testing

After conversion:

1. **Test locally**: Run the generated simulation in your dev environment
2. **Verify assertions**: Confirm all checks pass against your target system
3. **Compare metrics**: Run both LoadRunner and Gatling versions to compare results
4. **Review performance**: Ensure converted simulation generates similar load profiles
