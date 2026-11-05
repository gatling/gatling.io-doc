```gradle
repositories {
  maven {
    url = "https://packages.confluent.io/maven/"
  }
}

dependencies {
  gatlingImplementation("io.confluent:kafka-avro-serializer:8.4.0") {
    // Confluent's own build of the Kafka clients: keep Apache's, which the Kafka plugin depends on
    exclude(group: "org.apache.kafka", module: "kafka-clients")
  }
}
```
