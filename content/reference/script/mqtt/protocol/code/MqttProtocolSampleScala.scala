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

import io.gatling.core.Predef._
import scala.concurrent.duration._
//#imports
import io.gatling.mqtt.Predef._
//#imports
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import scala.util.Using
import io.gatling.mqtt.check.MessageCorrelator

class MqttProtocolSampleScala {
val check: MessageCorrelator = null
//#protocol-sample
val mqttProtocol = mqtt
  // enable protocol version 3.1
  .mqttVersion_3_1
  // enable protocol version 3.1.1 (default protocol version)
  .mqttVersion_3_1_1
  // enable protocol version 5
  .mqttVersion_5
  // broker address (default: localhost:1883)
  .broker("hostname", 1883)
  // if TLS should be enabled (default: false)
  .useTls(true)
  // Used to specify KeyManagerFactory for each individual virtual user. Input is the 0-based incremental id of the virtual user.
  .perUserKeyManagerFactory { userId =>
    val keyStore = KeyStore.getInstance("PKCS12")
    // P12 files stored under src/test/resources/keys
    Using(getClass.getClassLoader.getResourceAsStream("keys/pk-" + userId + ".p12")) {
      keyStore.load(_, null)
    }
    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    kmf.init(keyStore, null)
    kmf
  }
  // clientIdentifier sent in the connect payload (of not set, Gatling will generate a random one)
  .clientId("#{id}")
  // if session should be cleaned during connect (default: true)
  .cleanSession(true)
  // optional credentials for connecting
  .credentials("#{userName}", "#{password}")
  // connections keep alive timeout
  .keepAlive(30)
  // use at-most-once QoS (default: true)
  .qosAtMostOnce
  // use at-least-once QoS (default: false)
  .qosAtLeastOnce
  // use exactly-once QoS (default: false)
  .qosExactlyOnce
  // enable retain (default: false)
  .retain(false)
  // send last will, possibly with specific QoS and retain
  .lastWill(
    LastWill("#{willTopic}", StringBody("#{willMessage}"))
      .qosAtLeastOnce
      .retain(true)
  )
  // max number of reconnects after connection crash (default: 3)
  .reconnectAttemptsMax(1)
  // reconnect delay after connection crash in millis (default: 100)
  .reconnectDelay(1)
  // reconnect delay exponential backoff (default: 1.5)
  .reconnectBackoffMultiplier(1.5F)
  //  resend delay after send failure in millis (default: 5000)
  .resendDelay(1000)
  // resend delay exponential backoff (default: 1.0)
  .resendBackoffMultiplier(2.0F)
  // interval for timeout checker (default: 1 second)
  .timeoutCheckInterval(1)
  // check for pairing messages sent and messages received
  .correlateBy(check)
  // enable unmatched MQTT inbound messages buffering,
  // with a max buffer size of 5
  .unmatchedInboundMessageBufferSize(5)
//#protocol-sample

//#perUserKeyManagerFactory-keystore
// keys/multi-alias.p12 is a PKCS#12 file with one key entry per virtual user
mqtt.perUserKeyManagerFactory("keys/multi-alias.p12", "password")

// no password
mqtt.perUserKeyManagerFactory("keys/multi-alias.p12")
//#perUserKeyManagerFactory-keystore

//#dns
// resolve the broker hostname with Gatling's non-blocking DNS resolver,
// using the system's DNS servers
mqtt.asyncNameResolution()

// force DNS servers, as "host" or "host:port"
mqtt.asyncNameResolution("8.8.8.8", "8.8.4.4:53")

// give each virtual user its own DNS cache, like real devices
// only effective with asyncNameResolution
mqtt
  .asyncNameResolution()
  .perUserNameResolution
//#dns

//#timeouts-resends
mqtt
  // timeout for the broker to acknowledge a subscribe (default: 3 seconds)
  .subscribeTimeout(5.seconds)
  // max number of resends of an unacknowledged packet (default: 3)
  .resendAttemptsMax(5)
//#timeouts-resends

//#mqtt5-protocol
mqtt
  .mqttVersion_5
  // how long the broker keeps the session after the connection closes
  .sessionExpiryInterval(10.minutes)
  // user property sent in the CONNECT packet
  .connectUserProperty("deviceType", "#{deviceType}")
  // user property sent with every PUBLISH packet
  .publishUserProperty("tenant", "#{tenant}")
//#mqtt5-protocol

//#mqtt5-last-will
mqtt
  .mqttVersion_5
  .lastWill(
    LastWill("#{willTopic}", StringBody("#{willMessage}"))
      // wait 30 seconds after the connection is lost before publishing the last will
      .delayInterval(30.seconds)
      // same properties as for publish
      .contentType("text/plain")
      .messageExpiryInterval(5.minutes)
      .userProperty("reason", "connection-lost")
  )
//#mqtt5-last-will

//#connect
mqtt("Connecting").connect
//#connect

//#subscribe
mqtt("Subscribing")
  .subscribe("#{myTopic}")
  // optional, override default QoS
  .qosAtMostOnce
//#subscribe

//#subscribe-mqtt5
mqtt("Subscribing")
  .subscribe("#{myTopic}")
  // don't receive the messages this virtual user publishes
  .noLocal
  // keep the retain flag of the forwarded messages
  .retainAsPublished
  // receive the retained messages when subscribing (default)
  .retainHandlingSendAtSubscribe
  // receive the retained messages only if the subscription doesn't exist yet
  .retainHandlingSendAtNewSubscribe
  // don't receive the retained messages when subscribing
  .retainHandlingDontSend
//#subscribe-mqtt5

//#subscription-identifier
// await only matches the messages the broker forwards for this subscription
mqtt("Subscribing to all sensors")
  .subscribe("sensors/#")
  .subscriptionIdentifier(1)
  .await(1.second)

// the identifier can also be a Gatling Expression Language String or a function
mqtt("Subscribing to my sensor")
  .subscribe("sensors/#{sensorId}")
  .subscriptionIdentifier("#{subscriptionId}")
//#subscription-identifier

//#shared-subscription
// the broker spreads the messages of sensors/# among the members of the group
mqtt("Subscribing")
  .subscribe("$share/my-group/sensors/#")
  .expect(1.second)
//#shared-subscription

//#unsubscribe
mqtt("Unsubscribing").unsubscribe("#{myTopic}")
//#unsubscribe

//#publish
mqtt("Publishing")
  .publish("#{myTopic}")
  .message(StringBody("#{myTextPayload}"))
//#publish

//#publish-retain
mqtt("Publishing")
  .publish("#{myTopic}")
  .message(StringBody("#{myTextPayload}"))
  // override the protocol's retain flag
  .retain(true)
//#publish-retain

//#publish-mqtt5
mqtt("Publishing")
  .publish("#{myTopic}")
  .message(StringBody("""{"temperature": #{temperature}}"""))
  // add user properties, possibly several with the same name
  .userProperty("sensorId", "#{sensorId}")
  .userProperty("unit", "celsius")
  .contentType("application/json")
  // flag the payload as UTF-8 encoded text
  .payloadFormatUtf8
  // how long the broker keeps the message for subscribers that haven't received it yet
  .messageExpiryInterval(1.minute)
  // where responders publish their response
  .responseTopic("responses/#{id}")
  // set the Correlation Data as a UTF-8 String
  .correlationData("#{requestId}")

// set the Correlation Data as bytes
mqtt("Publishing")
  .publish("#{myTopic}")
  .message(StringBody("#{myTextPayload}"))
  .correlationDataBytes(session => session("requestId").validate[String].map(_.getBytes(StandardCharsets.UTF_8)))
//#publish-mqtt5

//#check
// subscribe and expect to receive a message within 100ms, without blocking flow
mqtt("Subscribing")
  .subscribe("#{myTopic2}")
  .expect(100.milliseconds)

// publish and await (block) until it receives a message withing 100ms
mqtt("Publishing")
  .publish("#{myTopic}")
  .message(StringBody("#{myPayload}"))
  .await(100.milliseconds)

// optionally, define in which topic the expected message will be received
mqtt("Publishing")
  .publish("#{myTopic}")
  .message(StringBody("#{myPayload}"))
  .await(100.milliseconds, "repub/#{myTopic}")

// optionally define check criteria to be applied on the matching received message
mqtt("Publishing")
  .publish("#{myTopic}")
  .message(StringBody("#{myPayload}"))
  .await(100.milliseconds)
  .check(jsonPath("$.error").notExists)
//#check

//#check-wildcards
// match the messages received on any topic under sensors/
mqtt("Subscribing")
  .subscribe("sensors/#")
  .await(1.second)

// match the response on any topic matching the topic filter
mqtt("Publishing")
  .publish("requests/#{deviceId}")
  .message(StringBody("#{myPayload}"))
  .await(1.second, "responses/+/#{deviceId}")
//#check-wildcards

//#correlate-correlation-data
val protocol = mqtt
  .mqttVersion_5
  .correlateByCorrelationData

val requestResponse =
  exec(
    mqtt("Subscribing to responses").subscribe("responses/#{id}"),
    mqtt("Requesting")
      .publish("requests")
      .message(StringBody("#{myPayload}"))
      .responseTopic("responses/#{id}")
      // mandatory with await or expect
      .correlationData("#{requestId}")
      .await(1.second, "responses/#{id}")
  )
//#correlate-correlation-data

//#waitForMessages
mqtt.waitForMessages.timeout(100.milliseconds)
//#waitForMessages

//#process
// store the unmatched messages in the Session
mqtt.processUnmatchedMessages("#{myTopic}") {
  (messages, session) => session.set("messages", messages)
}

// collect the last text message and store it in the Session
mqtt.processUnmatchedMessages("#{myTopic}") {
  (messages, session) => {
    val lastMessage = messages.reverseIterator.nextOption().map(_.payloadUtf8String)
    lastMessage.fold(session)(m => session.set("lastMessage", m))
  }
}
//#process

//#process-subscription-identifiers
// process the unmatched messages of all the topics matching a topic filter,
// and keep only the ones forwarded for the subscription with identifier 1
mqtt.processUnmatchedMessages("sensors/#") {
  (messages, session) =>
    session.set(
      "sensorMessages",
      messages
        .filter(_.subscriptionIdentifiers.contains(1))
        .map(_.payloadUtf8String)
    )
}
//#process-subscription-identifiers

//#example
class MqttSample extends Simulation {
  val mqttProtocol = mqtt
    .broker("localhost", 1883)
    .correlateBy(jsonPath("$.correlationId"))

  val scn = scenario("MQTT Test")
    .feed(csv("topics-and-payloads.csv"))
    .exec(mqtt("Connecting").connect)
    .exec(mqtt("Subscribing").subscribe("#{myTopic}"))
    .exec(mqtt("Publishing").publish("#{myTopic}")
      .message(StringBody("#{myTextPayload}"))
      .expect(100.milliseconds).check(jsonPath("$.error").notExists))

  setUp(scn.inject(rampUsersPerSec(10) to 1000 during (60)))
    .protocols(mqttProtocol)
}
//#example
}
