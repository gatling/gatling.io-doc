---
title: HTTP Checks
seotitle: Gatling HTTP protocol reference - checks
description: How to use HTTP specific checks, such as status, header or currentLocation, to validate your HTTP payloads and capture elements and store them in the Session.
lead: Checks specific to HTTP requests
date: 2021-04-20T18:30:56+02:00
---

## HTTP Specific Check Type {#http-check-type}

The HTTP Check implementation provides the following built-ins:

### HTTP status

`status`

Targets the HTTP response status code.

{{< include-code "status" >}}

{{< alert tip >}}
If you don't define an explicit status check on HTTP requests or HTTP protocol, Gatling will perform an implicit check that will verify that the response status code is 2XX or 304.
{{< /alert >}}

### Page location

#### `currentLocation`

Targets the current page absolute URL.
Useful when following redirects in order to check if the landing page is indeed the expected one.

{{< include-code "currentLocation" >}}

#### `currentLocationRegex`

A version of [currentLocation]({{< ref "#currentlocation" >}}) that applies a Java Regular Expression to capture some information out of it, like in the generic [regex]({{< ref "/concepts/checks#regex" >}}).

It takes one single parameter:
* `pattern`  can be a plain `String`, a Gatling Expression Language `String` or a function.

{{< include-code "currentLocationRegex" >}}

### HTTP header

#### `header`

It takes one single parameter:
* `headerName`  can be a plain `String`, a Gatling Expression Language `String` or a function.

{{< include-code "header" >}}

#### `headerRegex`

A version of [header]({{< ref "#header" >}}) that applies a Java Regular Expression to capture some information out of it, like in the generic [regex]({{< ref "/concepts/checks#regex" >}}).

It takes two parameters:
* `headerName`  can be a plain `String`, a Gatling Expression Language `String` or a function.
* `pattern`  can be a plain `String`, a Gatling Expression Language `String` or a function.

{{< include-code "headerRegex" >}}
