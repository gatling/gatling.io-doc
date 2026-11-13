---
title: Convert a JMeter test plan with Gatling AI Server
menutitle: Convert
seotitle: Convert a JMeter test plan to a Gatling Java project from your AI assistant
description: Ask your AI assistant to convert a JMeter test plan with Gatling AI Server, then build and run the Gatling project you download.
lead: Hand a JMeter test plan to your AI assistant and get back a Gatling Java project the server checked by compiling it.
date: 2026-11-13T00:00:00+00:00
---

Once your AI assistant is [connected to Gatling AI Server]({{< ref "/ai/server/connect" >}}), you convert a plan by
asking for it in plain language.

## Ask for a conversion

Ask your assistant to convert a `.jmx` file, for example:

```text
Convert examples/computer-database.jmx to a Gatling simulation.
```

The assistant uploads the plan to the server, starts the conversion, and returns a summary with a link to download
the Gatling project.

To convert a plan that reads other files, such as CSV data files or scripts, or several plans of one folder, ask the
assistant to convert the folder. It zips the folder once, keeping its relative paths, and converts each plan from the
same upload. By default, a zip holds at most 256 entries and an upload is at most 10 MiB.

{{< alert info >}}
When the server refuses a plan, it tells the assistant to show you the refusal, and not to change the plan or convert
a changed copy without your consent.
{{< /alert >}}

## Choose the build tool

The server generates a Maven project by default. To get a Gradle project instead, ask for it, for example "convert it
as a Gradle project". The assistant passes the `build` argument, `maven` or `gradle`, to the conversion.

## Choose who ports the code snippets

The converter translates the plan's elements without a model. The code snippets it can't translate, such as JSR223 or
BeanShell scripts, are ported to Java by a model, in one of two ways:

| **Completion** | **Who ports the snippets** | **What leaves the server** |
| --- | --- | --- |
| Endpoint (default) | The model endpoint your operator configured. | Each snippet's code and the porting instructions go to that endpoint. |
| Caller | Your own AI assistant, which receives the snippets in the conversion's result and ports them in the project you download. | No snippet code goes to the model endpoint. |

To use caller completion, ask for it, for example "convert it and port the snippets yourself". Your operator decides
which modes the server allows: a conversion that asks for a mode the server doesn't allow is refused.

## Answer the assistant's questions

Some snippets port only partially: their Java body compiles but keeps a `// FIXME` line, for an API Gatling has no
equivalent for, or a `// TODO` line, for what the scenario has to do. When your assistant supports MCP forms, the
server may ask you, through a form, whether to keep each partial port or to skip it and keep its stub instead. It may
also ask whether to keep the plan's JDBC and TCP requests as plain Java or to leave them out.

If you don't answer, the server keeps the partial ports and the plain Java requests. Your operator can turn the
questions off.

## Download the project

The download link works once, and expires 15 minutes after the server issues it, by default. Download the project before
then, or convert the plan again.

The project is a Maven or Gradle project with its wrapper, so you don't need Maven or Gradle installed: it needs Java
21 or later by default. Its `README.md` says what the conversion did:

- the source plan, the build tool, who ported the code snippets, and the guardian's level;
- whether the project compiled;
- the code snippets ported, each marked "Ported by LLM" in its Javadoc, and those not converted, with the stub that
  takes their place;
- the conversion's warnings, gaps, and the elements of protocols no Gatling module sends;
- how to compile the project and run the simulation.

Review every ported snippet before you trust it. The `README.md` names the plan's elements but holds none of their
code or values, so you can send it to Gatling support as it is.

{{< alert info >}}
Your operator can set the server to download the simulation's `.java` file alone. In that case, when a snippet wasn't
converted or the simulation needs a file the download leaves out, you get a zip named after the simulation's class
that holds the `.java` file and the `README.md`.
{{< /alert >}}

## Run the simulation

Follow the "Build and run" section of the project's `README.md` to compile it and run the simulation locally. To run
it on Gatling Enterprise, package and deploy it as any Gatling project.

## Next steps

- [Understand what data leaves the server]({{< ref "/ai/server/data-and-security" >}}).
