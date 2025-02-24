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

import { simulation } from "@gatling.io/core";
import { http } from "@gatling.io/http";

//#define-the-protocol-class
export default simulation((setUp) => {
  // Add the HttpProtocolBuilder:
  const httpProtocol = http
    .baseUrl("https://ecomm.gatling.io")
    // set the "accept" header to a value suited for the expected response
    .acceptHeader("application/json");
});
//#define-the-protocol-class
