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

//#imports
import { grpc } from "@gatling.io/grpc";
//#imports

//#protocol
const server = grpc
  .serverConfiguration("server")
  .forAddress("host", 50051)
  .useInsecureTrustManager(); // not required, useInsecureTrustManager is the default

const grpcProtocol = grpc
  .serverConfigurations(server);
//#protocol

//#expression
const request = (session) => {
  const message = session.get("message");
  return {
    message
  };
};
//#expression
