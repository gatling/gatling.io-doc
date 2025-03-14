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
  group,
  feed,
  pause,
  randomSwitch,
  uniformRandomSwitch,
  percent,
  global,
  constantUsersPerSec,
  incrementUsersPerSec,
  rampUsersPerSec,
  stressPeakUsers,
  getParameter,
  jsonFile,
} from "@gatling.io/core";
import { http, status } from "@gatling.io/http";
import { ElFileBody, jmesPath } from "@gatling.io/core";

const setUp = null as unknown as SetUpFunction;

export const loginPage = http("LoginPage").get(`/login`).check(status().in(200, 304));

export const homeAnonymous = group("homeAnonymous").on(
  http("").get("/")
);

export const homeAuthenticated = group("homeAuthenticated").on(
  http("").get("/")
);

export const addToCart = group("addToCart").on(
  http("").get("/")
);

export const buy = group("buy").on(
  http("").get("/")
);

//#project-structure
/*
.
├── .gatling/
└── src/
    ├── endpoints/
        ├── apiEndpoints.js
        └── webEndpoints.js
    ├── groups/
        ├── scenarioGroups.js
    ├── utils/
        ├── config.js
        └── keys.js
        └── targetEnvResolver.js
    ├── advancedSimulation.gatling.js
├── resources/
    ├── bodies/
    ├── data/
*/
//#project-structure

//#config
export const testType = getParameter("testType", "stress");
export const targetEnv = getParameter("targetEnv", "DEV");
//#config

//#login-endpoint
// Define login request
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#forms
export const login = http("Login")
  .post("/login")
  .asFormUrlEncoded()
  .formParam("username", "#{username}")
  .formParam("password", "#{password}")
  .check(status().is(200))
  .check(jmesPath("accessToken").saveAs("AccessToken"));
//#login-endpoint
//#with-authentication-headers-wrapper
// Add authentication header if an access token exists in the session
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#headers
export function withAuthenticationHeader(protocolBuilder) {
  return protocolBuilder.header("Authorization", (session) =>
    session.contains("AccessToken") ? session.get("AccessToken") : ""
  );
}
//#with-authentication-headers-wrapper
//#homepage-endpoint
// Define the home page request with response status validation
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
export const homePage = http("HomePage").get("https://ecomm.gatling.io").check(status().in(200, 304)); // Accept both OK (200) and Not Modified (304) statuses
//#homepage-endpoint
//#authenticate-group
// Define a feeder for user data
// Reference: https://docs.gatling.io/reference/script/core/feeder/
export const usersFeeder = jsonFile("data/users_dev.json").circular();

// Define authentication process
export const authenticate = group("authenticate").on(
  loginPage,
  feed(usersFeeder),
  pause(5, 15),
  login
);

//#authenticate-group
() => {
//#http-protocol-builder-simple
const httpProtocol = http
  .baseUrl("https://api-ecomm.gatling.io")
  .acceptHeader("application/json")
  .userAgentHeader(
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
  );
//#http-protocol-builder-simple
}
() => {
//#http-protocol-builder-with-headers
const httpProtocol = withAuthenticationHeader(
  http
    .baseUrl("https://api-ecomm.gatling.io")
    .acceptHeader("application/json")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
    )
);
//#http-protocol-builder-with-headers
}
//#scenario-1
// Define scenario 1 with a random traffic distribution
// Reference: https://docs.gatling.io/reference/script/core/scenario/#randomswitch
const scn1 = scenario("Scenario 1")
  .exitBlockOnFail()
  .on(
    randomSwitch().on(
      percent(70).then(
        group("fr").on(
          homeAnonymous,
          pause(5, 15),
          authenticate,
          homeAuthenticated,
          pause(5, 15),
          addToCart,
          pause(5, 15),
          buy
        )
      ),
      percent(30).then(
        group("us").on(
          homeAnonymous,
          pause(5, 15),
          authenticate,
          homeAuthenticated,
          pause(5, 15),
          addToCart,
          pause(5, 15),
          buy
        )
      )
    )
  )
  .exitHereIfFailed();
