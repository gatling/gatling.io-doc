```scala
resolvers += "Confluent" at "https://packages.confluent.io/maven/"
libraryDependencies += ("io.confluent" % "kafka-avro-serializer" % "8.4.0" % "test,it")
  // Confluent's own build of the Kafka clients: keep Apache's, which the Kafka plugin depends on
  .exclude("org.apache.kafka", "kafka-clients")
```
