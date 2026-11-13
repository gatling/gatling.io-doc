---
title: Data and security in Gatling AI Server
menutitle: Data and security
seotitle: Gatling AI Server data handling, what leaves the server and what it keeps
description: Understand what Gatling AI Server sends to Gatling's sign-in service and to your model endpoint, what it keeps, what it logs, and how it screens code.
lead: Know what data leaves Gatling AI Server, where it goes, and what the server keeps.
date: 2026-11-13T00:00:00+00:00
---

Gatling AI Server runs on your infrastructure, so your test plans and the generated projects stay with you. This page
describes the two services the server calls, and what it keeps.

## Understand what goes to Gatling's sign-in service

Your users sign in on Gatling's sign-in service, `auth.gatling.io`, with their Gatling Enterprise account. Their AI
assistant then sends the access token it receives with every request to the server.

The server validates each token itself: it reads the sign-in service's discovery document and public keys (JWKS), and
checks the token's signature against those keys, its issuer, its audience `gatling-ai-server`, and its scope. It sends no plan, no code, and no token to the
sign-in service, and doesn't call Gatling Enterprise. It needs no Gatling Enterprise API token. Any Gatling Enterprise
account can sign in, so the users who can reach your server are the ones your network lets through.

Every Gatling AI Server checks the same audience, `gatling-ai-server`. A token issued for one installation is
accepted by any other, so the operator of a server can see tokens that another installation would accept: have your
users connect only to servers your organization runs.

The server never forwards a user's token to another service.

## Understand what goes to your model endpoint

Under endpoint completion, the default, the server sends your model endpoint one request per code snippet to port,
and one per repair of a snippet that doesn't compile. It asks once more for a snippet whose answer it can't use: an
answer it can't read, with no body, or with a body it refuses. A request that times out, can't connect, or gets an HTTP
408, 429, or 5xx status is sent again, up to `GATLING_MODEL_ENDPOINT_MAX_RETRIES` times, 2 by default. A request
holds:

- the snippet's code, its language, the element it comes from, and the element's path in the plan;
- the session and global variables the snippet can read, and the signature of the Java method to write;
- the porting instructions, for Gatling and for the snippet's language;
- for a repair, the body that didn't compile and its compile errors;
- for a second request, a fixed message that says why the first answer couldn't be used, without the answer.

The server never sends the whole plan to the model endpoint. The rest of the plan is converted without a model.

To send no code to the model endpoint at all, use caller completion: the user's own AI assistant ports the snippets,
so they go to that assistant's model provider instead.
Your operator can enforce it by leaving `endpoint` out of `GATLING_CONVERSION_COMPLETION_MODES`, as the
[configuration reference]({{< ref "/ai/server/configuration/conversions-and-limits#configure-the-conversions" >}})
describes.

## Understand what the server keeps

The server keeps nothing on a volume: a new pod, task, revision or instance starts empty. Until then, it keeps:

- **Uploads and generated projects**, in memory, for at most one day (`GATLING_STORE_INMEMORY_FILE_TTL`). An upload
  URL accepts its upload for 15 minutes, and a download link works once, for 15 minutes, by default.
- **Ended conversions**, readable for one day (`GATLING_STORE_INMEMORY_TASK_RETENTION`).
- **The model token count** of the current quota window, one day by default, on the container's own disk. A new pod,
  task, revision or instance starts it over.
- **The audit trail**, one record per tool call, on the container's own disk with the token count: the time, the user's
  ID and scopes from their token, the tool, the conversion's ID, whether the server allowed the call, and the error when
  it failed. It never holds the call's arguments. By default, it keeps a record for 7 days and the latest 10,000
  records, as [Telemetry and logs]({{< ref "/ai/server/configuration/telemetry-and-logs#keep-the-audit-trail" >}})
  describes. A new pod, task, revision or instance starts it over.

The server keeps neither the plans nor the generated code on disk once a conversion is done.

## Understand what the server logs

The server writes its log on its output. For each tool call, it writes one line in the category
`io.gatling.ai.audit`, with the tool, whether the server allowed the call, and the kind of error when the call fails.
The line never holds the token, the call's arguments, or the plan's content. It names the user only at the debug
level.

## Understand the guardian

A test plan is untrusted input: its text could address an AI model, or hide characters, to make the model write
something else than a port. The guardian screens the plan text of every code snippet before any model reads it, and
every ported body before the server puts it in the project:

- A ported body the guardian refuses keeps its stub, at every level.
- At the `standard` level, the default, a flagged snippet is still ported, and the conversion's result and
  `README.md` name it.
- At the `strict` level, a flagged snippet keeps its stub, and no model is asked to port it.

The operator sets the level with `GATLING_GUARDIAN_STRICTNESS`, and a conversion can only raise it.

## Understand what reaches your users

Tool results and error messages reach the user's AI assistant, and through it the assistant's model provider. They
hold no token, no secret, and no stack trace: a short cause and a correlation reference, with the details in the
server's log. The server's log never holds a token, an `Authorization` header, or the plan's contents.
