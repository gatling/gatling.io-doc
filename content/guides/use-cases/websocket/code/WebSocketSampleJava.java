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

//#websocket-example
package websockets;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.http.action.ws.WsInboundMessage;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class WebSocketSampleJava extends Simulation {

  HttpProtocolBuilder httpProtocol =
    http.wsBaseUrl("ws://localhost:8765")
      .wsUnmatchedInboundMessageBufferSize(100);

  ScenarioBuilder scn = scenario("Users")
    .exec(
      ws("Connect").connect("/").await(10).on(
        ws.checkTextMessage("Connect:check")
          .check(bodyString().is("connected"))
      ),
      ws("Send hello message")
        .sendText("Hello WebSocket!")
        .await(5).on(ws.checkTextMessage("Send hello message:check").check(bodyString().saveAs("message1"))),
      exec(session -> {
          System.out.println("First message received: " + session.getString("message1"));
          return session;
      }),
      during(10).on(
        ws.processUnmatchedMessages((messages, session) -> {
          if (messages.isEmpty()) {
            System.out.println("No message received during the last second");
          } else {
            var data = messages.stream()
              .filter(m -> m instanceof WsInboundMessage.Text)
              .map(m -> ((WsInboundMessage.Text) m).message())
              .collect(Collectors.joining(", "));
            System.out.println("Messages received during the last second: " + data);
          }
          return session;
        }),
        pause(1)
      ),
      ws("Close").close()
    );
}
//#websocket-example

class WebSocketWithInjectionProfileSampleJava extends Simulation {

  HttpProtocolBuilder httpProtocol = http;
  ScenarioBuilder scn = scenario("scenario");

  {
    //#user-example
    setUp(
      scn.injectOpen(atOnceUsers(1))
    ).protocols(httpProtocol);
    //#user-example
  }
}
