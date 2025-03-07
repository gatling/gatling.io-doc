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

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AdvancedTutorialSampleScala extends Simulation {
  val httpProtocol = http

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

//#isolate-processes
val search =
  // let's give proper names, as they are displayed in the reports
  exec(http("Home")
    .get("/"))
    .pause(7)
    .exec(http("Search")
      .get("/computers?f=macbook"))
    .pause(2)
    .exec(http("Select")
      .get("/computers/6"))
    .pause(3)

val browse = ???

val edit = ???
//#isolate-processes

  object Chains {
//#processes
val scn = scenario("Scenario Name")
  .exec(search, browse, edit)
//#processes

//#populations
val users = scenario("Users")
  .exec(search, browse)
val admins = scenario("Admins")
  .exec(search, browse, edit)
//#populations
  }

  import Chains._

//#setup-users
setUp(users.inject(atOnceUsers(10)).protocols(httpProtocol))
//#setup-users

//#setup-users-and-admins
setUp(
  users.inject(rampUsers(10).during(10)),
  admins.inject(rampUsers(2).during(10))
).protocols(httpProtocol)
//#setup-users-and-admins

object FeederSample {
//#feeder
val feeder = csv("search.csv").random // 1, 2

val search = exec(http("Home")
  .get("/"))
  .pause(1)
  .feed(feeder) // 3
  .exec(http("Search")
    .get("/computers?f=#{searchCriterion}") // 4
    .check(
      css("a:contains('#{searchComputerName}')", "href")
        .saveAs("computerUrl") // 5
    )
  )
  .pause(1)
  .exec(http("Select")
    .get("#{computerUrl}")) // 6
  .pause(1)
//#feeder
}

object BrowseLoopSimple {
//#loop-simple
def gotoPage(page: Int) =
  exec(http("Page " + page)
    .get("/computers?p=" + page))
    .pause(1)

val browse =
  exec(
    gotoPage(0),
    gotoPage(1),
    gotoPage(2),
    gotoPage(3),
    gotoPage(4)
  )
//#loop-simple
}

object BrowseLoopFor {
//#loop-for
val browse =
  repeat(5, "n") { // 1
    exec(http("Page #{n}").get("/computers?p=#{n}")) // 2
      .pause(1)
  }
//#loop-for
}

object CheckAndTryMax {
//#check
val edit =
  exec(http("Form").get("/computers/new"))
    .pause(1)
    .exec(http("Post")
      .post("/computers")
      .formParam("name", "computer xyz")
      .check(status.is(session =>
        200 + java.util.concurrent.ThreadLocalRandom.current.nextInt(2) // 2
      ))
    )
//#check

//#tryMax-exitHereIfFailed
val tryMaxEdit = tryMax(2) { // 1
  exec(edit)
}.exitHereIfFailed // 2
//#tryMax-exitHereIfFailed
}
}
