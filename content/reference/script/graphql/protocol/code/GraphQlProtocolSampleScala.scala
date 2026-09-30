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
import io.gatling.http.Predef._
//#imports
import io.gatling.graphql.Predef._
//#imports

class GraphQlProtocolSampleScala extends Simulation {

//#protocol-sample
val httpProtocol = http
  .baseUrl("https://api.example.com")
  .wsBaseUrl("wss://api.example.com")

val graphQlProtocol = graphql
  // path operations are sent to (default: /graphql)
  .endpoint("/graphql")
  // path subscriptions open a WebSocket to (default: /graphql)
  .wsEndpoint("/graphql")

// register both protocols
setUp(
  scenario("GraphQL").exec(graphql.query("{ me { id } }")).inject(atOnceUsers(1))
).protocols(httpProtocol, graphQlProtocol)
//#protocol-sample

//#error-policy
// fail as soon as there's an error (default)
graphql.failOnErrors
// only fail when there's an error and no data at all
graphql.failOnDataNull
// don't check the errors
graphql.ignoreErrors
//#error-policy

//#naming
// reject anonymous operations
graphQlProtocol.requireNamedOperations
// name anonymous operations after their file name
graphql.inferredOperationNaming(GraphQlOperationNaming.FileName)
// name anonymous operations after their root fields
graphql.inferredOperationNaming(GraphQlOperationNaming.RootFields)
// name anonymous operations after the hash of their document
graphql.inferredOperationNaming(GraphQlOperationNaming.Sha256)
// combine strategies, the first one that finds a name wins
graphql.inferredOperationNaming(GraphQlOperationNaming.FileName.orElse(GraphQlOperationNaming.RootFields))
// name anonymous operations with a custom strategy
graphql.inferredOperationNaming(document => Some(document.operationType.keyword + document.sha256))
//#naming

//#queries-over-get
graphql.queriesOverGet
//#queries-over-get

//#apq
// send the hash alone with POST once the server has registered it
graphql.automaticPersistedQueries
// send the hash alone with GET, so responses can be cached by a CDN
graphql.automaticPersistedQueriesOverGet
// share what the server knows between all virtual users
graphql.automaticPersistedQueries.sharePersistedQueries
//#apq
}
