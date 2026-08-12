---
title: Credit consumption
menutitle: Credit consumption
seotitle: Credit consumption in Gatling Enterprise Edition
description: Learn how tests consume credits in Gatling Enterprise Edition.
lead: Learn how tests consume credits in Gatling Enterprise Edition.
date: 2026-01-20T09:00:00+00:00
---

A test run consumes **1 credit per load generator per minute**.

{{< alert info >}}
Credit consumption includes simulation initialization time.
{{< /alert >}}

**Once all load generators are deployed, credits are consumed every minute until the execution ends.** 

_The first minute is not charged again, as it has already been accounted for._

{{< alert warning >}}
[Private Deployment]({{< ref "/reference/deploy/private-locations/introduction" >}}) and [Building From Sources]({{< ref "/reference/deploy/private-locations/build-from-git" >}}) are not charged.

If either fails, however, the equivalent of one minute of execution time is charged.
{{< /alert >}}
