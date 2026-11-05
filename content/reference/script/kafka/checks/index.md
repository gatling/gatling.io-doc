---
title: Checks
seotitle: Gatling Kafka protocol reference - checks on record values, keys, and headers
description: How to check the replies and the expected records of Kafka actions with Gatling, including their value, key, and headers.
lead: Learn how to check the records Gatling reads, and save values for the next requests.
date: 2026-11-05T00:00:00+00:00
---

## Understand Kafka checks

Kafka checks are regular Gatling [checks]({{< ref "/concepts/checks" >}}): they support `find`, `findAll`, `count`, `saveAs`, `transform`, and the usual validators such as `is` and `gt`.
They run on the record that Gatling reads: the reply of a [request-reply]({{< ref "/reference/script/kafka/actions#request-reply" >}}), or the record of an [expect]({{< ref "/reference/script/kafka/actions#expect" >}}).
`send` reads no record, so it has no checks.

Checks run once the record is read, outside of the response time. A failing check makes the request fail.

## Check the value {#value}

The [generic checks]({{< ref "/concepts/checks#check-type" >}}) on the body read the record's value:

| **Check** | **Reads the value as** |
| --- | --- |
| `bodyString`, `substring`, `regex` | Text, decoded as UTF-8 |
| `jsonPath`, `jmesPath` | JSON |
| `bodyBytes`, `bodyLength`, `bodyStream` | Bytes |

{{< include-code "value" java kt scala >}}

A record without a value, such as a tombstone, fails these checks.

When a request-reply decodes its reply with a schema registry, the text and JSON checks read the decoded value as JSON, and the bytes checks still read the record's bytes.
See [decode replies]({{< ref "/reference/script/kafka/schema-registry#decode" >}}).

## Check the key and the headers {#key-header}

`kafkaKey` extracts the record's key, and `kafkaHeader(name)` the value of the record's last header with this name, both decoded as UTF-8:

{{< include-code "key-header" java kt scala >}}
