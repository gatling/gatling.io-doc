---
name: update-doc
description: Applies a specific change to an existing Gatling documentation page while enforcing style guide compliance on touched content. Use when making targeted edits — adding a section, updating prerequisites, rewriting for voice and tense, or updating code examples. Does not do a full style audit; use review-doc for that.
compatibility: Designed for Claude Code. Requires Read and Edit access to documentation files.
metadata:
  author: gatling
  version: "1.0"
allowed-tools: Read Edit
---

# Update Doc

## Step 1: Understand the request
You need two things from the user before proceeding:

1. **File path** — the page to edit (e.g., `content/guides/use-cases/kafka/index.md`)
2. **Change description** — what needs to change (e.g., "Update prerequisites to require Gatling 3.14+", "Add a SASL authentication section", "Rewrite the intro in active voice")

If either is missing, ask for it.

---

## Step 2: Read the file
Read the full file before making any edits. Identify:

- The section(s) affected by the change
- The surrounding context to preserve
- Any style violations **in the sections you're about to touch** — note these and fix them opportunistically

Do **not** audit the entire file. Only touch content you are directly modifying.

---

## Step 3: Apply the change
Make the requested edit. Apply these rules to any content you write or rewrite.

### Front matter
- If updating `title`, check whether `seotitle`, `description`, and `lead` are stale and update them too
- `seotitle` must differ from `title`
- `description` and `lead` must not be identical to each other or to `title`

### Structure
- New sections: H2 or H3 — never skip heading levels
- Bullet lists: use `-` not `*` or `+`
- Numbered lists: sequential steps only

### Section headers (H2 and H3 must start with a verb)

| ❌ | ✅ |
| --- | --- |
| `## Prerequisites` | `## Meet the prerequisites` |
| `## Configuration` | `## Configure the protocol` |
| `## Overview` | `## Understand how X works` |
| `## Next Steps` | `## Next steps` |

### Voice and tense
- Active voice: "Gatling sends the request" not "The request is sent"
- Present tense: "Add the dependency" not "You will need to add the dependency"
- Imperative for instructions: "Run the command" not "You must run the command"
- Remove: "simply", "just", "easy", "straightforward", "please", "note that"

### Code blocks
- Every code block needs a prose intro sentence before it
- Language tags: `java`, `kotlin`, `scala`, `javascript`, `typescript`, `console`, `yaml`, `dockerfile`, `gradle`, `xml`
  - XML goes in `xml` blocks, not `java`
  - Shell commands go in `console` blocks, not `bash`
- If adding code for one language on a multi-language page, add variants for all other languages the page already covers

### Hugo shortcodes
- Internal links: `{{< ref "/path/to/page" >}}` — never raw relative URLs
- Images: `{{< img src="file.webp" alt="Descriptive alt text" >}}`
- Callouts: `{{< alert info >}}` / `{{< alert warning >}}` / `{{< alert tip >}}`

### Tables
- When adding a list of items with consistent attributes (options, parameters, functions), use a markdown table instead of bullets

### Screenshots
- Do not add screenshots of Gatling Enterprise UI or third-party UIs — describe behavior in prose instead

---

## Step 4: Output the result
Show only the changed sections, not the entire file — unless the change is small enough that the full file is cleaner.

Use this structure:

**What changed:** One sentence.

**Updated content:**

```markdown
[Modified section(s) with enough surrounding context to locate them in the file]
```

**Style fixes applied:** Bullet list of opportunistic fixes made to surrounding content. Omit if none.

**Action required:** Any follow-up the user needs to handle (e.g., "Add this slug to the `ordering:` list in the parent `_index.md`", "Create Java and Kotlin code files for the new `{{< include-code >}}` shortcode").
