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
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.kafka.KafkaProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.kafka.KafkaDsl.*;

public class KafkaSampleJava extends Simulation {

  public static final String BOOTSTRAP_SERVERS =
    System.getProperty("BOOTSTRAP_SERVERS", "localhost:9092");
  // "local" stays within the Community Edition limits, "capacity" runs on Gatling Enterprise Edition
  public static final String PROFILE = System.getProperty("PROFILE", "local");

  private final KafkaProtocolBuilder kafkaProtocol = kafka
    .bootstrapServers(BOOTSTRAP_SERVERS);

  private final ScenarioBuilder kafkaProducer = scenario("Kafka Producer")
    .exec(
      kafka("Place order")
        .send("orders")
        .value(StringBody(
          "{\"amount\": #{randomInt(1,1000)}, \"createdAt\": #{currentTimeMillis()}}"))
        // a different key for each record spreads the records over the partitions
        .key("#{randomUuid()}")
        .header("source", "gatling")
    );

  private final PopulationBuilder population = PROFILE.equals("capacity")
    ? kafkaProducer.injectOpen(
        // raise the levels until the response time or the error rate changes
        incrementUsersPerSec(1000)
          .times(4)
          .eachLevelLasting(60)
          .separatedByRampsLasting(10)
          .startingFrom(100.0))
    : kafkaProducer.injectOpen(atOnceUsers(5));

  {
    setUp(population)
      .protocols(kafkaProtocol)
      // fail the run when more than 1% of the records fail
      .assertions(global().failedRequests().percent().lt(1.0));
  }
}
//#complete

// the snippet of the guide's "Secure the connection" section
class KafkaSecuredProtocolSampleJava {

  static final String BOOTSTRAP_SERVERS = KafkaSampleJava.BOOTSTRAP_SERVERS;

//#secured-protocol
public static final String KAFKA_USER = System.getProperty("KAFKA_USER", "");
public static final String KAFKA_PASSWORD = System.getProperty("sensitive.KAFKA_PASSWORD", "");
public static final String REGISTRY_URL = System.getProperty("REGISTRY_URL", "");
public static final String REGISTRY_USER = System.getProperty("REGISTRY_USER", "");
public static final String REGISTRY_PASSWORD = System.getProperty("sensitive.REGISTRY_PASSWORD", "");

private final KafkaProtocolBuilder kafkaProtocol = kafka
  .bootstrapServers(BOOTSTRAP_SERVERS)
  // security.protocol is SASL_SSL
  .saslPlain(KAFKA_USER, KAFKA_PASSWORD)
  .useTls()
  .schemaRegistry(REGISTRY_URL)
  .schemaRegistryBasicAuth(REGISTRY_USER, REGISTRY_PASSWORD);
//#secured-protocol
}
