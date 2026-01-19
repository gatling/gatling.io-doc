---
title: Various test limits
menutitle: Limits
seotitle: Various test limits applied when running tests on Gatling Enterprise Edition
description: Learn about the various test limits applied when running tests on Gatling Enterprise Edition
lead: Various test limits applied when running tests on Gatling Enterprise Edition

---

## Metrics limits

The number of different metrics is limited:
* to not saturate our platform
* because metrics only make sense if they aggregate lots of events. Unique events are useless in a load test.

| Metric type                  | Limit | Overflow consequence |
|------------------------------|-------|----------------------|
| Unique scenario names        | 100   | Test crash           |
| Unique group names           | 500   | Test crash           |
| Unique request names         | 2000  | Test crash           |
| Unique error messages        | 2000  | Test crash           |
| Unique hostnames             | 1000  | Test crash           |
| Unique remote addresses      | 1000  | Test crash           |
| Maximum error message length | 5000  | Truncation           |

## Important timeouts

Please be aware that some timeouts are applied the different phases of your tests.

| Description                                   | Duration   | Possible cause                                                                                                                                                                                                                       |
|-----------------------------------------------|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Time to build a package from source           | 5 minutes  | Cloning the git repository takes too long.<br>Compiling the sources takes too long.<br>The Control Plane can't connect to the private package storage because of network rules.<br>Cleaning up the working directory takes too long. |
| Time to spawn the load generators             | 5 minutes  | Spawning the load generators takes too long, in particular when having to spawn new nodes on a Kubernetes cluster.                                                                                                                   |
| Time to load the simulation                   | 5 minutes  | The constructor of the simulation takes too long, typically because of custom code.<br>The load generators can't connect to Gatling Enterprise because of network rules.                                                             |
| Time to upload stats                          | 10 seconds | A load generator has stopped sending stats to Gatling Enterprise, typically because its CPU or I/O is saturated.                                                                                                                     | 
| Time to execute the simulation's "after" hook | 5 minutes  | The "after" hook in the simulation takes too long.                                                                                                                                                                                   |
