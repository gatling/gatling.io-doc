---
title: GraphQL Requests
seotitle: Gatling GraphQL protocol reference - queries and mutations
description: How to send GraphQL queries and mutations with Gatling, set variables, and perform checks on the response.
lead: Learn how to send GraphQL queries and mutations, and check their responses.
date: 2026-09-30T00:00:00+00:00
---

## Prerequisites

Gatling GraphQL SDK is not imported by default.

You have to manually add the following imports:

{{< include-code "imports" >}}

## Define an operation

Use the `graphql` object to define an operation.
The [protocol]({{< ref "/reference/script/graphql/protocol" >}}) must be registered.

Gatling parses the document when the Simulation is built.
It fails fast if the document is invalid or can't be found.

### Inline documents

Use `query` and `mutation` to define an operation with an inline document.
Gatling checks that the document declares the expected operation type.

{{< include-code "inline" >}}

### Files

Use `file` to load a document from a classpath resource, typically a `.graphql` file.
The operation type is whatever the document declares.

{{< include-code "file" >}}

If the document declares several operations, use `operationName` to pick the one to execute.

{{< include-code "operation-name" >}}

### Any document

Use `document` to define an operation with an inline document, whatever operation type it declares.

{{< include-code "document" >}}

### Dynamic documents {#dynamic-document}

Use `dynamicDocument` when the document is only known at runtime, for example to replay a corpus of captured operations from a feeder.
As Gatling can't parse the document when the Simulation is built, you must provide the request name.
This name is only used in the reports: Gatling doesn't send an `operationName` with a dynamic document.
Use `operationName`, with an Expression Language String or a function, to send one, for example when the document declares several operations.
Dynamic documents don't support operation type assertion, persisted queries, or `overGet`.

{{< include-code "dynamic-document" >}}

## Request name

Gatling reports a request under the name `<operation type> <operation name>`, for example `query GetUser`.
See [operation naming]({{< ref "/reference/script/graphql/protocol#operation-naming" >}}) for how to name anonymous operations.

Use `requestName` to set the name explicitly.

{{< include-code "request-name" >}}

## Variables

Use `variable` to set a variable.
The value can be a static value, a Gatling Expression Language String, or a function.

{{< include-code "variable" >}}

Use `variables` to set several variables at once, with a `Map` or a function that returns a `Map`.
Top level String values of a `Map` can contain Gatling Expression Language placeholders.

{{< include-code "variables" >}}

Use `variablesJson` to set all the variables at once, as a JSON object that can contain Gatling Expression Language placeholders.
The JSON is sent as is, so use `jsonStringify()` for a value that must be JSON escaped.
`variablesJson` can't be combined with `variable` or `variables`.

{{< include-code "variables-json" >}}

## HTTP request options

A GraphQL request supports the following HTTP options:

{{< include-code "options" >}}

* `endpoint`: override the protocol endpoint for this request only.
* `header` and `headers`: set headers.
* `requestTimeout`: override the request timeout.
* `silent` and `notSilent`: control whether the request is reported in the statistics.
* `ignoreProtocolChecks`: ignore the checks defined on the HTTP protocol.

### Send a query over GET

Use `overGet` to send a query with a `GET` request, with the `query`, `operationName` and `variables` as query string parameters.
This makes responses cacheable by a CDN.
Unlike the protocol `queriesOverGet`, `overGet` is strict:

* only queries can be sent this way, never mutations
* the document must be known when the Simulation is built, so use `query` or `file`, not `document`
* it can't be combined with `dynamicDocument` or persisted queries

{{< include-code "over-get" >}}

## Checks

Use `check` to perform checks on the response.
By default, Gatling checks the HTTP status and the [errors]({{< ref "/reference/script/graphql/protocol#error-handling" >}}) of the response.

In addition to the [usual HTTP checks]({{< ref "/reference/script/http/checks" >}}), Gatling provides `jsonPath` and `jmesPath` checks rooted at a top level member of the GraphQL response:

* `graphql.data` for the `data` member
* `graphql.errors` for the `errors` member
* `graphql.extensions` for the `extensions` member

For example, you can write `$.user.name` instead of `$.data.user.name`.
These are regular `jsonPath` and `jmesPath` checks otherwise, so you can use the same criteria and `saveAs`.

{{< include-code "checks" >}}

You can also apply a function on the Session resulting from the checks, after they've been applied, with `postCheck`.
Throw an exception to make the request fail with the exception's message (in Scala, the function returns a `Validation`: a `Success` wrapping the new Session, or a `Failure`).

{{< include-code "post-check" >}}
