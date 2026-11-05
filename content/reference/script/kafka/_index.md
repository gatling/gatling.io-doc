---
title: Kafka
seotitle: Gatling Kafka protocol reference
description: Load test Apache Kafka with Gatling, by producing records, measuring request-reply and end-to-end latencies, checking records, and using a schema registry.
lead: SDK to load test Apache Kafka, from producing records to measuring request-reply and end-to-end latencies.
ordering:
  - setup
  - protocol
  - actions
  - checks
  - schema-registry
---

{{< alert enterprise >}}
Enhanced usage of this feature is available with Gatling Enterprise. [Explore our plans to learn more](https://gatling.io/pricing?utm_source=docs).
{{< /alert >}}

[Apache Kafka](https://kafka.apache.org/) is a distributed event streaming platform.
With the Gatling Kafka plugin, you can load test Kafka brokers and the systems built around them.

Gatling supports:

- producing records, with a key, headers, and a partition, and a value from any Gatling body
- request-reply, measuring the time until the reply to a request is read
- waiting for a record that another system produces, measuring the end-to-end latency of a pipeline
- checks on the value, the key, and the headers of the records read
- Avro, Protobuf, and JSON Schema keys and values, with a schema registry
- SASL authentication (PLAIN, SCRAM, OAUTHBEARER, AWS MSK IAM, and GSSAPI) and TLS
