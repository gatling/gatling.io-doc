---
title: Timings
description: Description of the different metrics reported in Gatling's HTML reports.
lead: "Learn about all the recorded metrics: users start rate, concurrent users, response times and counts."
date: 2021-05-20T18:30:56+02:00
---

As Gatling runs and executes requests, several timings are recorded, which make up the basis of all forms of reporting in Gatling: console, HTML reports, etc...

## Users start rate

The number of users started per second. This would match your injection profile if you're using an open one.

## Number of concurrent users

This would match your injection profile if you're using a closed one.

## Requests

### Response Time

The response time is the elapsed time between:

* the instant Gatling tries to send a request. It accounts for:
  * DNS resolution time (might be bypassed if it's already cached). Note that the DNS resolution time metric is available in Gatling Enterprise Edition.
  * TCP connect time (might be bypassed if a keep-alive connection is available in the connection pool). Note that the TCP connect time metric is available in Gatling Enterprise Edition.
  * TLS handshake time (might be bypassed if a keep-alive connection is available in the connection pool). Note that the TLS handshake time metric is available in Gatling Enterprise Edition.
  * HTTP round trip
* the instant Gatling receives a complete response or experiences an error (timeout, connection error, etc)

## Groups

### Count

The counts are the number of group executions, not the sum of the counts of each individual request in that group.

### Response Time

This metric provides the response times for all the requests belonging to the specified group.

In Gatling Enterprise Edition, this metric is available in the "Request" stats.

{{< alert warning >}}
This metric is only available in Gatling Enterprise Edition, not in Gatling Community Edition HTML reports.
{{< /alert >}}

### Cumulated Response Time

Group cumulated response time is the time in a group when requests are flying: requests' response time and resources start to end duration.
In short, it's the group duration minus the pauses.

In Gatling Enterprise Edition, this metric is available in the "Group" stats, in the "Cumulated Response Time" chart.
In Gatling Community Edition HTML reports, this metric is available when selecting a group.

### Duration

Group duration is the elapsed time between the instant a virtual user enters a group and the instant it exits.

In Gatling Enterprise Edition, this metric is available in the "Group" stats, in the "Duration" chart.
In Gatling Community Edition HTML reports, this metric is available when selecting a group.

{{< alert tip >}}
The Gatling Community Edition HTML reports can only display one metric.
By default, they display the "cumulated response time" metric.
You can switch to displaying the "duration" metric: turn the `charting.charting.useGroupDurationMetric` option to `true` in `gatling.conf` before generating the reports.
{{< /alert >}}
