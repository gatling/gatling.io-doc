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

import {
  ChainBuilder,
  SetUpFunction,
  atOnceUsers,
  css,
  csv,
  exec,
  rampUsers,
  repeat,
  scenario,
  tryMax,
} from "@gatling.io/core";
import { http, status } from "@gatling.io/http";

const setUp = null as unknown as SetUpFunction;

//#login-endpoint
// TODO
//#login-endpoint
//#with-authentication-headers-wrapper
// TODO
//#with-authentication-headers-wrapper
//#homepage-endpoint
// TODO
//#homepage-endpoint
//#authenticate-group
// TODO
//#authenticate-group
//#http-protocol-builder-simple
// TODO
//#http-protocol-builder-simple
//#http-protocol-builder-with-headers
// TODO
//#http-protocol-builder-with-headers
//#scenario-1
// TODO
//#scenario-1
//#scenario-2
// TODO
//#scenario-2
//#injection-profile-switch
// TODO
//#injection-profile-switch
//#assertions
// TODO
//#assertions
//#setup-block
// TODO
//#setup-block
//#config
// TODO
//#config
//#keys
// TODO
//#keys
//#keys-usage
// TODO
//#keys-usage
//#target-env-resolver
// TODO
//#target-env-resolver

() => {
  //#isolate-processes
  const search =
    // let's give proper names, as they are displayed in the reports
    exec(http("Home").get("/"))
      .pause(7)
      .exec(http("Search").get("/computers?f=macbook"))
      .pause(2)
      .exec(http("Select").get("/computers/6"))
      .pause(3);

  const browse = undefined; // TODO

  const edit = undefined; // TODO
  //#isolate-processes
};

const search = null as unknown as ChainBuilder;
const browse = null as unknown as ChainBuilder;
const edit = null as unknown as ChainBuilder;

//#processes
const scn = scenario("Scenario Name").exec(search, browse, edit);
//#processes

//#populations
const users = scenario("Users").exec(search, browse);
const admins = scenario("Admins").exec(search, browse, edit);
//#populations

const httpProtocol = http;

//#setup-users
setUp(users.injectOpen(atOnceUsers(10)).protocols(httpProtocol));
//#setup-users

//#setup-users-and-admins
setUp(
  users.injectOpen(rampUsers(10).during(10)),
  admins.injectOpen(rampUsers(2).during(10))
).protocols(httpProtocol);
//#setup-users-and-admins

() => {
  //#feeder
  const feeder = csv("search.csv").random(); // 1, 2

  const search = exec(http("Home").get("/"))
    .pause(1)
    .feed(feeder) // 3
    .exec(
      http("Search")
        .get("/computers?f=#{searchCriterion}") // 4
        .check(
          css("a:contains('#{searchComputerName}')", "href").saveAs(
            "computerUrl"
          ) // 5
        )
    )
    .pause(1)
    .exec(http("Select").get("#{computerUrl}")) // 6
    .pause(1);
  //#feeder
};

() => {
  //#loop-simple
  // @ts-ignore
  const gotoPage = (page) =>
    exec(http("Page " + page).get("/computers?p=" + page)).pause(1);

  const browse = exec(
    gotoPage(0),
    gotoPage(1),
    gotoPage(2),
    gotoPage(3),
    gotoPage(4)
  );
  //#loop-simple
};

() => {
  //#loop-for
  const browse = repeat(5, "n").on(
    // 1
    exec(http("Page #{n}").get("/computers?p=#{n}")) // 2
      .pause(1)
  );
  //#loop-for
};

() => {
  //#check
  const edit = exec(http("Form").get("/computers/new"))
    .pause(1)
    .exec(
      http("Post")
        .post("/computers")
        .formParam("name", "computer xyz")
        .check(
          status().is(
            (session) => 200 + Math.floor(Math.random() * 2) // 2
          )
        )
    );
  //#check
};

//#tryMax-exitHereIfFailed
const tryMaxEdit = tryMax(2)
  .on(
    // 1
    exec(edit)
  )
  .exitHereIfFailed(); // 2
//#tryMax-exitHereIfFailed
