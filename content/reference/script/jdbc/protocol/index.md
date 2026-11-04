---
title: JDBC Protocol
seotitle: Gatling JDBC protocol reference - protocol configuration
description: How to configure the JDBC protocol in Gatling, including the database URL or DataSource, the connection pool, the timeouts, and what Gatling measures.
lead: Learn how to configure the JDBC protocol, and how Gatling pools connections and measures response times.
date: 2026-11-04T00:00:00+00:00
---

## Add the imports

The Gatling JDBC SDK isn't imported by default. Add the following imports:

{{< include-code "imports" >}}

## Configure the protocol

Use the `jdbc` object to create a JDBC protocol, then register it like any other protocol:

{{< include-code "protocol-sample" >}}

The protocol accepts the following options:

| **Option** | **Default** | **Description** |
| --- | --- | --- |
| `url` | | The JDBC URL of the database. Mutually exclusive with `dataSource`. |
| `dataSource` | | A `javax.sql.DataSource` to get connections from. Mutually exclusive with `url`. |
| `credentials` | none | The username and password. |
| `property` | none | A property passed to the driver when opening connections. Only with `url`. |
| `maxConnections` | `10` | The size of the connection pool, which is also the maximum number of statements running concurrently. |
| `loginTimeout` | the driver's | How long opening a connection may take. |
| `queryTimeout` | none | How long a statement may run. |
| `fetchSize` | the driver's | How many rows the driver fetches per round trip. |

### Pass driver properties

`property` passes a property to the JDBC driver when opening connections, along with the credentials, for example to enable TLS.
Refer to the documentation of your driver for the available properties.

{{< include-code "properties" >}}

### Get connections from a DataSource

`dataSource` makes Gatling get its connections from a `javax.sql.DataSource` instead of a URL, typically for drivers that need driver-specific settings.
Configure these settings on the `DataSource` itself: `property` is rejected with a `DataSource`.
When you set `credentials`, Gatling calls `getConnection(username, password)`, otherwise `getConnection()`.

{{< include-code "data-source" >}}

In JavaScript and TypeScript, `dataSource` takes a Java `DataSource` object, created with [`Java.type`](https://www.graalvm.org/latest/reference-manual/js/JavaInteroperability/) from a class provided by a [JVM dependency]({{< ref "/integrations/build-tools/js-cli#jvm-dependencies" >}}).

{{< alert warning >}}
Pass a plain `DataSource`, not a pooling one such as HikariCP: Gatling already pools the connections, as described below.
{{< /alert >}}

## Understand how Gatling pools connections {#pool}

Each protocol has its own pool of `maxConnections` connections, shared by all the virtual users of all the scenarios using this protocol.
Gatling opens all the connections when the simulation starts, so the first virtual users don't pay for it, and the simulation fails right away if the database can't be reached.

{{< include-code "pool" >}}

A virtual user borrows a connection for each statement, and gives it back once the statement is over.
When all the connections are borrowed, virtual users wait for one in order, without blocking any thread.
`maxConnections` is therefore the maximum number of statements running concurrently against the database, like the pool size of an application.

When the database or the network breaks a connection, the request using it fails, Gatling discards the connection, and the next request on this slot opens a new one.

Gatling runs JDBC calls, which are blocking, on one thread per connection, and processes the results, the checks included, on the virtual user's own thread, so slow checks never delay other requests.

{{< alert warning >}}
Gatling only restores what it changes on a connection itself: auto-commit, and the isolation level and read-only mode of [transactions]({{< ref "/reference/script/jdbc/transactions" >}}).
Session state changed with SQL, such as `SET NOCOUNT ON` on SQL Server, `SET TRANSACTION ISOLATION LEVEL`, or session variables, stays on the connection for the next virtual user that borrows it.
{{< /alert >}}

## Understand what Gatling measures {#response-time}

The response time of a JDBC request includes:

- waiting for a free connection, as an application would
- running the statement
- reading all the rows of all the results the statement produced, and every value of every row, as an application would

Checks then run on the results, outside of the response time.
Gatling only keeps the values of the rows in memory when a check needs them, so large results don't fill the memory of the load generator.

## Configure timeouts {#timeouts}

`loginTimeout` limits how long opening a connection may take, and `queryTimeout` limits how long a statement may run.
JDBC timeouts have a one second granularity, so Gatling rounds them up to the next second.

{{< include-code "timeouts" >}}

{{< alert warning >}}
With `url`, `loginTimeout` sets [`DriverManager.setLoginTimeout`](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/DriverManager.html#setLoginTimeout(int)), which applies to the whole JVM.
With `dataSource`, it sets the login timeout of this `DataSource`.
It's up to the driver to honor it: some drivers have their own connect timeout property, that you can set with `property`.
{{< /alert >}}

## Configure the fetch size {#fetch-size}

`fetchSize` sets how many rows the driver fetches per round trip, see [`Statement.setFetchSize`](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/Statement.html#setFetchSize(int)).

{{< include-code "fetch-size" >}}

Drivers handle it differently, so check the [database specifics]({{< ref "/reference/script/jdbc/databases#fetch-size" >}}) before setting it.
For example, a small fetch size makes Oracle Database perform many more round trips than its default.
