---
title: Validation
seotitle: Gatling session scripting reference - validation
description: How to use the Validation monad to handle composable failures in Scala functions. Intended for advanced Scala developers only.
lead: Manipulate Session data in a monadic way (advanced Scala only)
date: 2021-04-20T18:30:56+02:00
aliases:
  - /reference/script/core/session/validation/
---

## Concept

{{< alert tip >}}
This section is for advanced Scala users only. Don't bother otherwise.
{{</ alert >}}

`Validation` is an abstraction for describing something that can either be a valid result, or an error message.
Scalaz has a great implementation, but Gatling has its own, less powerful but much more simple.

The benefit of using this abstraction is that it's composable, so one can chain operations that consume and produce validations without having to determine on every operation if it's actually dealing with a succeeding operation or not.

`Validation[T]` has a type parameter `T` that is the type of the value in case of a success.

It has 2 implementations:

* `Success[T](value: T)` that wraps a value in case of a success
* `Failure(message: String)` that wraps a String error message

The goal of such an abstraction is to deal with "unexpected results" in a composable and cheap way instead of using Exceptions.

## Usage

### Creating instances

First, import the `validation` package:

{{< include-code "ValidationSample.scala#import" scala >}}

Then, you can either directly create new instance of the case classes:

{{< include-code "ValidationSample.scala#with-classes" scala >}}

or use the helpers:

{{< include-code "ValidationSample.scala#with-helpers" scala >}}

### Manipulating

`Validation` can be used with pattern matching:

{{< include-code "ValidationSample.scala#pattern-matching" scala >}}

`Validation` has the standard Scala "monadic" methods such as:

* `map`:expects a function that takes the value if it's a success and return a value.
* `flatMap`: expects a function that takes the value if it's a success and return a new `Validation`

Basically, `map` is used to **chain with an operation that can't fail**, hence return a raw value:

{{< include-code "ValidationSample.scala#map" scala >}}

`flatMap` is used to **chain with an operation that can fail**, hence return a `Validation`:

{{< include-code "ValidationSample.scala#flatMap" scala >}}

In both cases, the chained function is not called if the original `Validation` was a `Failure`:

{{< include-code "ValidationSample.scala#map-failure" scala >}}

You can also use Scala *"for comprehension"* syntactic sugar.

For the impatient, just consider it's like a super loop that can iterate other multiple objects of the same kind (like embedded loops) and can iterate over other things that collections, such as `Validation`s or `Option`s.

Here's what the above example would look like using a *"for comprehension"*:

{{< include-code "ValidationSample.scala#for-comp" scala >}}
