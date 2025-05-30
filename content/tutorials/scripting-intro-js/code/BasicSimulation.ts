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
import { http } from "@gatling.io/http";

export default simulation((setUp) => {
  // Define HTTP configuration
  // Reference: https://docs.gatling.io/reference/script/http/protocol/
  const httpProtocol = http
    .baseUrl("https://api-ecomm.gatling.io")
    .acceptHeader("application/json")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"
    );

  // Define scenario
  // Reference: https://docs.gatling.io/concepts/scenario/
  const scn = scenario("Scenario").exec(http("Session").get("/session"));
  
  // Define injection profile and execute the test
  // Reference: https://docs.gatling.io/concepts/injection/
  setUp(scn.injectOpen(constantUsersPerSec(2).during(60)))
    .protocols(httpProtocol);
});
//#full-example
