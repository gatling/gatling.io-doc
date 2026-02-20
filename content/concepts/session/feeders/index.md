---
title: Feeders
seotitle: Gatling session scripting reference - feeders
description: How to use feeders to inject test data into your virtual users' Session so that they don't all hit the same content and don't wrongfully test your caches.
lead: Inject data into your virtual users from an external source, eg a CSV file
date: 2021-04-20T18:30:56+02:00
aliases:
  - /reference/script/core/session/feeders/
---

{{< alert tip >}}Learning to use feeders is covered in the [Writing realistic tests]({{< ref "/guides/optimize-scripts/writing-realistic-tests/" >}}) tutorial.{{< /alert >}}

Feeders are a stock of data that your virtual users can consume records from.

The SDK provides a `feed` method that can be called at the same place as `exec`.

{{< include-code "feed-keyword" >}}

This defines a step where **every virtual user** feeds on the same Feeder.

Every time a virtual user reaches this step, it collects a record from the Feeder.\
This record is then injected into the user's [Session]({{< ref "/concepts/session/api/#session" >}}), making new attributes available.

It's also possible to feed multiple records at once. In this case, values are Java List or Scala Seq containing all the values of the same key.

{{< include-code "feed-multiple" >}}

## Using arrays and lists

Gatling lets you use in-memory data structures as Feeders.

{{< include-code "feeder-in-memory" >}}

## File based feeders

Gatling provides multiple file-based feeders.

When using Java, Kotlin or Scala, files must be placed in `src/main/resources` or `src/test/resources` (or `src/gatling/resources` when using Gradle).\
When using JavaScript or TypeScript, files must be places in `resources`.\
You then have to configure the **relative path** from this root.\
This is the recommended strategy.

{{< alert warning >}}
Don't use relative filesystem paths such as ~~`src/main/resources/data/file.csv`~~, instead use a classpath path `data/file.csv`.
{{< /alert >}}

As an alternative, you can also configure an absolute path if you want to deploy your feeder files separately and have them directly sit on the host's filesystem.

### CSV feeders

Gatling provides several built-ins for reading character-separated values files.

