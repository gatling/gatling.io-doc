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

import { exec, scenario } from "@gatling.io/core";
import { grpc, response, statusCode } from "@gatling.io/grpc";

const message = {};
const message1 = {};
const message2 = {};

const unaryInstantiation = () => {
  //#unaryInstantiation
  // with a static value
  grpc("request name").unary("example.ExampleService/Example");
  // with a Gatling EL string
  grpc("#{requestName}").unary("example.ExampleService/Example");
  // with a function
  grpc((session) => session.get("requestName"))
    .unary("example.ExampleService/Example");
  //#unaryInstantiation

  //#unaryLifecycle
  const scn = scenario("scenario name").exec(
    // Sends a request and awaits a response, similarly to regular HTTP requests
    grpc("request name")
      .unary("example.ExampleService/Example")
      .send({ message: "hello" })
  );
  //#unaryLifecycle
};

const serverStreamInstantiation = () => {
  //#serverStreamInstantiation
  // with a static value
  grpc("request name").serverStream("example.ExampleService/Example");
  // with a Gatling EL string
  grpc("#{requestName}").serverStream("example.ExampleService/Example");
  // with a function
  grpc((session) => session.get("requestName"))
    .serverStream("example.ExampleService/Example");
  //#serverStreamInstantiation

  //#serverStreamLifecycle
  const stream =
    grpc("request name").serverStream("example.ExampleService/Example");

  const scn = scenario("scenario name").exec(
    stream.send(message),
    stream.awaitStreamEnd()
  );
  //#serverStreamLifecycle

  //#serverStreamNames
  const stream1 =
    grpc("request name")
      // specify streamName initially
      .serverStream("example.ExampleService/Example", "first-stream");
  const stream2 =
    grpc("request name")
      .serverStream("example.ExampleService/Example")
      // or use the streamName method
      .streamName("second-stream");

  exec(
    stream1.send(message),
    stream2.send(message)
  );
  // both streams are concurrently open at this point
  //#serverStreamNames
};

const clientStreamInstantiation = () => {
  //#clientStreamInstantiation
  // with a static value
  grpc("request name").clientStream("example.ExampleService/Example");
  // with a Gatling EL string
  grpc("#{requestName}").clientStream("example.ExampleService/Example");
  // with a function
  grpc((session) => session.get("requestName"))
    .clientStream("example.ExampleService/Example");
  //#clientStreamInstantiation

  //#clientStreamLifecycle
  const stream =
    grpc("request name")
      .clientStream("example.ExampleService/Example");

  const scn = scenario("scenario name").exec(
    stream.start(),
    stream.send(message1),
    stream.send(message2),
    stream.halfClose(),
    stream.awaitStreamEnd()
  );
  //#clientStreamLifecycle

  //#clientStreamNames
  const stream1 =
    grpc("request name")
      // specify streamName initially
      .clientStream("example.ExampleService/Example", "first-stream");
  const stream2 =
    grpc("request name")
      .clientStream("example.ExampleService/Example")
      // or use the streamName method
      .streamName("second-stream");

  exec(
    stream1.start(),
    stream2.start()
  );
  // both streams are concurrently open at this point
  //#clientStreamNames
};

const bidiStreamInstantiation = () => {
  //#bidiStreamInstantiation
  // with a static value
  grpc("request name").bidiStream("example.ExampleService/Example");
  // with a Gatling EL string
  grpc("#{requestName}").bidiStream("example.ExampleService/Example");
  // with a function
  grpc((session) => session.get("requestName"))
    .bidiStream("example.ExampleService/Example");
  //#bidiStreamInstantiation

  //#bidiStreamLifecycle
  const stream =
    grpc("request name")
      .bidiStream("example.ExampleService/Example");

  const scn = scenario("scenario name").exec(
    stream.start(),
    stream.send(message1),
    stream.send(message2),
    stream.halfClose(),
    stream.awaitStreamEnd()
  );
  //#bidiStreamLifecycle

  //#bidiStreamNames
  const stream1 =
    grpc("request name")
      // specify streamName initially
      .bidiStream("example.ExampleService/Example", "first-stream");
  const stream2 =
    grpc("request name")
      .bidiStream("example.ExampleService/Example")
      // or use the streamName method
      .streamName("second-stream");

  exec(
    stream1.start(),
    stream2.start()
  );
  // both streams are concurrently open at this point
  //#bidiStreamNames
};

const unarySend = () => {
  //#unarySend
  // with a static payload
  grpc("name")
    .unary("example.ExampleService/Example")
    .send({ message: "hello" });
  // with a function payload
  grpc("name")
    .unary("example.ExampleService/Example")
    .send((session) => ({ message: session.get("message") }));
  //#unarySend
};

const clientStreamSend = () => {
  //#clientStreamSend
  const stream =
    grpc("name")
      .clientStream("example.ExampleService/Example");

  exec(
    stream.send({ message: "first message" }),
    stream.send({ message: "second message" }),
    stream.send((session) => ({ message: session.get("third-message") }))
  );
  //#clientStreamSend
};

const serverConfiguration = () => {
  //#server-configuration
  const exampleServer1 = grpc
    .serverConfiguration("example1")
    .forAddress("host", 50051);

  const exampleServer2 = grpc
    .serverConfiguration("example2")
    .forAddress("host", 50052);

  const grpcProtocol = grpc
    // First server configuration listed becomes the default
    .serverConfigurations(exampleServer1, exampleServer2);

  grpc("name")
    .unary("example.ExampleService/Example")
    // Using server configuration `example2` by name:
    .serverConfiguration("example2")
    .send(message);
  //#server-configuration
};

