---
title: Shared queues
seotitle: Gatling session scripting reference - shared queues
description: How to use shared queues to exchange data between virtual users.
lead: Use shared queues to pass values from one virtual user to another
date: 2026-09-29T12:00:00+02:00
---

Virtual users are isolated: each one has its own Session.
Shared queues let virtual users exchange data, for example one group of virtual users producing identifiers that another group consumes.

You reference a queue by name.
All the virtual users of a load generator that use the same name share the same queue.

## Put values into a queue

Use `put` to add a value at the end of the queue.
The value can be a literal, a Gatling EL string, or a function that reads the Session.

{{< include-code "shared-queue-put" >}}

## Take values from a queue

Use `take` to remove the value at the head of the queue and store it in the virtual user's Session, under the attribute name you pass.

{{< include-code "shared-queue-take" >}}

`take` waits until a value is available.
Use `timeout` to limit how long a virtual user waits.
Java, Kotlin and Scala accept a duration; JavaScript and TypeScript accept a number of seconds.

{{< include-code "shared-queue-take-timeout" >}}

## Poll values from a queue

Use `poll` to retrieve a value without waiting.

{{< include-code "shared-queue-poll" >}}

## Get the size of a queue

Use `size` to store the current number of elements in the queue into the Session.

{{< include-code "shared-queue-size" >}}
