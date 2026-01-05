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

import { SetUpFunction, rampUsers, scenario } from "@gatling.io/core";

const scn = scenario("scenario");
const setUp = null as unknown as SetUpFunction;

//#injection-from-options
import { getParameter } from "@gatling.io/core";

const nbUsers = parseInt(getParameter(
  "users", // Key used to identify the option
  "1" // Default value (optional)
));
const myRamp = parseInt(getParameter("ramp", "0"));

setUp(scn.injectOpen(rampUsers(nbUsers).during(myRamp)));
//#injection-from-options

//#injection-from-env-vars
import { getEnvironmentVariable } from "@gatling.io/core";

const mySecret = getEnvironmentVariable(
  "MY_SECRET", // Name of the environment variables
  "FOO"// Default value (optional)
);
//#injection-from-env-vars
