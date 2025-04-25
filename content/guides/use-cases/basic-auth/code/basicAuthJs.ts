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

//#basic-auth-example
import {
  simulation,
  scenario,
  csv,
  pause,
  feed,
  rampUsers,
  StringBody,
  
} from "@gatling.io/core";
import { http, status } from "@gatling.io/http";

export default simulation((setUp) => {
  // Load credentials from CSV
  const credentialsFeeder = csv("credentials.csv").circular();

  // Set up HTTP protocol with basic auth
  const httpProtocol = http
    .baseUrl("https://api.example.com")
    .acceptHeader("application/json")
    // Use built-in basicAuth with dynamic credentials from the feeder
    .basicAuth( "#{username}", "#{password}");

  // Define the test scenario
  const authenticatedScenario = scenario("Authenticated Requests").exec(
    feed(credentialsFeeder),
    http("Get Protected Resource").get("/api/protected-resource").check(status().is(200)),
    pause(2),
    http("Post Protected Data")
      .post("/api/protected-data")
      .asJson()
      .body(StringBody(`{"data": "test"}`))
      .check(status().is(200))
  );

  // Set up the simulation with open workload model
  setUp(
    authenticatedScenario.injectOpen(
      rampUsers(50).during({ amount: 5, unit: "minutes" }), // Ramp up to 50 users over 5 minutes
      rampUsers(100).during({ amount: 15, unit: "minutes" }), // Continue ramping to 100 users over 15 minutes
      rampUsers(0).during({ amount: 10, unit: "minutes" }) // Ramp down to 0 over 10 minutes
    )
  ).protocols(httpProtocol);
});
//#basic-auth-example
