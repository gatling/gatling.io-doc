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
package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class BrowseAndAdd{

  public static ScenarioBuilder browseAndAdd =
    scenario("Browse & Add To Cart")
      .exec(http("Home").get("/").check(status().is(200)))
      .pause(1)
      .exec(http("Search").get("/search?q=fish").check(status().is(200)))
      .pause(2)
      .exec(http("View Item").get("/item/12345").check(status().is(200)))
      .pause(1)
      .exec(http("Add To Cart").post("/cart")
          .formParam("itemId", "12345")
          .formParam("qty", "1")
          .check(status().is(200)));
}
//#full-example
