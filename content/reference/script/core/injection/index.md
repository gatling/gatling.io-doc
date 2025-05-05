---
title: Injection
seotitle: Gatling injection scripting reference
description: How to define the way virtual users are started over time and injected into a scenario. Explain the difference between open and closed workload models and which type suits your application best.
lead: Injection profiles, differences between open and closed workload models
date: 2021-04-20T18:30:56+02:00
---

The definition of the injection profile of users is done with the `injectOpen` and `injectClosed` methods (just `inject` in Scala). This method takes as an argument a sequence of injection steps that will be processed sequentially.

## Open vs closed workload models

When it comes to load model, systems behave in 2 different ways:

* Closed systems, where you control the concurrent number of users
* Open systems, where you control the arrival rate of users

Make sure to use the proper load model that matches the load your live system experiences.

Closed system are system where the number of concurrent users is capped.
At full capacity, a new user can effectively enter the system only once another exits.

Typical systems that behave this way are:

* call center where all operators are busy
* ticketing websites where users get placed into a queue when the system is at full capacity

On the contrary, open systems have no control over the number of concurrent users: users keep on arriving even though applications have trouble serving them.
Most websites behave this way.

{{< alert warning >}}
Don't reason in terms of  concurrent users if your system can't push excess traffic into a queue.

If you're using a closed workload model in your load tests while your system actually is an open one, your test is broken, and you're testing some different imaginary behavior.
In such case, when the system under test starts to have some trouble, response times will increase, journey time will become longer, so number of concurrent users will increase
and the virtual users injection will slow down to match the imaginary cap you've set.
{{< /alert >}}

You can read more about open and closed models [here](https://www.usenix.org/legacy/event/nsdi06/tech/full_papers/schroeder/schroeder.pdf).

{{< alert warning >}}
Open and closed workload models are antinomical and you can't mix them in the same injection profile.
{{< /alert >}}

## Open model

### Core components {#open-model-core}

{{< include-code "open-injection" >}}

The building blocks for open model profile injection are:

1. `nothingFor(duration)`: Pause for a given duration.
2. `atOnceUsers(nbUsers)`: Injects a given number of users at once.
3. `rampUsers(nbUsers).during(duration)`: Injects a given number of users distributed evenly on a time window of a given duration.
4. `constantUsersPerSec(rate).during(duration)`: Injects users at a constant rate, defined in users per second, during a given duration. Users will be injected at regular intervals.
5. `constantUsersPerSec(rate).during(duration).randomized`: Injects users at a constant rate, defined in users per second, during a given duration. Users will be injected at randomized intervals.
6. `rampUsersPerSec(rate1).to(rate2).during(duration)`: Injects users from starting rate to target rate, defined in users per second, during a given duration. Users will be injected at regular intervals.
7. `rampUsersPerSec(rate1).to(rate2).during(duration).randomized`: Injects users from starting rate to target rate, defined in users per second, during a given duration. Users will be injected at randomized intervals.
8. `stressPeakUsers(nbUsers).during(duration)`: Injects a given number of users following a smooth approximation of the [heaviside step function](http://en.wikipedia.org/wiki/Heaviside_step_function) stretched to a given duration.

{{< alert tip >}}
Rates can be expressed as fractional values.
{{< /alert >}}

### Stairs {#open-model-stairs}

Quite often, you'll want to build capacity tests where you progressively increase the load with a sequence of ramps and levels.
While it's entirely possible to build this kind of **open model** injection profile manually from individual blocks,
Gatling provides a built-in helper named `incrementUsersPerSec`.

{{< include-code "incrementUsersPerSec" >}}

`separatedByRampsLasting` and `startingFrom` are both optional.
If you don't specify a ramp, the test will jump from one level to another as soon as it is finished.
If you don't specify the number of starting users the test will start at 0 user per sec and will go to the next step right away.

## Closed model

### Core components {#closed-model-core}

{{< include-code "closed-injection" >}}

The building blocks for closed model profile injection are:

1. `constantConcurrentUsers(nbUsers).during(duration)`: Inject so that number of concurrent users in the system is constant
2. `rampConcurrentUsers(fromNbUsers).to(toNbUsers).during(duration)`: Inject so that number of concurrent users in the system ramps linearly from a number to another

{{< alert warning >}}
Ramping down the number of concurrent users won't force the existing users to interrupt.
The only way for virtual users to terminate is to complete their scenario.
{{< /alert >}}

### Stairs {#closed-model-stairs}

Quite often, you'll want to build capacity tests where you progressively increase the load with a sequence of ramps and levels.
While it's entirely possible to build this kind of **closed model** injection profile manually from individual blocks,
Gatling provides a built-in helper named `incrementConcurrentUsers`.

{{< include-code "incrementConcurrentUsers" >}}

`separatedByRampsLasting` and `startingFrom` are both optional.
If you don't specify a ramp, the test will jump from one level to another as soon as it is finished.
If you don't specify the number of starting users the test will start at 0 concurrent user and will go to the next step right away.

## Chaining injection steps

For a given scenario, you can pass a sequence of injection steps.
They must all be of the model kind.
The next injection step will start when all the virtual users defined by the current one have started.

{{< include-code "chainingInjectionSteps" >}}

## Concurrent scenarios

You can configure multiple scenarios in the same `setUp` block to start at the same time and execute concurrently.

{{< include-code "concurrentScenarios" >}}

## Sequential scenarios

It's also possible with `andThen` to chain scenarios, so that children scenarios start once all the users in the parent scenario terminate.

{{< include-code "andThen" >}}

When chaining `andThen` calls, Gatling will define the new children to only start once all the users of the previous children have terminated, descendants included.

## Disabling Gatling Enterprise load sharding

By default, Gatling Enterprise will distribute your injection profile amongst all load generators when running a distributed test from multiple nodes.

This might not be the desired behavior, typically when running a first initial scenario with one single user in order to fetch some auth token to be used by the actual scenario.
Indeed, only one node would run this user, leaving the other nodes without an initialized token.

You can use `noShard` to disable load sharding. In this case, all the nodes will use the injection and throttling profiles as defined in the Simulation.

{{< include-code "noShard" >}}
