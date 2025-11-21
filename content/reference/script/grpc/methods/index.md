---
title: gRPC Methods
seotitle: Gatling gRPC protocol reference - SDK methods
description: All methods of the Gatling gRPC SDK
lead: Learn how to use each method offered by the Gatling gRPC SDK
date: 2023-08-24T11:27:53+0200
aliases:
  - /reference/script/protocols/grpc/methods/
---

## Summary

With gRPC, [four types of methods can be defined](https://grpc.io/docs/what-is-grpc/core-concepts/#service-definition):
unary, server streaming, client streaming and bidirectional streaming. Different Gatling SDK methods can be used
depending on the type of the gRPC method.

{{< table >}}
|                                                                       | Unary                                             | Server Stream                                                                             | Client Stream                                              | Bidirectional Stream                                                                      |
|-----------------------------------------------------------------------|---------------------------------------------------|-------------------------------------------------------------------------------------------|------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| Instantiate                                                           | [`unary`]({{< ref "#instantiate-unary" >}})       | [`serverStream`]({{< ref "#instantiate-server-stream" >}})                                | [`clientStream`]({{< ref "#instantiate-client-stream" >}}) | [`bidiStream`]({{< ref "#instantiate-bidi-stream" >}})                                    |
| [Server configuration]({{< ref "#server-configuration" >}})           | `serverConfiguration`                             | `serverConfiguration`                                                                     | `serverConfigration`                                       | `serverConfiguration`                                                                     |
| [Add request headers]({{< ref "#method-headers" >}})                  | `asciiHeader(s)`<br>`binaryHeader(s)`<br>`header` | `asciiHeader(s)`<br>`binaryHeader(s)`<br>`header`                                         | `asciiHeader(s)`<br>`binaryHeader(s)`<br>`header`          | `asciiHeader(s)`<br>`binaryHeader(s)`<br>`header`                                         |
| [Add call credentials]({{< ref "#method-call-credentials" >}})        | `callCredentials`                                 | `callCredentials`                                                                         | `callCredentials`                                          | `callCredentials`                                                                         |
| [Add deadline]({{< ref "#method-deadline" >}})                        | `deadlineAfter`                                   | `deadlineAfter`                                                                           | `deadlineAfter`                                            | `deadlineAfter`                                                                           |
| [Add checks]({{< ref "#method-checks" >}})                            | `check`                                           | `check`                                                                                   | `check`                                                    | `check`                                                                                   |
| [Response time policy]({{< ref "#method-response-time" >}})           | :x:                                               | `messageResponseTimePolicy`                                                               | `messageResponseTimePolicy`                                | `messageResponseTimePolicy`                                                               |
| [Open stream]({{< ref "#method-start" >}})                            | :x:                                               | *implied by* `send`                                                                       | `start`                                                    | `start`                                                                                   |
| [Send a message]({{< ref "#method-send" >}})                          | `send`                                            | `send`                                                                                    | `send`                                                     | `send`                                                                                    |
| [Half-close stream]({{< ref "#method-half-close" >}})                 | :x:                                               | *implied by* `send`                                                                       | `halfClose`                                                | `halfClose`                                                                               |
| [Wait for stream end]({{< ref "#method-wait-end" >}})                 | :x:                                               | `awaitStreamEnd`                                                                          | `awaitStreamEnd`                                           | `awaitStreamEnd`                                                                          |
| [Process unmatched messages]({{< ref "#method-process-unmatched" >}}) | :x:                                               | `processUnmatchedMessages`<br>`awaitStreamEndAndProcess`<br>&nbsp;  ⤷ `UnmatchedMessages` | :x:                                                        | `processUnmatchedMessages`<br>`awaitStreamEndAndProcess`<br>&nbsp;  ⤷ `UnmatchedMessages` |
| [Cancel stream]({{< ref "#method-cancel" >}})                         | :x:                                               | `cancel`                                                                                  | `cancel`                                                   | `cancel`                                                                                  |
{{< /table >}}

## gRPC method descriptor {#method-descriptor}

The Gatling gRPC SDK will need a method descriptor, of type `io.grpc.MethodDescriptor`, to define each gRPC method used.
The most common use case is to use [generated code](https://grpc.io/docs/languages/java/generated-code/) from a .proto
specification file which describes the gRPC service, but the method descriptor could also be constructed by hand.

In all code examples on this page, we assume a method descriptor defined by Java code similar to this:

```java
public final class ExampleServiceGrpc {
  public static MethodDescriptor<ExampleRequest, ExampleResponse> getExampleMethod() {
    // generated method descriptor code here
  }
}
```

## Instantiate a gRPC request

### Unary method calls {#instantiate-unary}

For unary gRPC methods, Gatling gRPC requests are declared with the `unary` keyword.

`grpc(requestName)` is the entrypoint for any gRPC request with the Gatling gRPC SDK. `unary(methodDescriptor)` then
takes a [method descriptor]({{< ref "#method-descriptor" >}}) describing the gRPC method to call (which must describe a
unary method).

{{< include-code "unaryInstantiation" >}}

When you `send` a message, Gatling gRPC will automatically handle the client-side lifecycle of the underlying gRPC
stream (open a stream, send a single message, half-close the stream) and wait for the server to respond and close the
stream.

{{< include-code "unaryLifecycle" >}}

### Streaming method calls

For streaming gRPC methods, Gatling gRPC requests are declared with the `serverStream`, `clientStream`, and `bidiStream`
keyword. Including one of them in a scenario creates a gRPC stream which may stay open for a long time, and allows you
to perform several actions on the same stream at various times during the scenario's execution.

#### Server stream {#instantiate-server-stream}

`grpc(requestName)` is the entrypoint for any gRPC request with the Gatling gRPC SDK. `serverStream(methodDescriptor)` then
takes a [method descriptor]({{< ref "#method-descriptor" >}}) describing the gRPC method to call (which must describe a
server streaming method).

{{< include-code "serverStreamInstantiation" >}}

The typical lifecycle of a server stream consists of:

- Sending a single message with the `send` method (this will also half-close the stream, signaling that the client will
  not send any more messages)
- Waiting until the stream gets closed by the server with the `awaitStreamEnd` method

{{< include-code "serverStreamLifecycle" >}}

If several server streams are opened concurrently by a virtual user, they must be given explicit stream names to
differentiate them:

{{< include-code "serverStreamNames" >}}

#### Client stream {#instantiate-client-stream}

`grpc(requestName)` is the entrypoint for any gRPC request with the Gatling gRPC SDK. `clientStream(methodDescriptor)` then
takes a [method descriptor]({{< ref "#method-descriptor" >}}) describing the gRPC method to call (which must describe a
client streaming method).

{{< include-code "clientStreamInstantiation" >}}

The typical lifecycle of a client stream consists of:

- Opening the stream with the `start` method
- Sending messages with the `send` method
- Half-closing the stream with the `halfClose` method when done sending messages
- Waiting until the stream gets closed by the server with the `awaitStreamEnd` method

{{< include-code "clientStreamLifecycle" >}}

If several client streams are opened concurrently by a virtual user, they must be given explicit stream names to
differentiate them:

{{< include-code "clientStreamNames" >}}

#### Bidirectional stream {#instantiate-bidi-stream}

`grpc(requestName)` is the entrypoint for any gRPC request with the Gatling gRPC SDK. `bidiStream(methodDescriptor)` then
takes a [method descriptor]({{< ref "#method-descriptor" >}}) describing the gRPC method to call (which must describe a
bidirectional streaming method).

{{< include-code "bidiStreamInstantiation" >}}

The typical lifecycle of a bidirectional stream consists of:

- Opening the stream with the `start` method
- Sending messages with the `send` method
- Half-closing the stream with the `halfClose` method when done sending messages
- Waiting until the stream gets closed by the server with the `awaitStreamEnd` method

{{< include-code "bidiStreamLifecycle" >}}

If several bidirectional streams are opened concurrently by a virtual user, they must be given explicit stream names to
differentiate them:

{{< include-code "bidiStreamNames" >}}

## Configure a gRPC server {#server-configuration}

{{< badge info unary />}}
{{< badge info serverStream />}}
{{< badge info clientStream />}}
{{< badge info bidiStream />}}

If you do not want to use the default server configuration for a specific gRPC call, you can override it using its name:

{{< include-code "server-configuration" >}}

{{< alert tip >}}
`serverConfiguration` takes a `String` as an input, not the server configuration itself.
{{< /alert >}}

## Methods reference

### Add request headers {#method-headers}

{{< badge info unary />}}
{{< badge info serverStream />}}
{{< badge info clientStream />}}
{{< badge info bidiStream />}}

You can easily add ASCII format request headers (they will use the standard ASCII marshaller,
`io.grpc.Metadata#ASCII_STRING_MARSHALLER`):

{{< include-code "unaryAsciiHeaders" >}}

Or binary format headers (they will use the standard binary marshaller,
`io.grpc.Metadata#BINARY_BYTE_MARSHALLER`):

{{< include-code "unaryBinaryHeaders" >}}

If you need to use custom marshallers, you can add headers one at a time with your own `io.grpc.Metadata.Key`:

{{< include-code "unaryCustomHeaders" >}}

Note that in gRPC, headers are per-stream, not per-message. Even in client or bidirectional streaming methods,
request headers are sent only once, when starting the stream:

{{< include-code "clientStreamAsciiHeaders" >}}

Also note that keys in gRPC headers are allowed to be associated with more than one value, so adding the same key a
second time will simply add a second value, not replace the first one.

### Add call credentials {#method-call-credentials}

{{< badge info >}}unary{{< /badge >}}
{{< badge info >}}serverStream{{< /badge >}}
{{< badge info >}}clientStream{{< /badge >}}
{{< badge info >}}bidiStream{{< /badge >}}

You can specify call credentials by providing an instance of `io.grpc.CallCredentials`. This will override any value set
[on the protocol]({{< ref "protocol#callcredentials" >}}).

{{< include-code "unaryCallCredentials" >}}

### Add deadline {#method-deadline}

{{< badge info >}}unary{{< /badge >}}
{{< badge info >}}serverStream{{< /badge >}}
{{< badge info >}}clientStream{{< /badge >}}
{{< badge info >}}bidiStream{{< /badge >}}

You can specify a [deadline](https://grpc.io/docs/guides/deadlines/#deadlines-on-the-client) to use for the request:

{{< include-code "deadline" >}}

The actual deadline will be computed just before the start of each gRPC request based on the provided duration.

### Add checks {#method-checks}

{{< badge info >}}unary{{< /badge >}}
{{< badge info >}}serverStream{{< /badge >}}
{{< badge info >}}clientStream{{< /badge >}}
{{< badge info >}}bidiStream{{< /badge >}}

You can specify one or more checks, to be applied to the response headers, trailers, status, or message:

{{< include-code "unaryChecks" >}}

See the [checks section]({{< ref "checks" >}}) for more details on gRPC checks.

If you define response checks for server or bidirectional streaming methods, they will be applied to every message
received from the server. Other checks are only applied once, at the end of the stream.

### Response time policy {#method-response-time}

{{< badge info >}}serverStream{{< /badge >}}
{{< badge info >}}clientStream{{< /badge >}}
{{< badge info >}}bidiStream{{< /badge >}}

For streaming methods only, you can specify how to calculate the response time logged for each response message received.

- `FromStreamStartPolicy`: measure the time since the start of the entire stream. When receiving several response
  messages on the same stream, they show increasing response times. This is the default because it can always be
  computed as expected, but it may not be what you are interested in for long-lived server or bidirectional streams.
- `FromLastMessageSentPolicy`: measure the time since the last request message was sent. If no request message was sent
  previously, falls back to `FromStreamStartPolicy`.
- `FromLastMessageReceivedPolicy`: measure the time since the previous response message was received. If this is the
  first response message received, falls back to `FromStreamStartPolicy`.

{{< include-code "bidiMessageResponseTimePolicy" >}}

### Open stream {#method-start}

{{< badge info clientStream />}}
{{< badge info bidiStream />}}

For client or bidirectional streaming methods only, you must start the stream to signal that the client is ready to
send messages. Only then can you send messages and/or half-close the stream.

{{< include-code "clientStreamStart" >}}

### Send a message {#method-send}

{{< badge info >}}unary{{< /badge >}}
{{< badge info >}}serverStream{{< /badge >}}
{{< badge info >}}clientStream{{< /badge >}}
{{< badge info >}}bidiStream{{< /badge >}}

The message sent must be of the type specified in the method descriptor for outbound messages. You can pass a static
message, or a function to construct the message from the Gatling Session.

{{< include-code "unarySend" >}}

For client streaming and bidirectional streaming methods, you can send several messages.

{{< include-code "clientStreamSend" >}}

### Half-close stream {#method-half-close}

{{< badge info >}}clientStream{{< /badge >}}
{{< badge info >}}bidiStream{{< /badge >}}

For client or bidirectional streaming methods only, you can half-close the stream to signal that the client has finished
sending messages. You can then no longer use the `send` method on the same stream.

{{< include-code "clientStreamHalfClose" >}}

### Wait for stream end {#method-wait-end}

{{< badge info >}}serverStream{{< /badge >}}
{{< badge info >}}clientStream{{< /badge >}}
{{< badge info >}}bidiStream{{< /badge >}}

For streaming methods only, you can use the `awaitStreamEnd` method to wait until the server closes the connection.
During that time, you may also still receive response messages from the server.

{{< include-code "bidiStreamWaitEnd" >}}

### Process unmatched messages {#method-process-unmatched}

{{< badge info >}}serverStream{{< /badge >}}
{{< badge info >}}bidiStream{{< /badge >}}

For server or bidirectional streaming methods only, you can use `processUnmatchedMessages` to process inbound messages
that haven't been matched with a check and have been buffered.
By default, unmatched inbound messages are not buffered, you must enable this feature by
[setting the size of the buffer on the protocol]({{< ref "./protocol#unmatchedinboundmessagebuffersize" >}}).

The buffer is reset when:
* sending an outbound message
* calling `processUnmatchedMessages` so we don't present the same message twice

You can then pass your processing logic as a function.
The list of messages passed to this function is sorted in timestamp ascending (meaning older messages first).
It contains instances of type `io.gatling.grpc.action.GrpcInboundMessage`.

{{< include-code "bidiStreamProcessUnmatchedMessages" >}}

### Cancel stream {#method-cancel}

{{< badge info >}}serverStream{{< /badge >}}
{{< badge info >}}clientStream{{< /badge >}}
{{< badge info >}}bidiStream{{< /badge >}}

For streaming methods only, you can use the `cancel` method to cancel the gRPC stream and prevent any further processing.

{{< include-code "bidiStreamCancel" >}}
