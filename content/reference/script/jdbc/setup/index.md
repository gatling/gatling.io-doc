---
title: Project Setup
seotitle: Gatling JDBC protocol reference - project setup
description: How to set up your project to use the Gatling JDBC protocol, including the plugin and the JDBC driver dependencies.
lead: Learn how to set up your project to use the Gatling JDBC protocol.
date: 2026-11-04T00:00:00+00:00
---

## Understand the license and limitations {#license}

**The Gatling JDBC component is distributed under the
[Gatling Enterprise Component License]({{< ref "/project/licenses/enterprise-component" >}}).**

The Gatling JDBC protocol can be used with both the [Community Edition](https://gatling.io/products) and
[Enterprise](https://gatling.io/products) versions of Gatling.

Its usage is unlimited when running tests on [Gatling Enterprise](https://gatling.io/products). When used with
[Gatling Community Edition](https://gatling.io/products), usage is limited to:

- 5 users maximum
- 5 minute duration tests

Limits after which the test will stop. This means that all locally executed tests using the JDBC protocol are limited by these values.

These limits only apply to the JDBC protocol: the [JDBC feeder]({{< ref "/reference/script/jdbc/feeder" >}}) alone isn't limited.

## Add the Gatling JDBC dependency {#gatling-jdbc-dependency}

The Gatling JDBC plugin isn't included with Gatling by default. Add the Gatling JDBC dependency, in addition to the
usual Gatling dependencies.

The Gatling JDBC plugin requires Gatling {{< var gatlingVersion >}} or later.

For Java or Kotlin:

{{< include-file >}}
1-Maven: includes/dependency.maven.java.md
2-Gradle: includes/dependency.gradle.java.md
{{< /include-file >}}

For JavaScript/TypeScript:

```console
npm install @gatling.io/jdbc@{{< var gatlingJsVersion >}}
```

For Scala:

{{< include-file >}}
1-Maven: includes/dependency.maven.scala.md
2-Gradle: includes/dependency.gradle.scala.md
3-sbt: includes/dependency.sbt.scala.md
{{< /include-file >}}

## Add the JDBC driver of your database {#driver}

Gatling doesn't ship any JDBC driver. Add the driver of your database as a dependency:

- For Java, Kotlin, and Scala, in the same scope as the Gatling dependencies.
- For JavaScript and TypeScript, as a [JVM dependency]({{< ref "/integrations/build-tools/js-cli#jvm-dependencies" >}}) in the `gatling` section of your `package.json`:

```json
{
  "gatling": {
    "jvmDependencies": ["org.postgresql:postgresql:42.7.14"]
  }
}
```

The drivers of the main databases are:

| **Database** | **Driver dependency** |
| --- | --- |
| PostgreSQL | `org.postgresql:postgresql` |
| MySQL | `com.mysql:mysql-connector-j` |
| MariaDB | `org.mariadb.jdbc:mariadb-java-client` |
| SQL Server | `com.microsoft.sqlserver:mssql-jdbc` |
| Oracle Database | `com.oracle.database.jdbc:ojdbc11` |

Gatling finds JDBC 4 drivers automatically: there's no driver class to configure.
