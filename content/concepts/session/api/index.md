---
title: Session API
seotitle: Gatling session scripting reference - session API
description: How to store and fetch data programmatically for your virtual users' Session, typically when using functions.
lead: Programmatically handle virtual users' data
date: 2021-04-20T18:30:56+02:00
---

## Concept

### Going stateful

{{< alert tip >}}
In order to build realistic tests and draw conclusions that are valid for your live system, you must ensure your virtual users use application data in a realistic fashion.
{{</ alert >}}

In most load testing use cases, it's important that the virtual users don't play the same data.
Otherwise, you might end up not testing your application but your caches.

Moreover, if you're running an application on a Java Virtual Machine, the Just In Time compiler (JIT) will make impressive optimizations but your system will behave in a very different fashion than in your production environment.

### Session

Session is a virtual user's state.

Basically, it's a `Map<String, Object>`: a map with key Strings.
In Gatling, entries in this map are called **Session attributes**.

{{< alert tip >}}
A Gatling scenario is a workflow where every step is an `Action`.
`Session`s are the messages that are passed along a scenario workflow.
{{< /alert >}}

### Injecting data

The first step is to inject state into the virtual users.

There's 3 ways of doing that:

* using [Feeders]({{< ref "feeders" >}})
* extracting data from responses and saving them, e.g. with [Check's saveAs]({{< ref "/concepts/checks#saving" >}})
* programmatically with the Session API

### Fetching data

Once you have injected data into your virtual users, you'll naturally want to retrieve and use it.

There are 2 ways of implementing this:

* using Gatling's [Expression Language]({{< ref "el" >}})
* programmatically with the Session API

{{< alert tip >}}
If Gatling complains that an attribute could not be found, check that:
* you don't have a typo in a feeder file header
* you don't have a typo in a Gatling EL expression
* your feed action is properly called (e.g. could be properly chained with other action because a dot is missing)
* the check that should have saved it actually failed
{{< /alert >}}

## Session API

### Setting attributes

{{< include-code "set" >}}

{{< alert warning >}}
`Session` instances are immutable, meaning that methods such as `set` return a new instance and leave the original instance unmodified!
{{< /alert >}}

{{< include-code "sessions-are-immutable" >}}

### Getting attributes

{{< include-code "get" >}}

### Virtual user properties

{{< include-code "properties" >}}

### Handling session state

{{< include-code "state" >}}
