```xml
<repositories>
  <repository>
    <id>confluent</id>
    <url>https://packages.confluent.io/maven/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-avro-serializer</artifactId>
    <version>8.4.0</version>
    <scope>test</scope>
    <exclusions>
      <!-- Confluent's own build of the Kafka clients: keep Apache's, which the Kafka plugin depends on -->
      <exclusion>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-clients</artifactId>
      </exclusion>
    </exclusions>
  </dependency>
</dependencies>
```
