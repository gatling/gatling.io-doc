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

import scala.concurrent.duration._

import io.gatling.core.Predef._
//#imports
import io.gatling.kafka.Predef._
//#imports

class KafkaProtocolSampleScala extends Simulation {

//#protocol-sample
val kafkaProtocol = kafka
  .bootstrapServers("broker1:9092", "broker2:9092")

val scn = scenario("Kafka")
  .exec(
    kafka("Place order")
      .send("orders")
      .value(StringBody("""{"id": "#{orderId}"}"""))
      .key("#{orderId}")
  )

setUp(
  scn.inject(constantUsersPerSec(10).during(60))
).protocols(kafkaProtocol)
//#protocol-sample

//#properties
kafka
  .bootstrapServers("broker1:9092")
  // for the producers and the reply consumer
  .property("client.id", "gatling")
  .properties(Map("request.timeout.ms" -> "10000"))
  // for the producers only, overriding the shared properties
  .producerProperty("acks", "all")
  .producerProperties(Map("compression.type" -> "lz4"))
  // for the reply consumer only, overriding the shared properties
  .consumerProperty("fetch.max.wait.ms", "100")
  .consumerProperties(Map("max.poll.records" -> "1000"))
//#properties

//#sasl
// SASL PLAIN over TLS: security.protocol is SASL_SSL
kafka.bootstrapServers("broker1:9093")
  .saslPlain("user", "password")
  .useTls
// SCRAM without TLS: security.protocol is SASL_PLAINTEXT
kafka.bootstrapServers("broker1:9092")
  .saslScramSha256("user", "password")
kafka.bootstrapServers("broker1:9092")
  .saslScramSha512("user", "password")
//#sasl

//#oauth
kafka.bootstrapServers("broker1:9093")
  .saslOauthBearer("https://idp.example.com/oauth2/token", "client-id", "client-secret")
  .useTls
// with a scope
kafka.bootstrapServers("broker1:9093")
  .saslOauthBearer("https://idp.example.com/oauth2/token", "client-id", "client-secret", "kafka")
  .useTls
//#oauth

//#aws-msk-iam
// the AWS default credentials provider chain
kafka.bootstrapServers("b-1.mycluster.abc123.c2.kafka.eu-west-1.amazonaws.com:9098")
  .saslAwsMskIam
// a named profile of the AWS credentials files
kafka.bootstrapServers("b-1.mycluster.abc123.c2.kafka.eu-west-1.amazonaws.com:9098")
  .saslAwsMskIamProfile("load-tests")
// a role assumed with the default credentials provider chain
kafka.bootstrapServers("b-1.mycluster.abc123.c2.kafka.eu-west-1.amazonaws.com:9098")
  .saslAwsMskIamRole("arn:aws:iam::123456789012:role/gatling-msk")
// with a session name, an external id, and the STS endpoint's region
kafka.bootstrapServers("b-1.mycluster.abc123.c2.kafka.eu-west-1.amazonaws.com:9098")
  .saslAwsMskIamRole(
    "arn:aws:iam::123456789012:role/gatling-msk", "gatling", "my-external-id", "eu-west-1")
//#aws-msk-iam

//#gssapi
kafka.bootstrapServers("broker1:9093")
  // the brokers' service name, the client's keytab and principal
  .saslGssapi("kafka", "/etc/security/gatling.keytab", "gatling@EXAMPLE.COM")
  .useTls
//#gssapi

//#tls
// trust the JVM's default certificates
kafka.bootstrapServers("broker1:9093")
  .useTls
// trust the certificates of a trust store
kafka.bootstrapServers("broker1:9093")
  .trustStore("/path/to/truststore.jks", "changeit")
// mutual TLS, with the type of each store
kafka.bootstrapServers("broker1:9093")
  .trustStore("/path/to/ca.pem", "", "PEM")
  .keyStore("/path/to/client.p12", "changeit", "PKCS12")
//#tls

//#security-properties
kafka.bootstrapServers("broker1:9093")
  .saslScramSha512("user", "password")
  .trustStore("/path/to/truststore.jks", "changeit")
  // a key that the typed options don't generate
  .property("ssl.enabled.protocols", "TLSv1.3")
//#security-properties

//#matching
// the reply has the request's key (default)
kafka.bootstrapServers("broker1:9092")
  .matchByKey
// the reply has the request's header with this name
kafka.bootstrapServers("broker1:9092")
  .matchByHeader("correlationId")
// the reply's value has the request's id where the check finds it
kafka.bootstrapServers("broker1:9092")
  .matchByValue(jsonPath("$.orderId"))
//#matching

//#reply-timeout
kafka.bootstrapServers("broker1:9092")
  .replyTimeout(10.seconds)
//#reply-timeout

//#consume-buffer
kafka.bootstrapServers("broker1:9092")
  // at most 50,000 records, each kept up to 30 seconds
  .consumeBuffer(50000, 30.seconds)
//#consume-buffer

//#linger
kafka.bootstrapServers("broker1:9092")
  // the same as the producers of the system under test
  .producerProperty("linger.ms", "0")
//#linger
}
