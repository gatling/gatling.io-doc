---
title: JDBC
seotitle: Gatling JDBC protocol reference
description: Load test relational databases over JDBC with Gatling, with statements, prepared statements, stored procedures, batches, transactions, and checks.
lead: SDK to load test relational databases over JDBC, from plain SQL to stored procedures and transactions.
ordering:
  - setup
  - protocol
  - statements
  - checks
  - transactions
  - feeder
  - databases
---

{{< alert enterprise >}}
Enhanced usage of this feature is available with Gatling Enterprise. [Explore our plans to learn more](https://gatling.io/pricing?utm_source=docs).
{{< /alert >}}

[JDBC](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/package-summary.html) is the Java API to access relational databases.
With the Gatling JDBC plugin, you can load test any database that has a JDBC driver, such as PostgreSQL, MySQL, MariaDB, SQL Server, or Oracle Database.

Gatling supports:

- plain statements, prepared statements with bound parameters, and batches
- stored procedures, with OUT and INOUT parameters, and cursors
- transactions, with their isolation level
- checks on update counts, rows, column values, generated keys, and OUT parameters
- a feeder that loads its records from a database
