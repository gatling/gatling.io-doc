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

//#complete
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.kafka.Predef._

class KafkaSampleScala extends Simulation {

  val bootstrapServers = System.getProperty("BOOTSTRAP_SERVERS", "localhost:9092")
  // "local" stays within the Community Edition limits, "capacity" runs on Gatling Enterprise Edition
  val profile = System.getProperty("PROFILE", "local")

  val kafkaProtocol = kafka
    .bootstrapServers(bootstrapServers)

  val kafkaProducer = scenario("Kafka Producer")
    .exec(
      kafka("Place order")
        .send("orders")
        .value(StringBody(
          """{"amount": #{randomInt(1,1000)}, "createdAt": #{currentTimeMillis()}}"""))
        // a different key for each record spreads the records over the partitions
        .key("#{randomUuid()}")
        .header("source", "gatling")
    )

  val population =
    if (profile == "capacity")
      kafkaProducer.inject(
        // raise the levels until the response time or the error rate changes
        incrementUsersPerSec(1000)
          .times(4)
          .eachLevelLasting(60.seconds)
          .separatedByRampsLasting(10.seconds)
          .startingFrom(100))
    else
      kafkaProducer.inject(atOnceUsers(5))

  setUp(population)
    .protocols(kafkaProtocol)
    // fail the run when more than 1% of the records fail
    .assertions(global.failedRequests.percent.lt(1.0))
}
//#complete

// the snippet of the guide's "Secure the connection" section
class KafkaSecuredProtocolSampleScala(bootstrapServers: String) {

//#secured-protocol
val kafkaUser = System.getProperty("KAFKA_USER", "")
val kafkaPassword = System.getProperty("sensitive.KAFKA_PASSWORD", "")
val registryUrl = System.getProperty("REGISTRY_URL", "")
val registryUser = System.getProperty("REGISTRY_USER", "")
val registryPassword = System.getProperty("sensitive.REGISTRY_PASSWORD", "")

val kafkaProtocol = kafka
  .bootstrapServers(bootstrapServers)
  // security.protocol is SASL_SSL
  .saslPlain(kafkaUser, kafkaPassword)
  .useTls
  .schemaRegistry(registryUrl)
  .schemaRegistryBasicAuth(registryUser, registryPassword)
//#secured-protocol
}
