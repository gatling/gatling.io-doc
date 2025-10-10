---
title: Public APIs
seotitle: Gatling Enterprise Edition public APIs
description: Learn how to use the Gatling Enterprise Edition public APIs with its Swagger (OpenAPI) documentation.
lead: Usage of the Gatling Enterprise Edition public API
date: 2021-03-08T12:50:24+00:00
toc: false
swagger-ui: true
aliases:
  - /reference/execute/cloud/user/api/
---

The Gatling Enterprise Edition API server exposes a public API that you can use to trigger runs or fetch run results and metrics.

{{< alert info >}}
This API requires an `Authorization` HTTP header populated with an [API token]({{< ref "/reference/administration/api-tokens" >}}).
{{< /alert >}}

## Helpful tips
- Provide the run ID as a query parameter to fetch other run metadata (load generators, remotes, hostnames, scenarios, groups, and requests)
- Fetch the total run time window from the `/runs` endpoint (`injectStart`, `injectEnd`).
- The `/summaries/requests` API returns the following percentiles: `min`, `p25`, `p50`, `p75`, `p80`, `p85`, `p90`, `p95`, `p99`, `p999`, `p9999` and `max`.


{{< swagger-ui src="https://gatling.github.io/gatling-enterprise-api/openapi/openapi.yaml" >}}

