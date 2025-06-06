---
title: Datadog integration for Gatling
menutitle: Datadog
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Introduction

The Datadog integration allows Gatling Enterprise to send load-test metrics - such as response times, throughput, and error rates - directly into Datadog’s observability platform. Once enabled, performance data from Gatling Enterprise is sent to Datadog, where it can be correlated with infrastructure and application metrics already collected in your Datadog account 

With this integration in place, you can:

- Monitor Gatling scenarios alongside server-level KPIs (CPU, memory, network) in a single dashboard.
- Investigate performance issues more effectively by overlaying load-test metrics on traces, logs, and resource utilization charts.

## Prerequisites 

- A valid Datadog API key and your Datadog site. 
- A Gatling Enterprise account with private locations that can connect to the Datadog network. 

## Install the Datadog integration

The Datadog integration requires installation steps in your Datadog account and on your private locations control plane.

1. See the [official Datadog documentation](https://docs.datadoghq.com/integrations/gatling_enterprise/) for installing Gatling Enterprise in your Datadog account.

2. In your [control-plane configuration](https://docs.gatling.io/reference/install/cloud/private-locations/introduction/), in the section `system-properties`, add:

  ```bash
  control-plane {
    locations = [
      {
        system-properties {
          "gatling.enterprise.dd.api.key" = "<your api key>"
          "gatling.enterprise.dd.site" = "datadoghq.com"  
        }
      }
    ]
  }

  ```
 
## Uninstall the Datadog integration

To remove the link between Gatling Enterprise and Datadog, remove the lines containing `gatling.enterprise.dd` in your control-plane configuration.

## Metrics pushed to Datadog

Gatling Enterprise pushes the following list of load test metrics to Datadog:

**Short name**|**Metric name**|**Description**
:-----|:-----:|:-----:
User start|`gatling_enterprise.user.start_count`|Number of injected users
User end|`gatling_enterprise.user.end_count`|Number of stopped users
Concurrent user|`gatling_enterprise.user.concurrent`|Number of concurrent users
Request|`gatling_enterprise.request.count`|Number of requests
Response|`gatling_enterprise.response.count`|Number of responses
Response time max|`gatling_enterprise.response.response_time.max`|Maximum response time
Response time min|`gatling_enterprise.response.response_time.min`|Minimum response time
Response time p95|`gatling_enterprise.response.response_time.p95`|Response time for the 95th percentile 
Response time p99|`gatling_enterprise.response.response_time.p99`|Response time for the 99th percentile
Response time p999|`gatling_enterprise.response.response_time.p999`|Response time for the 99.9th percentile
