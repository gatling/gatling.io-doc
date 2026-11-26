---
title: Telemetry and log settings for Gatling AI Server
menutitle: Telemetry and logs
seotitle: Gatling AI Server settings for OpenTelemetry traces and logs
description: Turn on OpenTelemetry traces and JSON logs in Gatling AI Server, and set the level of its log.
lead: Export one OpenTelemetry trace per conversion, and shape the server's log for your log collector.
date: 2026-11-23T00:00:00+00:00
---

The server exports no metrics. It can export traces, and write its log as JSON.

Each setting on this page is an environment variable. The
[configuration reference]({{< ref "/ai/server/configuration/overview#find-where-each-setting-goes" >}}) says where it goes
with your installation.

## Export traces

OpenTelemetry is an open standard for traces, and OTLP is its protocol. The following settings turn on one trace per
conversion, sent to your OTLP collector:

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `QUARKUS_OTEL_SDK_DISABLED` | `true` | `false` exports one OpenTelemetry trace per conversion, over OTLP. |
| `QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT` | <code>http://<wbr>localhost:4317/</code> | The OTLP collector the traces go to. |
| `QUARKUS_OTEL_EXPORTER_OTLP_PROTOCOL` | `grpc` | The OTLP protocol: `grpc` or `http/protobuf`. |
| `QUARKUS_OTEL_SERVICE_NAME` | `gatling-ai-server` | The service the traces are reported under. |

## Configure the log

The following settings shape the server's log:

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `QUARKUS_LOG_CONSOLE_JSON_ENABLED` | `false` | `true` writes one JSON object per log line, with the trace ID, the span ID, and the tenant as fields. |
| `QUARKUS_LOG_LEVEL` | `INFO` | The level of the server's log. At `DEBUG`, the log also identifies the user who made each tool call. |

## Keep the audit trail

The server keeps one audit record per tool call on the container's own disk, as
[Data and security]({{< ref "/ai/server/data-and-security#understand-what-the-server-keeps" >}}) describes. A new pod,
task, revision, or instance starts it over. The following settings bound it:

| **Environment variable** | **Default** | **What it does** |
| --- | --- | --- |
| `GATLING_STORE_SQLITE_AUDIT_RETENTION` | `P7D` | How long an audit record is kept. |
| `GATLING_STORE_SQLITE_AUDIT_RECORDS_PER_TENANT` | `10000` | The most audit records kept. The oldest goes first. |
