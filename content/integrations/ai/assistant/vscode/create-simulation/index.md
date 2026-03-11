---
title: Create a Simulation
description: Create new Gatling simulations using the interactive wizard
lead: Generate new Gatling simulations with guided configuration
---

The **Create Simulation** feature provides an interactive wizard that generates complete, ready-to-run Gatling simulations. Answer a few questions about your test scenario, and the wizard generates properly structured code in your chosen language.

{{< alert info >}}
**Create Simulation** requires you to have a Gatling project set up in your workspace. If you don't have one yet, download your preferred Gatling SDK from the [Gatling website](https://gatling.io/download-gatling-community-edition) and open it in VS Code.
{{< /alert >}}

## How to use Create Simulation

### Option 1: Command Palette

1. Open the Command Palette (`Ctrl+Shift+P` / `Cmd+Shift+P`)
2. Type and select **"Gatling: Create Simulation"**
3. Follow the wizard prompts

### Option 2: Welcome Page

1. Open the Command Palette and run **"Gatling: Show Welcome Page"**
2. Click the **"Create New Simulation"** button
3. Follow the wizard prompts

### Option 3: Activity Bar

1. Click the Gatling icon in the Activity Bar (left sidebar)
2. Click the **"Create Simulation"** quick action button
3. Follow the wizard prompts

## The wizard workflow

The wizard guides you through 7 steps to configure your simulation:

### Step 1: Simulation Name
Enter a name for your simulation (must start with uppercase).

**Example:** `MyApiLoadTest`

### Step 2: Target URL
Enter the base URL of the system you want to test.

**Example:** `https://api.example.com`

### Step 3: Injection Profile
Choose how users will be injected into the simulation:

| Profile | Description | Use Case |
|---------|-------------|----------|
| **constantUsersPerSec** | Constant rate of users per second | Sustained load testing |
| **rampUsers** | Gradually increase users over time | Realistic traffic growth |
| **atOnceUsers** | All users start at once | Spike testing |
| **stressPeak** | Peak stress pattern | Find breaking points |

### Step 4: Number of Users
Specify how many virtual users to simulate.

**Example:** `100`

### Step 5: Duration (if applicable)
For `constantUsersPerSec` and `stressPeak` profiles, specify test duration in seconds.

**Example:** `60` (1 minute)

### Step 6: Ramp Duration (if applicable)
For `rampUsers` profile, specify how long to ramp up in seconds.

**Example:** `30` (30 seconds to reach full load)

### Step 7: Data Feeders
Choose whether to include data feeders:

- **No**: Simple simulation without external data
- **Yes - CSV**: Load test data from CSV file
- **Yes - JSON**: Load test data from JSON file
- **Yes - Array**: Use in-memory array for data

## What gets generated

The wizard creates a complete, runnable Gatling simulation with:

### Basic structure
- **Imports**: All necessary Gatling DSL imports
- **HTTP protocol**: Configured with your target URL and standard headers
- **Scenario**: A basic scenario with a sample GET request
- **Load injection**: Your chosen injection profile configured
- **Status check**: Basic 200 OK assertion

### Optional additions
- **Feeder setup**: Pre-configured feeder code if you selected data feeders
- **Comments**: Helpful documentation about your configuration

### Example output (TypeScript with rampUsers)

**Wizard inputs:**
- Name: `MyApiLoadTest`
- URL: `https://api.example.com`
- Profile: `rampUsers`
- Users: `100`
- Ramp: `60` seconds
- Feeders: No

**Generated code:**
```typescript
import { simulation, scenario, exec, rampUsers } from '@gatling.io/core';
import { http, status } from '@gatling.io/http';

/**
 * MyApiLoadTest - Gatling Load Test Simulation
 * 
 * Target: https://api.example.com
 * Users: 100
 * Duration: n/a
 * Profile: rampUsers
 */

// HTTP Protocol Configuration
const httpProtocol = http
  .baseUrl('https://api.example.com')
  .acceptHeader('application/json')
  .contentTypeHeader('application/json')
  .userAgentHeader('Gatling');

// Scenario Definition
const scn = scenario('MyApiLoadTest Scenario')
  .exec(
    http('Request')
      .get('/')
      .check(status().is(200))
  );

// Simulation Configuration
export default simulation((setUp) => {
  setUp(
    scn.injectOpen(rampUsers(100).during(60))
  ).protocols(httpProtocol);
});
```

