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

class GraphQlSubscriptionsSampleScala {

//#connect
exec(graphqlWs.connect)
exec(
  graphqlWs.connect
    .requestName("Open GraphQL WebSocket")
    .endpoint("/other/graphql")
    .connectionInitPayload("authorization", "Bearer #{token}")
    .connectionInitPayload(Map("locale" -> "en"))
    .ackTimeout(5.seconds)
)
exec(
  graphqlWs.connect
    .connectionInitPayloadJson("""{"authorization": "Bearer #{token.jsonStringify()}"}""")
)
//#connect

//#subscribe
exec(
  graphqlWs
    .subscribe("subscription OrderEvents($id: ID!) { orderCreated(customerId: $id) { id } }")
    .variable("id", "#{customerId}")
    .subscriptionName("orders")
)
exec(graphqlWs.subscribeFile("graphql/orderEvents.graphql"))
//#subscribe

//#await
exec(
  graphqlWs
    .subscribeFile("graphql/orderEvents.graphql")
    .await(10.seconds)(
      graphqlWs.checkNext
        .check(
          graphqlWs.data.jsonPath("$.orderCreated.id").saveAs("orderId"),
          graphqlWs.errors.jsonPath("$[0].message").optional
        )
    )
)
//#await

//#await-next
exec(
  graphqlWs
    .subscribeFile("graphql/orderEvents.graphql")
    .await(10.seconds)(graphqlWs.checkNext("first event"))
    // wait for 5 more messages
    .awaitNext(30.seconds, 5)
)
//#await-next

//#unsubscribe
exec(graphqlWs.unsubscribe("orders"))
exec(graphqlWs.unsubscribe("orders").requestName("Stop orders"))
//#unsubscribe

//#close
exec(graphqlWs.close)
//#close

//#example
scenario("Subscriptions").exec(
  graphqlWs.connect.connectionInitPayload("authorization", "Bearer #{token}"),
  graphqlWs
    .subscribe("subscription OrderEvents($id: ID!) { orderCreated(customerId: $id) { id } }")
    .variable("id", "#{customerId}")
    .subscriptionName("orders")
    .await(10.seconds)(
      graphqlWs.checkNext.check(graphqlWs.data.jsonPath("$.orderCreated.id").saveAs("orderId"))
    )
    .awaitNext(30.seconds, 3),
  graphqlWs.unsubscribe("orders"),
  graphqlWs.close
)
//#example
}
