---
name: review-doc
description: Reviews a Gatling documentation page against the team style guide. Use when writing, editing, or reviewing a docs page — checks front matter completeness, verb-led headings, active voice, present tense, code example quality, Hugo shortcode usage, screenshot guidance, markdown tables, and Google Style Guide compliance.
compatibility: Designed for Claude Code. Requires Read access to documentation files.
metadata:
  author: gatling
  version: "1.0"
allowed-tools: Read
---

# Review Doc

## How to use this skill
Read the file at the path provided by the user. If no path is given, ask for one before proceeding.

Then read the full [style guide](references/style-guide.md).

Apply every rule in the style guide to the file. Produce a structured review using the output format below. For each issue found, quote the **exact problematic text**, explain why it's an issue, and provide a corrected version.

## Output format
Structure your review with these sections. Use "✅ No issues found" for any section that's clean — don't skip sections.

### Front Matter
### Structure
### Section Headers
### Voice and Tense
### Code Examples
### Links and Shortcodes
### Screenshots
### Tables
### Google Style Guide

### Summary
End with a priority-ordered table of the most impactful changes, with a rough effort estimate (quick / medium / involved) for each:

| Priority | Issue | Effort |
| --- | --- | --- |
| 1 | ... | quick |
