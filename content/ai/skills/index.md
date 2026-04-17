---
title: Claude Skills
description: Overview of the available Claude skills for Gatling.
lead: The Gatling AI extensions provides skills to help you deploy and run load tests on Gatling Enterprise directly from your IDE.
date: 2026-04-17T14:16:00+00:00
---

## Bootstrap a Gatling project {#bootstrap-project}

Skill: [`gatling-bootstrap-project`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-bootstrap-project/SKILL.md)

Create a new Gatling project from scratch by either the prompt or by using the following command inside Claude Code:

```console
/Gatling:gatling-bootstrap-project java maven
```

## Build tools {#build-tools}

Skill: [`gatling-build-tools`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-build-tools/SKILL.md)

Deploy and start Gatling tests on Gatling Enterprise using your project's build tool (Maven, Gradle, sbt, or the
JavaScript CLI). Detects the build tool, verifies prerequisites, runs the deploy command, and optionally starts a test
run.

## Configuration as Code {#configuration-as-code}

Skill: [`gatling-configuration-as-code`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-configuration-as-code/SKILL.md)

Generate or update the `.gatling/package.conf` package descriptor file for Gatling Enterprise deployments.
Collects simulation class names, queries your account for teams, packages, tests, and locations, then writes or updates
the configuration file following Configuration as Code rules.

## Convert a JMeter Test Plan to Gatling {#convert-from-jmeter}

Skill: [`gatling-convert-from-jmeter`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-convert-from-jmeter/SKILL.md)

{{< arcade id="XPlB8jfY8HEXNO59L3de" title="AI Converter JMeter to Gatling" >}}

The skill will generally be loaded automatically by the LLM when needed, based on your natural language queries, when trying to convert an [Apache JMeter™](https://jmeter.apache.org/) test.
In Claude Code, you can also explicitly invoke it, for instance:

```console
/Gatling:gatling-convert-from-jmeter
```

Using this skill, the LLM will look for JMeter files to convert, and for an existing Gatling project as a destination. It will also help you set up a new Gatling project if necessary.

Do not hesitate to give more context from the start if you want less back and forth with the LLM; for instance, if you want to convert the `Test Plan.jmx` file to a new Java/Maven Gatling project:

```console
/Gatling:gatling-convert-from-jmeter Test Plan.jmx java maven
```

You can download a [sample JMeter project](https://github.com/gatling/gatling-ai-extensions/raw/refs/heads/main/plugins/gatling/skills/gatling-convert-from-jmeter/samples/EcommApp.zip) to try out the skill:

```console
/Gatling:gatling-convert-from-jmeter EcommApp.zip java maven
```

## Convert a LoadRunner Script to Gatling {#convert-from-loadrunner}

Skill: [`gatling-convert-from-loadrunner`](https://github.com/gatling/gatling-ai-extensions/tree/main/plugins/gatling/skills/gatling-convert-from-loadrunner/SKILL.md)

{{< arcade id="m51KGGNMaycdbmNTQT6t" title="Turn Your LoadRunner Scripts Into Gatling" >}}

The skill will generally be loaded automatically by the LLM when needed, based on your natural language queries, when trying to convert a [LoadRunner](https://www.opentext.com/products/professional-performance-engineering) script.
In Claude Code, you can also explicitly invoke it, for instance:

```console
/Gatling:gatling-convert-from-loadrunner
```

Do not hesitate to give more context from the start if you want less back and forth with the LLM; for instance, if you want to convert a LoadRunner project to a new Java/Maven Gatling project:

```console
/Gatling:gatling-convert-from-loadrunner EcommApp.zip java maven
```

You can download a [sample LoadRunner project](https://github.com/gatling/gatling-ai-extensions/raw/refs/heads/main/plugins/gatling/skills/gatling-convert-from-loadrunner/samples/EcommApp.zip) to try out the skill.
