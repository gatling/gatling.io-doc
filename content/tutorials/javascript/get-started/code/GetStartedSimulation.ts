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

//#full-example
import { constantUsersPerSec, scenario, simulation } from "@gatling.io/core";
import { http, status } from "@gatling.io/http";

export default simulation((setUp) => {
  const httpProtocol = http
    .baseUrl("https://api-ecomm.gatling.io")
    .acceptHeader("application/json");

  const scn = scenario("JS Browse")
    .exec(
      http("GET session")
        .get("/session")
        .check(status().is(200))
    )
    .pause(1)
    .exec(
      http("GET catalog")
        .get("/catalog")
        .check(status().in(200, 304))
    );

  const usersPerSec = 2;
  const durationSeconds = 60;

  setUp(
    scn.injectOpen(constantUsersPerSec(usersPerSec).during(durationSeconds))
  ).protocols(httpProtocol);
});
//#full-example
