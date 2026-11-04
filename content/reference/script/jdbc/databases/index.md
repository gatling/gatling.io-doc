---
title: Database Specifics
seotitle: Gatling JDBC protocol reference - PostgreSQL, MySQL, MariaDB, SQL Server, and Oracle Database specifics
description: How PostgreSQL, MySQL, MariaDB, SQL Server, and Oracle Database and their JDBC drivers behave differently with the Gatling JDBC protocol, and how to write tests that work with each of them.
lead: Learn how the main databases and their JDBC drivers differ, and how to write tests that behave as expected with each of them.
date: 2026-11-04T00:00:00+00:00
---

## Understand why databases differ

JDBC standardizes the API, not the behavior: drivers differ in how they convert values, return generated keys, fetch rows, or handle transactions.
This page lists the differences that matter for load tests, as observed with:

| **Database** | **Version** | **Driver** |
| --- | --- | --- |
| PostgreSQL | 18 | `org.postgresql:postgresql` 42.7 |
| MySQL | 9.7 | `com.mysql:mysql-connector-j` 26.10 |
| MariaDB | 12.3 | `org.mariadb.jdbc:mariadb-java-client` 4.0 |
| SQL Server | 2022 | `com.microsoft.sqlserver:mssql-jdbc` 13.6 |
| Oracle Database | 23 | `com.oracle.database.jdbc:ojdbc11` 23.26 |

Other versions may behave differently.

## Bind parameters {#params}

Databases don't all accept a String for a non-text column.
Use [typed parameters]({{< ref "/reference/script/jdbc/statements#typed-params" >}}) with `param(value, jdbcType)`, so that Gatling converts the values the same way whatever the database.

| **Database** | **Untyped String for a numeric or date column** | **Untyped NULL** |
| --- | --- | --- |
| PostgreSQL | Rejected | Accepted |
| MySQL, MariaDB | Converted by the server | Accepted |
| SQL Server | Converted by the server | Rejected for `DATE` and `DATETIME2` columns |
| Oracle Database | Depends on the session's NLS settings, derived from the JVM locale, for decimals and dates | Accepted |

On SQL Server, bind NULLs for date and time columns with a type, such as `param(null, JDBCType.DATE)`.

## Read values {#values}

Checks extract the values the driver returns with `getObject`, so their Java type depends on the driver:

| **Database** | **Notable Java types** |
| --- | --- |
| PostgreSQL | As expected from the SQL types. |
| MySQL | Integer literals are `Long`, `DATETIME` is `java.time.LocalDateTime`, `TRUE` is the integer `1`. |
| MariaDB | Integer literals are `Integer`, `DATETIME` is `java.sql.Timestamp`, `TRUE` is the integer `1`. |
| SQL Server | `TINYINT` is `Short`, `MONEY` is a `BigDecimal` with a scale of 4, `DATETIMEOFFSET` is the driver's `microsoft.sql.DateTimeOffset`. |
| Oracle Database | Every `NUMBER` is a `BigDecimal` without trailing zeros, `DATE` is `java.sql.Timestamp`, `TIMESTAMP` is the driver's `oracle.sql.TIMESTAMP`. |

With Oracle Database, compare numbers with `BigDecimal` values, and set the `oracle.jdbc.J2EE13Compliant` property to `true` to read `TIMESTAMP` columns as `java.sql.Timestamp`.

Column labels also differ: PostgreSQL turns unquoted names into lower case, Oracle Database into upper case, and the others keep them as written.
Gatling matches column names ignoring case, so `column("email")` works everywhere.
On SQL Server, an expression without alias, such as `SELECT 1 + 1`, has an empty label: use an alias, or `column(1)`.

## Return generated keys {#generated-keys}

Without column names, drivers don't return the same columns:

| **Database** | **`returnGeneratedKeys()` returns** |
| --- | --- |
| PostgreSQL | The generated columns, with their names. |
| MySQL | The key, labeled `GENERATED_KEY`, as a `BigInteger`. |
| MariaDB | The key, labeled `insert_id`, as a `BigInteger`. |
| SQL Server | The key, labeled `GENERATED_KEYS`, as a `BigDecimal`. |
| Oracle Database | The `ROWID` of the rows, not their key. |

Use `generatedKey` without argument to read the first column whatever its label.
With Oracle Database, use `returnGeneratedKeys("ID")` to get the actual key.

On SQL Server, a batch with `returnGeneratedKeys` fails, although the driver inserts the rows: don't combine them.

## Run batches {#batches}

All drivers report one update count per row by default.
Some driver options that rewrite batches into multi-row statements make the driver report `Statement.SUCCESS_NO_INFO` instead, so `updateCount.count` works, but `updateCount.findAll` fails:

- PostgreSQL with `reWriteBatchedInserts=true`
- MySQL with `rewriteBatchedStatements=true`

MariaDB's driver ignores `rewriteBatchedStatements`.

## Call stored procedures {#stored-procedures}

All drivers support OUT and INOUT parameters with `callableStatement`.

- **PostgreSQL**: cursors are closed at the end of the transaction, so read `refcursor` values inside a [transaction]({{< ref "/reference/script/jdbc/transactions" >}}).
- **Oracle Database**: `SYS_REFCURSOR` values, as OUT parameters or columns, can be read outside of a transaction.
- **MySQL, MariaDB**: a `CALL` produces the result sets of the procedure, then an update count for its last statement.
- **SQL Server**: without `SET NOCOUNT ON` in the procedure, triggers and each statement produce their own update count.

## Configure transactions {#transactions}

| **Database** | **Isolation levels** | **`readOnly`** |
| --- | --- | --- |
| PostgreSQL | All | Enforced |
| MySQL, MariaDB | All | Enforced |
| SQL Server | All standard levels | Ignored by the driver |
| Oracle Database | `TRANSACTION_READ_COMMITTED` and `TRANSACTION_SERIALIZABLE` only, others fail on `begin` | Ignored by the driver |

- **SQL Server**: the `SNAPSHOT` isolation level isn't a standard JDBC level, so `transactionIsolation` can't set it.
  Don't run `SET TRANSACTION ISOLATION LEVEL SNAPSHOT` either: it would stay on the connection for the next virtual users.
  Consider the `READ_COMMITTED_SNAPSHOT` database option instead.
  To make connections read-only, add `ApplicationIntent=ReadOnly` to the URL.
- **Oracle Database**: to make a transaction read-only, run `SET TRANSACTION READ ONLY` as its first statement.

## Configure the fetch size {#fetch-size}

Drivers handle `fetchSize` differently, especially outside of a transaction, in auto-commit mode:

| **Database** | **Behavior** |
| --- | --- |
| PostgreSQL | Ignored in auto-commit mode: the driver reads the whole result. It only fetches by batches inside a transaction. |
| MySQL | Ignored: the driver reads the whole result, unless you add `useCursorFetch=true` to the URL. |
| MariaDB | Honored, including in auto-commit mode. |
| SQL Server | Depends on the driver's `selectMethod` and `responseBuffering` settings. |
| Oracle Database | Adapted by the driver when not set. A small explicit value, such as 10, multiplies the round trips: leave it unset, or set it large. |

## Understand error messages {#errors}

SQL Server's and Oracle Database's drivers translate their messages into the language of the JVM's locale.
Checks and assertions on error messages should rely on error codes, such as `ORA-01722`, rather than on text.
