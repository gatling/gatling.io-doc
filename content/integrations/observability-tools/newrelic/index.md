---
title: New Relic integration for Gatling
menutitle: New Relic
seotitle: Integrate Gatling with New Relic
description: Send load test metrics to New Relic.
lead: Send load test metrics to New Relic.
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Introduction

The New Relic integration allows Gatling Enterprise Edition to send load-test metrics - such as response times, throughput, and error rates - directly into New Relic.
Once enabled, performance data from Gatling Enterprise Edition is sent to New Relic, where it can be correlated with infrastructure and application metrics already collected in New Relic.

With this integration in place, you can:

- Monitor Gatling scenarios alongside server-level KPIs (CPU, memory, network) in a single dashboard.
- Investigate performance issues more effectively by overlaying load-test metrics on traces, logs, and resource utilization charts.

## Prerequisites 

- A valid New Relic license key (for the metrics and events APIs)
- Your New Relic organization id (for the events)
- A Gatling Enterprise Edition account with private locations that can connect to the New Relic database. 

## Install the New Relic integration

The New Relic integration requires installation steps on your private locations control plane.

In your [control-plane configuration]({{< ref "/reference/deploy/private-locations/introduction" >}}), in the section `system-properties`, add:

```hocon
control-plane {
  locations = [
    {
      system-properties {
        "gatling.enterprise.newrelic.license.key" = "<your NewRelic license key>"
        "gatling.enterprise.newrelic.account" = "<your NewRelic account>" # for events
        "gatling.enterprise.newrelic.dc" = "US|EU" # optional, default is US
        "gatling.enterprise.newrelic.useProxy" = "<true to use the same proxy as for the Gatling API>" # optional, default is false
      }
    }
  ]
}
```
 
## Uninstall the New Relic integration

To remove the link between Gatling Enterprise Edition and New Relic, remove the lines containing `gatling.enterprise.newrelic` in your control-plane configuration.

## Metrics

### Common attributes

All metrics Gatling Enterprise Edition pushes to NewRelic use the following attributes:

**Common attributes**|**Description**
:-----|:-----|
`run_id`|the ID of your run
`test`|the name of your test
`team`|the ID of the team your test belongs to
`load_generator`|the 0-based index of the load generator in the cluster used to run your test

### Extra user-defined attributes

You can set extra attributes with Java System properties set on your test. These System properties must follow the convention:

* name: `gatling.enterprise.newrelic.attributes.<ATTRIBUTE_NAME>`
* vale: `<ATTRIBUTE_VALUE>`

### Pushed Metrics

Gatling Enterprise Edition sends the following metrics in your New Relic account:

**Metrics**| **Type**                                                                                |**Specific attributes**
:-----|:----------------------------------------------------------------------------------------|:-----
`gatling_enterprise.user.start_count`<br>`gatling_enterprise.user.end_count`<br>`gatling_enterprise.user.concurrent`|count|scenario
`gatling_enterprise.request.count`|count|scenario<br>group<br>request
`gatling_enterprise.response.count`|count|scenario<br>group<br>request<br>status
`gatling_enterprise.response.code`|count|scenario<br>group<br>request<br>code
`gatling_enterprise.response.response_time.min`<br>`gatling_enterprise.response.response_time.p95`<br>`gatling_enterprise.response.response_time.p99`<br>`gatling_enterprise.response.response_time.p999`<br>`gatling_enterprise.response.response_time.max`|gauge|scenario<br>group<br>request<br>status
`gatling_enterprise.group.count`|count|scenario<br>group<br>status
`gatling_enterprise.group.duration.min`<br>`gatling_enterprise.group.duration.p95`<br>`gatling_enterprise.group.duration.p99`<br>`gatling_enterprise.group.duration.p999`<br>`gatling_enterprise.group.duration.max`<br>`gatling_enterprise.group.cumulated.min`<br>`gatling_enterprise.group.cumulated.p95`<br>`gatling_enterprise.group.cumulated.p99`<br>`gatling_enterprise.group.cumulated.p999`<br>`gatling_enterprise.group.cumulated.max`|gauge|scenario<br>group<br>status
`gatling_enterprise.dns.count`|count|hostname<br>status
`gatling_enterprise.dns.time.min`<br>`gatling_enterprise.dns.time.p95`<br>`gatling_enterprise.dns.time.p99`<br>`gatling_enterprise.dns.time.p999`<br>`gatling_enterprise.dns.time.max`|gauge|hostname<br>status
`gatling_enterprise.bandwidth_usage.sent`<br>`gatling_enterprise.bandwidth_usage.received`<br>`gatling_enterprise.tcp.open_count`<br>`gatling_enterprise.tcp.close_count`|count|remote
`gatling_enterprise.tcp.connection_count`<br>`gatling_enterprise.tls.handshake_count`|count|remote<br>status
`gatling_enterprise.tcp.connect_time.min`<br>`gatling_enterprise.tcp.connect_time.p95`<br>`gatling_enterprise.tcp.connect_time.p99`<br>`gatling_enterprise.tcp.connect_time.p999`<br>`gatling_enterprise.tcp.connect_time.max`<br>`gatling_enterprise.tls.handshake_time.min`<br>`gatling_enterprise.tls.handshake_time.p95`<br>`gatling_enterprise.tls.handshake_time.p99`<br>`gatling_enterprise.tls.handshake_time.p999`<br>`gatling_enterprise.tls.handshake_time.max`|gauge|remote<br>status
`gatling_enterprise.tcp.states.count`|gauge|remote<br>state
`gatling_enterprise.cpu.user`<br>`gatling_enterprise.cpu.sys`<br>`gatling_enterprise.mem.ram.max`<br>`gatling_enterprise.mem.ram.used`<br>`gatling_enterprise.mem.mem.heap.max`<br>`gatling_enterprise.mem.heap.committed`<br>`gatling_enterprise.mem.heap.used`|gauge||

## Events

Gatling Enterprise Edition sends events to notify of the start and the end of a test.

**Key**|**Value
:------|:------
`eventType`|GatlingTestStart / GatlingTestEnd
`run_id`| <the ID of your run>
`test`| <the name of your test>
`team`| <the ID of the team your test belongs to>
`source`|`gatling-enterprise`
