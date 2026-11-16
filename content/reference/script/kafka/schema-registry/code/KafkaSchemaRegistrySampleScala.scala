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
import io.gatling.kafka.Predef._

class KafkaSchemaRegistrySampleScala extends Simulation {

//#registry
kafka
  .bootstrapServers("broker1:9092")
  .schemaRegistry("https://registry.example.com:8081")
  .schemaRegistryBasicAuth("user", "password")
  // properties of Confluent's serializers and deserializers
  .schemaRegistryProperty("auto.register.schemas", "false")
  .schemaRegistryProperties(Map("use.latest.version" -> "true"))
//#registry

//#avro
val orderSchema = """
  {
    "type": "record",
    "name": "Order",
    "fields": [
      {"name": "id", "type": "string"},
      {"name": "amount", "type": "double"},
      {"name": "coupon", "type": ["null", "string"], "default": null}
    ]
  }"""

kafka("Avro order")
  .send("orders")
  .value(avro(
    orderSchema,
    // a union's non-null value is wrapped in an object keyed by its type
    StringBody("""{"id": "#{orderId}", "amount": #{amount}, "coupon": {"string": "#{coupon}"}}""")
  ))
//#avro

//#protobuf
kafka("Protobuf order")
  .send("orders")
  .value(protobuf(
    """
    syntax = "proto3";
    message Order {
      string id = 1;
      double amount = 2;
    }""",
    StringBody("""{"id": "#{orderId}", "amount": #{amount}}""")
  ))
//#protobuf

//#json-schema
kafka("JSON Schema order")
  .send("orders")
  .value(jsonSchema(
    """
    {
      "type": "object",
      "properties": {
        "id": {"type": "string"},
        "amount": {"type": "number"}
      }
    }""",
    StringBody("""{"id": "#{orderId}", "amount": #{amount}}""")
  ))
//#json-schema

//#subject
kafka("Avro order")
  .send("orders")
  .value(avroSubject("orders-value", ElFileBody("order.json")))
kafka("Protobuf order")
  .send("orders")
  .value(protobufSubject("orders-value", ElFileBody("order.json")))
kafka("JSON Schema order")
  .send("orders")
  .value(jsonSchemaSubject("orders-value", ElFileBody("order.json")))
//#subject

//#key
kafka("Avro key")
  .send("orders")
  .value(avroSubject("orders-value", ElFileBody("order.json")))
  // an Avro string, as JSON
  .key(avro("""{"type": "string"}""", StringBody("\"#{orderId}\"")))
//#key

//#decode
kafka("Check order")
  .requestReply("order-requests", "order-replies")
  .value(avroSubject("order-requests-value", ElFileBody("order.json")))
  .key("#{orderId}")
  // the checks read the reply's value as JSON
  .decodeAvroReply
  .check(jsonPath("$.status").is("ACCEPTED"))
//#decode
}
