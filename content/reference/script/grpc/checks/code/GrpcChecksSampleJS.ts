/*
 * Copyright 2011-2026 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { exec } from "@gatling.io/core";
import {
  asciiHeader,
  asciiTrailer,
  binaryHeader,
  binaryTrailer,
  grpc,
  response,
  statusCode,
  statusDescription
} from "@gatling.io/grpc";

const message = {};

// Checks

const checks = () => {
  grpc("status checks")
    .unary("example.ExampleService/Example")
    .send(message)
    .check(
      //#statusCode
      statusCode().is("OK")
      //#statusCode
      ,
      //#statusDescription
      statusDescription().is("actual status description")
      //#statusDescription
      ,
      //#statusDescriptionIsNull
      statusDescription().isNull()
      //#statusDescriptionIsNull
      /*
      //#statusCause
      NOT SUPPORTED
      //#statusCause
      //#statusCauseIsNull
      NOT SUPPORTED
      //#statusCauseIsNull
      */
    );

  grpc("header checks")
    .unary("example.ExampleService/Example")
    .send(message)
    .check(
      /*
      //#header
      INFO:
      The `header` method is not directly supported by Gatling JS. We recommend using
      [`asciiHeader`]({{< ref "/reference/script/grpc/checks#asciiheader" >}}) or
      [`binaryHeader`]({{< ref "/reference/script/grpc/checks#binaryheader" >}}) instead.
      //#header
      //#headerMultiValued
      INFO:
      The `header` method is not directly supported by Gatling JS. We recommend using
      [`asciiHeader`]({{< ref "/reference/script/grpc/checks#asciiheader" >}}) or
      [`binaryHeader`]({{< ref "/reference/script/grpc/checks#binaryheader" >}}) instead.
      //#headerMultiValued
      */
      //#asciiHeader
      asciiHeader("header").is("value")
      //#asciiHeader
      ,
      //#binaryHeader
      binaryHeader("header-bin").is([118, 97, 108, 117, 101])
      //#binaryHeader
    );

  grpc("trailer checks")
    .unary("example.ExampleService/Example")
    .send(message)
    .check(
      /*
      //#trailer
      INFO:
      The `trailer` method is not directly supported by Gatling JS. We recommend using
      [`asciiTrailer`]({{< ref "/reference/script/grpc/checks#asciitrailer" >}}) or
      [`binaryTrailer`]({{< ref "/reference/script/grpc/checks#binarytrailer" >}}) instead.
      //#trailer
      //#trailerMultiValued
      INFO:
      The `trailer` method is not directly supported by Gatling JS. We recommend using
      [`asciiTrailer`]({{< ref "/reference/script/grpc/checks#asciitrailer" >}}) or
      [`binaryTrailer`]({{< ref "/reference/script/grpc/checks#binarytrailer" >}}) instead.
      //#trailerMultiValued
      */
      //#asciiTrailer
      asciiTrailer("header").is("value")
      //#asciiTrailer
      ,
      //#binaryTrailer
      binaryTrailer("trailer-bin").is([118, 97, 108, 117, 101])
      //#binaryTrailer
    );

  grpc("message checks")
    .unary("example.ExampleService/Example")
    .send(message)
    .check(
      //#message
      response((response) => response.message)
        .is("actual result")
      //#message
    );
};

// Priorities and scope

const ordering = () => {
  //#ordering
  grpc("unary checks")
    .unary("example.ExampleService/Example")
    .send(message)
    .check(
      response((response) => response.message).is("message value"),
      asciiTrailer("trailer").is("trailer value"),
      asciiHeader("header").is("header value"),
      statusCode().is("OK")
    );
  //#ordering

  //#unaryChecks
  grpc("unary")
    .unary("example.ExampleService/Example")
    .send(message)
    .check(
      response((response) => response.message).is("message value")
    );
  //#unaryChecks

  //#serverStreamChecks
  const serverStream =
    grpc("server stream")
      .serverStream("example.ExampleService/Example")
      .check(
        response((response) => response.message).is("message value")
      );

  exec(
    serverStream.send(message),
    serverStream.awaitStreamEnd()
  );
  //#serverStreamChecks

  //#clientStreamChecks
  const clientStream =
    grpc("client stream")
      .clientStream("example.ExampleService/Example")
      .check(
        response((response) => response.message).is("message value")
      );

  exec(
    clientStream.start(),
    clientStream.send(message),
    clientStream.halfClose(),
    clientStream.awaitStreamEnd()
  );
  //#clientStreamChecks

  //#bidiStreamChecks
  const bidiStream =
    grpc("bidi stream")
      .bidiStream("example.ExampleService/Example")
      .check(
        response((response) => response.message).is("message value")
      );

  exec(
    bidiStream.start(),
    bidiStream.send(message),
    bidiStream.halfClose(),
    bidiStream.awaitStreamEnd()
  );
  //#bidiStreamChecks
};

const reconcile = () => {
  //#reconcile
  const serverStream =
    grpc("server stream")
      .serverStream("example.ExampleService/Example")
      .check(
        // Overwrites the 'result' key for each message received
        response((response) => response.message).saveAs("result")
      );

  exec(
    serverStream.send(message),
    serverStream.awaitStreamEnd((main, forked) =>
      // Message checks operate on a forked session, we need
      // to reconcile it with the main session at the end
      main.set("result", forked.get("result"))
    ),
    exec((session) => {
      // 'result' contains the last message received
      const result = session.get("result");
      return session;
    })
  );
  //#reconcile
};
