```xml
<build>
  <plugins>
    <plugin>
      <groupId>io.github.ascopes</groupId>
      <artifactId>protobuf-maven-plugin</artifactId>
      <version>${protobuf-maven-plugin.version}</version>
      <executions>
        <execution>
          <goals>
            <goal>generate-test</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <protocVersion>${protobuf.version}</protocVersion>
        <binaryMavenPlugins>
          <binaryMavenPlugin>
            <groupId>io.grpc</groupId>
            <artifactId>protoc-gen-grpc-java</artifactId>
            <version>${grpc.version}</version>
            <!-- Avoid dependency on javax.annotation-api -->
            <options>@generated=omit</options>
          </binaryMavenPlugin>
        </binaryMavenPlugins>
      </configuration>
    </plugin>
  </plugins>
</build>
```

With the following properties:

```xml
<properties>
  <grpc.version>1.75.0</grpc.version>
  <protobuf.version>4.32.1</protobuf.version>
  <protobuf-maven-plugin.version>3.9.1</protobuf-maven-plugin.version>
</properties>
```

Add your proto files in the `src/test/proto` directory. 

Check the demo project for a full example:
[Gatling gRPC Java demo with Maven](https://github.com/gatling/gatling-grpc-demo/tree/main/java/maven).
