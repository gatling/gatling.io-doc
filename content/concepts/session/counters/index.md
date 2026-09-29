---
title: Counters
seotitle: Gatling session scripting reference - counters
description: How to use counters to generate incrementing values and store them in the virtual users' Session.
lead: Use counters to generate incrementing values, such as unique identifiers
date: 2026-09-29T12:00:00+02:00
---

Counters store an incrementing value into the virtual user's Session.
Use them for example to generate unique identifiers, without having to write a custom feeder or function.

The name you pass to `counter` is the name of the Session attribute that's set when a virtual user reaches this action.
In the example below, each virtual user reaching the counter gets the next value stored in its Session under the `id` attribute, which you can then use anywhere, for example with the Gatling EL expression `#{id}`.

{{< include-code "counter-basic" >}}

## Configure a counter

| Method | Description | Default |
| --- | --- | --- |
| `startingAt` | The first value to emit. Must be positive. | `0` |
| `withIncrement` | The gap between 2 successive values. | `1` |
| `upTo` | The exclusive upper bound, except for `Integer.MAX_VALUE`. When reached, the load generator stops, unless you use `wrapAround`. | `Integer.MAX_VALUE` |

## Wrap around at the upper bound

By default, Gatling stops the load generator when the counter reaches its upper bound.
Use `wrapAround` to start over from the first value instead.

{{< alert warning >}}
Values are no longer unique once the counter wraps around.
{{< /alert >}}

{{< include-code "counter-wrap-around" >}}

## Track a counter for each virtual user

By default, all the virtual users of a load generator share one single sequence of values.
Use `perUser` to track the counter independently for each virtual user, so they all get the very same sequence.

Only in this mode, `startingAt`, `withIncrement` and `upTo` accept a Gatling EL string or a function instead of a static value.
Otherwise, building the Simulation fails.

{{< include-code "counter-per-user" >}}

## Keep values unique across a cluster

When running with Gatling Enterprise, use `shard` to distribute the values evenly amongst all the load generators, so they remain unique cluster wide.
Each load generator only gets a slice of the range, so the values emitted by the whole cluster have holes.
Outside of Gatling Enterprise, `shard` has no effect.

{{< include-code "counter-shard" >}}
