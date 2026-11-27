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
import {
  StringBody,
  atOnceUsers,
  getParameter,
  global,
  incrementUsersPerSec,
  scenario,
  simulation
} from "@gatling.io/core";
import { kafka } from "@gatling.io/kafka";

export default simulation((setUp) => {
  const bootstrapServers = getParameter("BOOTSTRAP_SERVERS", "localhost:9092");
  // "local" stays within the Community Edition limits, "capacity" runs on Gatling Enterprise Edition
  const profile = getParameter("PROFILE", "local");

  const kafkaProtocol = kafka
    .bootstrapServers(bootstrapServers);

  const kafkaProducer = scenario("Kafka Producer")
    .exec(
      kafka("Place order")
        .send("orders")
        .value(StringBody(
          '{"amount": #{randomInt(1,1000)}, "createdAt": #{currentTimeMillis()}}'))
        // a different key for each record spreads the records over the partitions
        .key("#{randomUuid()}")
        .header("source", "gatling")
    );

  const population = profile === "capacity"
    ? kafkaProducer.injectOpen(
        // raise the levels until the response time or the error rate changes
        incrementUsersPerSec(1000)
          .times(4)
          .eachLevelLasting(60)
          .separatedByRampsLasting(10)
          .startingFrom(100))
    : kafkaProducer.injectOpen(atOnceUsers(5));

  setUp(population)
    .protocols(kafkaProtocol)
    // fail the run when more than 1% of the records fail
    .assertions(global().failedRequests().percent().lt(1.0));
});
//#complete

// the snippet of the guide's "Secure the connection" section
export const securedProtocolSample = (bootstrapServers: string) => {
//#secured-protocol
const kafkaUser = getParameter("KAFKA_USER", "");
const kafkaPassword = getParameter("sensitive.KAFKA_PASSWORD", "");
const registryUrl = getParameter("REGISTRY_URL", "");
const registryUser = getParameter("REGISTRY_USER", "");
const registryPassword = getParameter("sensitive.REGISTRY_PASSWORD", "");

const kafkaProtocol = kafka
  .bootstrapServers(bootstrapServers)
  // security.protocol is SASL_SSL
  .saslPlain(kafkaUser, kafkaPassword)
  .useTls()
  .schemaRegistry(registryUrl)
  .schemaRegistryBasicAuth(registryUser, registryPassword);
//#secured-protocol
  return kafkaProtocol;
};
