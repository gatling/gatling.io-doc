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
import io.gatling.javaapi.grpc.*

import io.gatling.javaapi.grpc.GrpcDsl.*
//#imports

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*

import io.grpc.*

import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.Executor

class GrpcProtocolSampleKotlin : Simulation() {

  init {
    //#protocol-configuration
    val grpcProtocol = grpc
      .forAddress("host", 50051)

    val scn = scenario("Scenario") // etc.

    setUp(
      scn.injectOpen(atOnceUsers(1))
        .protocols(grpcProtocol)
    )
    //#protocol-configuration
  }

  init {
    //#forAddress
    grpc.forAddress("host", 50051)
    //#forAddress
    //#forTarget
    grpc.forTarget("dns:///host:50051")
    //#forTarget
    //#asciiHeader
    grpc
      // with a static header value
      .asciiHeader("key").value("value")
      // with a Gatling EL string header value
      .asciiHeader("key").valueEl("#{headerValue}")
      // with a function value
      .asciiHeader("key").value { session -> session.getString("headerValue") }
    //#asciiHeader
    //#asciiHeaders
    grpc.asciiHeaders(
      mapOf("key" to "value")
    )
    //#asciiHeaders
    //#binaryHeader
    grpc
      // with a static header value
      .binaryHeader("key").value("value".toByteArray(UTF_8))
      // with a Gatling EL string header value
      .binaryHeader("key").valueEl("#{headerValue}")
      // with a function value
      .binaryHeader("key").value { session -> session.get("headerValue") }    //#binaryHeader
    //#binaryHeaders
    grpc.binaryHeaders(
      mapOf("key" to "value".toByteArray(UTF_8))
    )
    //#binaryHeaders
    //#header
    val key = Metadata.Key.of("key", Metadata.ASCII_STRING_MARSHALLER)
    grpc
      // with a static header value
      .header(key).value("value")
      // with a Gatling EL string header value
      .header(key).valueEl("#{headerValue}")
      // with a function value
      .header(key).value { session -> session.get("headerValue") }
    //#header
    //#shareChannel
    grpc.shareChannel()
    //#shareChannel
    //#shareSslContext
    grpc.shareSslContext()
    //#shareSslContext
    val callCredentials = callCredentialsForUser("")
    //#callCredentials
    grpc
      // with a constant
      .callCredentials(callCredentials)
      // or with an EL string to retrieve CallCredentials already stored in the session
      .callCredentials("#{callCredentials}")
      // or with a function
      .callCredentials { session ->
        val name = session.getString("myUserName")!!
        callCredentialsForUser(name)
      }
    //#callCredentials
    val channelCredentials = channelCredentialsForUser("")
    //#channelCredentials
    grpc
      // with a constant
      .channelCredentials(channelCredentials)
      // or with an EL string to retrieve CallCredentials already stored in the session
      .channelCredentials("#{channelCredentials}")
      // or with a function
      .channelCredentials { session ->
        val name = session.getString("myUserName")!!
        channelCredentialsForUser(name)
      }
    //#channelCredentials
    //#tlsMutualAuthChannelCredentials
    grpc.channelCredentials(
      TlsChannelCredentials.newBuilder()
        .keyManager(
          ClassLoader.getSystemResourceAsStream("client.crt"),
          ClassLoader.getSystemResourceAsStream("client.key"))
        .trustManager(ClassLoader.getSystemResourceAsStream("ca.crt"))
        .build()
    )
    //#tlsMutualAuthChannelCredentials
    //#insecureTrustManagerChannelCredentials
    TlsChannelCredentials.newBuilder()
      .trustManager(
        *io.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE.trustManagers
      )
    //#insecureTrustManagerChannelCredentials
    //#overrideAuthority
    grpc.overrideAuthority("test.example.com")
    //#overrideAuthority
    //#usePlaintext
    grpc.usePlaintext()
    //#usePlaintext
    //#useInsecureTrustManager
    grpc.useInsecureTrustManager()
    //#useInsecureTrustManager
    //#useStandardTrustManager
    grpc.useStandardTrustManager()
    //#useStandardTrustManager
    //#useCustomCertificateTrustManager
    grpc.useCustomCertificateTrustManager("certificatePath")
    //#useCustomCertificateTrustManager
    //#useCustomLoadBalancingPolicy
    grpc.useCustomLoadBalancingPolicy("pick_first")
    //#useCustomLoadBalancingPolicy
    //#useCustomLoadBalancingPolicy2
    grpc.useCustomLoadBalancingPolicy("pick_first", "{}")
    //#useCustomLoadBalancingPolicy2
    //#usePickFirstLoadBalancingPolicy
    grpc.usePickFirstLoadBalancingPolicy()
    //#usePickFirstLoadBalancingPolicy
    //#usePickRandomLoadBalancingPolicy
    grpc.usePickRandomLoadBalancingPolicy()
    //#usePickRandomLoadBalancingPolicy
    //#useRoundRobinLoadBalancingPolicy
    grpc.useRoundRobinLoadBalancingPolicy()
    //#useRoundRobinLoadBalancingPolicy
    //#useChannelPool
    grpc.useChannelPool(4)
    //#useChannelPool
  }

  private fun callCredentialsForUser(name: String): CallCredentials =
    object: CallCredentials() {
      override fun applyRequestMetadata(requestInfo: RequestInfo?, appExecutor: Executor?, applier: MetadataApplier?) {}
    }

  private fun channelCredentialsForUser(name: String): ChannelCredentials =
    object: ChannelCredentials() {
      override fun withoutBearerTokens(): ChannelCredentials {
        return this
      }
    }
}
