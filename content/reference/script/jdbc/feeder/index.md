---
title: JDBC Feeder
seotitle: Gatling JDBC feeder reference - load feeder records from a database
description: How to load the records of a Gatling feeder from a database with a SQL query, with the Gatling JDBC plugin.
lead: Learn how to load the records of a feeder from your database with a SQL query.
date: 2026-11-04T00:00:00+00:00
---

## Understand the JDBC feeder

The JDBC feeder runs a SQL query when the simulation starts, and loads the rows it returns as feeder records: each row is a record, whose keys are the column labels.
It's part of the Gatling JDBC plugin: add its dependency and the JDBC driver of your database, as described in [project setup]({{< ref "/reference/script/jdbc/setup" >}}).
The usage limits of the Community Edition don't apply to the feeder.

{{< alert info >}}
Up to Gatling 3.16.0, the JDBC feeder shipped with Gatling. Since Gatling 3.16.1, it's part of the Gatling JDBC plugin, with the same API and imports.
{{< /alert >}}

## Create a JDBC feeder {#jdbc-feeder}

Add the following imports:

{{< include-code "imports" >}}

Then create the feeder with the JDBC URL of the database, the credentials, and the SQL query:

{{< include-code "jdbc-feeder" >}}

Like file-based feeders, it loads all its records in memory, and supports the same [strategies]({{< ref "/concepts/session/feeders" >}}), such as `circular` or `random`.

{{< alert tip >}}
Column labels become the keys of the records, and some databases change their case, for example Oracle Database turns unquoted names into upper case.
Use aliases in your query, such as `SELECT user_id AS "userId"`, to control them.
{{< /alert >}}
