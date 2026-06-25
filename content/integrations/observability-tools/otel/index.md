---
title: OpenTelemetry integration for Gatling
menutitle: OpenTelemetry
seotitle: Integrate Gatling with OpenTelemetry
description: Send load test metrics to OpenTelemetry.
lead: Send load test metrics to OpenTelemetry.
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Introduction

The OpenTelemetry integration allows Gatling Enterprise Edition to send load-test metrics - such as response times, throughput, and error rates - to an OpenTelemetry Collector and then to [any compatible backend](https://opentelemetry.io/ecosystem/vendors/), including:

* [Datadog](https://docs.datadoghq.com/opentelemetry/)
* [Dynatrace](https://docs.dynatrace.com/docs/ingest-from/opentelemetry)
* [Elastic](https://www.elastic.co/docs/reference/opentelemetry/motlp)
* [NewRelic](https://docs.newrelic.com/docs/opentelemetry/opentelemetry-introduction/)
* [Prometheus](https://prometheus.io/docs/guides/opentelemetry/)
* [Splunk](https://github.com/signalfx/splunk-otel-collector)

Once enabled, performance data from Gatling Enterprise Edition is sent to OpenTelemetry, where it can be correlated with infrastructure and application metrics already collected in OpenTelemetry.

With this integration in place, you can:

- Monitor Gatling scenarios alongside server-level KPIs (CPU, memory, network) in a single dashboard.
- Investigate performance issues more effectively by overlaying load-test metrics on traces, logs, and resource utilization charts.

## Prerequisites 

- A valid OpenTelemetry collector
- A Gatling Enterprise Edition account with private locations that can connect to the OpenTelemetry collector endpoints. 

## Install the OpenTelemetry integration

The OpenTelemetry integration requires installation steps on your private locations control plane.

In your [control-plane configuration]({{< ref "/reference/deploy/private-locations/introduction" >}}), in the section `system-properties`, add:

```hocon
control-plane {
  locations = [
    {
      system-properties {
        "gatling.enterprise.opentelemetry.metrics.endpoint" = "<your endpoint for metrics>" # eg http://my-api-endpoint/v1/metrics
        "gatling.enterprise.opentelemetry.logs.endpoint" = "<your endpoint for logs>" # optional (if undefined, run start and stop events won't be logged), eg http://my-api-endpoint/v1/log
        "gatling.enterprise.opentelemetry.http.headers" = "<extra HTTP headers, eg Authorization>" # optional, eg api-key=key,other-config-value=value (values must be percent-encoded)
        "gatling.enterprise.opentelemetry.metrics.deltaExponentialHistogramsSupported" = "true|false" # optional, default is false
        "gatling.enterprise.opentelemetry.useProxy" = "<true to use the same proxy as for the Gatling API>" # optional, default is false
      }
    }
  ]
}
```
 
## Uninstall the OpenTelemetry integration

To remove the link between Gatling Enterprise Edition and OpenTelemetry, remove the lines containing `gatling.enterprise.newrelic` in your control-plane configuration.

## Common parameters (both metrics and logs)

The "Instrumentation Scope Info" is `io.gatling.enterprise`.

All data use the same resource attributes:

**Resource Attribute**|**Description**
:-----|:----------------------------|
`run.id`|the id of the test run
`service.name`|the "gatling.enterprise" string
`team.name`|the name of the team the test belongs to
`test.name`|the name of the test

## Metrics

Gatling Enterprise Edition sends the following metrics in your OpenTelemetry account:

**Metrics**| **Type**                    |**Attributes**
:-----|:----------------------------|:-----
`gatling_enterprise.user.start.count`<br>`gatling_enterprise.user.end.count`|sum|load.generator<br>scenario
`gatling_enterprise.user.concurrent`|gauge|load.generator<br>scenario
`gatling_enterprise.request.count`|sum|load.generator<br>scenario<br>group<br>request
`gatling_enterprise.response.code.count`|sum|load.generator<br>scenario<br>group<br>request<br>code
`gatling_enterprise.response.count`<br> (\*)|sum|load.generator<br>scenario<br>group<br>request<br>status
`gatling_enterprise.response.time.min`<br>`gatling_enterprise.response.time.p95`<br>`gatling_enterprise.response.time.p99`<br>`gatling_enterprise.response.time.p999`<br>`gatling_enterprise.response.time.max`<br> (\*)|gauge|load.generator<br>scenario<br>group<br>request<br>status
`gatling_enterprise.response.time`<br> (\*\*)|exponential histogram|load.generator<br>scenario<br>group<br>request<br>status
`gatling_enterprise.group.duration.count`<br> (\*)|sum|load.generator<br>scenario<br>group<br>status
`gatling_enterprise.group.duration.min`<br>`gatling_enterprise.group.duration.p95`<br>`gatling_enterprise.group.duration.p99`<br>`gatling_enterprise.group.duration.p999`<br>`gatling_enterprise.group.duration.max`<br>`gatling_enterprise.group.cumulated.min`<br>`gatling_enterprise.group.cumulated.p95`<br>`gatling_enterprise.group.cumulated.p99`<br>`gatling_enterprise.group.cumulated.p999`<br>`gatling_enterprise.group.cumulated.max` (\*)|gauge|load.generator<br>scenario<br>group<br>status
`gatling_enterprise.group.duration`<br>`gatling_enterprise.group.cumulated`<br> (\*\*)|exponential histogram|load.generator<br>scenario<br>group<br>status
`gatling_enterprise.dns.count`<br> (\*)|sum|load.generator<br>hostname<br>status
`gatling_enterprise.dns.time.min`<br>`gatling_enterprise.dns.time.p95`<br>`gatling_enterprise.dns.time.p99`<br>`gatling_enterprise.dns.time.p999`<br>`gatling_enterprise.dns.time.max`<br> (\*)|gauge|load.generator<br>hostname<br>status
`gatling_enterprise.dns.time`<br> (\*\*)|exponential histogram |load.generator<br>hostname<br>status
`gatling_enterprise.bits.sent`<br>`gatling_enterprise.bits.received`|sum|load.generator<br>remote
`gatling_enterprise.tcp.open.count`<br>`gatling_enterprise.tcp.close.count`|sum|load.generator<br>remote
`gatling_enterprise.tcp.connect.count`<br> (\*)|sum|load.generator<br>remote<br>status
`gatling_enterprise.tcp.connect.time.min`<br>`gatling_enterprise.tcp.connect.time.p95`<br>`gatling_enterprise.tcp.connect.time.p99`<br>`gatling_enterprise.tcp.connect.time.p999`<br>`gatling_enterprise.tcp.connect.time.max`<br> (\*)|gauge|load.generator<br>remote<br>status
`gatling_enterprise.tcp.connect.time`<br> (\*\*)|exponential histogram |load.generator<br>remote<br>status
`gatling_enterprise.tcp.state.count`|gauge|load.generator<br>state
`gatling_enterprise.tls.handshake.count`<br> (\*)|sum|load.generator<br>remote<br>status
`gatling_enterprise.tls.handshake.time.min`<br>`gatling_enterprise.tls.handshake.time.p95`<br>`gatling_enterprise.tls.handshake.time.p99`<br>`gatling_enterprise.tls.handshake.time.p999`<br>`gatling_enterprise.tls.handshake.time.max`<br> (\*)|gauge|load.generator<br>remote<br>status
`gatling_enterprise.tls.handshake.time`<br> (\*\*)|exponential histogram |load.generator<br>remote<br>status
`gatling_enterprise.cpu.user`<br>`gatling_enterprise.cpu.sys`<br>`gatling_enterprise.mem.ram.total`<br>`gatling_enterprise.mem.ram.used`<br>`gatling_enterprise.mem.heap.max`<br>`gatling_enterprise.mem.heap.committed`<br>`gatling_enterprise.mem.heap.used`|gauge|load.generator

**(\*) only when `deltaExponentialHistogramsSupported` is set to `false` (default)**

**(\*\*)  only when `deltaExponentialHistogramsSupported` is set to `true`**

{{< alert warning >}}
Gatling always use `delta` temporality for sums and histograms (when enabled).
Please check that your OpenTelemetry collector supports it.
For example, when using Prometheus, you must [enable its experimental support](https://prometheus.io/docs/guides/opentelemetry/#delta-temporality).
{{< /alert >}}

## Logs

Gatling Enterprise Edition can log test start and end events.

* event name: `gatling.run.start` or `gatling.run.end`
* body: `start` or `end`
