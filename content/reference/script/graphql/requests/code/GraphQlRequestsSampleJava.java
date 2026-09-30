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

import io.gatling.javaapi.core.*;

import java.time.Duration;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
//#imports
import static io.gatling.javaapi.graphql.GraphQlDsl.*;
//#imports

class GraphQlRequestsSampleJava {

  {
//#inline
exec(graphql.query("query GetUser($id: ID!) { user(id: $id) { name } }").variable("id", 42));
exec(graphql.mutation("mutation CreateOrder($input: OrderInput!) { createOrder(input: $input) { id } }")
  .variable("input", Map.of("sku", "abc")));
//#inline

//#file
exec(graphql.file("graphql/getUser.graphql").variable("id", 42));
//#file

//#operation-name
exec(graphql.file("graphql/multiple.graphql").operationName("CreateOrder"));
//#operation-name

//#document
exec(graphql.document("{ me { id } }"));
//#document

//#dynamic-document
// request name and document as Expression Language Strings
exec(graphql.dynamicDocument("#{operationName}", "#{document}"));
// request name as a String, document as a function
exec(graphql.dynamicDocument("dynamic", session -> session.getString("document")));
//#dynamic-document

//#request-name
exec(graphql.file("graphql/getUser.graphql").requestName("Get current user"));
exec(graphql.file("graphql/getUser.graphql").requestName(session -> "Get user " + session.getString("userId")));
//#request-name

//#variable
graphql.file("graphql/getUser.graphql")
  // static value
  .variable("id", 42)
  // Expression Language String
  .variable("locale", "#{locale}")
  // function
  .variable("token", session -> session.getString("token"));
//#variable

//#variables
graphql.file("graphql/getProducts.graphql")
  .variables(Map.of("first", 10, "after", "#{cursor}"));
graphql.file("graphql/getProducts.graphql")
  .variables(session -> Map.of("first", 10, "after", session.getString("cursor")));
//#variables

//#variables-json
graphql.mutation("mutation CreateOrder($input: OrderInput!) { createOrder(input: $input) { id } }")
  .variablesJson("{\"input\": {\"items\": [{\"sku\": #{sku.jsonStringify()}, \"quantity\": #{randomInt(1, 4)}}]}}");
//#variables-json

//#options
graphql.file("graphql/getUser.graphql")
  .endpoint("/other/graphql")
  .header("X-Trace", "#{traceId}")
  .headers(Map.of("X-Client", "gatling"))
  .requestTimeout(Duration.ofSeconds(10))
  .silent()
  .ignoreProtocolChecks();
//#options

//#over-get
graphql.query("query GetUser($id: ID!) { user(id: $id) { name } }")
  .variable("id", 42)
  .overGet();
//#over-get

//#checks
graphql.file("graphql/getUser.graphql")
  .check(
    graphqlData.jsonPath("$.user.name").is("Stephane"),
    graphqlData.jsonPath("$.user.id").saveAs("userId"),
    graphqlData.jmesPath("user.name").exists(),
    graphqlErrors.jsonPath("$[0].extensions.code").optional().saveAs("errorCode"),
    graphqlExtensions.jsonPath("$.tracing.duration").optional(),
    // usual HTTP checks
    status().is(200)
  );
//#checks

//#post-check
graphql.file("graphql/getUser.graphql")
  .check(graphqlData.jsonPath("$.user.name").saveAs("userName"))
  .postCheck(session -> {
    if (!session.contains("userName")) {
      throw new IllegalStateException("userName is missing");
    }
    return session.set("greeting", "Hello " + session.getString("userName"));
  });
//#post-check
  }
}
