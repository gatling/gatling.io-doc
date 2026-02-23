# Gatling Docs Style Guide

The [Google developer documentation style guide](https://developers.google.com/style) applies by default. The rules below are Gatling-specific additions or clarifications.

---

## Hugo front matter

Every page must have all of these fields. Flag any that are missing or look copy-pasted (e.g., `description` and `lead` are identical to `title`):

- `title` — Short page title used in navigation
- `seotitle` — Longer, keyword-rich SEO title (must differ from `title`)
- `description` — One-sentence description for meta tags and search
- `lead` — Intro sentence shown at the top of the page (can match `description` but should be engaging, not a copy of `title`)
- `ordering` — Required in `_index.md` files; lists child slugs in nav order
- `aliases` — Required when a page was moved; lists previous URL paths

---

## Structure

- **One H1 per page.** The `title` front matter field acts as the H1. Any `# Heading` in the body is a violation.
- **Use H2 and H3 liberally.** Long sections without subheadings are harder to scan and less AI-friendly.
- **Bullet points for unordered lists.** Features, options, and non-sequential items use `-` bullets, not prose.
- **Numbered lists for sequential steps only.** If the order doesn't matter, use bullets.
- **No orphan content.** Every section needs at least a brief intro sentence before code blocks or bullets.

---

## Section headers

H2 and H3 headers must start with a verb. H4 and below are exempt.

| ❌ | ✅ |
| --- | --- |
| `## Prerequisites` | `## Meet the prerequisites` |
| `## Setup` | `## Set up your environment` |
| `## Configuration` | `## Configure the protocol` |
| `## Installation` | `## Install the plugin` |
| `## Authentication` | `## Authenticate with [method]` |
| `## Overview` | `## Understand how X works` |
| `## Conclusion` | `## Next steps` (or remove entirely) |
| `## Next Steps` | `## Next steps` (only first word capitalized) |

---

## Voice and tense

- **Active voice.** Passive voice obscures who does what.
  - ❌ "The configuration is defined by..." → ✅ "Define the configuration..."
  - ❌ "Messages are sent and received independently." → ✅ "Kafka sends and receives messages independently."
- **Present tense.** Avoid future tense for instructions.
  - ❌ "You will need to add..." → ✅ "Add..."
  - ❌ "This will allow you to..." → ✅ "This lets you..."
- **Imperative for instructions.** Steps are commands, not descriptions.
  - ❌ "Next, you must add the imports..." → ✅ "Add the following imports..."
- **Second person.** Use "you" for instructions. "We" is acceptable for Gatling recommendations only ("We recommend...").

---

## Content type rules

| Type | Location | Purpose | Shows how-to steps? |
| --- | --- | --- | --- |
| Reference | `content/reference/` | Describes how things work, API options, config | No — describe, don't instruct |
| Guide | `content/guides/` | Practical how-to for a specific use case | Yes — step-by-step |
| Tutorial | `content/tutorials/` | Start-to-finish learning experience | Yes — with full context |

Flag content that doesn't match its section. Examples: a reference page that walks through steps, or a guide with no actionable instructions.

---

## Code examples

- **Every code block needs a context sentence.** There must be prose before the code explaining what it does or why.
  - ❌ A code block that appears immediately after a heading with no intro sentence
  - ✅ "Add the following dependency to your `pom.xml`:" then the code
- **Code must be usable.** Snippets should be runnable or have clearly labeled placeholders (e.g., `YOUR_API_KEY`). Flag unexplained gaps.
- **Correct language tags.** Use the right syntax identifier:
  - `java` — Java only (not XML, not Gradle Groovy)
  - `xml` — Maven `pom.xml` content
  - `gradle` — Gradle Groovy DSL
  - `kotlin` — Kotlin
  - `scala` — Scala
  - `javascript` / `typescript` — JS/TS SDK
  - `console` — Shell commands (not `bash` or `shell`)
  - `yaml` — YAML config files
  - `dockerfile` — Dockerfile content
- **Multi-language pages.** If the page covers JVM languages, check whether Java, Kotlin, and Scala examples are all present. Flag missing variants.

---

## Hugo shortcodes and links

- **Internal links must use `ref`.** Flag raw relative URLs like `[text](/reference/foo)` or `[text](../foo)`. They should be `{{< ref "/path/to/page" >}}`.
- **Images must use `{{< img >}}`.** Raw markdown `![alt](src)` is not allowed. Every `{{< img >}}` must have a descriptive `alt` attribute.
- **Alerts.** Valid: `{{< alert info >}}`, `{{< alert warning >}}`, `{{< alert tip >}}`. Flag any alert without a type argument.

---

## Screenshots

Flag any screenshot that shows:
- Gatling Enterprise UI — changes with product updates
- Third-party UIs — AWS Console, GCP, Azure, GitHub, etc.

For each flagged screenshot, suggest replacing it with descriptive prose. If the screenshot is a diagram (not a UI screenshot), it can stay but must have a strong `alt` description.

---

## Tables

When a page lists multiple items with consistent attributes (API functions, config options, parameters), flag the absence of a table. Suggest using the pipe table format:

```markdown
| **Column** | **Column** |
| --- | --- |
| `value` | Description. |
```

---

## Google Style Guide rules

Apply these specific rules. Flag violations with the exact text and a correction.

| Rule | ❌ | ✅ |
| --- | --- | --- |
| Contractions are fine | "do not", "you will" | "don't", "you'll" |
| No Latin abbreviations | "e.g.," / "i.e.," | "for example" / "that is" |
| No "please" | "Please add the dependency" | "Add the dependency" |
| No minimizing language | "simply", "just", "easy", "straightforward" | Remove or rephrase |
| No weak callout phrases | "Note that", "keep in mind", "it's important to note" | Use `{{< alert info >}}` |
| Numbers under 10 spelled out in prose | "add 3 dependencies" | "add three dependencies" |
| Em dash not double hyphen | "foo -- bar" | "foo — bar" |
| Oxford comma | "X, Y and Z" | "X, Y, and Z" |
| "log in" is a verb; "login" is a noun/adjective | "login to the dashboard" | "log in to the dashboard" |
| Product names capitalized correctly | "gatling", "GEE" | "Gatling", "Gatling Enterprise Edition" |
