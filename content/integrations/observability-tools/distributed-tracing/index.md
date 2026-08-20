---
title: Distributed tracing for Gatling
menutitle: Distributed tracing
seotitle: Trace Gatling requests end to end with OpenTelemetry
description: Export Gatling requests as OpenTelemetry spans and correlate them with your application traces.
lead: Export Gatling requests as OpenTelemetry spans and correlate them with your application traces.
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Introduction

Load-test metrics tell you *that* a request was slow. Distributed tracing tells you *where* the time went.

When distributed tracing is enabled, Gatling propagates a W3C trace context on the requests it sends and exports its own view of each of those requests as an OpenTelemetry span. Your application, already instrumented, continues the same trace on its side. Both halves meet in your observability backend under a single trace ID.

The two views are complementary:

- Gatling measures what the client experienced: total time on the wire, from the injector.
- Your application measures what it spent internally: controllers, database calls, downstream services.

The gap between the two is network time and queueing.

Spans are exported over OTLP (HTTP/protobuf), so any backend that accepts OTLP works: Datadog, Dynatrace, Elastic, Grafana Tempo, Honeycomb, Jaeger, New Relic, or an OpenTelemetry Collector you run yourself.

## Prerequisites

- Gatling 3.5 or later, which is every version Gatling Enterprise supports.
- An OTLP traces endpoint reachable from your load generators to export parent spans.
- An application under test that is instrumented and configured to **continue an incoming trace**. See [Requirements on the application under test](#requirements-on-the-application-under-test).

## Enable distributed tracing

Tracing turns itself on when an endpoint is configured, and stays off otherwise. There is no separate switch.

You can set the properties either on a private location, which applies to every test running there, or on a single test.

### On a private location

In your [control-plane configuration]({{< ref "/reference/deploy/private-locations/introduction" >}}), in the section `system-properties`, add:

```hocon
control-plane {
  locations = [
    {
      system-properties {
        "gatling.enterprise.tracing.endpoint" = "<your OTLP traces endpoint>" # eg https://otlp.example.com/v1/traces
        "gatling.enterprise.tracing.http.headers" = "<extra HTTP headers, eg authentication>" # optional, eg api-key=key,other=value (values must be percent-encoded)
        "gatling.enterprise.tracing.sampledRequestsPerSecond" = "10" # optional, default is 10
        "gatling.enterprise.tracing.useProxy" = "<true to use the same proxy as for the Gatling API>" # optional, default is false
      }
    }
  ]
}
```

### On a single test

Set the same properties as Java system properties in your [test configuration]({{< ref "/reference/run-tests/tests/optional-config" >}}). This is also the way to enable tracing on Gatling-managed locations.

{{< alert warning >}}
Authentication headers are secrets. System properties set on a test are saved in the run snapshot and are readable by anyone with read access to the run. See the [test configuration reference]({{< ref "/reference/run-tests/tests/optional-config" >}}) for how to keep a property out of the snapshot.
{{< /alert >}}

### Disable distributed tracing

Remove the lines containing `gatling.enterprise.tracing` from your configuration. Without an endpoint, Gatling neither propagates trace context nor exports spans.

## How sampling works

Gatling traces a fixed number of requests per second and leaves the rest untouched.

`sampledRequestsPerSecond` is the rate **for the whole run**, not per load generator. Gatling splits it across the load generators of the run, so a run at the default of 10 sends about 10 traced requests per second in total whether it runs on one injector or forty.

Details that affect what you see:

- The budget is refreshed every second. Requests that arrive once the budget of the current second is spent are not traced, and are sent without trace context.
- The rate is a target, not a hard ceiling. Under heavy concurrency a second may carry slightly more than the configured number.
- If the rate is lower than the number of load generators, some load generators get a share of zero and trace nothing.

## What Gatling sends

### Trace context on the wire

On each traced request, Gatling sets two headers:

```
traceparent: 00-<trace-id>-<span-id>-03
tracestate: ot=th:0,dd=s:2
```

The `traceparent` flags are `03`: `sampled`, plus the `random-trace-id` bit of [W3C Trace Context Level 2](https://www.w3.org/TR/trace-context-2/).

Both `tracestate` entries mean that the trace was deliberately kept and must not be sampled again: `ot=th:0` for OpenTelemetry, `dd=s:2` for Datadog. A given SDK reads its own entry and ignores the others.

### Spans

One span per traced request, of kind `CLIENT`. The instrumentation scope is `io.gatling.enterprise`, and the span name is the request name from your simulation.

Resource attributes, on every span:

**Resource attribute**|**Description**
:-----|:----------------------------|
`service.name`|the `gatling.enterprise` string
`service.instance.id`|the run id and the index of the load generator within the run
`run.id`|the id of the test run
`team.name`|the name of the team the test belongs to
`test.name`|the name of the test

Span attributes:

**Span attribute**|**Description**
:-----|:----------------------------|
`http.request.method`|the HTTP method
`url.full`|the full request URL
`http.response.status_code`|the response status code, absent when no response was received
`gatling.scenario`|the scenario the virtual user belongs to
`gatling.group`|the enclosing Gatling groups, slash-separated, absent when the request is not in a group

A failed request produces a span with an `ERROR` status carrying the Gatling error message.

### What is not traced

- **Silent requests** are never traced, and never carry trace context.
- **Redirects and other follow-ups** continue the trace of the request that triggered them instead of starting a new one, and do not consume sampling budget.
- **Sub-timings** such as DNS resolution, TCP connect and TLS handshake are not reported as child spans. They are available as load-test metrics instead.

## Requirements on the application under test

For the two halves of a trace to meet, your application must continue the trace it receives rather than start its own. Its instrumentation needs to:

- **Extract W3C trace context.** Gatling only sends `traceparent` and `tracestate`, no proprietary headers. If your SDK is configured with a different propagation format, it will not see Gatling's context and will start an unrelated trace.
- **Respect the incoming sampling decision.** A parent-based sampler does this. A sampler that decides independently of the parent may drop a trace Gatling asked to keep.

A quick way to check the plumbing before running a full test is to send a single request by hand with a trace context you chose, then look for that trace ID in your backend. The trace should be there, with your application's server span at the root of its side.

## Current limitations

- **The service name is fixed** to `gatling.enterprise` and cannot be overridden. Traces from Gatling appear under that service in your backend.
- **HTTP requests only.** Other protocols do not report spans.

## Troubleshooting

**No spans arrive.** Check the load generator logs for the run. Export failures are logged as warnings there, with the HTTP status returned by your endpoint. A `401` or `403` points to the authentication header or the wrong regional endpoint; a name resolution failure points to the endpoint host or to egress from the load generator.

**Spans arrive but are not part of the application's trace.** The trace context is not being continued on the application side. Check its propagation format and its sampler, as described in [Requirements on the application under test](#requirements-on-the-application-under-test).

**Fewer traces than expected.** Remember that the configured rate is shared across the whole run, and that follow-up requests reuse the trace of the request that triggered them rather than counting as new ones.

**Malformed authentication headers.** If the `http.headers` value cannot be parsed, tracing is disabled for the run and a warning is logged.
