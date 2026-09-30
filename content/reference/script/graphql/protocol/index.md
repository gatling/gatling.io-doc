---
title: GraphQL Protocol
seotitle: Gatling GraphQL protocol reference - protocol configuration
description: How to configure the GraphQL protocol in Gatling, including endpoints, error handling, operation naming and persisted queries.
lead: Learn how to configure the GraphQL protocol.
date: 2026-09-30T00:00:00+00:00
---

## Prerequisites

The Gatling GraphQL SDK is not imported by default.

You have to manually add the following imports:

{{< include-code "imports" java kt scala >}}

## Configure the protocol

GraphQL runs on top of the HTTP protocol, which configures the transport: base URL, headers, TLS, proxy, etc.
You must register both the HTTP protocol and the GraphQL protocol.

Use the `graphql` object to create a GraphQL protocol.

{{< include-code "protocol-sample" java kt scala >}}

## Endpoints

`endpoint` defines the path GraphQL operations are sent to.
It is resolved against the HTTP protocol `baseUrl` and defaults to `/graphql`.

`wsEndpoint` defines the path subscriptions open a WebSocket to.
It is resolved against the HTTP protocol `wsBaseUrl` and defaults to `/graphql`.

Both accept a Gatling Expression Language String or a function.
You can also override the endpoint on each [request]({{< ref "/reference/script/graphql/requests" >}}).

## Error handling

GraphQL servers usually answer with a `200` status, even when the operation failed.
Gatling therefore inspects the `errors` member of the response.

{{< include-code "error-policy" java kt scala >}}

* `failOnErrors` (default): fail the request as soon as the response contains at least one error, even if `data` is partially populated.
* `failOnDataNull`: only fail the request when the response contains errors and no data at all, that is to say tolerate partial results.
* `ignoreErrors`: don't check the `errors` member, only the HTTP status.

The error policy also applies to the messages of [subscriptions]({{< ref "/reference/script/graphql/subscriptions" >}}).

## Operation naming

Gatling reports a request under the name `<operation type> <operation name>`, for example `query GetUser`.

Anonymous operations, such as `{ me { id } }`, don't have a name.
By default, Gatling names them after the name of the file they were loaded from, then after their root fields, for example `query me+orders`.
If it can't infer any name, Simulation fails on start.

You can pick another strategy, or make sure you never rely on inference:

{{< include-code "naming" java kt scala >}}

* `requireNamedOperations`: fail on anonymous operations when the Simulation is built.
* File name: `queries/getUser.graphql` gives `getUser`.
* Root fields: `{ me { id } orders { id } }` gives `me+orders`.
* Hash: the first characters of the SHA-256 of the document. The name is stable across runs, and identical documents share the same entry.
* Custom: a function that takes the document and returns a name, or nothing to make the Simulation fail on this operation.

You can always set the name of a given request explicitly with `requestName`.
Gatling refuses to report two different documents under the same name.

## Queries over GET

By default, Gatling sends operations with a `POST` request and a JSON body.
`queriesOverGet` makes Gatling send queries with a `GET` request instead, with the `query`, `operationName` and `variables` as query string parameters.
This makes responses cacheable by a CDN.

{{< include-code "queries-over-get" java kt scala >}}

This only applies to queries whose document is known when the Simulation is built.
Mutations and requests with a [dynamic document]({{< ref "/reference/script/graphql/requests#dynamic-document" >}}) are still sent with a `POST` request.

## Automatic Persisted Queries

With [Automatic Persisted Queries](https://www.apollographql.com/docs/apollo-server/performance/apq) (APQ), a client identifies a document with its SHA-256 hash instead of sending it over and over.

{{< include-code "apq" java kt scala >}}

With `automaticPersistedQueries`, Gatling sends the hash alone as a `POST` request once the server is known to have registered it, and the full document otherwise.
If the server answers that it doesn't know the hash (`PersistedQueryNotFound`), Gatling sends the document.
If the server answers that it doesn't support persisted queries at all (`PersistedQueryNotSupported`), Gatling sends the document and stops sending hashes.

With `automaticPersistedQueriesOverGet`, Gatling sends the hash alone as a `GET` request, and falls back to a `POST` request with the document if the server doesn't know the hash yet.
This makes responses cacheable by a CDN.

{{< alert warning >}}
`automaticPersistedQueriesOverGet` can't fall back to plain requests against a server that doesn't support persisted queries at all: every request would probe, then retry.
Only use it against a server that supports persisted queries.
{{< /alert >}}

By default, every virtual user finds out on its own what the server knows.
`sharePersistedQueries` makes all virtual users share this knowledge.
Use it to simulate server to server traffic, where a handful of long lived clients are behind all the requests.

Persisted queries require a document known when the Simulation is built, so they can't be used with a [dynamic document]({{< ref "/reference/script/graphql/requests#dynamic-document" >}}).
