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

//#web-example
const WebSocket = require("ws");

const server = new WebSocket.Server({ port: 8765 });

function send(socket, data, i, n) {
  if (i <= n) {
    setTimeout(function() {
      const message = `${data} ${i}/${n}`;
      socket.send(message);
      console.log(`-> ${message}`);

      send(socket, data, i + 1, n);
    }, Math.floor(Math.random() * 1000));
  }
}

server.on("connection", (socket) => {
  console.log("new client connected");
  socket.send("connected");
  socket.send("connected");

  //socket.on("message", (data) => {
    const message = "hello client"
    //console.log(`message received: ${message}`);
    const n = 5 + Math.floor(Math.random() * 15);
    console.log(`responding with ${n} messages`);
    send(socket, message, 1, n);
  //});

  socket.on("close", () => {
    console.log("client disconnected");
  });
});

console.log("listening on ws://localhost:8765");
//#web-example