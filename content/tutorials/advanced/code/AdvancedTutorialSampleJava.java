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

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class AdvancedTutorialSampleJava {
  public static final String pageUrl = "https://ecomm.gatling.io";
  //#login-endpoint
  public static final class APIendpoints {

  public static final String ACCESS_TOKEN = "AccessToken";
  public static final HttpRequestActionBuilder login =
      http("Login")
          .post("/login")
          .asFormUrlEncoded() // Short for header("Content-Type",
          // "application/x-www-form-urlencoded")
          .formParam("username", "#{username}")
          .formParam("password", "#{password}")
          .check(status().is(200))
          .check(jmesPath("accessToken").saveAs(ACCESS_TOKEN));

  }
  //#login-endpoint

  //#homepage-endpoint
  public static class WebEndpoints {
    // Define the home page request with response status validation
    // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
    public static final HttpRequestActionBuilder homePage =
        http("HomePage")
            .get(pageUrl)
            .check(status().in(200, 304)); // Accept both OK (200) and Not Modified (304) statuses
  }
  //#homepage-endpoint





  public static final class Step1 extends Simulation {
//#isolate-processes
ChainBuilder search =
  // let's give proper names, as they are displayed in the reports
  exec(http("List all products")
    .get("/products"))
    .pause(7)
    .exec(http("Search")
      .get("/products?search=Gatling"))
   .pause(3);

ChainBuilder browse = null; // TODO

ChainBuilder addToCart = null; // TODO

ChainBuilder checkout = null; // TODO
//#isolate-processes

//#processes
ScenarioBuilder scn = scenario("Scenario Name")
  .exec(search, browse, addToCart, checkout);
//#processes

//#populations
ScenarioBuilder browsingUsers = scenario("Browsing Users")
  .exec(search, browse);
ScenarioBuilder purchasingUsers = scenario("Purchasing Users")
  .exec(search, browse, addToCart, checkout);
//#populations

    HttpProtocolBuilder httpProtocol = http;

//#setup-users
{
  setUp(browsingUsers.injectOpen(atOnceUsers(10)).protocols(httpProtocol));
}
//#setup-users

//#setup-users-and-admins
{
  setUp(
    browsingUsers.injectOpen(rampUsers(10).during(10)),
    purchasingUsers.injectOpen(rampUsers(2).during(10))
  ).protocols(httpProtocol);
}
//#setup-users-and-admins
  }

//#feeder
FeederBuilder.Batchable<String> feeder =
  csv("search.csv").random(); // 1, 2

ChainBuilder search = exec(http("List all products")
  .get("/products"))
  .pause(1)
  .feed(feeder) // 3
  .exec(http("Search")
    .get("/products?search=#{searchCriterion}") // 4
    .check(
      jmesPath("products[0].name").isEL("#{searchProductName}")
    )
  );
//#feeder

  public static final class BrowseLoopSimple {

//#loop-simple
private static ChainBuilder gotoPage(int page) {
  return exec(http("Page " + page)
    .get("/products?page=" + page))
    .pause(1);
}

ChainBuilder browse =
  exec(
    gotoPage(0),
    gotoPage(1),
    gotoPage(2),
    gotoPage(3)
  );
//#loop-simple
  }

  public static final class BrowseLoopFor {

//#loop-for
ChainBuilder browse =
  repeat(4, "n").on( // 1
    exec(http("Page #{n}").get("/products?page=#{n}")) // 2
      .pause(1)
  );
//#loop-for
  }

  public static final class CheckAndTryMax {
//#check
ChainBuilder search = exec(http("List all products")
  .get("/products"))
  .pause(1)
  .exec(http("Search")
    .get("/products?search=Bag") // 4
    .check(
      jmesPath("products[0].name").is("Pink Throwback Hip Bag")
    )
    .check(status().is(session ->
        200 + java.util.concurrent.ThreadLocalRandom.current().nextInt(2) // 2
      ))
  );
//#check

//#tryMax-exitHereIfFailed
ChainBuilder tryMaxEdit = tryMax(2).on( // 1
  exec(search)
).exitHereIfFailed(); // 2
//#tryMax-exitHereIfFailed
  }
}
