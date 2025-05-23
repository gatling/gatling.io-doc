---
menutitle: Project licenses
title: Gatling project licenses
seotitle: Licenses for all Gatling projects
description: Gatling is licensed under the Apache License v2.0 and other specific licenses.
lead: Gatling is licensed under the Apache License v2.0 and other specific licenses.
date: 2021-04-20T18:30:56+02:00
---

## Developer motivations

We try as much as possible to distribute Gatling and its side projects under Apache License v2.0.
As much as possible means that we sometimes have to cope with some dependencies that are not Apache License v2.0 compatible or even not open source friendly.

Copyright of the source code and the Gatling logos belongs to GatlingCorp.

## Main Gatling project

Gatling is licensed under the Apache License v2.0 available here: [HTML](http://www.apache.org/licenses/LICENSE-2.0.html) | [TXT](http://www.apache.org/licenses/LICENSE-2.0.txt)

## Highcharts based modules

The default report generation module makes use of Highcharts and Highstock, two great javascript libraries developed by Highsoft.
These libraries are neither open source, nor free for our use case.

We purchased developer licenses and got an agreement with Highsoft so we could distribute their libraries with some restrictions.
As a consequence, this module cannot be open sourced and has to be developed in a separated project: [gatling-highcharts](https://github.com/gatling/gatling-highcharts).
However, for convenience reasons, we distribute a ready to use bundle.

Making it short, we distribute this module for free with the following restrictions:

* one can not edit the source code without buying a Highcharts license for working on Gatling
* the shipped Highcharts or Highstock copies cannot be used outside of Gatling standard usage

For more information, please refer to the [gatling-highcharts license]({{< ref "./highcharts" >}}) or feel free to [ask](https://community.gatling.io).

## Gatling Enterprise Component License

Certain components meant to be used with Gatling Enterprise are licensed under the
[Gatling Enterprise Component License]({{< ref "./enterprise-component" >}}).
