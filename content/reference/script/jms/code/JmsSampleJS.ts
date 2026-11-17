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

import { ElFileBody, RawFileBody, rampUsersPerSec, scenario, simulation, xpath } from "@gatling.io/core";
//#imprts
import { jms, jmsJndiConnectionFactory, jmsProperty, simpleCheck } from "@gatling.io/jms";
//#imprts
import { JmsMessage } from "@gatling.io/jms";

{
//#jndi
const jndiBasedConnectionFactory = jmsJndiConnectionFactory
  .connectionFactoryName("ConnectionFactory")
  .url("tcp://localhost:61616")
  // optional, for performing JNDI lookup
  .credentials("username", "password")
  // optional, custom JNDI property
  .property("foo", "bar")
  .contextFactory("org.apache.activemq.jndi.ActiveMQInitialContextFactory");

const jmsProtocol = jms
  .connectionFactory(jndiBasedConnectionFactory);
//#jndi

//#options
jms
  .connectionFactory(jndiBasedConnectionFactory)
  .credentials("username", "password")
  // optional, default to non persistent
  .useNonPersistentDeliveryMode()

  // optional, default to 1
  // listener thread count
  // some JMS implementation (like IBM MQ) need more than one MessageListener
  // to achieve full readout performance
  .listenerThreadCount(5)

  // optional, default to `matchByMessageId`
  // specify how request and response messages should be matched when using `requestReply`
  // Use `matchByCorrelationId` for ActiveMQ.
  .matchByCorrelationId()
  .matchByMessageId()
  // use a custom matching strategy
  .messageMatcher({
    requestMatchId: (message) => message.correlationId(),
    responseMatchId: (message) => message.correlationId()
  })

  // optional, default to none
  .replyTimeout(1000);
//#options
}

{
//#message
// with a static text message
jms("name").send().queue("queueName")
  .textMessage("message");
// with a Gatling EL string text message
jms("name").send().queue("queueName")
  .textMessage("#{message}");
// with a function text message
jms("name").send().queue("queueName")
  .textMessage((session) => session.get("message"));
// with a ElFileBody template text message
jms("name").send().queue("queueName")
  .textMessage(ElFileBody("templatePath"));

// with a static bytes message
jms("name").send().queue("queueName")
  .bytesMessage([0, 1, 2]);
// with a RawFileBody bytes message
jms("name").send().queue("queueName")
  .bytesMessage(RawFileBody("templatePath"));
//#message

//#extra
jms("name").send().queue("queueName")
  .textMessage("message")
  .jmsType("type")
  .property("foo", "bar")
  // from 0 (lowest) to 9 (highest)
  .priority(9)
  // in seconds, or a Duration
  .timeToLive(30)
  .correlationId("#{orderId}");
//#extra

//#jmsPropertyCheck
jms("name").requestReply().queue("queueName")
  .textMessage("message")
  // check a String property
  .check(jmsProperty("foo").is("bar"))
  // check an int property
  .check(jmsProperty("foo").ofInt().is(1))
  // save a property
  .check(jmsProperty("foo").saveAs("fooProperty"));
//#jmsPropertyCheck
}

{
//#simple
const checkBodyTextCorrect = (m: JmsMessage): boolean =>
  // this assumes that the service just does an "uppercase" transform on the text
  // text() is null if the message isn't a text message
  m.text() === "HELLO FROM GATLING JMS DSL";

const request =
  jms("name").requestReply().queue("queueName")
    .textMessage("message")
    .check(simpleCheck(checkBodyTextCorrect));
//#simple
}

//#example-simulation
export default simulation((setUp) => {
  // look up the ConnectionFactory with JNDI
  // search the documentation of your JMS broker
  const connectionFactory = jmsJndiConnectionFactory
    .connectionFactoryName("ConnectionFactory")
    .url("tcp://localhost:61616")
    .credentials("user", "secret")
    .contextFactory("org.apache.activemq.jndi.ActiveMQInitialContextFactory");

  const jmsProtocol = jms
    .connectionFactory(connectionFactory)
    .usePersistentDeliveryMode();

  const scn = scenario("JMS DSL test").repeat(1).on(
    jms("req reply testing").requestReply()
      .queue("jmstestq")
      .textMessage("hello from gatling jms dsl")
      .property("test_header", "test_value")
      .jmsType("test_jms_type")
      .check(xpath("//foo"))
  );

  setUp(scn.injectOpen(rampUsersPerSec(10).to(1000).during(60)))
    .protocols(jmsProtocol);
});
//#example-simulation
