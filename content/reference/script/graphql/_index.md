---
title: GraphQL
seotitle: Gatling GraphQL protocol reference
description: GraphQL protocol description language
lead: SDK for GraphQL queries, mutations and subscriptions.
ordering:
  - setup
  - protocol
  - requests
  - subscriptions
---

{{< alert enterprise >}}
Enhanced usage of this feature is available with Gatling Enterprise. [Explore our plans to learn more](https://gatling.io/pricing?utm_source=docs).
{{< /alert >}}

[GraphQL](https://graphql.org/) is a query language for APIs. Gatling supports:

* queries and mutations, sent over HTTP
* subscriptions, sent over WebSocket with the `graphql-transport-ws` protocol
