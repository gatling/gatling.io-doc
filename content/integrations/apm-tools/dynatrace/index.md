---
title: Dynatrace integration for Gatling
menutitle: Dynatrace
seotitle: Integrate Gatling with Dynatrace
description: Send load test metrics to Dynatrace and set custom test headers on all generated requests.
lead: Send load test metrics to Dynatrace and set custom test headers on all generated requests.
aliases:
  - /guides/dynatrace
  - /guides/analysis/dynatrace/
---

## Gatling Enterprise Edition Integration

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

### Introduction

The Dynatrace integration allows Gatling Enterprise Edition to send load-test metrics - such as response times, throughput, and error rates - directly into Dynatrace's observability platform. Once enabled, performance data from Gatling Enterprise Edition is sent to Dynatrace, where it can be correlated with infrastructure and application metrics already collected in your Dynatrace account.

With this integration in place, you can:

- Monitor Gatling scenarios alongside server-level KPIs (CPU, memory, network) in a single dashboard.
- Investigate performance issues more effectively by overlaying load-test metrics on traces, logs, and resource utilization charts.

### Prerequisites

- A valid Dynatrace API key
- Your Dynatrace site identifier
- A Gatling Enterprise Edition account with private locations that can connect to the Dynatrace network.

### Install the Dynatrace integration

The Dynatrace integration requires installation steps in your Dynatrace account and on your private locations control plane.

1. See the [official Dynatrace API key documentation](https://docs.dynatrace.com/docs/discover-dynatrace/references/dynatrace-api/basics/dynatrace-api-authentication) for creating an API key in your Dynatrace account. Ensure your API key has the following permissions:
    - **Ingest metrics** (`metrics.ingest`)
    - **Ingest events** (`events.ingest`)

2. Identify your Dynatrace site identifier from your Dynatrace URL. For example, if your Dynatrace environment URL is `https://abc12345.apps.dynatrace.com`, your environment ID is `abc12345`.

3. In your [control-plane configuration](https://docs.gatling.io/reference/install/cloud/private-locations/introduction/), in the section `system-properties`, add:

  ```bash
  control-plane {
    locations = [
      {
        system-properties {
          "gatling.enterprise.dt.api.key" = "<your Dynatrace api key>"
          "gatling.enterprise.dt.site" = "<your Dynatrace site identifier>"
        }
      }
    ]
  }
  ```

### Uninstall the Dynatrace integration

To remove the link between Gatling Enterprise Edition and Dynatrace, remove the lines containing `gatling.enterprise.dt` in your control-plane configuration.

### Events pushed to Dynatrace

Gatling Enterprise Edition generates custom information events for load test injection `start` and `end`.

All events are of type `CUSTOM_INFO` with the following properties:

**Short name**|**Property name**|**Description**
:-----|:-----|:-----
Source|`source`|Source reference for Gatling Enterprise events
Team|`team`|Name of the team that owns the test
Phase|`phase`|Phase of the injection (start or end)
Test|`test`|Test name
Run ID|`run_id`|ID of the run

See the official Dynatrace documentation for [exploring events](https://docs.dynatrace.com/docs/analyze-explore-automate/logs/lma-analysis/logs-and-events#advanced-mode).

### Metrics pushed to Dynatrace

Gatling Enterprise Edition pushes the following list of load test metrics to Dynatrace:

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

See the official Dynatrace documentation for [exploring metrics](https://docs.dynatrace.com/docs/analyze-explore-automate/explorer).

### Use metric dimensions to enhance your Dynatrace dashboard

#### Default Dimensions

Gatling Enterprise Edition pushes the following dimensions to Dynatrace:

**Short name**|**Dimension name**|**Description**
:-----|:-----|:-----
Team|`team`|Name of the team that owns the test
Test|`test`|Test name
Load generator|`load_generator`|Load generator reference integer starting with 0
Scenario|`scenario`|Scenario name
Status|`status`|Status of the run (ok or ko)
Run ID|`run_id`|ID of the run

#### Custom Dimensions

You can add custom dimensions by adding system properties, either at the control-plane level or in your test configuration (except for no-code tests):
`gatling.enterprise.dt.dimensions.<custom_dimension>` = `<your value>`

---

## HTTP Request Integration

### Using Gatling and Dynatrace to capture request attributes

Pass Gatling load test request attributes to Dynatrace using additional HTTP headers. Dynatrace can handle, extract, and tag information from incoming HTTP headers containing information such as:

- script name,
- test step name, and
- virtual user ID.

You can then filter your monitoring data based on the defined tags.

#### Configure Dynatrace extraction rules

You can use any HTTP headers or HTTP parameters to pass contextual information. To configure the extraction rules in Dynatrace reference the [extraction rules](https://docs.dynatrace.com/docs/platform-modules/applications-and-microservices/services/request-attributes/capture-request-attributes-based-on-web-request-data) documentation.

#### Add contextual information to headers

The header `x-dynatrace-test` is used in the following example with the following set of key/value pairs for the header:

| **Acronym** | **Full Term**            | **Description**                                                                                              |
|-------------|--------------------------|--------------------------------------------------------------------------------------------------------------|
| **VU**      | Virtual User ID          | A unique identifier for the virtual user who sent the request.                                               |
| **SI**      | Source ID                | Identifies the product that triggered the request (e.g., Gatling).                                           |
| **TSN**     | Test Step Name           | Represents a logical test step within the load testing script (e.g., Login, Add to Cart).                    |
| **LSN**     | Load Script Name         | Name of the load testing script that groups test steps into a multistep transaction (e.g., Online Purchase). |
| **LTN**     | Load Test Name           | Uniquely identifies a test execution (e.g., 6h Load Test – June 25).                                         |
| **PC**      | Page Context             | Provides information about the document loaded on the currently processed page.                              |

{{< img src="dynatrace.png" alt="Dynatrace Report" >}}

#### Defining a global signing function (example)

The idea here is to use [`sign`]({{< ref "/reference/script/http/protocol#sign" >}}) on the HttpProtocol to define a global signing function to be applied on all generated requests.

{{< include-code "dynatrace-sample" >}}