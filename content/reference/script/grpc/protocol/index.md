---
title: gRPC Protocol
seotitle: Gatling gRPC protocol reference - protocol configuration
description: How to configure Gatling gRPC service address, headers, TLS and load balancing
lead: Learn about gRPC protocol settings, TLS, and load balancing
date: 2023-08-24T11:27:53+0200
aliases:
  - /reference/script/protocols/grpc/protocol/
---

## Bootstrapping

The Gatling gRPC DSL is not imported by default.

You have to manually add the following imports:

{{< include-code "imports" >}}

## Protocol

Use the `grpc` object in order to create a gRPC protocol.

As with every protocol in Gatling, the gRPC protocol can be configured for a scenario. This is done thanks to the following
statements:

{{< include-code "protocol-configuration" >}}

### Target {{% badge danger required /%}} {#target}

The first and only mandatory step is to configure the remote service address and port.

##### `forAddress`

It can be done with either the address as host and port.

{{< include-code "forAddress" >}}

##### `forTarget`

Or by using a URL or FDQN with both host and port directly:

{{< include-code "forTarget" >}}

### Channel options

By default, every user will use their own gRPC channel to connect to the remote service.

##### `shareChannel`

It is possible to share the same channel for all users by using:

{{< include-code "shareChannel" >}}

Be aware that even though the same channel is used for all users, the number of underlying connections used doesn't
scale automatically.

##### `useChannelPool`

Use a pool of gRPC channels, instead of a single channel. This allows opening more underlying HTTP/2 connections.
Useful when using `shareChannel` and when performance is limited by the maximum number of concurrent gRPC streams open
on each connection.

{{< include-code "useChannelPool" >}}

### Encryption and authentication

It is possible to use either unencrypted or encrypted connections to a remote service.

##### `usePlaintext`

If you don't want the connection to be encrypted:

{{< include-code "usePlaintext" >}}

##### `useInsecureTrustManager` {{% badge info default /%}} {#useinsecuretrustmanager}

If you want to trust all certificates without any verification, you can use an insecure trust manager.
A useful option for self-signed certificates:

{{< include-code "useInsecureTrustManager" >}}

This is the default option as it is more performant and validating certificates typically isn't important for load
tests.

##### `useStandardTrustManager`

Finally, you can use the standard trust manager that comes with the JVM:

{{< include-code "useStandardTrustManager" >}}

##### `useCustomCertificateTrustManager`

Or use your own certificate:

{{< include-code "useCustomCertificateTrustManager" >}}

##### `shareSslContext`

TLS handshake will be performed only once and the TLS sessions will be shared between all the users.
Use this option if you want to avoid the overhead of TLS while still having per-user channels.

{{< include-code "shareSslContext" >}}

#### `callCredentials`

You can specify call credentials by providing an instance of `io.grpc.CallCredentials`. This will be applied to each
gRPC call, except when [overridden on specific calls]({{< ref "methods#method-call-credentials" >}}).

{{< include-code "callCredentials" >}}

#### `channelCredentials`

You can specify channel credentials by providing an instance of `io.grpc.ChannelCredentials`.

{{< include-code "channelCredentials" >}}

This is most often used for mutual auth TLS, for instance:

{{< include-code "tlsMutualAuthChannelCredentials" >}}

{{<alert warning>}}
Because `io.grpc.ChannelCredentials` can specify its own trust manager, this option is **not** compatible with the
`useInsecureTrustManager`, `useStandardTrustManager`, or `useCustomCertificateTrustManager` options.

To avoid the overhead of validating the server certificate, you can explicitly build your channel credentials with an
insecure trust manager, for instance:

{{< include-code "insecureTrustManagerChannelCredentials" >}}
{{< /alert >}}

#### `overrideAuthority`

You can override the authority used with TLS and HTTP virtual hosting.

{{< include-code "overrideAuthority" >}}

### Headers

Define gRPC headers to be set on all requests. Note that keys in gRPC headers are allowed to be associated with more
than one value, so adding the same key a second time will simply add a second value, not replace the first one.

###### `asciiHeader`

Shortcut for a single [header]({{< ref "#header" >}}) with the default ASCII marshaller, i.e.
`io.grpc.Metadata#ASCII_STRING_MARSHALLER`:

{{< include-code "asciiHeader" >}}

###### `asciiHeaders`

Shortcut for multiple [headers]({{< ref "#header" >}}) with the default ASCII marshaller as a map of multiple key and
value pairs, i.e. `io.grpc.Metadata#ASCII_STRING_MARSHALLER`:

{{< include-code "asciiHeaders" >}}

###### `binaryHeader`

Shortcut for a single [header]({{< ref "#header" >}}) with the default binary marshaller, i.e.
`io.grpc.Metadata#BINARY_BYTE_MARSHALLER`:

{{< include-code "binaryHeader" >}}

###### `binaryHeaders`

Shortcut for multiple [headers]({{< ref "#header" >}}) with the default binary marshaller as a map of multiple key and
pairs, i.e. `io.grpc.Metadata#BINARY_BYTE_MARSHALLER`:

{{< include-code "binaryHeaders" >}}

##### `header`

Add a single header with a custom key.

{{< include-code "header" >}}

### Load balancing

When the name resolver returns a list of several service IP addresses, you probably want to configure a load balancing
policy. The policy is responsible for maintaining connections to the services and picking a connection to use each time
a request is sent.

##### `useCustomLoadBalancingPolicy`

Use a [custom load balancing](https://grpc.io/docs/guides/custom-load-balancing/) by name:

{{< include-code "useCustomLoadBalancingPolicy" >}}

Or with JSON configuration:

{{< include-code "useCustomLoadBalancingPolicy2" >}}

Check the [gRPC documentation](https://grpc.io/docs/guides/custom-load-balancing/) for more details.

##### `usePickFirstLoadBalancingPolicy`

This policy actually does no load balancing but just tries each address it gets from the name resolver and uses the
first one it can connect to:

{{< include-code "usePickFirstLoadBalancingPolicy" >}}

##### `usePickRandomLoadBalancingPolicy`

Randomly pick an address from the name resolver:

{{< include-code "usePickRandomLoadBalancingPolicy" >}}

This load balancing policy is bundled with Gatling gRPC but not a standard of gRPC.

##### `useRoundRobinLoadBalancingPolicy`

Round-robin load balancing over the addresses returned by the name resolver:

{{< include-code "useRoundRobinLoadBalancingPolicy" >}}
