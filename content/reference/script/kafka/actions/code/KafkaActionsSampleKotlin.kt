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

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.*
import io.gatling.javaapi.kafka.KafkaDsl.*
import java.time.Duration

class KafkaActionsSampleKotlin : Simulation() {

  init {
//#send
kafka("Place order")
  .send("orders")
  .value(StringBody("""{"id": "#{orderId}", "amount": #{amount}}"""))
//#send

//#send-options
kafka("Place order")
  .send("orders")
  .value(ElFileBody("order.json"))
  // the key, encoded as UTF-8
  .key("#{orderId}")
  // headers, whose values are encoded as UTF-8
  .header("source", "gatling")
  .header("traceId", "#{traceId}")
  // the partition, else the producer's partitioner picks it
  .partition(0)
//#send-options

//#bodies
// text, with the Gatling Expression Language or Pebble templates
kafka("Text").send("orders").value(StringBody("#{order}"))
kafka("Template").send("orders").value(ElFileBody("order.json"))
kafka("Pebble template").send("orders").value(PebbleFileBody("order.peb"))
// bytes, sent as is
kafka("File").send("payments").value(RawFileBody("payment.bin"))
kafka("Bytes").send("payments").value(ByteArrayBody("#{paymentBytes}"))
// a key from a body
kafka("Bytes key")
  .send("payments")
  .value(ByteArrayBody("#{paymentBytes}"))
  .key(ByteArrayBody("#{keyBytes}"))
//#bodies

//#functions
kafka { session -> "Send to " + session.getString("topic") }
  .send { session -> session.getString("topic") }
  .value(StringBody("#{payload}"))
  .key { session -> session.getString("id") }
  .header("type") { session -> session.getString("type") }
  .partition { session -> session.getInt("partition") }
//#functions

//#request-reply
kafka("Check order")
  .requestReply("order-requests", "order-replies")
  .value(StringBody("""{"id": "#{orderId}"}"""))
  .key("#{orderId}")
  .check(jsonPath("$.status").shouldBe("ACCEPTED"))
  // overrides the protocol's replyTimeout
  .replyTimeout(Duration.ofSeconds(5))
//#request-reply

//#consume
val placeOrder =
  // the start of the end-to-end latency, in epoch milliseconds
  exec { session -> session.set("sentAt", System.currentTimeMillis()) }
    .exec(
      kafka("Place order")
        .send("orders")
        .value(StringBody("""{"id": "#{orderId}"}"""))
        .key("#{orderId}")
    )
    .exec(
      kafka("Order enriched")
        .consume("enriched-orders")
        // matched with the protocol's matcher, here the record's key
        .correlationId("#{orderId}")
        .check(jsonPath("$.status").shouldBe("ENRICHED"))
        // overrides the protocol's replyTimeout
        .timeout(Duration.ofSeconds(30))
        .startTimestampFrom("sentAt")
    )
//#consume

//#subscribe
val checkout =
  exec(
    // before the HTTP request that makes the system produce the record
    kafka("Order shipped")
      .subscribe("order-events")
      // matched with the protocol's matcher, here the record's key
      .correlationId("#{orderId}")
      .check(jsonPath("$.trackingNumber").saveAs("trackingNumber"))
      // overrides the protocol's replyTimeout
      .timeout(Duration.ofSeconds(30)),
    http("Checkout").post("/orders/#{orderId}/checkout"),
    http("Browse").get("/products"),
    // waits for the subscriptions, then applies what their checks saved
    kafka.poll(),
    http("Track").get("/tracking/#{trackingNumber}")
  )
//#subscribe

//#subscribe-attached
val pipeline =
  exec(
    kafka("Place order")
      .send("orders")
      .value(StringBody("""{"id": "#{orderId}"}"""))
      .key("#{orderId}")
      // the request name, the topic, and the correlation id
      .subscribe("Order enriched", "enriched-orders", "#{orderId}")
      // or a subscription with checks and a timeout
      .subscribe(
        kafka("Order billed")
          .subscribe("billing-events")
          .correlationId("#{orderId}")
          .check(jsonPath("$.status").shouldBe("BILLED"))
          .timeout(Duration.ofSeconds(30))
      ),
    kafka("Check order")
      .requestReply("order-requests", "order-replies")
      .value(StringBody("""{"id": "#{orderId}"}"""))
      .key("#{orderId}")
      .subscribe("Order shipped", "order-events", "#{orderId}"),
    kafka.poll()
  )
//#subscribe-attached
  }
}
