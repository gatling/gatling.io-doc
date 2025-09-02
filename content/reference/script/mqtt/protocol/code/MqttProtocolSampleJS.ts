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

import { CheckBuilder, StringBody, csv, jsonPath, rampUsersPerSec, scenario, simulation } from "@gatling.io/core";

//#imports
import { mqtt } from "@gatling.io/mqtt";
//#imports

import { LastWill } from "@gatling.io/mqtt";

const check: CheckBuilder = null as any as CheckBuilder;

//#protocol-sample
const mqttProtocol = mqtt
  // enable protocol version 3.1
  .mqttVersion_3_1()
    // enable protocol version 3.1.1 (default protocol version)
  .mqttVersion_3_1_1()
  // enable protocol version 5
  .mqttVersion_5()
  // broker address (default: localhost:1883)
  .broker("hostname", 1883)
  // if TLS should be enabled (default: false)
  .useTls(true)
  // clientIdentifier sent in the connect payload (of not set, Gatling will generate a random one)
  .clientId("#{id}")
  // if session should be cleaned during connect (default: true)
  .cleanSession(true)
  // optional credentials for connecting
  .credentials("#{userName}", "#{password}")
  // connections keep alive timeout
  .keepAlive(30)
  // use at-most-once QoS (default: true)
  .qosAtMostOnce()
  // use at-least-once QoS (default: false)
  .qosAtLeastOnce()
  // use exactly-once QoS (default: false)
  .qosExactlyOnce()
  // enable retain (default: false)
  .retain(false)
  // send last will, possibly with specific QoS and retain
  .lastWill(
    LastWill("#{willTopic}", StringBody("#{willMessage}"))
      .qosAtLeastOnce()
      .retain(true)
  )
  // max number of reconnects after connection crash (default: 3)
  .reconnectAttemptsMax(1)
  // reconnect delay after connection crash in millis (default: 100)
  .reconnectDelay(1)
  // reconnect delay exponential backoff (default: 1.5)
  .reconnectBackoffMultiplier(1.5)
  //  resend delay after send failure in millis (default: 5000)
  .resendDelay(1000)
  // resend delay exponential backoff (default: 1.0)
  .resendBackoffMultiplier(2.0)
  // interval for timeout checker (default: 1 second)
  .timeoutCheckInterval(1)
  // check for pairing messages sent and messages received
  .correlateBy(check)
  // enable unmatched MQTT inbound messages buffering,
  // with a max buffer size of 5
  .unmatchedInboundMessageBufferSize(5);
//#protocol-sample

//#connect
mqtt("Connecting").connect();
//#connect

//#subscribe
mqtt("Subscribing").subscribe("#{myTopic}")
  // optional, override default QoS
  .qosAtMostOnce();
//#subscribe

//#publish
mqtt("Publishing").publish("#{myTopic}")
  .message(StringBody("#{myTextPayload}"));
//#publish

//#check
// subscribe and expect to receive a message within 100ms, without blocking flow
mqtt("Subscribing").subscribe("#{myTopic2}")
  .expect({ amount: 100, unit: "milliseconds" });

// publish and await (block) until it receives a message withing 100ms
mqtt("Publishing").publish("#{myTopic}")
  .message(StringBody("#{myPayload}"))
  .await({ amount: 100, unit: "milliseconds" });

// optionally, define in which topic the expected message will be received
mqtt("Publishing").publish("#{myTopic}")
  .message(StringBody("#{myPayload}"))
  .await({ amount: 100, unit: "milliseconds" }, "repub/#{myTopic}");

// optionally define check criteria to be applied on the matching received message
mqtt("Publishing").publish("#{myTopic}")
  .message(StringBody("#{myPayload}"))
  .await({ amount: 100, unit: "milliseconds" }, "repub/#{myTopic}")
  .check(jsonPath("$.error").notExists());
//#check

//#waitForMessages
mqtt.waitForMessages().timeout({ amount: 100, unit: "milliseconds" });
//#waitForMessages

//#process
// store the unmatched messages in the Session
mqtt.processUnmatchedMessages("#{myTopic}", (messages, session) => session.set("messages", messages));

// collect the last text message and store it in the Session
mqtt.processUnmatchedMessages(
  "#{myTopic}",
  (messages, session) =>
    messages.length > 0
      ? session.set("lastMessage", messages[messages.length - 1].payloadUtf8String())
      : session
);
//#process

//#example
export default simulation((setUp) => {
  const mqttProtocol = mqtt
    .broker("localhost", 1883)
    .correlateBy(jsonPath("$.correlationId"));

  const scn = scenario("MQTT Test")
    .feed(csv("topics-and-payloads.csv"))
    .exec(mqtt("Connecting").connect())
    .exec(mqtt("Subscribing").subscribe("#{myTopic}"))
    .exec(mqtt("Publishing").publish("#{myTopic}")
      .message(StringBody("#{myTextPayload}"))
      .expect({ amount: 100, unit: "milliseconds" })
      .check(jsonPath("$.error").notExists()));

  setUp(scn.injectOpen(rampUsersPerSec(10).to(1000).during(60)))
    .protocols(mqttProtocol);
});
//#example
