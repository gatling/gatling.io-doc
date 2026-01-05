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

//#ecommerce-example
import {
  simulation,
  scenario,
  atOnceUsers,
  global,
} from "@gatling.io/core";
import { http } from "@gatling.io/http";

export default simulation((setUp) => {

  const httpProtocol = http
    .baseUrl("https://ecomm.gatling.io")

  const GetHome = scenario("Ecommerce").exec(
    http("Get home").get("/")
  );
//#ecommerce-example

//#set-up
setUp(
    GetHome.injectOpen(atOnceUsers(1)),
).assertions(
    global().successfulRequests().percent().gt(90.0)
).protocols(httpProtocol);
//#set-up
});
