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

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

class ScriptingIntro3SampleJava {
  //#write-the-scenario
  public class EcommSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.baseUrl("https://ecomm.gatling.io")
        // set the "accept" header to a value suited for the expected response
        .acceptHeader("application/json");

    // Add the ScenarioBuilder:
    ScenarioBuilder myScenario = scenario("My Scenario")
        .exec(http("Request 1").get("/products/"));
  }
  //#write-the-scenario
}
