---
title: Credit consumption
menutitle: Credit consumption
seotitle: Credit consumption in Gatling Enterprise Edition
description: Learn how tests consume credits in Gatling Enterprise Edition.
lead: Learn how tests consume credits in Gatling Enterprise Edition.
date: 2026-01-20T09:00:00+00:00
---

Please note the following rules regarding credit consumption:

* A test consumes one credit, per load generator, per minute.
* Credits are consumed the same way for private locations (hosted on your end) and managed locations (hosted on our end).
* Credits are consumed as soon as the load generators instances or containers are up. In particular, the simulation initialization (that might include long custom code) is charged.
* You are still charged one credit per load generator if the test fails to start because of an error on your end, eg:
  * your infrastructure not being able to spawn your private location load generators
  * crash or timeout in simulation initialization caused by your code
