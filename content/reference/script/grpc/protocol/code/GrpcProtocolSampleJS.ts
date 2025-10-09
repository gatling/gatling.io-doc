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
  const grpcProtocol = grpc
    .forAddress("host", 50051);

  const scn = scenario("Scenario"); // etc.

  setUp(
    scn.injectOpen(atOnceUsers(1))
      .protocols(grpcProtocol)
  );
  //#protocol-configuration
});

const protocol = () => {
  //#forAddress
  grpc.forAddress("host", 50051);
  //#forAddress
  //#forTarget
  grpc.forTarget("dns:///host:50051");
  //#forTarget
  //#asciiHeader
  grpc
    // with a static header value
    .asciiHeader("key").value("value")
    // with a Gatling EL string header value
    .asciiHeader("key").valueEL("#{headerValue}")
    // with a function value
    .asciiHeader("key").value((session) => session.get("headerValue"));
  //#asciiHeader
  //#asciiHeaders
  grpc.asciiHeaders({ key: "value" });
  //#asciiHeaders
  //#binaryHeader
  grpc
    // with a static header value
    // FIXME .binaryHeader("key").value("value".getBytes(UTF_8))
    // with a Gatling EL string header value
    .binaryHeader("key").valueEL("#{headerValue}")
    // with a function value
    .binaryHeader("key").value((session) => session.get("headerValue"));
  //#binaryHeader
  //#binaryHeaders
  // FIXME grpc.binaryHeaders({ key: "value".getBytes(UTF_8) });
  //#binaryHeaders
/*
//#header
NOT SUPPORTED
//#header
*/
  //#shareChannel
  grpc.shareChannel();
  //#shareChannel
  //#shareSslContext
  grpc.shareSslContext();
  //#shareSslContext
  // FIXME const callCredentials = callCredentialsForUser("");
  //#callCredentials
  grpc
    // with a constant
    // FIXME .callCredentials(callCredentials)
    // or with an EL string to retrieve CallCredentials already stored in the session
    // FIXME .callCredentials("#{callCredentials}")
    // or with a function
    // FIXME .callCredentials((session) => {
    //  const name = session.get("myUserName");
    //  return callCredentialsForUser(name);
    //});
  //#callCredentials
  var channelCredentials = channelCredentialsForUser("");
  //#channelCredentials
  grpc
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
  grpc.channelCredentials({
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
  grpc.overrideAuthority("test.example.com");
  //#overrideAuthority
  //#usePlaintext
  grpc.usePlaintext();
  //#usePlaintext
  //#useInsecureTrustManager
  grpc.useInsecureTrustManager();
  //#useInsecureTrustManager
  //#useStandardTrustManager
  grpc.useStandardTrustManager();
  //#useStandardTrustManager
  //#useCustomCertificateTrustManager
  grpc.useCustomCertificateTrustManager("certificatePath");
  //#useCustomCertificateTrustManager
  //#useCustomLoadBalancingPolicy
  grpc.useCustomLoadBalancingPolicy("pick_first");
  //#useCustomLoadBalancingPolicy
  //#useCustomLoadBalancingPolicy2
  grpc.useCustomLoadBalancingPolicy("pick_first", "{}");
  //#useCustomLoadBalancingPolicy2
  //#usePickFirstLoadBalancingPolicy
  grpc.usePickFirstLoadBalancingPolicy();
  //#usePickFirstLoadBalancingPolicy
  //#usePickRandomLoadBalancingPolicy
  grpc.usePickRandomLoadBalancingPolicy();
  //#usePickRandomLoadBalancingPolicy
  //#useRoundRobinLoadBalancingPolicy
  grpc.useRoundRobinLoadBalancingPolicy();
  //#useRoundRobinLoadBalancingPolicy
  //#useChannelPool
  grpc.useChannelPool(4);
  //#useChannelPool
};

// FIXME const callCredentialsForUser = (name: string): CallCredentials => {};
const channelCredentialsForUser = (name: string): ChannelCredentials => ({});