Our parser honors the [RFC4180](https://tools.ietf.org/html/rfc4180) specification.

The only difference is that header fields get trimmed of wrapping whitespaces.

{{< include-code "sep-values-feeders" >}}

### JSON feeders

Some users might want to use data in JSON format instead of CSV:

{{< include-code "json-feeders" >}}

For example, the following JSON:

```json
[
 {
  "id":19434,
  "foo":1
  },
  {
    "id":19435,
    "foo":2
  }
]
```

is turned into:

```scala
Map("id" -> 19434, "foo" -> 1) // record #1
Map("id" -> 19435, "foo" -> 2) // record #2
```

Note that the root element must be an array.

### Sitemap feeder

Gatling supports a feeder that reads data from a [Sitemap](http://www.sitemaps.org/protocol.html) file.

{{< include-code "sitemap-imports,sitemap-feeder" >}}

The following Sitemap file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
  <url>
    <loc>http://www.example.com/</loc>
    <lastmod>2005-01-01</lastmod>
    <changefreq>monthly</changefreq>
    <priority>0.8</priority>
  </url>

  <url>
    <loc>http://www.example.com/catalog?item=12&amp;desc=vacation_hawaii</loc>
    <changefreq>weekly</changefreq>
  </url>

  <url>
    <loc>http://www.example.com/catalog?item=73&amp;desc=vacation_new_zealand</loc>
    <lastmod>2004-12-23</lastmod>
    <changefreq>weekly</changefreq>
  </url>
</urlset>
```

will be turned into:

```scala
// record #1
Map(
  "loc" -> "http://www.example.com/",
  "lastmod" -> "2005-01-01",
  "changefreq" -> "monthly",
  "priority" -> "0.8"
)

// record #2
Map(
  "loc" -> "http://www.example.com/catalog?item=12&amp;desc=vacation_hawaii",
  "changefreq" -> "weekly"
)

// record #3
Map(
  "loc" -> "http://www.example.com/catalog?item=73&amp;desc=vacation_new_zealand",
  "lastmod" -> "2004-12-23",
  "changefreq" -> "weekly"
) 
```

### Zipped files

If your files are very large, you can provide them zipped and ask gatling to `unzip` them on the fly:

{{< include-code "unzip" >}}

Supported formats are gzip and zip (but archive must contain only one single file).

### Distributed files {{% badge enterprise "Enterprise" /%}} {#distributed}

If you want to run distributed tests with [Gatling Enterprise Edition](https://gatling.io/products/)
and you want to distribute data so that users don't use the same data when they run on different load generators, you can use the `shard` option.
For example, if you have a file with 30,000 records deployed on 3 load generators, each will use a 10,000 records slice.

{{< alert warning >}}
`shard` is only effective when running with Gatling Enterprise Edition, otherwise it's just a noop.
{{< /alert >}}

{{< include-code "shard" >}}

## JDBC feeder {#jdbc}

Gatling also provides a built-in that reads from a JDBC connection.

{{< include-code "jdbc-imports,jdbc-feeder" >}}

Just like File parser built-ins, this returns a `RecordSeqFeederBuilder` instance.

* The databaseUrl must be a JDBC URL (e.g. `jdbc:postgresql:gatling`),
* the username and password are the credentials to access the database,
* sql is the query that gets the needed values.

Only JDBC4 drivers are supported, so that they automatically register to the DriverManager.

{{< alert tip >}}
Do not forget to add the required JDBC driver jar in the classpath (`lib` folder in the bundle)
{{< /alert >}}

## Redis feeder {#redis}

This feature was originally contributed by Krishnen Chedambarum.

Gatling can read data from Redis using one of the following Redis commands.

* LPOP - remove and return the first element of the list
* SPOP - remove and return a random element from the set
* SRANDMEMBER - return a random element from the set
* RPOPLPUSH - return the last element of the list and also store as first element to another list

By default, RedisFeeder uses LPOP command:

{{< include-code "redis-imports,redis-LPOP" >}}

You can then override the desired Redis command:

{{< include-code "redis-SPOP" >}}

{{< include-code "redis-SRANDMEMBER" >}}

You can create a circular feeder by using the same keys with RPOPLPUSH

{{< include-code "redis-RPOPLPUSH" >}}

## Strategies

Gatling provides multiple strategies for the built-in feeders.

### queue

This is the default strategy that will be applied if you don't specify one.

The `queue` strategy makes the virtual users consume the records, meaning that:
* no virtual user can collect the same record
* at some point, the whole stock of data is consumed

This strategy is suited when:
* no virtual users must collect the same record because it would result in duplicates, e.g. multiple virtual users using the same credentials
* you want the records to be consumed in the exact order as they are defined in your source, e.g. your CSV file

{{< include-code "queue" >}}

```
For example, given a CSV file with the following content:
key
1
2
3

When using the `queue` strategy:
* the 1st virtual user consumes the record ("key", "1").
* the 2nd virtual user consumes the record ("key", "2").
* the 3rd virtual user consumes the record ("key", "3").
* the 4th virtual user will cause Gatling to crash.
```

{{< alert warning >}}
If you try to consume more records than what's available, Gatling will crash with an error
{{< /alert >}}

### shuffle

The `shuffle` strategy is very similar to the `queue` one, except that the records are collected in a random order.

This strategy is suited when:
* no virtual users must collect the same record because it would result in duplicates, e.g. multiple virtual users using the same credentials
* you want to introduce some randomness in the order the records are consumed

{{< include-code "shuffle" >}}

```
For example, given a CSV file with the following content:
key
1
2
3

When using the `shuffle` strategy:
* the 1st virtual user consumes a random record, e.g. ("key", "3").
* the 2nd virtual user consumes a random record from the remaining ones, e.g. ("key", "2").
* the 3rd virtual user consumes the last available record ("key", "1").
* the 4th virtual user will cause Gatling to crash.
```

{{< alert warning >}}
If you try to consume more records than what's available, Gatling will crash with an error
{{< /alert >}}

### random

The `random` strategy makes the virtual users collect records in a random order, meaning that:
* multiple virtual users can collect the same record
* your stock of data will never run out

This strategy is suited when:
* you don't care that multiple virtual users use the same data, e.g. search keywords
* you want to introduce some randomness in the order the records are consumed

{{< include-code "random" >}}

```
For example, given a `data.csv` file with the following content:
key
1
2
3

When using the `random` strategy:
* the 1st virtual user collects a random record, e.g ("key", "3").
* the 2nd virtual user collects a random record, e.g ("key", "1").
* the 3rd virtual user collects a random record, e.g ("key", "3") again.
* the 4th virtual user collects a random record, e.g ("key", "2").
```

### circular

The `circular` strategy is very similar to the `random` one, except that it preserves the original record order from your source:
* multiple virtual users can collect the same record: when the reading index reaches the end of the source, it moves back to the beginning
* you want the records to be consumed in the exact order as they are defined in your source, e.g. your CSV file

```
For example, given a `data.csv` file with the following content:
key
1
2
3

When using the `circular` strategy:
* the 1st virtual user collects the record ("key", "1").
* the 2nd virtual user collects the record ("key", "2").
* the 3rd virtual user collects the record ("key", "3").
* the 4th virtual user collects the record ("key", "1") again.
```

{{< include-code "circular" >}}

## Transforming records {#transform}

Sometimes, you might want to transform the raw data you receive from your feeder.

For example, a csv feeder would give you only Strings, but you might want to transform one of the attributes into an Int.

`transform` takes:
* in Java and Kotlin, a `BiFunction<String, T, Object>`
* in Scala a `PartialFunction[(String, T), Any]` that is defined only for records you want to transform, leaving the other ones as-is

For example:

{{< include-code "transform" >}}

## Loading all the records in memory {#read-records}

Sometimes, you might just want to reuse a convenient built-in feeder for custom needs and get your hands on the actual records.

{{< include-code "records" java kt scala >}}

{{< alert warning >}}
Beware that each `readRecords` call will read the underlying source, eg parse the CSV file.
{{< /alert >}}

## Count the number of records {#recordsCount}

Sometimes, you want to know the size of your feeder without having to use `readRecords` and copy all the data in memory.

{{< include-code "recordsCount" >}}

## Custom feeders {#custom}

Feeder is a type alias for `Iterator<Map<String, T>>`, meaning that the component created by the feed method will poll `Map<String, T>` records and inject its content.

It's very simple to build a custom one. For example, here's how one could build a random email generator:

{{< include-code "random-imports,random-mail-generator" >}}
