# Claude Code for Gatling Docs Contributors

This directory contains [Claude Code](https://docs.anthropic.com/en/docs/claude-code) configuration for the Gatling documentation repository. Claude Code is a CLI tool that brings AI assistance directly into your terminal and editor.

The skills here help you write, review, and scaffold documentation that follows our style guide and Hugo conventions — without having to memorize every rule.

---

## Getting started

### Install Claude Code

```console
npm install -g @anthropic/claude-code
```

Then authenticate:

```console
claude
```

### Run Claude Code in this repo

From the repo root:

```console
claude
```

Claude Code automatically loads the skills in this directory when you're working in the project.

---

## Available skills

Invoke skills with a `/` prefix inside any Claude Code session.

### `/review-doc`

Reviews an existing documentation page against the Gatling style guide. Checks for:

- Complete and correct Hugo front matter
- Structural issues (heading hierarchy, bullet vs. numbered lists)
- Section headers that start with verbs
- Active voice and present tense
- Code blocks that lack context or have wrong language tags
- Misuse of Hugo shortcodes and raw internal links
- Screenshots of UIs that are expensive to maintain
- Opportunities to use markdown tables
- Google Style Guide violations

**Usage:**

```
/review-doc content/guides/use-cases/kafka/index.md
```

Or open a file in your editor and run `/review-doc` without arguments — Claude Code will use the open file.

---

### `/new-doc`

Scaffolds a new documentation page with correct front matter, structure, and placeholder sections based on the doc type (guide, reference, or tutorial).

**Usage:**

```
/new-doc
```

Claude Code will ask you a few questions (title, doc type, location, languages) and generate a ready-to-edit `index.md` file.

---

### `/update-doc`

Applies a specific change to an existing page while keeping the surrounding content consistent with the style guide. Unlike `/review-doc`, this skill edits — it doesn't just report.

It fixes style issues opportunistically (only in content it touches) and tells you about any follow-up actions needed, such as updating the parent `_index.md` ordering or adding code sample files.

**Usage:**

```
/update-doc content/guides/use-cases/kafka/index.md "Update prerequisites to require Gatling 3.14+"
```

Or describe the change interactively after invoking the skill.

---

## Style guide summary

These are the core rules both skills enforce. See below for details; the [Google developer style guide](https://developers.google.com/style) covers everything not listed here.

### Structure

- One H1 per page (the `title` front matter field acts as the H1 — don't add a `#` heading in the body)
- Use H2 and H3 liberally to break up long sections
- Bullet points for unordered lists; numbered lists only for sequential steps

### Section headers

H2 and H3 headers must start with a verb:

| ❌ | ✅ |
|---|---|
| `## Prerequisites` | `## Meet the prerequisites` |
| `## Setup` | `## Set up your environment` |
| `## Configuration` | `## Configure the protocol` |
| `## Overview` | `## Understand how X works` |
| `## Conclusion` | `## Next steps` |

### Voice and tense

- Present tense: "Add the dependency" not "You will need to add the dependency"
- Active voice: "Gatling sends messages" not "Messages are sent"
- Imperative for instructions: "Run the simulation" not "Next, you must run the simulation"

### Code examples

- Every code block needs an intro sentence before it
- Code must be runnable or have clearly labeled placeholders
- Use the correct language tag (`java`, `kotlin`, `scala`, `javascript`, `console`, `yaml`, `dockerfile`)
- Internal code samples belong in a `code/` subdirectory and are included with `{{< include-code "sectionName" >}}`

### Hugo conventions

- Internal links: `{{< ref "/reference/script/http" >}}` — not raw relative URLs
- Images: `{{< img src="file.webp" alt="Descriptive alt text" >}}`
- Callouts: `{{< alert info >}}` / `{{< alert warning >}}` / `{{< alert tip >}}`

### Front matter

Every page needs:

```yaml
---
title: Short nav title
seotitle: Longer, keyword-rich SEO title
description: One-sentence description for meta tags
lead: Engaging first sentence shown at the top of the page
---
```

Section index pages (`_index.md`) also need an `ordering:` list.

---

## Content types

| Type | Location | Purpose |
|---|---|---|
| **Reference** | `content/reference/` | Describes how things work — not how to do them |
| **Guide** | `content/guides/` | Practical how-to for a specific use case |
| **Tutorial** | `content/tutorials/` | Learning-oriented, start-to-finish walkthrough |

When in doubt about which type to use: if someone needs to do a specific task once, it's a guide. If they're learning a new skill from scratch, it's a tutorial. If they need to look something up, it's reference.

---

## Adding or improving skills

Skills are markdown files in `.claude/commands/`. Each file is a prompt that Claude Code uses when you invoke the corresponding `/skill-name` command.

To add a new skill:

1. Create a new `.md` file in `.claude/commands/`
2. Write clear instructions at the top, then the detailed rules below
3. Define the expected output format at the end
4. Test it on a real doc page
5. Open a PR with your changes

To improve an existing skill, edit the relevant file and open a PR. Skills are versioned with the repo, so everyone on the team gets the update automatically.