## Automatic file placement

The wizard automatically places your simulation in the correct directory based on your project structure and language:

### JavaScript/TypeScript projects
- `javascript/src/` or `typescript/src/`
- Falls back to `src/` if language-specific folder doesn't exist

### Java/Scala/Kotlin projects
The wizard detects your build tool and uses the appropriate structure:

| Build Tool | Default Location |
|------------|------------------|
| **Maven** | `src/test/{language}/simulations/` |
| **Gradle** | `src/test/{language}/simulations/` |
| **sbt** | `src/test/scala/simulations/` |

**Note:** sbt only supports Scala. The wizard will warn you if you try to create non-Scala simulations in an sbt project.

### Custom directories
Configure custom simulation directories in VS Code settings:

**File → Preferences → Settings → Extensions → Gatling AI Assistant → Simulation Directory**

Set per-language directories:
- `gatling.simulationDirectory.javascript`
- `gatling.simulationDirectory.typescript`
- `gatling.simulationDirectory.java`
- `gatling.simulationDirectory.scala`
- `gatling.simulationDirectory.kotlin`

## Next steps

The wizard creates a foundation for your simulation. Customize it using resources from the Gatling documentation:

| Task | Reference |
|------|-----------|
| Update requests, add query parameters, set request bodies | [HTTP requests]({{< ref "/reference/script/http/request" >}}) |
| Add multiple requests to your scenario | [Scenarios]({{< ref "/concepts/scenario" >}}) |
| Load test data from CSV, JSON, or other sources | [Feeders]({{< ref "/concepts/session/feeders" >}}) |
| Validate responses with checks and assertions | [Checks]({{< ref "/concepts/checks" >}}) & [Assertions]({{< ref "/concepts/assertions" >}}) |
| Configure different load injection strategies | [Load injection]({{< ref "/concepts/injection" >}}) |
| Extract values from responses for use in subsequent requests | [Session]({{< ref "/concepts/session" >}}) |
| Add think times and pauses between requests | [Pauses]({{< ref "/concepts/scenario/#pause" >}}) |

## Using AI Chat for advanced scenarios

While the wizard creates basic simulations, you can use the **AI Chat** feature to develop more complex scenarios:

### When to use the wizard
- ✅ Quick start with basic structure
- ✅ Standard load patterns
- ✅ Single scenario simulations
- ✅ Learning Gatling syntax

### When to use AI Chat
- ✅ Complex user journeys with multiple paths
- ✅ Custom authentication flows
- ✅ Advanced correlation and data extraction
- ✅ Multi-scenario simulations
- ✅ Performance tuning advice

## Troubleshooting

### "Could not determine target directory"

**Cause:** Your project doesn't have a recognizable structure for the selected language.

**Solutions:**
1. **Download Gatling SDK**: Click "Download Gatling SDK" to get proper project structure
2. **Manual directory**: Create `src/test/java`, `src/test/scala`, etc., manually
3. **Custom path**: Set a custom directory in VS Code settings

### "sbt projects only support Scala simulations"

**Cause:** You're trying to create a Java or Kotlin simulation in an sbt project.

**Solutions:**
1. **Switch to Scala**: The wizard offers to switch automatically
2. **Use Maven/Gradle**: Convert your project to Maven or Gradle for Java/Kotlin support
3. **Create anyway**: You can create the file, but you won't be able to run it with sbt

### File already exists

**Cause:** A simulation with that name already exists in the target directory.

**Solutions:**
1. Choose a different name
2. Delete or rename the existing file
3. Manually edit the existing simulation