//#scenario-1
//#scenario-2
// Define scenario 2 with a uniform traffic distribution
// Reference: https://docs.gatling.io/reference/script/core/scenario/#uniformrandomswitch
const scn2 = scenario("Scenario 2")
  .exitBlockOnFail()
  .on(
    uniformRandomSwitch().on(
      group("fr").on(
        homeAnonymous,
        pause(5, 15),
        authenticate,
        homeAuthenticated,
        pause(5, 15),
        addToCart,
        pause(5, 15),
        buy
      ),
      group("us").on(
        homeAnonymous,
        pause(5, 15),
        authenticate,
        homeAuthenticated,
        pause(5, 15),
        addToCart,
        pause(5, 15),
        buy
      )
    )
  )
  .exitHereIfFailed();
//#scenario-2
//#injection-profile-switch
// Define different load injection profiles
// Reference: https://docs.gatling.io/reference/script/core/injection/
const injectionProfile = (scn) => {
  switch (testType) {
    case "capacity":
      return scn.injectOpen(
        incrementUsersPerSec(1)
          .times(4)
          .eachLevelLasting({ amount: 10, unit: "seconds" })
          .separatedByRampsLasting(4)
          .startingFrom(10)
      );
    case "soak":
      return scn.injectOpen(
        constantUsersPerSec(1).during({ amount: 180, unit: "seconds" })
      );
    case "stress":
      return scn.injectOpen(stressPeakUsers(200).during({ amount: 20, unit: "seconds" }));
    case "breakpoint":
      return scn.injectOpen(
        rampUsersPerSec(0).to(20).during({ amount: 120, unit: "seconds" })
      );
    case "ramp-hold":
      return scn.injectOpen(
        rampUsersPerSec(0).to(20).during({ amount: 30, unit: "seconds" }),
        constantUsersPerSec(20).during({ amount: 1, unit: "minutes" })
      );
    case "smoke":
      return scn.injectOpen(atOnceUsers(1));
    default:
      return scn.injectOpen(atOnceUsers(1));
  }
};
//#injection-profile-switch
//#assertions
// Define assertions for different test types
// Reference: https://docs.gatling.io/reference/script/core/assertions/
const assertions = [
  global().responseTime().percentile(90.0).lt(500),
  global().failedRequests().percent().lt(5.0)
];

const getAssertions = () => {
  switch (testType) {
    case "capacity":
    case "soak":
    case "stress":
    case "breakpoint":
    case "ramp-hold":
      return assertions;
    case "smoke":
      return [global().failedRequests().count().lt(1.0)];
    default:
      return assertions;
  }
};
//#assertions
() => {
const httpProtocol = withAuthenticationHeader(
  http
    .baseUrl("https://api-ecomm.gatling.io")
    .acceptHeader("application/json")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
    )
);
//#setup-block
// Set up the simulation with scenarios, load profiles, and assertions
setUp(injectionProfile(scn1), injectionProfile(scn2))
.assertions(...getAssertions())
.protocols(httpProtocol);
//#setup-block
}
//#keys
export const ACCESS_TOKEN = "AccessToken";
//#keys
() => {
//#keys-usage
const login = http("Login")
  .post("/login")
  .asFormUrlEncoded()
  .formParam("username", "#{username}")
  .formParam("password", "#{password}")
  .check(status().is(200))
  .check(jmesPath("accessToken").saveAs(ACCESS_TOKEN));
//#keys-usage
}
//#target-env-resolver
// Resolve environment-specific configuration based on the target environment
const targetEnvResolver = (targetEnv) => {
  switch (targetEnv) {
    case "DEV":
      return {
        pageUrl: "https://ecomm.gatling.io",
        baseUrl: "https://api-ecomm.gatling.io",
        usersFeederFile: "data/users_dev.json",
        productsFeederFile: "data/products_dev.csv"
      };
    default:
      return {
        pageUrl: "https://ecomm.gatling.io",
        baseUrl: "https://api-ecomm.gatling.io",
        usersFeederFile: "data/users_dev.json",
        productsFeederFile: "data/products_dev.csv"
      };
  }
};
//#target-env-resolver