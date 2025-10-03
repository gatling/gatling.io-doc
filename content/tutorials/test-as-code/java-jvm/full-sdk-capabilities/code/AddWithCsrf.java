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

public class AddWithCsrf{

  public static ScenarioBuilder addWithCsrf =
    scenario("Add With CSRF")
      .exec(http("Get Form")
        .get("/account")
        .check(status().is(200))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf")))
      .pause(1)
      .exec(http("Post Form")
        .post("/account")
        .formParam("csrfToken", "#{csrf}")
        .formParam("email", "user@example.com")
        .check(status().in(200, 302)));
}
//#full-example