const unaryAsciiHeaders = () => {
  //#unaryAsciiHeaders
  // Extracting a map of headers allows you to reuse these in several requests
  const sentHeaders = {
    "header-1": "first value",
    "header-2": "second value"
  };

  grpc("name")
    .unary("example.ExampleService/Example")
    .send(message)
    // Adds several headers at once
    .asciiHeaders(sentHeaders)
    // Adds another header, with a static value
    .asciiHeader("header").value("value")
    // with a Gatling EL string header value
    .asciiHeader("header").valueEL("#{headerValue}")
    // with a function value
    .asciiHeader("header").value((session) => session.get("headerValue"));
  //#unaryAsciiHeaders
};

const unaryBinaryHeaders = () => {
  //#unaryBinaryHeaders
  // Extracting a map of headers allows you to reuse these in several requests
  const sentHeaders = {
    "header-1-bin": [118, 97, 108, 117, 101],
    "header-2-bin": [118, 97, 108, 117, 101]
  };

  grpc("name")
    .unary("example.ExampleService/Example")
    .send(message)
    // Adds several headers at once
    .binaryHeaders(sentHeaders)
    // Adds another header, with a static value
    .binaryHeader("header-bin").value([118, 97, 108, 117, 101])
    // with a Gatling EL string header value
    .binaryHeader("header-bin").valueEL("#{headerValue}")
    // with a function value
    .binaryHeader("header-bin").value((session) => session.get("headerValue"));
  //#unaryBinaryHeaders
};

/*
//#unaryCustomHeaders
INFO:
The `header` DSL method is not supported by Gatling JS. We recommend using
`asciiHeader` or `binaryHeader` instead.
[See here for more information]({{< ref "/reference/script/grpc/methods#method-headers" >}}).
//#unaryCustomHeaders
*/

const clientStreamAsciiHeaders = () => {
  //#clientStreamAsciiHeaders
  const stream =
    grpc("name")
      .clientStream("example.ExampleService/Example")
      .asciiHeader("header").value("value");

  exec(
    stream.start(), // Header is sent only once, on stream start
    stream.send(message1),
    stream.send(message2)
  );
  //#clientStreamAsciiHeaders
};

/*
//#unaryCallCredentials
INFO:
The `callCredentials` DSL method is not supported by Gatling JS. We recommend using
`asciiHeader` or `binaryHeader` with a Gatling EL.
[See here for more information]({{< ref "/reference/script/grpc/methods#method-headers" >}}).
//#unaryCallCredentials
*/

const deadline = () => {
  //#deadline
  grpc("name")
    .unary("example.ExampleService/Example")
    .send(message)
    // with a number of seconds
    .deadlineAfter(10)
    // or with a java.time.Duration
    .deadlineAfter({ amount: 10, unit: "seconds" });
  //#deadline
};

const unaryChecks = () => {
  //#unaryChecks
  grpc("name")
    .unary("example.ExampleService/Example")
    .send(message)
    .check(
      statusCode().is("OK"),
      response((response) => response.message).is("hello")
    );
  //#unaryChecks
};

const bidiMessageResponseTimePolicy = () => {
  //#bidiMessageResponseTimePolicy
  grpc("name")
    .bidiStream("example.ExampleService/Example")
    // Default: from the start of the entire stream
    .messageResponseTimePolicy("FROM_STREAM_START")
    // From the time when the last request message was sent
    .messageResponseTimePolicy("FROM_LAST_MESSAGE_SENT")
    // From the time the previous response message was received
    .messageResponseTimePolicy("FROM_LAST_MESSAGE_RECEIVED");
  //#bidiMessageResponseTimePolicy
};

const clientStreamStart = () => {
  //#clientStreamStart
  const stream =
    grpc("name")
      .clientStream("example.ExampleService/Example");

  exec(stream.start());
  //#clientStreamStart
};

const clientStreamHalfClose = () => {
  //#clientStreamHalfClose
  const stream =
    grpc("name")
      .clientStream("example.ExampleService/Example");

  exec(stream.halfClose());
  //#clientStreamHalfClose
};

const bidiStreamWaitEnd = () => {
  //#bidiStreamWaitEnd
  const stream =
    grpc("name")
      .bidiStream("example.ExampleService/Example");

  exec(stream.awaitStreamEnd());
  // Optionally reconcile the forked session (accessible only by this stream) with the main session;
  // e.g. if you used a gRPC check to save a value, it was saved in the forked session.
  exec(stream.awaitStreamEnd((main, forked) => main.set("key", forked.get("key"))));
  //#bidiStreamWaitEnd
};

const bidiStreamProcessUnmatchedMessages = () => {
  //#bidiStreamProcessUnmatchedMessages
  const stream =
    grpc("name")
      .bidiStream("example.ExampleService/Example");

  // Process unmatched messages at a point during the stream lifecycle
  exec(stream.processUnmatchedMessages((messages, session) =>
    session.set("messages", messages)));
  // Combine with awaitStreamEnd(): process remaining unmatched messages when the stream ends
  exec(stream.awaitStreamEndAndProcessUnmatchedMessages((messages, session) =>
    session.set("messages", messages)));
  // Combine with awaitStreamEnd(), including reconciling the forked session
  exec(stream.awaitStreamEndAndProcessUnmatchedMessages((messages, main, forked) =>
    main.set("messages", messages).set("key", forked.getString("key"))));
  //#bidiStreamProcessUnmatchedMessages
};

const bidiStreamCancel = () => {
  //#bidiStreamCancel
  const stream =
    grpc("name")
      .bidiStream("example.ExampleService/Example");

  exec(stream.cancel());
  //#bidiStreamCancel
};
