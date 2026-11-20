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

Ask your assistant to convert a `.jmx` file, a folder, or a zip. For example, the repository of the
[Helm chart](https://github.com/gatling/helm-gatling-ai-server) and of each
[Terraform module]({{< ref "/ai/server/installation/terraform" >}}) holds a sample plan, `samples/EcommApp.zip`. From
a clone of the one your operator installed from, ask:

```text
Convert samples/EcommApp.zip to a Gatling simulation.
```

The sample holds the JMeter plan of an e-commerce site and the two CSV files it reads.

The assistant uploads the plan to the server, starts the conversion, and returns a summary with a link to download
the Gatling project.

You don't have to zip anything yourself. To convert several plans at once, or a plan with the files it reads, such as
CSV data files or scripts, point the assistant at the files or at their folder:

```text
Convert every .jmx file in tests/ to Gatling simulations.
```

The assistant zips the files, keeping their relative paths, uploads the zip once, and converts each plan from it, with
one project per plan. By default, a zip holds at most 256 entries and an upload is at most 10 MiB.

{{< alert info >}}
When the server refuses a plan, it tells the assistant to show you the refusal, and not to change the plan or convert
a changed copy without your consent.
{{< /alert >}}

## Choose the build tool

The server generates a Maven project by default. To get a Gradle project instead, ask for it, for example "convert it
as a Gradle project". The assistant passes the `build` argument, `maven` or `gradle`, to the conversion. When you ask
for the simulation file only, the server ignores the build tool.

## Ask for the simulation file only

By default, you download the whole Gatling project: the simulation, the files it reads, and the build with its
wrapper. To put the simulation in a Gatling project of your own instead, ask for the simulation file alone, for
example "convert it, simulation file only". The assistant passes the `packaging` argument to the conversion:

- `project`, the default: the whole Gatling project, in `gatling-project.zip`.
- `simulation_file_only`: the simulation's `.java` file alone, in a zip named after the simulation's class.

Either zip holds a `README.md` that says what the conversion did. With the simulation file only, the zip holds no build
and none of the files the simulation reads: the `README.md` lists the files the simulation needs, and says where to put
the simulation in your project.

## Choose who ports the code snippets

The converter translates the plan's elements without a model. The code snippets it can't translate, such as JSR223 or
BeanShell scripts, are ported to Java by a model, in one of two ways:

- **Endpoint (default)**

  The model endpoint your operator configured ports the snippets.

  Each snippet's code and the porting instructions go to that endpoint.

- **Caller**

  Your own AI assistant ports the snippets: it receives them in the conversion's result and ports them in the project
  you download.

  No snippet code goes to the model endpoint.

To use caller completion, ask for it, for example "convert it and port the snippets yourself". Your operator decides
which modes the server allows: a conversion that asks for a mode the server doesn't allow is refused.

## Answer the assistant's questions

Some snippets port only partially: their Java body compiles but keeps a `// FIXME` line, for an API Gatling has no
equivalent for, or a `// TODO` line, for what the scenario has to do. By default, the server asks no question: it keeps
the partial ports, and the plan's JDBC and TCP requests as plain Java.

To be asked instead, tell your assistant to convert with questions on (the tool's `elicitation` argument set to
`ask`). When your assistant supports MCP forms, the server then asks you, through a form, whether to keep each partial
port or to skip it and keep its stub, and whether to keep the JDBC and TCP requests as plain Java or to leave them out.
If you don't answer, the server keeps the partial ports and the plain Java requests. Your operator can turn the
questions off.

## Download the project

The download link works once, and expires 15 minutes after the server issues it, by default. Download the project before
then, or convert the plan again. When your operator turned the conversion history on, you can also
[ask for a new link](#find-an-earlier-conversion).

The project is a Maven or Gradle project with its wrapper, so you don't need Maven or Gradle installed: it needs Java
21 or later by default. Its `README.md` says what the conversion did:

- the source plan, the build tool, the packaging, who ported the code snippets, and the guardian's level;
- whether the project compiled;
- the code snippets ported, each marked "Ported by LLM" in its Javadoc, and those not converted, with the stub that
  takes their place;
- the conversion's warnings, gaps, and the elements of protocols no Gatling module sends;
- how to compile the project and run the simulation, or, for the simulation file only, the files it needs and where
  to put it.

Review every ported snippet before you trust it. The `README.md` names the plan's elements but holds none of their
code or values, so you can send it to Gatling support as it is.

## Run the simulation

Follow the "Build and run" section of the project's `README.md` to compile it and run the simulation locally. To run
it on Gatling Enterprise, package and deploy it as any Gatling project.

## Find an earlier conversion

{{< alert info >}}
The server keeps no history of your conversions unless your operator turns it on, as the
[configuration reference]({{< ref "/ai/server/configuration#keep-a-conversion-history" >}}) describes.
{{< /alert >}}

When the conversion history is on, the server keeps each conversion for 30 days by default, and your assistant gets
four more tools. Ask for what you need in plain language:

- **List your conversions**, for example "list my conversions from the last 7 days". The assistant calls
  `conversions.read_all`, which lists your conversions with their outcome, newest first. It can search them by name or
  id, and keep those of one source, one outcome, or one period.
- **Show one conversion**, for example "show me that conversion's snippets". The assistant calls
  `conversions.read_one`, which returns its code snippets, its simulation, its notes, its project's files, and its
  workflow steps.
- **Download a conversion again**, for example "give me a new download link for it". The assistant calls
  `conversions.create_download_link`, which returns a new one-time link, valid 15 minutes by default.
- **Delete a conversion**, for example "delete that conversion". The assistant calls `conversions.delete_one`, which
  deletes the conversion and everything kept with it: the uploaded file, the code snippets, and the conversion's notes.
  A conversion still running is stopped first. You can't undo a delete, and the server's audit trail records who
  deleted it.

You see only the conversions you started. The server answers about another user's conversion as if it didn't exist.

If the server couldn't issue a conversion's download link, because too many links were open, and the history is on,
the conversion's result tells your assistant to create the link later with `conversions.create_download_link`.

## Next steps

- [Understand what data leaves the server, and what it keeps]({{< ref "/ai/server/data-and-security" >}}).
