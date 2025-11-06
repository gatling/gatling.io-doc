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

The Datadog integration allows Gatling Enterprise Edition to send load-test metrics - such as response times, throughput, and error rates - directly into Datadog’s observability platform. Once enabled, performance data from Gatling Enterprise Edition is sent to Datadog, where it can be correlated with infrastructure and application metrics already collected in your Datadog account

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

3. In your [control-plane configuration](https://docs.gatling.io/reference/install/cloud/private-locations/introduction/), in the section `system-properties`, add:

  ```bash
  control-plane {
    locations = [
      {
        system-properties {
          "gatling.enterprise.dd.api.key" = "<your Datadog api key>"
          "gatling.enterprise.dd.application.key" = "<your Datadog application key>"
          "gatling.enterprise.dd.site" = "datadoghq.com"
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

**Short name**|**Tag name**|**Description**
:-----|:-----|:-----
Source|`source`|Source reference for Gatling Enterprise events
Team|`team`|Name of the team that owns the test
Phase|`phase`|Phase of the injection (start or end)
Test|`test`|Test name
Run ID|`run_id`|ID of the run

See the [official Datadog Events documentation](https://docs.datadoghq.com/fr/service_management/events/guides/usage/) for managing and displaying events.

## Metrics pushed to Datadog

Gatling Enterprise Edition pushes the following list of load test metrics to Datadog:

**Short name**|**Metric name**|**Description**
:-----|:-----|:-----
User start|`gatling_enterprise.user.start_count`|Number of injected users
User end|`gatling_enterprise.user.end_count`|Number of stopped users
Concurrent user|`gatling_enterprise.user.concurrent`|Number of concurrent users
Request|`gatling_enterprise.request.count`|Number of requests
Response|`gatling_enterprise.response.count`|Number of responses
Response time max|`gatling_enterprise.response.response_time.max`|Maximum response time
Response time min|`gatling_enterprise.response.response_time.min`|Minimum response time
Response time p95|`gatling_enterprise.response.response_time.p95`|Response time for the 95th percentile (95% of the requests)
Response time p99|`gatling_enterprise.response.response_time.p99`|Response time for the 99th percentile (99% of the requests)
Response time p999|`gatling_enterprise.response.response_time.p999`|Response time for the 99.9th percentile (99.9% of the requests)
Response Code|`gatling_enterprise.response.code`|Response code, in the case of HTTP, the HTTP response status code
Request Bits|`gatling_enterprise.bandwidth_usage.sent`|Outbound bandwidth usage
Response Bits|`gatling_enterprise.bandwidth_usage.received`|Inbound bandwidth usage
Request TCP open|`gatling_enterprise.tcp.open_count`|Number of opened TCP requests
Request TCP close|`gatling_enterprise.tcp.close_count`|Number of closed TCP requests
TCP connections|`gatling_enterprise.tcp.connection_count`|Number of new TCP connections
TCP connect time max|`gatling_enterprise.tcp.connect_time.min`|Minimum TCP connect time
TCP connect time min|`gatling_enterprise.tcp.connect_time.max`|Maximum TCP connect time
TCP connect time p95|`gatling_enterprise.tcp.connect_time.p95`|TCP connect time for the 95th percentile (95% of the requests)
TCP connect time p99|`gatling_enterprise.tcp.connect_time.p99`|TCP connect time for the 99th percentile (99% of the requests)
TCP connect time p999|`gatling_enterprise.tcp.connect_time.p999`|TCP connect time for the 99.9th percentile (99.9% of the requests)
TLS handshakes|`gatling_enterprise.tls.handshake_count`|Number of TLS handshakes
TLS handshake time max|`gatling_enterprise.tls.handshake_time.min`|Minimum TLS handshake time
TLS handshake time min|`gatling_enterprise.tls.handshake_time.max`|Maximum TLS handshake time
TLS handshake time p95|`gatling_enterprise.tls.handshake_time.p95`|TLS handshake time for the 95th percentile (95% of the requests)
TLS handshake time p99|`gatling_enterprise.tls.handshake_time.p99`|TLS handshake time for the 99th percentile (99% of the requests)
TLS handshake time p999|`gatling_enterprise.tls.handshake_time.p999`|TLS handshake time for the 99.9th percentile (99.9% of the requests)

## Use metrics tags to enhance your Datadog dashboard

### Defaults Tags

Gatling Enterprise Edition pushes the following tags to Datadog:

**Short name**|**Tag name**|**Description**
:-----|:-----|:-----
Team|`team`|Name of the team that owns the test
Test|`test`|Test name
Load generator|`load_generator`|Load generator reference integer starting with 0
Scenario|`scenario`|Scenario name
Status|`status`|Status of the run (OK or KO)
Run ID|`run_id`|ID of the run

### Custom Tags

You can add custom tags by adding system properties, either at the control-plane level or in your test configuration (except for no-code tests):
`gatling.enterprise.dd.tags.<custom_tag>` = `<your value>`
