---
title: Create a Gatling test from a browser session with Playwright and AI
menutitle: Playwright recorder
seotitle: Create a Gatling load test from a Playwright browser session with AI
description: Build a Gatling Java/Maven load test by browsing your site in a headed Playwright browser while your coding agent records the traffic and generates the test.
lead: Browse your site in a real browser. Your coding agent records every request, attributes it to the steps of your journey, and generates a working Gatling test that mirrors the journey as you walked it.
date: 2026-10-27T00:00:00+00:00
---

## Overview

Go from a live browser session to a working Gatling test, without leaving your coding agent. Your agent calls Playwright to open a real browser on your site, you walk your user journey, and the agent turns the recorded traffic into a Gatling test that follows the steps you took.

- **You browse**: you drive the browser yourself and name each step of your journey in the chat.
- **Your agent records**: through Playwright, it captures every request sent while you browse.
- **Your agent generates the test**: it attributes each request to a step, extracts the values that change between runs, and writes a parameterized Gatling Java/Maven project.

{{< home-cta eyebrow="Live demo | December 10, 2026" title="See it live: from click-through to load test" pills="Thursday, December 10 | 6 PM CET / 12 PM EST | 60 min with live Q&A" primary_label="Register for the live demo" primary_url="https://gatling.io/sessions/playwright-to-gatling-live-demo?utm_source=docs&utm_medium=playwright-recorder&utm_campaign=playwright-live-demo" secondary_label="Start free trial" secondary_url="https://gatling.io/sign-up?utm_source=docs&utm_medium=playwright-recorder&utm_campaign=enterprise-trial" note="Free. Can't attend live? Register and we'll send you the recording." image="https://cdn.prod.website-files.com/685bbddcf5b30f66e1a7ac63/6adf9ffd3de7b9b3546b0518_6adf9fe2b2f23fd35caa33ca_session-playwright-to-gatling.png" image_alt="Playwright to Gatling live demo with Heather Thacker, December 10, 2026" >}}
Watch Heather Thacker, Developer Advocate at Gatling, drive a Playwright browser, generate the Gatling test with AI, then deploy and run it on Gatling Enterprise Edition. Bring your questions for the live Q&A.
{{< /home-cta >}}

## Prerequisites

