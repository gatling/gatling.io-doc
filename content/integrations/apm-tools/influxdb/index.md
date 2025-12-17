---
title: InfluxDB integration for Gatling
menutitle: InfluxDB
seotitle: Integrate Gatling with InfluxDB
description: Send load test metrics to InfluxDB.
lead: Send load test metrics to InfluxDB.
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Introduction

The InfluxDB integration allows Gatling Enterprise Edition to send load-test metrics - such as response times, throughput, and error rates - directly into an InfluxDB time series database.
Once enabled, performance data from Gatling Enterprise Edition is sent to InfluxDB, where it can be correlated with infrastructure and application metrics already collected in your InfluxDB database.

With this integration in place, you can:

- Monitor Gatling scenarios alongside server-level KPIs (CPU, memory, network) in a single dashboard.
- Investigate performance issues more effectively by overlaying load-test metrics on traces, logs, and resource utilization charts.

## Prerequisites 

- A valid InfluxDB API key (for the metrics)
- Your InfluxDB database (we support InfluxDB 1, 2 and 3)
- A Gatling Enterprise Edition account with private locations that can connect to the InfluxDB database. 

## Install the InfluxDB integration

The InfluxDB integration requires installation steps on your private locations control plane.

In your [control-plane configuration](https://docs.gatling.io/reference/install/cloud/private-locations/introduction/), in the section `system-properties`, add:

```bash
control-plane {
  locations = [
    {
      system-properties {
        "gatling.enterprise.influx.api.key" = "<your InfluxDB API key>"
        "gatling.enterprise.influx.api.url" = "<your InfluxDB API url>"
      }
    }
  ]
}
```

{{< alert warning >}}
Your InfluxDB API url must match your InfluxDB version (1, 2 or 3) and must use **second** precision, eg:
{{< /alert >}}

```
// InfluxDB 1
http(s)//host:port/api/v3/write_lp?db=mydb&precision=s

// InfluxDB 2
http(s)//host:port/api/v2/write?org=myorg&bucket=mybucket&precision=s

// InfluxDB 3
http(s)//host:port/write?db=mydb&precision=second
```
 
## Uninstall the InfluxDB integration

To remove the link between Gatling Enterprise Edition and InfluxDB, remove the lines containing `gatling.enterprise.influx` in your control-plane configuration.

## Tables schema

Gatling Enterprise Edition creates the following schema in your InfluxDB database:

**Table**| **Tags**                                              |**Fields**
:-----|:------------------------------------------------------|:-----
gatling_enterprise_users|runId,<br>test,<br>team,<br>load_generator,<br>scenario|start_count,<br>end_count,<br>max_concurrent
gatling_enterprise_requests|runId,<br>test,<br>team,<br>load_generator,<br>scenario,<br>group,<br>request|count
gatling_enterprise_responses|runId,<br>test,<br>team,<br>load_generator,<br>scenario,<br>group,<br>request,<br>status|count,<br>time_min,<br>time_p95,<br>time_p99,<br>time_p999,<br>time_max
gatling_enterprise_responses_by_code|runId,<br>test,<br>team,<br>load_generator,<br>scenario,<br>group,<br>request,code|count
gatling_enterprise_connections|runId,<br>test,<br>team,<br>load_generator,<br>remote|bandwidth_usage_sent,<br>bandwidth_usage_received,<br>tcp_open_count,<br>tcp_close_count
gatling_enterprise_tcp_connects|runId,<br>test,<br>team,<br>load_generator,<br>remote,<br>status|count,<br>time_min,<br>time_p95,<br>time_p99,<br>time_p999,<br>time_max
gatling_enterprise_tls_handshakes|runId,<br>test,<br>team,<br>load_generator,<br>remote,<br>status|count,<br>time_min,<br>time_p95,<br>time_p99,<br>time_p999,<br>time_max
