---
title: Transactions
seotitle: Gatling JDBC protocol reference - transactions, isolation levels, and read-only mode
description: How to run several JDBC statements in a transaction with Gatling, with begin, commit and rollback, isolation levels, and read-only mode.
lead: Learn how to run several statements in a transaction, and how Gatling keeps its connection in between.
date: 2026-11-04T00:00:00+00:00
---

## Understand how transactions work

Outside of a transaction, each statement borrows a connection in auto-commit mode, and gives it back once over.

A transaction keeps a connection for the virtual user:

- `begin()` borrows a connection and turns auto-commit off
- every statement of the virtual user then runs on this connection, without waiting for another one
- `commit()` or `rollback()` ends the transaction, and gives the connection back, even when they fail

`begin`, `commit`, and `rollback` are requests: Gatling reports them with their own name and response time.
The response time of `begin` includes waiting for a free connection.

## Run a transaction {#transaction}

Put the statements of the transaction between `begin` and `commit`:

{{< include-code "transaction" >}}

Use `rollback` to discard what the transaction did instead:

{{< include-code "rollback" >}}

Gatling fails these requests, without sending anything to the database:

- `begin` when a transaction is already open
- `commit` or `rollback` when no transaction is open

A statement failing inside a transaction leaves it open: it's up to the scenario to roll it back, for example with `tryMax` or `exitBlockOnFail`.

To measure the whole transaction, put it in a group:

{{< include-code "group" >}}

## Configure the transaction {#options}

`begin` accepts the following options, that Gatling sets on the connection before turning auto-commit off:

| **Option** | **JDBC equivalent** | **Description** |
| --- | --- | --- |
| `transactionIsolation(level)` | `Connection.setTransactionIsolation` | One of the `java.sql.Connection.TRANSACTION_*` constants. |
| `readOnly` | `Connection.setReadOnly` | A hint that the transaction doesn't write. Some drivers enforce it, others ignore it. |

{{< include-code "options" >}}

Gatling restores the previous isolation level and read-only mode of the connection when the transaction is over.
Databases don't support every isolation level, and some drivers ignore the read-only mode, see [database specifics]({{< ref "/reference/script/jdbc/databases#transactions" >}}).

## Understand what happens when a virtual user exits {#exit}

When a virtual user exits with a transaction still open, for example on `exitHere` or at the end of its scenario, Gatling rolls the transaction back and gives the connection back.
When the simulation ends, Gatling aborts the connections still borrowed, so the database rolls their transactions back.
Gatling never commits a transaction you didn't commit.

{{< alert warning >}}
A virtual user keeps its connection for the whole transaction, including its pauses.
Long pauses inside transactions make other virtual users wait for a connection, as they would in an application.
{{< /alert >}}
