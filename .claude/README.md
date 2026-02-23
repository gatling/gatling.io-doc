# Claude Code for Gatling Docs Contributors

This directory contains [Claude Code](https://docs.anthropic.com/en/docs/claude-code) configuration and [Agent Skills](https://agentskills.io) for the Gatling documentation repository.

The skills help you write, review, and scaffold documentation that follows our style guide and Hugo conventions — without having to memorize every rule.

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

---

## Available skills

Skills live in `.claude/skills/` and follow the [Agent Skills standard](https://agentskills.io/specification). Each skill is a directory containing a `SKILL.md` file with metadata and instructions.

```
.claude/skills/
├── review-doc/
│   ├── SKILL.md
│   └── references/
│       └── style-guide.md
├── new-doc/
│   └── SKILL.md
└── update-doc/
    └── SKILL.md
```

### review-doc

Reviews an existing documentation page against the Gatling style guide. Checks:

- Complete and correct Hugo front matter
- Heading hierarchy and verb-led H2/H3 headers
- Active voice and present tense
- Code blocks missing context or using wrong language tags
- Hugo shortcode and internal link conventions
- UI screenshots that are expensive to maintain
- Opportunities to replace prose lists with markdown tables
- Google Style Guide violations

**Usage:** Ask Claude Code to review a file, referencing the `review-doc` skill or the path to `skills/review-doc/SKILL.md`.

The detailed style rules are in [skills/review-doc/references/style-guide.md](skills/review-doc/references/style-guide.md) and are loaded by the skill automatically.

---

### new-doc

Scaffolds a new documentation page with correct front matter, directory structure, and placeholder sections tailored to the page type (guide, reference, or tutorial).

**Usage:** Ask Claude Code to create a new doc page using the `new-doc` skill. It will ask for the title, doc type, location, and target SDK languages before generating.

---

### update-doc

Applies a specific change to an existing page while enforcing style compliance on the content it touches. Unlike `review-doc`, this skill edits — it doesn't just report. It fixes style issues opportunistically and surfaces any follow-up actions you need to take manually (such as updating the parent `_index.md` ordering or adding code sample files).

**Usage:** Ask Claude Code to update a file using the `update-doc` skill, describing what needs to change.

---

## Style guide summary

The [Google developer style guide](https://developers.google.com/style) applies by default. These are the Gatling-specific additions. The full rules are in [skills/review-doc/references/style-guide.md](skills/review-doc/references/style-guide.md).

### Structure

- One H1 per page — the `title` front matter field acts as the H1; don't add a `#` heading in the body
- Use H2 and H3 liberally to break up long sections
- Bullet points for unordered lists; numbered lists for sequential steps only

### Section headers

H2 and H3 headers must start with a verb:

| ❌ | ✅ |
| --- | --- |
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
- Correct language tags: `java`, `kotlin`, `scala`, `javascript`, `typescript`, `console`, `yaml`, `dockerfile`, `gradle`, `xml`
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
| --- | --- | --- |
| **Reference** | `content/reference/` | Describes how things work — not how to do them |
| **Guide** | `content/guides/` | Practical how-to for a specific use case |
| **Tutorial** | `content/tutorials/` | Learning-oriented, start-to-finish walkthrough |

When in doubt: if someone needs to do a specific task once, it's a guide. If they're learning from scratch, it's a tutorial. If they need to look something up, it's reference.

---

## Add or improve a skill

Skills follow the [Agent Skills format](https://agentskills.io/specification): a directory named after the skill containing a `SKILL.md` with YAML frontmatter and markdown instructions.

To add a new skill:

1. Create a directory in `.claude/skills/` matching the skill name (lowercase, hyphens only)
2. Add a `SKILL.md` with `name` and `description` frontmatter fields
3. Put detailed reference content in a `references/` subdirectory
4. Test it against a real doc page
5. Open a PR

To improve an existing skill, edit the relevant `SKILL.md` or reference file and open a PR. Skills are versioned with the repo, so all contributors get the update automatically.
