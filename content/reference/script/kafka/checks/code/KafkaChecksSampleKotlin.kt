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
import io.gatling.javaapi.kafka.KafkaDsl.*

class KafkaChecksSampleKotlin : Simulation() {

  init {
//#value
kafka("Check order")
  .requestReply("order-requests", "order-replies")
  .value(StringBody("""{"id": "#{orderId}"}"""))
  .key("#{orderId}")
  .check(
    // the value, as text
    bodyString().exists(),
    substring("ACCEPTED"),
    regex(""""status": *"(\w+)"""").saveAs("status"),
    // the value, as JSON
    jsonPath("\$.status").shouldBe("ACCEPTED"),
    jmesPath("total").ofInt().gt(0),
    // the value, as bytes
    bodyBytes().saveAs("replyBytes"),
    bodyLength().gt(0)
  )
//#value

//#key-header
kafka("Order enriched")
  .expect("enriched-orders")
  .correlationId("#{orderId}")
  .check(
    // the key, decoded as UTF-8
    kafkaKey().shouldBe("#{orderId}"),
    // the last header with this name, decoded as UTF-8
    kafkaHeader("eventType").shouldBe("OrderEnriched"),
    kafkaHeader("traceId").saveAs("traceId")
  )
//#key-header
  }
}
