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
import io.gatling.http.Predef._
//#imports
import io.gatling.graphql.Predef._
//#imports

class GraphQlRequestsSampleScala {

//#inline
exec(graphql.query("query GetUser($id: ID!) { user(id: $id) { name } }").variable("id", 42))
exec(
  graphql
    .mutation("mutation CreateOrder($input: OrderInput!) { createOrder(input: $input) { id } }")
    .variable("input", Map("sku" -> "abc"))
)
//#inline

//#file
exec(graphql.file("graphql/getUser.graphql").variable("id", 42))
//#file

//#operation-name
exec(graphql.file("graphql/multiple.graphql").operationName("CreateOrder"))
//#operation-name

//#document
exec(graphql.document("{ me { id } }"))
//#document

//#dynamic-document
// request name and document as Expression Language Strings
exec(graphql.dynamicDocument("#{operationName}", "#{document}"))
// request name as a String, document as a function
exec(graphql.dynamicDocument("dynamic", session => session("document").validate[String]))
//#dynamic-document

//#request-name
exec(graphql.file("graphql/getUser.graphql").requestName("Get current user"))
exec(graphql.file("graphql/getUser.graphql").requestName("Get user #{userId}"))
//#request-name

//#variable
graphql
  .file("graphql/getUser.graphql")
  // static value
  .variable("id", 42)
  // Expression Language String
  .variable("locale", "#{locale}")
  // function
  .variable("token", session => session("token").validate[String])
//#variable

//#variables
graphql
  .file("graphql/getProducts.graphql")
  .variables(Map("first" -> 10, "after" -> "#{cursor}"))
graphql
  .file("graphql/getProducts.graphql")
  .variables(session => session("variables").validate[Map[String, Any]])
//#variables

//#variables-json
graphql
  .mutation("mutation CreateOrder($input: OrderInput!) { createOrder(input: $input) { id } }")
  .variablesJson("""{"input": {"items": [{"sku": #{sku.jsonStringify()}, "quantity": #{randomInt(1, 4)}}]}}""")
//#variables-json

//#options
graphql
  .file("graphql/getUser.graphql")
  .endpoint("/other/graphql")
  .header("X-Trace", "#{traceId}")
  .headers(Map("X-Client" -> "gatling"))
  .requestTimeout(10.seconds)
  .silent
  .ignoreProtocolChecks
//#options

//#over-get
graphql
  .query("query GetUser($id: ID!) { user(id: $id) { name } }")
  .variable("id", 42)
  .overGet
//#over-get

//#checks
graphql
  .file("graphql/getUser.graphql")
  .check(
    graphqlData.jsonPath("$.user.name").is("Stephane"),
    graphqlData.jsonPath("$.user.id").saveAs("userId"),
    graphqlData.jmesPath("user.name").exists,
    graphqlErrors.jsonPath("$[0].extensions.code").optional.saveAs("errorCode"),
    graphqlExtensions.jsonPath("$.tracing.duration").optional,
    // usual HTTP checks
    status.is(200)
  )
//#checks

//#post-check
graphql
  .file("graphql/getUser.graphql")
  .check(graphqlData.jsonPath("$.user.name").saveAs("userName"))
  .postCheck(session => session("userName").validate[String].map(userName => session.set("greeting", s"Hello $userName")))
//#post-check
}
