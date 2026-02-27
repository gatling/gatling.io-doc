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

//#full-example
package perf.simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DualJourneySimulation extends Simulation {

  private static final String BASE_URL = System.getProperty("baseUrl", "https://ecomm.gatling.io");

  HttpProtocolBuilder httpProtocol = http
      .baseUrl(BASE_URL)
      .acceptHeader("application/json, text/html;q=0.9,*/*;q=0.8")
      .userAgentHeader("Gatling/Java DualJourney");

  FeederBuilder<String> searchFeeder = csv("data/search_terms.csv").random();

  ScenarioBuilder browse =
    scenario("01 Browse")
      .feed(searchFeeder)
      .exec(http("Home").get("/").check(status().is(200)))
      .pause(1, 3)
      .exec(http("Search ${term}").get("/search").queryParam("q", "#{term}")
           .check(status().is(200)))
      .pause(1, 2)
      .exec(http("View ${term}").get("/items/${term}").check(status().in(200, 304)));

  ScenarioBuilder purchase =
    scenario("02 Purchase")
      .exec(http("Get CSRF").get("/checkout")
           .check(status().is(200))
           .check(regex("name=\"csrf\" value=\"([^\"]+)\"").saveAs("csrf")))
      .pause(1)
      .exec(http("Submit Order").post("/checkout")
           .formParam("csrf", "#{csrf}")
           .formParam("sku", "SKU-12345")
           .formParam("qty", "1")
           .check(status().in(200, 302)))
      .pause(2);

  {
    setUp(
      browse.injectOpen(
        rampUsers(50).during(60),              // ramp to 50
        constantUsersPerSec(10).during(180)    // steady 3 minutes
      ).protocols(httpProtocol),

      purchase.injectOpen(
        rampUsers(10).during(60),
        constantUsersPerSec(2).during(180)
      ).protocols(httpProtocol)

    ).assertions(
      global().successfulRequests().percent().gt(99.0),
      forAll().responseTime().percentile3().lt(1500),
      details("02 Purchase", "Submit Order").successfulRequests().percent().gt(98.0)
    );
  }
}
//#full-example
