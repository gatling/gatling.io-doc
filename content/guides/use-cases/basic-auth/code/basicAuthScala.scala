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

//#basic-auth-example
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicAuthScala extends Simulation {

// Set up HTTP protocol with basic auth  
  val httpProtocol = http
    .baseUrl("https://api.example.com")
    .acceptHeader("application/json")
    .basicAuth("#{username}", "#{password}")

// Load credentials from CSV
  val credentialsFeeder = csv("credentials.csv").circular

// Define the test scenario
  val authenticatedScenario = scenario("Authenticated Requests").exec(
    feed(credentialsFeeder),
    http("Get Protected Resource")
      .get("/api/protected-resource")
      .check(status.is(200)),
    pause(2),
    http("Post Protected Data")
      .post("/api/protected-data")
      .asJson
      .body(StringBody("""{"data": "test"}"""))
      .check(status.is(200))
  )
  
// Set up the simulation with open workload model
  setUp(
    authenticatedScenario.inject(
      rampUsers(50) during 5.minutes,
      rampUsers(100) during 15.minutes,
      rampUsers(0) during 10.minutes
    )
  ).protocols(httpProtocol)
}
//#basic-auth-example
