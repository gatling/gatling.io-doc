---
title: Create a Gatling test from a browser session with Playwright and AI
menutitle: From a Playwright session
seotitle: Create a Gatling load test from a Playwright browser session with AI
description: Build a Gatling Java/Maven load test by browsing your site in a headed browser while your coding agent records the traffic with playwright-cli and generates the test.
lead: Browse your site in a real browser. Your coding agent records the requests with playwright-cli, attributes them to the steps of your journey, and generates a working Gatling test that mirrors the journey as you walked it.
date: 2026-10-27T00:00:00+00:00
---

## Understand how it works

Go from a live browser session to a working Gatling test, without leaving your coding agent. Your agent opens a real browser on your site with [playwright-cli](https://github.com/microsoft/playwright-cli), you walk your user journey, and the agent turns the recorded traffic into a Gatling test that follows the steps you took.

- **You browse**: you drive the browser yourself and name each step of your journey in the chat.
- **Your agent records**: at each step, it saves the list of requests from playwright-cli, with the full request and response of each API call.
- **Your agent generates the test**: it attributes each request to a step, extracts the values that change between runs, and writes a parameterized Gatling Java/Maven project.

Recording and analysis are two separate phases. The agent first saves the traffic of each step to disk, then builds the test from those files once the browser is closed. Working from the full recording, instead of deciding what matters while you click, gives better correlations and a more faithful replay, and the agent can re-check the capture without asking you to redo the journey.

{{< home-cta eyebrow="Live demo | December 10, 2026" title="See it live: from click-through to load test" pills="Thursday, December 10 | 6 PM CET / 12 PM EST | 60 min with live Q&A" primary_label="Register for the live demo" primary_url="https://gatling.io/sessions/playwright-to-gatling-live-demo?utm_source=docs&utm_medium=playwright-recorder&utm_campaign=playwright-live-demo" secondary_label="Start free trial" secondary_url="https://gatling.io/sign-up?utm_source=docs&utm_medium=playwright-recorder&utm_campaign=enterprise-trial" note="Free. Can't attend live? Register and we'll send you the recording." image="https://cdn.prod.website-files.com/685bbddcf5b30f66e1a7ac63/6adf9ffd3de7b9b3546b0518_6adf9fe2b2f23fd35caa33ca_session-playwright-to-gatling.png" image_alt="Playwright to Gatling live demo with Heather Thacker, December 10, 2026" >}}
Watch Heather Thacker, Developer Advocate at Gatling, drive a Playwright browser, generate the Gatling test with AI, then deploy and run it on Gatling Enterprise Edition. Bring your questions for the live Q&A.
{{< /home-cta >}}

## Meet the prerequisites

- A 64-bit OpenJDK LTS version, 11 through 25, on macOS, Linux, or Windows. We recommend the [Azul JDK](https://www.azul.com/downloads/?package=jdk#zulu).
- Node.js 24 or later, to run playwright-cli.
- playwright-cli and its skill for your coding agent:

  ```console
  npm install -g @playwright/cli@latest
  playwright-cli install --skills --global
  ```

  The second command installs the skill for Claude Code. For other coding agents, use `--skills agents`.

- A coding agent installed on your machine. This guide uses [Claude Code](https://docs.claude.com/en/docs/claude-code/overview).
- The Gatling AI extensions plugin. See the [installation instructions]({{< ref "/ai-plugins/overview#installation-for-claude-code" >}}).

During the run, the agent runs playwright-cli and build commands. Depending on your agent settings, it asks for your approval before each command.

{{< alert tip >}}
You don't need Maven installed. The generated project ships with the Maven Wrapper (`mvnw`), so it builds on a machine that only has a JDK.
{{< /alert >}}

## Try it on the demo site {#demo}

Start with the [Gatling e-commerce demo site](https://ecomm.gatling.io) to see the full workflow before using it on your own application. The prompt models only the requests sent to the demo site's API and excludes static assets.

### 1. Go to your workspace directory

Navigate to the directory where you want to create your Gatling project:

```console
cd ~/path/to/your/workspace
```

The agent creates everything inside a new subdirectory, so you can start from an empty folder.

### 2. Start your coding agent

For Claude Code:

```console
claude
```

### 3. Paste the prompt

Paste the following prompt verbatim. Its guardrails keep the discovery phase reliable.

```markdown
# Task: Generate a Gatling Java/Maven load test from a live browser session

## Context

Produce a Java/Maven Gatling load testing project for the Gatling e-commerce demo site:
https://ecomm.gatling.io.

Record the browser session with playwright-cli and its skill. Don't use a HAR export, a trace,
or another browser automation tool.

## Workflow: two phases, don't skip Phase 1

### Phase 1: Discovery (recorded session)

Discovery happens in two sub-phases: I drive the browser live and you save the traffic of each
step; then you analyze the saved files. Don't blend these: don't try to judge relevance while
I'm still clicking, and don't re-open a browser during analysis.

**1a. Live capture: headed browser, I drive**

1. Open a headed browser: `playwright-cli open https://ecomm.gatling.io --headed`.
   playwright-cli lists the network requests from that point.
2. The requests sent before my first marker form a first step named `initial load`.
3. Hand control to me. I'll manually perform whatever journey I choose to walk through the
   site. There's no predefined script; the steps and their order are up to me, and may change
   from run to run.
4. Before I start each step, I'll post a marker in the chat in the following format:
   `starting: <step_name>` (e.g. `starting: add to cart`). Don't try to infer step boundaries
   yourself from what's happening on the page, and don't assume a step implies any particular
   later step will follow.
5. Each marker ends the current step. When you see one, save that step before confirming:
   - Run `playwright-cli requests --static` and save its output to
     `discovery/<NN>-<step_name>/requests.md`, where NN is the step's position (`01` for
     `initial load`). It lists every request of the step, static assets included.
   - For each listed request to the API base URL (see Constraints), save
     `playwright-cli request <n>`, `playwright-cli request-body <n>` when the request has a
     body, and `playwright-cli response-body <n>` in the same directory with `--filename`.
     `request <n>` doesn't include the request body. Don't fetch details for any other request.
   - Run `playwright-cli requests --clear`, so the next step's list starts empty.
   - Briefly confirm the step name and the number of API requests saved.
6. When I say "done", save the last step the same way, then close the browser with
   `playwright-cli close`.

**1b. Offline analysis: browser closed, deterministic**

1. Work only from the files in `discovery/`. Don't rely on anything you observed live, and
   don't re-drive the browser.
2. Each step directory holds the requests attributed to that step, in order.
3. For each API request, extract method, path, query/body params, and any response fields
   needed to chain later requests (e.g. IDs). Note which values vary run-to-run (e.g. product
   IDs) so Phase 2 parameterizes them instead of hardcoding.
4. This step should be fully reproducible from the saved files alone. If you need to re-check
   something, re-read them rather than asking me to redo the journey.

### Phase 2: Implementation

Build a new Gatling scenario that reproduces the journey as I actually walked it, using the step
names and order of the `discovery/` directories, not a fixed script. Each step becomes a
corresponding block in the Gatling scenario, in the same sequence I performed them, built from
the API requests saved for it.

Then compile the project and run the simulation once with `./mvnw gatling:test`. Fix any
failure before finishing.

## Constraints

- Only model requests made to the API base URL:
  https://api-ecomm.gatling.io. Exclude any third-party/off-domain requests captured during the session.
- Exclude static assets (JS, CSS, images, fonts, etc.). Only model the XHR/fetch requests to the API base URL.
- Create a new directory `Ecomm-Gatling-Test` and add all project files inside it (the `discovery/` directory from Phase 1 goes here too).
- For the injection profile, run the scenario as a smoke test with one single user, and assert that no request fails.

## Flag these back to me rather than guessing

- If a request can't be cleanly attributed to a single step (e.g. an async/debounced call spanning two step markers), tell me instead of guessing which step it belongs to.
- If the capture appears to have missed requests (e.g. a step with no API requests where you'd expect some), tell me before proceeding to analysis.
```

### 4. Walk your journey

A browser window opens on the site. From there, your only jobs are to browse, to announce each step in the chat, and to type `done` at the end. See [Follow the run]({{< ref "#what-happens-during-the-run" >}}).

For example, to browse the catalog, add a product to the cart, log in, and check out, type these markers in the agent chat, one before each action:

```text
starting: go to page 2
starting: add first product on page 2 to cart
starting: unauthenticated checkout
starting: login
starting: authenticated checkout
done
```

### 5. Review the generated test

When the agent finishes, the `Ecomm-Gatling-Test` directory contains the Maven project, the simulation under `src/test/java/`, and the saved capture in `discovery/`. The agent has already run the test once with a single user. To run it again, from the project directory:

```console
./mvnw gatling:test
```

On Windows, use `mvnw.cmd gatling:test`.

## Use it on your own application {#own-application}

The workflow is the same. Copy the demo prompt and replace the demo values before pasting it:

| In the demo prompt | Replace with | Example |
|---|---|---|
| `https://ecomm.gatling.io` | The URL of the page where your journey starts. | `https://staging.example.com` |
| `https://api-ecomm.gatling.io` | The base URL of the API your front end calls. If it calls several APIs, list them all. | `https://api.staging.example.com` |
| `Ecomm-Gatling-Test` | The name of the directory to create for the Gatling project. | `Checkout-Gatling-Test` |

Also replace "the Gatling e-commerce demo site" with a short description of your application, and adjust the **Constraints** section to match your journey.

### Find your API base URL

1. Open your site in your browser and open the developer tools.
2. Go to the **Network** tab and filter on **Fetch/XHR**.
3. Browse a few pages and note the domains the requests are sent to. Ignore analytics, tag managers, and other third-party domains.

## Follow the run {#what-happens-during-the-run}

### Capture your journey (Phase 1a)

A headed browser window opens on the target site, and playwright-cli lists the requests from the first page load. These requests form the first step, `initial load`.

Before each action, type a step marker in the agent chat, for example:

```text
starting: add to cart
```

The agent saves the requests of the step that just ended in `discovery/`, with the full request and response of each API call, clears the list, and confirms. Then perform the step in the browser. When you're finished, type:

```text
done
```

The agent saves the last step and closes the browser.

### Let the agent analyze the capture (Phase 1b)

The agent works from the saved files only. It keeps the requests sent to your API base URL and identifies the values that change from one run to the next, such as IDs and tokens. If a request can't be attributed to a single step, or if the capture seems incomplete, the agent asks you instead of guessing.

### Let the agent generate the test (Phase 2)

The agent bootstraps a standard Gatling Java/Maven project and writes the Gatling test, with one block per step, in the order you performed them. It extracts the values that vary from run to run from responses instead of hardcoding them, and adds an assertion that fails the run if any request fails. Finally, it compiles the test and runs it once with a single user to check that it replays.

## Let the agent drive the browser {#agent-driven}

Your agent can also walk the journey itself. Describe the journey in the prompt instead of posting step markers, and ask the agent to drive playwright-cli in a headless browser. The agent knows which action it just performed, so it reads the requests of each step right after the action, before a page load can reset the list. Capture and analysis then happen in a single pass, without a browser window and without the `discovery/` directory.

On the demo site, this mode produces the same API requests as walking the same journey yourself. Keep the workflow you drive as the default, though:

- The agent picks the route through the UI. When the site offers several, such as logging in from the header or from the checkout, the scenario follows the agent's choice, not what your users do.
- The agent can get stuck on login flows, CAPTCHAs, bot detection, or UI it can't read. If your site sits behind a bot check, such as Cloudflare's, ask the agent to open the browser in headed mode (`--headed`): headed browsers pass these checks more often, and you can complete a challenge in the window yourself.
- The capture carries the User-Agent of a headless browser, and the pauses between steps are estimates, not your think times.

Let the agent drive for public journeys that you can describe in a sentence.

## Get better results

- **Take your time in the browser.** The agent waits for your next marker. Leaving clear gaps between steps makes attribution unambiguous and gives cleaner results.
- **Post the marker before the action.** Wait for the agent's confirmation, then click. Traffic sent before a marker belongs to the previous step.
- **Start a new step for each full page load.** playwright-cli lists the requests of the current page only, and a full page load starts a new list. Route changes in a single-page application, like the demo site, don't. The agent saves the list at every marker, so a full page load in the middle of a step loses the requests sent before it, and the agent can't detect it.
- **Use descriptive marker names.** The step names become the blocks of your Gatling scenario.
- **Steps don't imply each other.** Adding a product to the cart doesn't commit you to checking out. Abandon a flow if you want to: the generated scenario reflects what you actually did.

## Protect your session data

The `discovery/` directory holds the requests and responses of your session, including cookies, tokens, and the credentials you typed.

- Record your journey on a test or staging environment, with a test account.
- Don't commit the `discovery/` directory as is. Review it, or add it to your `.gitignore`.

## Next steps

- Scale the smoke test into a load test by changing the [injection profile]({{< ref "/concepts/injection" >}}).
- Deploy and run the test on Gatling Enterprise Edition from your coding agent with the [Gatling skills]({{< ref "/ai-plugins/skills" >}}) and the [MCP server]({{< ref "/ai-plugins/mcp-server" >}}).
