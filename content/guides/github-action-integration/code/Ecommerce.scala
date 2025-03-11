/*
 * Copyright 2011-2025 GatlingCorp (https://gatling.io)
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

//#ecommerce-example
import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class Ecommerce extends Simulation {

  val httpProtocol = http
    .baseUrl("https://ecomm.gatling.io")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val GetHome = scenario("Ecommerce")
    .exec(http("Get home")
      .get("/"))
//#ecommerce-example

//#set-up
  setUp(
    GetHome.inject(atOnceUsers(1))
  ).assertions(
    global.successfulRequests.percent.gt(90.0)
  ).protocols(httpProtocol)
}
//#set-up
