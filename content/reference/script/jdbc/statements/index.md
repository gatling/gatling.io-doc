---
title: Statements
seotitle: Gatling JDBC protocol reference - statements, prepared statements, batches, and stored procedures
description: How to run SQL with the Gatling JDBC protocol, including plain statements, prepared statements with bound and typed parameters, batches, generated keys, and stored procedures with OUT and INOUT parameters.
lead: Learn how to run plain statements, prepared statements, batches, and stored procedures against your database.
date: 2026-11-04T00:00:00+00:00
---

## Understand the kinds of statements

Use `jdbc("name")` to create a request, then pick the kind of statement, named after the JDBC one Gatling runs:

| **Method** | **JDBC statement** | **Use it for** |
| --- | --- | --- |
| `statement(sql)` | `java.sql.Statement` | SQL without bound parameters. |
| `preparedStatement(sql)` | `java.sql.PreparedStatement` | SQL with `?` placeholders bound with `params`, or run as a `batch`. |
| `callableStatement(sql)` | `java.sql.CallableStatement` | Stored procedures and functions, with OUT and INOUT parameters. |

Gatling runs every statement with `execute`, so they accept any SQL: queries, updates, DDL, or scripts producing several results.
The name is the name of the request in the reports.

{{< alert warning >}}
In the SQL, Gatling Expression Language placeholders such as `#{table}` are replaced with their value in the SQL text itself, like everywhere else in Gatling.
To bind values, use a prepared statement with `?` placeholders.
{{< /alert >}}

## Run a statement {#statement}

`statement` runs SQL as is. The SQL is a Gatling Expression Language String, or a function:

{{< include-code "statement" >}}

## Run a prepared statement {#prepared-statement}

`preparedStatement` binds the values of its `?` placeholders, in order, with `params`.
Strings are Gatling Expression Language: `"#{userId}"` binds the value of the `userId` attribute, with its own type.
Other values are bound as is.

{{< include-code "prepared-statement" >}}

To compute a value from the Session, pass a function: in Java and Kotlin, wrap it with `param`, in JavaScript, TypeScript, and Scala, pass it as is.

{{< include-code "param-function" >}}

### Bind typed parameters {#typed-params}

By default, Gatling binds values with `setObject`, and the driver infers the SQL type from the Java type.
Some databases, such as PostgreSQL, reject a String for a numeric or date column.

`param(value, jdbcType)` binds a value with the given `java.sql.JDBCType`, like JMeter's typed parameters.
In JavaScript and TypeScript, the JDBC type is its name, such as `"DECIMAL"`.
Gatling converts String values to the Java type matching the JDBC type, then binds them with the matching setter:

| **JDBC type** | **String converted with** | **Bound with** |
| --- | --- | --- |
| `INTEGER`, `BIGINT`, `SMALLINT`, `TINYINT` | `Integer.valueOf`, `Long.valueOf`, `Short.valueOf`, `Byte.valueOf` | `setInt`, `setLong`, `setShort`, `setByte` |
| `DECIMAL`, `NUMERIC` | `new BigDecimal(...)` | `setBigDecimal` |
| `DOUBLE`, `FLOAT`, `REAL` | `Double.valueOf`, `Float.valueOf` | `setDouble`, `setFloat` |
| `BOOLEAN`, `BIT` | `true`, `false`, `1`, or `0`, ignoring case | `setBoolean` |
| `DATE`, `TIME`, `TIMESTAMP` | `java.sql.Date.valueOf`, `Time.valueOf`, `Timestamp.valueOf` | `setDate`, `setTime`, `setTimestamp` |
| `CHAR`, `VARCHAR`, `LONGVARCHAR` | not converted | `setString` |
| `NCHAR`, `NVARCHAR`, `LONGNVARCHAR` | not converted | `setNString` |
| other types | not converted | `setObject(value, type)` |

A value that can't be converted makes the request crash before anything is sent to the database.
Values of other Java types aren't converted.

{{< include-code "typed-params" >}}

### Bind NULLs {#nulls}

`param(null, jdbcType)` binds a NULL with `setNull` and the given type.
An untyped NULL is bound with `setNull` and `java.sql.Types.NULL`, which some drivers reject for some column types.
Use a typed NULL when your driver needs it, see [database specifics]({{< ref "/reference/script/jdbc/databases" >}}).

{{< alert info >}}
Gatling Expression Language rejects Session attributes whose value is null: compute nulls with a function, or pass a literal `null`.
{{< /alert >}}

## Run a batch {#batch}

`batch` runs a prepared statement once per row, with `addBatch` and `executeBatch`.
Each row is a List of values bound to the `?` placeholders, in order.
A batch produces one update count per row.

{{< include-code "batch" >}}

`batch` and `params` can't be combined on the same statement.
The request crashes, without sending anything, when the batch is empty.
Rows contain plain values, so they can't contain `param(...)`: use the Java types matching the columns, such as `BigDecimal` or `java.sql.Timestamp`.

## Return generated keys {#generated-keys}

`returnGeneratedKeys` asks the database for the keys it generated, typically for identity columns, so you can check them with [`generatedKey`]({{< ref "/reference/script/jdbc/checks#generated-key" >}}).
It works with statements, prepared statements, and batches.

- `returnGeneratedKeys()` lets the driver decide which columns to return, see [`Statement.RETURN_GENERATED_KEYS`](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/Statement.html#RETURN_GENERATED_KEYS).
- `returnGeneratedKeys(column, columns...)` returns the given columns.

{{< include-code "generated-keys" >}}

Drivers don't agree on the columns they return without names: some return the actual key under a driver-specific label, and Oracle Database returns the `ROWID`.
Pass the column names when you need the actual values, see [database specifics]({{< ref "/reference/script/jdbc/databases#generated-keys" >}}).

## Call a stored procedure {#callable-statement}

`callableStatement` calls a stored procedure or a function, with the JDBC escape syntax such as `{call procedure(?, ?)}` or `{? = call function(?)}`, or a database-specific syntax.
Its `params` accept, in addition to the values of a prepared statement:

- `outParam(jdbcType)`: an OUT parameter, registered with `registerOutParameter`, with the JDBC type as a name in JavaScript and TypeScript, such as `"INTEGER"`
- `inOutParam(value, jdbcType)`: an INOUT parameter, bound like `param(value, jdbcType)`, then registered

{{< include-code "callable-statement" >}}

Check the values of OUT and INOUT parameters with [`outValue(index)`]({{< ref "/reference/script/jdbc/checks#out-value" >}}), where the index is the 1-based index of the parameter.
Gatling reads them after all the results the procedure produced.
A cursor, such as a `REF_CURSOR`, is read entirely, as a List of rows.

Only callable statements support OUT and INOUT parameters: `preparedStatement` rejects them.
