```gradle
repositories {
  maven {
    url = "https://packages.confluent.io/maven/"
  }
}

dependencies {
  gatlingImplementation("io.confluent:kafka-avro-serializer:8.4.0")
}
```
