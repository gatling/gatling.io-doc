---
title: Public APIs
seotitle: Gatling Enterprise public APIs
description: Learn how to use the Gatling Enterprise public APIs with its Swagger (OpenAPI) documentation.
lead: Usage of the Gatling Enterprise public API
toc: false
swagger-ui: true
aliases:
  - /reference/execute/cloud/user/api/
---

The Gatling Enterprise API server also exposes a public API that you can use to trigger runs or fetch run results and metrics.

{{< alert info >}}
This API requires an `Authorization` HTTP header populated with an [API token]({{< ref "/reference/collaborate/admin/api-tokens" >}}).
{{< /alert >}}

{{< swagger-ui src="https://gatling.github.io/gatling-enterprise-api/openapi/openapi.yaml" >}}

## Help
- You have to provide the run ID as a query parameter to fetch other run metadata (load generators, remotes, hostnames, scenarios, groups, requests)
- The `from` and `to` query parameters for the `/series` endpoint are the lower and upper timestamp bounds of the time window you want to query. 
- You can fetch the total run time window from the `/runs` endpoint (`injectStart`, `injectEnd`).
- The returned percentiles by the API are: `min`, `p25`, `p50`, `p75`, `p80`, `p85`, `p90`, `p95`, `p99`, `p999`, `p9999` and `max`.
