/*
 * Copyright 2011-2025 GatlingCorp (https://gatling.io)
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

//#imports
import { grpc } from "@gatling.io/grpc";
//#imports

import { atOnceUsers, scenario, simulation } from "@gatling.io/core";
import { ChannelCredentials } from "@gatling.io/grpc";

const protocolConfiguration = simulation((setUp) => {
  //#protocol-configuration
  const exampleServer = grpc
    .serverConfiguration("example")
    .forAddress("host", 50051);

  const grpcProtocol = grpc
    .serverConfigurations(exampleServer);

  const scn = scenario("Scenario"); // etc.

  setUp(
    scn.injectOpen(atOnceUsers(1))
      .protocols(grpcProtocol)
  );
  //#protocol-configuration
});

const serverConfiguration = simulation((setUp) => {
  //#server-configuration
  const exampleServer1 = grpc
    .serverConfiguration("example1")
    .forAddress("host", 50051);

  const exampleServer2 = grpc
    .serverConfiguration("example2")
    .forAddress("host", 50052);

  const grpcProtocol = grpc
    // exampleServer1 will serve as the default server configuration
    .serverConfigurations(exampleServer1, exampleServer2);

  const scn = scenario("Scenario"); // etc.

  setUp(
    scn.injectOpen(atOnceUsers(1))
      .protocols(grpcProtocol)
  );
  //#server-configuration
});

const protocol = () => {
  const exampleServer =
    grpc.serverConfiguration("serverConfiguration");
  //#forAddress
  exampleServer.forAddress("host", 50051);
  //#forAddress
  //#forTarget
  exampleServer.forTarget("dns:///host:50051");
  //#forTarget
  //#asciiHeader
  exampleServer
    // with a static header value
    .asciiHeader("key").value("value")
    // with a Gatling EL string header value
    .asciiHeader("key").valueEL("#{headerValue}")
    // with a function value
    .asciiHeader("key").value((session) => session.get("headerValue"));
  //#asciiHeader
  //#asciiHeaders
  exampleServer.asciiHeaders({ key: "value" });
  //#asciiHeaders
  //#binaryHeader
  exampleServer
    // with a static header value
    .binaryHeader("key").value([118, 97, 108, 117, 101])
    // with a Gatling EL string header value
    .binaryHeader("key").valueEL("#{headerValue}")
    // with a function value
    .binaryHeader("key").value((session) => session.get("headerValue"));
  //#binaryHeader
  //#binaryHeaders
  exampleServer.binaryHeaders({
    key: [118, 97, 108, 117, 101]
  });
  //#binaryHeaders
  /*
  //#header
  INFO:
  The `header` DSL method is not supported by Gatling JS. We recommend using
  [`asciiHeader`]({{< ref "/reference/script/grpc/protocol#asciiheader" >}}) or
  [`binaryHeader`]({{< ref "/reference/script/grpc/protocol#binaryheader" >}}) instead.
  //#header
  */
  //#shareChannel
  exampleServer.shareChannel();
  //#shareChannel
  //#shareSslContext
  exampleServer.shareSslContext();
  //#shareSslContext
  /*
  //#callCredentials
  INFO:
  The `callCredentials` DSL method is not supported by Gatling JS. We recommend using
  [`asciiHeader`]({{< ref "/reference/script/grpc/protocol#asciiheader" >}}) or
  [`binaryHeader`]({{< ref "/reference/script/grpc/protocol#binaryheader" >}}) with a
  Gatling EL instead.
  //#callCredentials
  */
  var channelCredentials = channelCredentialsForUser("");
  //#channelCredentials
  exampleServer
    // with a constant
    .channelCredentials(channelCredentials)
    // or with an EL string to retrieve CallCredentials already stored in the session
    .channelCredentials("#{channelCredentials}")
    // or with a function
    .channelCredentials((session) => {
      const name = session.get("myUserName");
      // @ts-ignore
      return channelCredentialsForUser(name);
    });
  //#channelCredentials
  //#tlsMutualAuthChannelCredentials
  exampleServer.channelCredentials({
    rootCerts: "ca.crt",
    certChain: "client.crt",
    privateKey: "client.key"
  });
  //#tlsMutualAuthChannelCredentials
  /*
  //#insecureTrustManagerChannelCredentials
  NOT SUPPORTED
  //#insecureTrustManagerChannelCredentials
  */
  //#overrideAuthority
  exampleServer.overrideAuthority("test.example.com");
  //#overrideAuthority
  //#usePlaintext
  exampleServer.usePlaintext();
  //#usePlaintext
  //#useInsecureTrustManager
  exampleServer.useInsecureTrustManager();
  //#useInsecureTrustManager
  //#useStandardTrustManager
  exampleServer.useStandardTrustManager();
  //#useStandardTrustManager
  //#useCustomCertificateTrustManager
  exampleServer.useCustomCertificateTrustManager("certificatePath");
  //#useCustomCertificateTrustManager
  //#useCustomLoadBalancingPolicy
  exampleServer.useCustomLoadBalancingPolicy("pick_first");
  //#useCustomLoadBalancingPolicy
  //#useCustomLoadBalancingPolicy2
  exampleServer.useCustomLoadBalancingPolicy("pick_first", "{}");
  //#useCustomLoadBalancingPolicy2
  //#usePickFirstLoadBalancingPolicy
  exampleServer.usePickFirstLoadBalancingPolicy();
  //#usePickFirstLoadBalancingPolicy
  //#usePickRandomLoadBalancingPolicy
  exampleServer.usePickRandomLoadBalancingPolicy();
  //#usePickRandomLoadBalancingPolicy
  //#useRoundRobinLoadBalancingPolicy
  exampleServer.useRoundRobinLoadBalancingPolicy();
  //#useRoundRobinLoadBalancingPolicy
  //#useChannelPool
  exampleServer.useChannelPool(4);
  //#useChannelPool
  //#unmatchedInboundMessageBufferSize
  exampleServer.unmatchedInboundMessageBufferSize(5);
  //#unmatchedInboundMessageBufferSize
};

const channelCredentialsForUser = (name: string): ChannelCredentials => ({});
