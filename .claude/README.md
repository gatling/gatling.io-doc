# Claude Code for Gatling Docs Contributors

This directory contains [Claude Code](https://docs.anthropic.com/en/docs/claude-code) configuration and [Agent Skills](https://agentskills.io) for the Gatling documentation repository.

The skills help you write, review, and scaffold documentation that follows our style guide and Hugo conventions — without having to memorize every rule.

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

Reviews an existing documentation page against the Gatling style guide.:

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

## Add or improve a skill
Skills follow the [Agent Skills format](https://agentskills.io/specification): a directory named after the skill containing a `SKILL.md` with YAML frontmatter and markdown instructions.

To add a new skill:

1. Create a directory in `.claude/skills/` matching the skill name (lowercase, hyphens only)
2. Add a `SKILL.md` with `name` and `description` frontmatter fields
3. Put detailed reference content in a `references/` subdirectory
4. Test it against a real doc page
5. Open a PR

To improve an existing skill, edit the relevant `SKILL.md` or reference file and open a PR. Skills are versioned with the repo, so all contributors get the update automatically.
