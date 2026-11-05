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
import io.gatling.kafka.Predef._

class KafkaActionsSampleScala extends Simulation {

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
kafka(session => "Send to " + session("topic").as[String])
  .send(session => session("topic").as[String])
  .value(StringBody("#{payload}"))
  .key(session => session("id").as[String])
  .header("type", session => session("type").as[String])
  .partition(session => session("partition").as[Int])
//#functions

//#request-reply
kafka("Check order")
  .requestReply("order-requests", "order-replies")
  .value(StringBody("""{"id": "#{orderId}"}"""))
  .key("#{orderId}")
  .check(jsonPath("$.status").is("ACCEPTED"))
  // overrides the protocol's replyTimeout
  .replyTimeout(5.seconds)
//#request-reply

//#expect
val placeOrder =
  // the start of the end-to-end latency, in epoch milliseconds
  exec(session => session.set("sentAt", System.currentTimeMillis()))
    .exec(
      kafka("Place order")
        .send("orders")
        .value(StringBody("""{"id": "#{orderId}"}"""))
        .key("#{orderId}")
    )
    .exec(
      kafka("Order enriched")
        .expect("enriched-orders")
        // matched with the protocol's matcher, here the record's key
        .correlationId("#{orderId}")
        .check(jsonPath("$.status").is("ENRICHED"))
        // overrides the protocol's replyTimeout
        .timeout(30.seconds)
        .startTimestampFrom("sentAt")
    )
//#expect
}
