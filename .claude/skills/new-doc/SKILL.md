---
name: new-doc
description: Scaffolds a new Gatling documentation page with correct Hugo front matter, directory structure, and placeholder sections based on the page type. Use when creating a new guide, reference page, or tutorial from scratch.
compatibility: Designed for Claude Code. Requires Read and Write access to documentation files.
metadata:
  author: gatling
  version: "1.0"
allowed-tools: Read Write
---

# New Doc

## Step 1: Gather information
Ask the user for the following if not already provided:

1. **Doc type** — `guide`, `reference`, or `tutorial`
2. **Title** — short human-readable title (for navigation)
3. **Purpose** — one sentence: what does this page help the user do or understand?
4. **Parent path** — where in `content/` should this live? (e.g., `content/guides/use-cases/`)
5. **Slug** — the directory name / URL segment (lowercase, hyphenated, e.g., `load-test-rabbitmq`)
6. **Languages** — which SDK languages does this page cover? (Java, Kotlin, Scala, JavaScript, TypeScript — or "all JVM", "all", etc.)

If the user provides a rough description, infer reasonable answers and confirm before generating.

---

## Step 2: Generate front matter
Fill in this template from the gathered information. The `seotitle` must be more keyword-rich than `title`. The `lead` must be an engaging first sentence — not a copy of `description` and not identical to `title`.

```yaml
---
title: <Short nav title>
seotitle: <Keyword-rich SEO title — different from title>
description: <One sentence for meta tags and search>
lead: <Engaging first sentence shown at the top of the page>
date: <today's date in RFC3339, e.g. 2025-06-10T00:00:00+00:00>
---
```

For **section index pages** (`_index.md`), also include an `ordering:` list with the expected child slugs:

```yaml
ordering:
  - child-slug-one
  - child-slug-two
```

---

## Step 3: Scaffold by doc type

### Guide
Guides are practical how-to articles. They are actionable and step-by-step.

```markdown
## Meet the prerequisites
Before you begin, make sure you have the following:

- Gatling [version] or later
- [Other requirement]

## Understand how [topic] works
[One or two short paragraphs explaining the relevant concept — enough context without being a full reference.]

## Set up [first major step]
[Intro sentence explaining what this step accomplishes.]

[Code block]

## Configure [second major step]
[Intro sentence.]

[Code block]

## Run the simulation
[Intro sentence.]

[Code block]

## Next steps
- [Link to related reference page]
- [Link to related tutorial or guide]
```

### Reference
Reference pages describe how things work. They do **not** walk through steps. Use tables for options, parameters, and API members.

```markdown
## Understand [core concept]
[Declarative description: "The X does Y." Not instructions.]

## Configure [thing]
[Description of configuration options followed by a table:]

| **Option** | **Type** | **Default** | **Description** |
| --- | --- | --- | --- |
| `optionName` | `String` | `""` | What this controls. |

{{< include-code "sectionName" >}}

## [Additional concept]
[Description.]
```

### Tutorial
Tutorials are learning-oriented, start-to-finish. Explain every step with full context — assume the user may be new to the technology.

```markdown
## What you'll learn
By the end of this tutorial, you'll be able to:

- [Learning objective 1]
- [Learning objective 2]

## Meet the prerequisites
- Gatling [version] or later
- [Other requirement]

## Understand [foundational concept]
[Explain the concept before asking the user to do anything.]

## Set up your project
[Step-by-step with full context for each action.]

## Write your first [thing]
[Step-by-step. Explain what each part of the code does.]

{{< include-code "sectionName" >}}

## Run and interpret the results
[What should the user see? What does it mean?]

## Next steps
- [What to try next]
- [Related guide or reference]
```

---

## Step 4: Remind about conventions
After generating the scaffold, tell the user:

- **Code files** live in a `code/` subdirectory next to `index.md`
- **Include code** with: `{{< include-code "sectionName" >}}`
- **Section markers** in code files: `//# sectionName` to open, `//#` to close
- **Internal links** must use `{{< ref "/path/to/page" >}}`, not raw relative URLs
- If the parent section's `_index.md` needs updating, show the line to add to its `ordering:` list

Expected directory layout:

```
content/guides/use-cases/my-new-guide/
├── index.md
└── code/
    ├── MyNewGuideSampleJava.java
    ├── MyNewGuideSampleKotlin.kt
    ├── MyNewGuideExampleScala.scala
    └── MyNewGuideExampleJS.ts

```

---

## Step 5: Output
Produce the complete `index.md` content, ready to paste. Then state the recommended file path, for example:

```
content/guides/use-cases/my-new-guide/index.md
```
