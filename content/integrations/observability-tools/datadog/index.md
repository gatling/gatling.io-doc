---
title: Datadog integration for Gatling
menutitle: Datadog
seotitle: Integrate Gatling with Datadog
description: Send load test metrics to Datadog.
lead: Send load test metrics to Datadog.
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Introduction

The Datadog integration allows Gatling Enterprise Edition to send load-test metrics - such as response times, throughput, and error rates - directly into Datadog’s observability platform.
Once enabled, performance data from Gatling Enterprise Edition is sent to Datadog, where it can be correlated with infrastructure and application metrics already collected in your Datadog account.

With this integration in place, you can:

- Monitor Gatling scenarios alongside server-level KPIs (CPU, memory, network) in a single dashboard.
- Investigate performance issues more effectively by overlaying load-test metrics on traces, logs, and resource utilization charts.

## Prerequisites 

- A valid Datadog API key (for the metrics)
- A valid Datadog Application key (for the events)
- Your Datadog site
- A Gatling Enterprise Edition account with private locations that can connect to the Datadog network. 

## Install the Datadog integration

The Datadog integration requires installation steps in your Datadog account and on your private locations control plane.

1. See the [official Datadog Integration documentation](https://docs.datadoghq.com/integrations/gatling_enterprise/) for installing Gatling Enterprise Edition in your Datadog account.

2. See the [official Datadog API/App key documentation](https://docs.datadoghq.com/fr/account_management/api-app-keys/) for creating an API key and an Application key in your Datadog account.

3. In your [control-plane configuration]({{< ref "/reference/deploy/private-locations/introduction" >}}), in the section `system-properties`, add:

  ```hocon
  control-plane {
    locations = [
      {
        system-properties {
          "gatling.enterprise.dd.api.key" = "<your Datadog API key>"
          "gatling.enterprise.dd.application.key" = "<your Datadog application key>"
          "gatling.enterprise.dd.site" = "<your Datadog site, depends on where you are hosted, see https://docs.datadoghq.com/getting_started/site/#access-the-datadog-site>"
          "gatling.enterprise.dd.useProxy" = "<true to use the same proxy as for the Gatling API>" # optional, default is false
        }
      }
    ]
  }
  ```
 
## Uninstall the Datadog integration

To remove the link between Gatling Enterprise Edition and Datadog, remove the lines containing `gatling.enterprise.dd` in your control-plane configuration.

## Events pushed to Datadog

Gatling Enterprise Edition generates events for load test injection `start` and `end`.

All events are available under the `source:gatling-enterprise` tag, and have the following tags:

**Tag name**|**Description**
:-----|:-----
`source`|Source reference for Gatling Enterprise events
`team`|Name of the team that owns the test
`phase`|Phase of the injection (start or end)
`test`|Test name
`run_id`|ID of the run

See the [official Datadog Events documentation](https://docs.datadoghq.com/fr/service_management/events/guides/usage/) for managing and displaying events.

## Metrics pushed to Datadog

### Common Tags

All metrics Gatling Enterprise Edition pushes to Datadog use the following tags:

**Tag**|**Description**
:-----|:-----
`team`|Name of the team that owns the test
`test`|Test name
`load_generator`|Load generator reference integer starting with 0
`run_id`|ID of the run

### Available metrics

Gatling Enterprise Edition pushes the following list of load test metrics to Datadog:

**Metric**|**Type**|**Specific Tags**
:---------|:-------|:----------------
`gatling_enterprise.user.start_count`<br>`gatling_enterprise.user.end_count`|count|scenario
`gatling_enterprise.user.concurrent`|gauge|scenario
`gatling_enterprise.request.count`|count|scenario<br>group<br>request
`gatling_enterprise.response.count`|count|scenario<br>group<br>request<br>status
`gatling_enterprise.response.response_time.min`<br>`gatling_enterprise.response.response_time.p95`<br>`gatling_enterprise.response.response_time.p99`<br>`gatling_enterprise.response.response_time.p999`<br>`gatling_enterprise.response.response_time.max`|gauge|scenario<br>group<br>request<br>status
`gatling_enterprise.response.code`|count|scenario<br>group<br>request<br>code
`gatling_enterprise.group.count`|count|scenario<br>group
`gatling_enterprise.group.duration.min`<br>`gatling_enterprise.group.duration.p95`<br>`gatling_enterprise.group.duration.p99`<br>`gatling_enterprise.group.duration.p999`<br>`gatling_enterprise.group.duration.max`<br>`gatling_enterprise.group.cumulated.min`<br>`gatling_enterprise.group.cumulated.p95`<br>`gatling_enterprise.group.cumulated.p99`<br>`gatling_enterprise.group.cumulated.p999`<br>`gatling_enterprise.group.cumulated.max`|gauge|scenario<br>group<br>status
`gatling_enterprise.dns.count`|count|hostname<br>status
`gatling_enterprise.dns.time.min`<br>`gatling_enterprise.dns.time.p95`<br>`gatling_enterprise.dns.time.p99`<br>`gatling_enterprise.dns.time.p999`<br>`gatling_enterprise.dns.time.max`|gauge|hostname<br>status
`gatling_enterprise.tcp.open_count`<br>`gatling_enterprise.tcp.close_count`<br>`gatling_enterprise.bandwidth_usage.sent`<br>`gatling_enterprise.bandwidth_usage.received`|count|remote
`gatling_enterprise.tcp.connection_count`<br>`gatling_enterprise.tls.handshake_count`|count|remote<br>status
`gatling_enterprise.tcp.connect_time.min`<br>`gatling_enterprise.tcp.connect_time.p95`<br>`gatling_enterprise.tcp.connect_time.p99`<br>`gatling_enterprise.tcp.connect_time.p999`<br>`gatling_enterprise.tcp.connect_time.max`<br>`gatling_enterprise.tls.handshake_time.min`<br>`gatling_enterprise.tls.handshake_time.p95`<br>`gatling_enterprise.tls.handshake_time.p99`<br>`gatling_enterprise.tls.handshake_time.p999`<br>`gatling_enterprise.tls.handshake_time.max`|gauge|remote<br>status
`gatling_enterprise.tcp.state_count`|gauge|remote<br>state
`gatling_enterprise.cpu.user`<br>`gatling_enterprise.cpu.sys`<br>`gatling_enterprise.mem.ram.max`<br>`gatling_enterprise.mem.ram.used`<br>`gatling_enterprise.mem.heap.max`<br>`gatling_enterprise.mem.heap.committed`<br>`gatling_enterprise.mem.heap.used`|gauge

### Custom Tags

You can add custom tags by adding system properties, either at the control-plane level or in your test configuration (except for no-code tests):
`gatling.enterprise.dd.tags.<custom_tag>` = `<your value>`
