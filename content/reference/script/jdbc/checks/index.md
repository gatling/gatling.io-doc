---
title: Checks
seotitle: Gatling JDBC protocol reference - checks on rows, update counts, generated keys, and OUT parameters
description: How to check the results of JDBC statements with Gatling, including update counts, row counts, column values, whole rows, generated keys, and OUT parameters.
lead: Learn how to check what your statements produced, and save values for the next requests.
date: 2026-11-04T00:00:00+00:00
---

## Understand JDBC checks

JDBC checks are regular Gatling [checks]({{< ref "/concepts/checks" >}}): they support `find`, `findAll`, `count`, `saveAs`, `transform`, and the usual validators such as `is` and `gt`.
They run once the statement succeeded. A failing check makes the request fail.

A statement can produce several results, such as a script or a stored procedure. Each check reads the first result of its kind:

| **Check** | **Type** | **Extracts** |
| --- | --- | --- |
| `updateCount` | multiple | The update counts, the first one with `find`. A batch produces one per row. |
| `rowCount` | single | The number of rows of the first result set. |
| `column(name)`, `column(index)` | multiple | The values of a column of the first result set, one per row. |
| `rows` | single | All the rows of the first result set, each one as a Map of the column labels to the values. |
| `generatedKey`, `generatedKey(name)`, `generatedKey(index)` | multiple | The values of a column of the generated keys, one per inserted row. |
| `outValue(index)` | single | The value of an OUT or INOUT parameter. |

Gatling only keeps the values of the rows in memory when a `column` or `rows` check needs them.
Values are the ones the driver returns with `getObject`, whose Java types depend on the driver, see [database specifics]({{< ref "/reference/script/jdbc/databases" >}}).

## Check update counts {#update-count}

`updateCount` extracts the number of rows the statement affected, for example with `INSERT`, `UPDATE`, and `DELETE`:

{{< include-code "update-count" >}}

Some drivers report that a batch entry succeeded without telling how many rows it affected, with `Statement.SUCCESS_NO_INFO`.
`updateCount.count` counts them, but `find` and `findAll` fail on them, as they have no count.

## Check the number of rows {#row-count}

`rowCount` extracts the number of rows of the first result set:

{{< include-code "row-count" >}}

## Check column values {#column}

`column` extracts the values of a column of the first result set, one per row, by name or by 1-based index.
The name is a Gatling Expression Language String.
Like JDBC's `findColumn`, Gatling matches names exactly first, then ignoring case, as databases don't agree on the case of unquoted names.
A wrong name fails with the list of the available columns.

{{< include-code "column" >}}

## Check whole rows {#rows}

`rows` extracts all the rows of the first result set, each one as a Map of the column labels to the values, in the column order.
Save them to iterate over them with `foreach`:

{{< include-code "rows" >}}

## Check generated keys {#generated-key}

`generatedKey` extracts the values of a column of the keys the database generated, one per inserted row.
Without argument, it extracts the first column.
It requires the statement to run with [`returnGeneratedKeys`]({{< ref "/reference/script/jdbc/statements#generated-keys" >}}).

{{< include-code "generated-key" >}}

## Check OUT parameters {#out-value}

`outValue` extracts the value of the OUT or INOUT parameter at the given 1-based index of a [callable statement]({{< ref "/reference/script/jdbc/statements#callable-statement" >}}):

{{< include-code "out-value" >}}

## Apply checks conditionally {#check-if}

`checkIf` only applies checks when a condition holds:

{{< include-code "check-if" >}}
