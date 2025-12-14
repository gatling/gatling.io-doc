---
title: Refine Selection
description: AI-powered code refinement for Gatling simulations in VS Code
lead: Improve and optimize your Gatling code with intelligent AI assistance
---

Select any portion of your Gatling code and ask the AI Assistant to refine it. This feature helps you optimize performance, add error handling, improve clarity, or make any other improvements to your simulation code.

{{<  alert info >}}
This is an experimental feature. Review the generated simulations carefully before use.
{{< /alert >}}

## How to use Refine Selection

### 1. Select code

Highlight the section of your Gatling simulation you want to improve in VS Code.

### 2. Open context menu

Right-click on the selection and choose **"Refine Selection"** from the context menu.

Alternatively, you can:
- Open the Command Palette (`Ctrl+Shift+P` / `Cmd+Shift+P`)
- Run `Gatling: Refine Selection`

### 3. Describe your goal

Enter what you want to accomplish with the code refinement:
- `optimize for performance`
- `add error handling`
- `refactor for clarity`
- `add comments`
- `simplify logic`
- `improve error messages`

### 4. Review changes

The AI applies the refinement immediately with visual highlighting. You'll see three options:

- **✓ Accept**: Apply the refined code permanently
- **✗ Reject**: Revert to your original code
- **⎄ View Diff**: Open a side-by-side comparison to review changes in detail

## What it can refine

The assistant can help with:

- **Performance optimization**: Reduce unnecessary operations, improve efficiency
- **Error handling**: Add proper checks, validation, and error recovery
- **Code clarity**: Simplify complex logic, improve naming, add documentation
- **Best practices**: Apply Gatling patterns and idioms correctly
- **Type safety**: Add proper type annotations (for TypeScript/Java/Kotlin)
- **Refactoring**: Extract repeated code, consolidate logic, remove duplication
- **Comments and documentation**: Add helpful explanations to complex sections

## Example refinements

### Adding error handling

**Original code:**
```typescript
http("Get Product")
  .get("/api/products/#{productId}")
  .check(status().is(200))
```

**Refinement goal:** `add error handling`

**Refined code:**
```typescript
http("Get Product")
  .get("/api/products/#{productId}")
  .check(status().is(200))
  .check(status().not(404).saveAs("productNotFound"))
  .check(status().not(500).saveAs("serverError"))
  .check(jsonPath("$.id").exists())
  .checkIf(response => response.status().is(200))
    .then(jsonPath("$.name").exists())
```

### Optimizing performance

**Original code:**
```typescript
exec(http("Get User").get("/api/users/#{userId}"))
  .pause(1)
  .exec(http("Get Profile").get("/api/profiles/#{userId}"))
  .pause(1)
  .exec(http("Get Settings").get("/api/settings/#{userId}"))
```

**Refinement goal:** `optimize for performance`

**Refined code:**
```typescript
// Use resource hints and parallel requests where appropriate
exec(
  http("Get User Data")
    .get("/api/users/#{userId}")
    .resources(
      http("Get Profile").get("/api/profiles/#{userId}"),
      http("Get Settings").get("/api/settings/#{userId}")
    )
)
```

### Adding documentation

**Original code:**
```typescript
setUp(
  scenario1.injectOpen(rampUsers(100).during(60)),
  scenario2.injectOpen(constantUsersPerSec(10).during(120))
).protocols(httpProtocol)
```

**Refinement goal:** `add comments explaining the load pattern`

**Refined code:**
```typescript
setUp(
  // Primary user flow: gradually ramp up from 0 to 100 users over 1 minute
  // Simulates morning traffic surge
  scenario1.injectOpen(rampUsers(100).during(60)),
  
  // Background API calls: maintain constant rate of 10 requests/sec for 2 minutes
  // Represents automated systems and cron jobs
  scenario2.injectOpen(constantUsersPerSec(10).during(120))
).protocols(httpProtocol)
```

## Tips for effective refinements

### Be specific with your goal

Instead of vague requests like "make it better," provide specific goals:
- ❌ `improve this`
- ✅ `add response time assertions under 500ms`
- ✅ `extract repeated HTTP configuration into a function`

### Select appropriate scope

Select enough code to give context, but not more than necessary:
- **Too little**: Just a single line might lack context
- **Too much**: An entire file might produce unfocused results
- **Just right**: A complete logical block (request chain, scenario, or setUp)

### Review before accepting

Always review the refined code before accepting:
- Use **View Diff** to see exactly what changed
- Verify the changes align with your goal
- Check that no unintended modifications were made

### Iterate if needed

If the first refinement isn't perfect:
1. **Reject** the changes to revert
2. Select the code again
3. Provide a more specific refinement goal
4. Review the new result

## Integration with other features

**Refine Selection** works well with other Gatling AI Assistant features:

- **Explain Code**: First explain unfamiliar code, then refine it once you understand it
- **Contextual Chat**: Ask questions about refinement options before applying changes
- **Create Simulation**: Generate initial simulations, then refine specific sections

## Common use cases

### Preparing for production

Refine test scenarios to add:
- Comprehensive error handling
- Performance assertions
- Realistic think times and pauses
- Proper logging and debugging hooks

### Code review improvements

Apply feedback from code reviews:
- `add type safety to this function`
- `extract this repeated code into a helper`
- `simplify this nested conditional`

### Learning Gatling patterns

Improve your own code to learn better patterns:
- `apply Gatling best practices`
- `use more idiomatic Gatling DSL`
- `optimize this for Gatling's execution model`

## Privacy and context

When refining code, the AI Assistant sends:
- Your selected code (with sensitive data automatically redacted)
- Your refinement goal
- Current file context (language, filename)
- Workspace context (when relevant)

See [Privacy and Security]({{< ref "/integrations/ide-tools/vscode/overview#privacy-and-security" >}}) for full details.

## Limitations

- **AI accuracy**: Always review generated code before accepting
- **Selection size**: Very large selections (>1000 lines) may produce less focused results
- **Context limitations**: The AI has limited visibility into your full codebase

## Troubleshooting

### Refinement doesn't match my goal

Try being more specific in your refinement goal or selecting a smaller, more focused section of code.

### Changes are too aggressive

If the AI makes more changes than you wanted, reject the refinement and:
- Select a smaller scope
- Be more specific about what should stay the same
- Use a more constrained goal like "only add error handling, don't refactor"

### Code doesn't compile after refinement

Always test refined code:
1. Reject the changes if they don't compile
2. Try again with more context (select surrounding code)
3. Use **View Diff** to identify problematic changes
4. Ask in **Contextual Chat** for help with specific issues
