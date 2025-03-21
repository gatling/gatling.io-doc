---
title: Update Your API Clients
private: true
---

## Summary

Your organization needs to update its Gatling API clients to continue having uninterrupted access to Gatling Enterprise. Older API clients will stop working with Gatling Enterprise on May 20th, 2025.   

## What you should know

Six months ago, we migrated our API to `api.gatling.io`. At this time, we also released updates of our API clients (build tool plugins and CI platforms plugins) to target the new endpoint:

| API Client             | minimum supported version | latest version |
|------------------------|:-------------------------:|:--------------:|
| gatling-maven-plugin   |           4.10.0          |     4.16.2     |
| gatling-sbt            |           4.10.0          |     4.13.2     |
| gatling-gradle-plugin  |          3.12.0.1         |     3.13.5.1   |
| Jenkins plugin         |           1.18.0          |     1.18.0     |
| Bamboo plugin          |           1.18.0          |     1.18.0     |
| TeamCity plugin        |           1.18.0          |     1.18.0     |
| Gatling GitHub Actions |            1.2            |      1.2.1     |

In order to not break existing setups and give our users time to upgrade, we've also kept on exposing our API on `cloud.gatling.io`.
Our API will stop being exposed on `cloud.gatling.io` on May, 20th. At this time, outdated clients will cease functioning.

## What you should do

**Upgrade your API clients before May, 20th so you're not impacted by the endpoint termination.**

You're seeing this message because we noticed that you're organization is still using the old endpoint, meaning using outdated API clients. We recommend upgrading to the latest available version. If you have any questions, please contact your CSM using the support portal. 
