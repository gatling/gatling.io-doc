---
title: Credit consumption
menutitle: Credit consumption
seotitle: Credit consumption in Gatling Enterprise Edition
description: Learn how tests consume credits in Gatling Enterprise Edition.
lead: Learn how tests consume credits in Gatling Enterprise Edition.
date: 2026-01-20T09:00:00+00:00
---

Please note the following rules regarding credit consumption.

* A test consumes one credit, per load generator, per minute.
* We charge the time when load traffic is generated but also the time taken by any custom code, typically during simulation initialization.
* We still charge a minimum of one credit per load generator if the test fails to start because of a crash or a timeout on your end, eg:
  * when cloning the git repository containing your test sources;
  * when compiling your cloned test sources;
  * when spawning your private location load generators;
  * when executing some custom code during simulation initialization.
* Credits are consumed the same way for private locations (hosted on your end) and managed locations (hosted on our end).
