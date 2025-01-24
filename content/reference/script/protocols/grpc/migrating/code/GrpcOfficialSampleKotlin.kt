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

import io.gatling.javaapi.core.*

import io.gatling.javaapi.core.CoreDsl.*

//#imports
import io.gatling.javaapi.grpc.*

import io.gatling.javaapi.grpc.GrpcDsl.*
//#imports

import io.grpc.*

class GrpcOfficialSampleKotlin : Simulation() {

  init {
    //#protocol
    val grpcProtocol = grpc
      .forAddress("host", 50051)
      .useInsecureTrustManager() // not required, useInsecureTrustManager is the default
    //#protocol
  }

  private class RequestMessage private constructor(val message: String?) {
    data class Builder(var message: String? = null) {
      fun setMessage(message: String?) = apply {
        this.message = message
      }
      fun build() = RequestMessage(message)
    }
    companion object {
      fun newBuilder(): Builder = Builder()
    }
  }

  init {
    //#expression
    val request = { session: Session ->
      val message = session.getString("message")
      RequestMessage.newBuilder()
        .setMessage(message)
        .build()
    }
    //#expression
  }
}