- A 64-bit OpenJDK LTS version, 11 through 25, on macOS, Linux, or Windows. We recommend the [Azul JDK](https://www.azul.com/downloads/?package=jdk#zulu).
- A coding agent installed on your machine. This guide uses [Claude Code](https://docs.claude.com/en/docs/claude-code/overview).
- The Gatling AI extensions plugin. See the [installation instructions]({{< ref "/ai-plugins/overview#installation-for-claude-code" >}}).

During the run, the agent installs and launches Playwright and runs build commands. Depending on your agent settings, it asks for your approval before each command.

{{< alert tip >}}
Maven itself is not required. The generated project ships with the Maven Wrapper (`mvnw`), so it builds on a machine that only has a JDK.
{{< /alert >}}

## Try it on the demo site {#demo}

Start with the Gatling e-commerce demo site to see the full workflow before using it on your own application.

This guide models only requests to the Gatling Ecommerce Demo website’s API base URL and excludes all static assets. Adjust the “Constraints” section in the prompt as needed to reflect your user journey.

### 1. Go to your workspace directory

Navigate to the directory where you want to create your Gatling project:

```console
cd ~/path/to/your/workspace
```

Everything is created inside a new subdirectory, so an empty folder is a fine starting point.

### 2. Start your coding agent

For Claude Code:

```console
claude
```

### 3. Paste the prompt

Paste the following prompt in full. Paste it verbatim the first time: its guardrails are what keep the discovery phase reliable.

```markdown
# Task: Generate a Gatling Java/Maven load test from a live browser session

## Context

Produce a Java/Maven Gatling load testing project for the Gatling e-commerce website: https://ecomm.gatling.io.

## Workflow: two phases, don't skip Phase 1

### Phase 1: Discovery (recorded session)

Discovery happens in two sub-phases: I drive the browser live and you capture raw traffic;
then you analyze the capture offline. Don't blend these: don't try to judge relevance while
I'm still clicking, and don't re-open a browser during analysis.

**1a. Live capture: headed browser, I drive**

1. Launch a headed Playwright browser against https://ecomm.gatling.io.
2. Immediately start unfiltered network capture for the whole session: a full HAR export (or
   equivalent CDP `Network.*` event log written continuously to disk). Capture everything;
   don't filter or decide relevance yet.
3. Any relevant requests that were triggered should be considered the first step of my capture. Name them in whichever way you find relevant.
4. Hand control to me. I will manually perform whatever journey I choose to walk through the
   site. There's no predefined script; the steps and their order are up to me, and may change
   from run to run.
5. Before I start each step, I'll post a short marker in chat naming that step in the following format: starting: <step_name> (e.g. "starting:
   add to cart"). When you see it, record a timestamp against that step name and briefly confirm
   you've logged it. Don't try to infer step boundaries yourself from what's happening on the
   page, and don't assume a step implies any particular later step will follow.
6. When I say "done", stop the capture. Save the HAR/network log and the step-marker timestamps
   into the project directory for traceability, e.g.:
   - `discovery/session.har`
   - `discovery/step-markers.json`

**1b. Offline analysis: headless, deterministic**

1. With the browser closed, parse the saved HAR/network log from disk. Don't rely on anything
   you observed live, and don't re-drive the browser.
2. Use the step markers to slice the request timeline and attribute each request to the journey
   step it belongs to.
3. Within each step, keep only requests to the API base URL (see Constraints) and drop static
   assets, per the Constraints below.
4. For each retained request, extract method, path, query/body params, and any response fields
   needed to chain later requests (e.g. IDs). Note which values vary run-to-run (e.g. product
   IDs) so Phase 2 parameterizes them instead of hardcoding.
5. This step should be fully reproducible from the saved artifacts alone. If you need to
   re-check something, re-parse the HAR rather than asking me to redo the journey.

### Phase 2: Implementation

Build a new Gatling scenario that reproduces the journey as I actually walked it, using the step
names and order from `discovery/step-markers.json`, not a fixed script. Each named step from 1a
becomes a corresponding block in the Gatling scenario, in the same sequence I performed them,
built from the requests attributed to it in the 1b analysis.

## Constraints

- Only model requests made to the API base URL:
  https://api-ecomm.gatling.io. Exclude any third-party/off-domain requests captured during the session.
- Exclude static assets (JS, CSS, images, fonts, etc.). Only model the navigational page/document/XHR requests relevant to the journey above.
- Create a new directory `Ecomm-Gatling-Test` and add all project files inside it (the `discovery/` capture artifacts from Phase 1 go here too).
- For the injection profile, run the scenario as a smoke test with one single user.

## Flag these back to me rather than guessing

- If a request can't be cleanly attributed to a single step (e.g. an async/debounced call spanning two step markers), tell me instead of guessing which step it belongs to.
- If the HAR/network capture appears to have missed requests (e.g. gaps around a step boundary), tell me before proceeding to analysis.
```

### 4. Walk your journey

A Chromium window opens on the site. From there, your only jobs are to browse, to announce each step in the chat, and to type `done` at the end. See [what happens during the run](#what-happens-during-the-run).

For example, to browse the catalog, add a product to the cart, log in, and check out, post these markers in the chat, one before each action:

```console
starting: go to page 2
starting: add first product on page 2 to cart
starting: unauthenticated checkout
starting: login
starting: authenticated checkout
done
```

## Use it on your own application {#own-application}

The workflow is the same. Only the prompt changes: replace the placeholders before pasting it.

| Placeholder | What to put | Example |
|---|---|---|
| `<SITE_URL>` | The URL of the page where your journey starts. | `https://staging.example.com` |
| `<API_BASE_URL>` | The base URL of the API your front end calls. If it calls several APIs, list them all. | `https://api.staging.example.com` |
| `<PROJECT_DIR>` | The name of the directory to create for the Gatling project. | `Checkout-Gatling-Test` |

### Find your API base URL

1. Open your site in your browser and open the developer tools.
2. Go to the **Network** tab and filter on **Fetch/XHR**.
3. Browse a few pages and note the domains the requests are sent to. Ignore analytics, tag managers, and other third-party domains.

### The prompt with placeholders

```markdown
# Task: Generate a Gatling Java/Maven load test from a live browser session

## Context

Produce a Java/Maven Gatling load testing project for my website: <SITE_URL>.

## Workflow: two phases, don't skip Phase 1

### Phase 1: Discovery (recorded session)

Discovery happens in two sub-phases: I drive the browser live and you capture raw traffic;
then you analyze the capture offline. Don't blend these: don't try to judge relevance while
I'm still clicking, and don't re-open a browser during analysis.

**1a. Live capture: headed browser, I drive**

1. Launch a headed Playwright browser against <SITE_URL>.
2. Immediately start unfiltered network capture for the whole session: a full HAR export (or
   equivalent CDP `Network.*` event log written continuously to disk). Capture everything;
   don't filter or decide relevance yet.
3. Any relevant requests that were triggered should be considered the first step of my capture. Name them in whichever way you find relevant.
4. Hand control to me. I will manually perform whatever journey I choose to walk through the
   site. There's no predefined script; the steps and their order are up to me, and may change
   from run to run.
5. Before I start each step, I'll post a short marker in chat naming that step in the following format: starting: <step_name> (e.g. "starting:
   add to cart"). When you see it, record a timestamp against that step name and briefly confirm
   you've logged it. Don't try to infer step boundaries yourself from what's happening on the
   page, and don't assume a step implies any particular later step will follow.
6. When I say "done", stop the capture. Save the HAR/network log and the step-marker timestamps
   into the project directory for traceability, e.g.:
   - `discovery/session.har`
   - `discovery/step-markers.json`

**1b. Offline analysis: headless, deterministic**

1. With the browser closed, parse the saved HAR/network log from disk. Don't rely on anything
   you observed live, and don't re-drive the browser.
2. Use the step markers to slice the request timeline and attribute each request to the journey
   step it belongs to.
3. Within each step, keep only requests to the API base URL (see Constraints) and drop static
   assets, per the Constraints below.
4. For each retained request, extract method, path, query/body params, and any response fields
   needed to chain later requests (e.g. IDs). Note which values vary run-to-run (e.g. product
   IDs) so Phase 2 parameterizes them instead of hardcoding.
5. This step should be fully reproducible from the saved artifacts alone. If you need to
   re-check something, re-parse the HAR rather than asking me to redo the journey.

### Phase 2: Implementation

Build a new Gatling scenario that reproduces the journey as I actually walked it, using the step
names and order from `discovery/step-markers.json`, not a fixed script. Each named step from 1a
becomes a corresponding block in the Gatling scenario, in the same sequence I performed them,
built from the requests attributed to it in the 1b analysis.

## Constraints

- Only model requests made to the API base URL(s):
  <API_BASE_URL>. Exclude any third-party/off-domain requests captured during the session.
- Exclude static assets (JS, CSS, images, fonts, etc.). Only model the navigational page/document/XHR requests relevant to the journey above.
- Create a new directory `<PROJECT_DIR>` and add all project files inside it (the `discovery/` capture artifacts from Phase 1 go here too).
- For the injection profile, run the scenario as a smoke test with one single user.

## Flag these back to me rather than guessing

- If a request can't be cleanly attributed to a single step (e.g. an async/debounced call spanning two step markers), tell me instead of guessing which step it belongs to.
- If the HAR/network capture appears to have missed requests (e.g. gaps around a step boundary), tell me before proceeding to analysis.
```

## What happens during the run {#what-happens-during-the-run}

### Phase 1a: live capture (you drive)

A headed Chromium window opens on the target site and the capture starts immediately. The requests sent when the page first loads are recorded as the first step.

Before each action, type a step marker in the chat, for example:

```console
starting: add to cart
```

The agent timestamps it and confirms. Then perform the step in the browser. When you are finished, type:

```console
done
```

The capture stops, the browser closes, and the HAR file and the step markers are saved in the `discovery/` directory of the project.

### Phase 1b: offline analysis (browser closed)

The agent parses the saved files, attributes each request to a step, keeps only the requests sent to your API base URL, and identifies the values that change from one run to the next, such as IDs and tokens. If a request can't be attributed to a single step, or if the capture seems incomplete, the agent asks you instead of guessing.

### Phase 2: implementation

The agent bootstraps a standard Gatling Java/Maven project and writes the Gatling test, with one block per step, in the order you performed them. Values that vary from run to run are extracted from responses instead of hardcoded. Finally, the test is compiled and executed once with a single user to check that it replays.

To run the test again yourself, from the project directory:

```console
./mvnw gatling:test
```

On Windows, use `mvnw.cmd gatling:test`.

## Tips and troubleshooting

- **Take your time in the browser.** There is no timeout. Leaving clear gaps between steps makes attribution unambiguous and gives cleaner results.
- **Post the marker before the action.** Post the marker, wait for the confirmation, then click. A marker posted after the action attributes that traffic to the previous step.
- **Use descriptive marker names.** The step names become the blocks of your Gatling scenario.
- **Steps don't imply each other.** Adding a product to the cart doesn't commit you to checking out. Abandon a flow if you want to: the generated scenario reflects what you actually did.

## Security

The HAR file contains everything the browser sent and received during the session, including cookies, tokens, and the credentials you typed.

- Record your journey on a test or staging environment, with a test account.
- Don't commit the `discovery/` directory as is. Review it, or add it to your `.gitignore`.

## Next steps

- Scale the smoke test into a load test by changing the [injection profile]({{< ref "/concepts/injection" >}}).
- Deploy and run the test on Gatling Enterprise Edition from your coding agent with the [Gatling skills]({{< ref "/ai-plugins/skills" >}}) and the [MCP server]({{< ref "/ai-plugins/mcp-server" >}}).
