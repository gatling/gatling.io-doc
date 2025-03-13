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
import io.gatling.core.structure.ChainBuilder
import io.gatling.core.structure.{ScenarioBuilder, PopulationBuilder}
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class AdvancedTutorialSampleScala extends Simulation {
  val httpProtocol = http

  val testType: String = "smoke";

  val homeAnonymous: ChainBuilder = 
    group("homeAnonymous") (
      exec(http("Get").get("/"))
    )
  val authenticate: ChainBuilder = 
    group("homeAnonymous") (
      exec(http("Get").get("/"))
    )
  val homeAuthenticated: ChainBuilder = 
    group("homeAnonymous") (
      exec(http("Get").get("/"))
    )
  val addToCart: ChainBuilder = 
    group("homeAnonymous") (
      exec(http("Get").get("/"))
    )
  val buy: ChainBuilder = 
    group("homeAnonymous") (
      exec(http("Get").get("/"))
    )

//#project-structure
/*
.
├── .gatling/
└── src/
    ├── test/
        ├── scala/
            ├── example/
                ├── endpoints/
                    ├── APIendpoints.scala
                    └── WebEndpoints.scala
                ├── groups/
                    ├── ScenarioGroups.scala
                ├── utils/
                    ├── Config.scala
                    └── Keys.scala
                    └── TargetEnvResolver.scala
                ├── AdvancedSimulation.scala
        ├── resources/
            ├── bodies/
            └── data/
*/
//#project-structure

//#login-endpoint
object APIendpoints {
  // Define login request
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#forms
  val login = http("Login")
    .post("/login")
    .asFormUrlEncoded
    .formParam("username", "#{username}")
    .formParam("password", "#{password}")
    .check(status.is(200))
    .check(jmesPath("accessToken").saveAs("AccessToken"))
}
//#login-endpoint

//#with-authentication-headers-wrapper
// Add authentication header if an access token exists in the session
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#headers
def withAuthenticationHeader(protocolBuilder: HttpProtocolBuilder): HttpProtocolBuilder = {
  protocolBuilder.header(
    "Authorization",
    session => if (session.contains("AccessToken")) session("AccessToken").as[String] else ""
  )
}
//#with-authentication-headers-wrapper

//#homepage-endpoint
object WebEndpoints {
  // Define the home page request with response status validation
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
  val homePage = http("HomePage")
    .get("https://ecomm.gatling.io")
    .check(status.in(200, 304)) // Accept both OK (200) and Not Modified (304) statuses
}
//#homepage-endpoint


object ScenarioGroupsWrapper {

val login = http("Login").post("/login")
val loginPage = http("LoginPage").get("/login")

//#authenticate-group
object ScenarioGroups {
  // Define a feeder for user data
  // Reference: https://docs.gatling.io/reference/script/core/feeder/
  private val usersFeeder = jsonFile("data/users_dev.json").circular

  // Define authentication process
  val authenticate: ChainBuilder = 
    group("authenticate") (
      loginPage,
      feed(usersFeeder),
      pause(5, 15),
      login
    )
}
//#authenticate-group
}

class AdvancedSimulation extends Simulation {

//#http-protocol-builder-simple
private val httpProtocol =
        http.baseUrl("https://api-ecomm.gatling.io")
            .acceptHeader("application/json")
            .userAgentHeader(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0");
//#http-protocol-builder-simple

//#http-protocol-builder-with-headers
private val httpProtocolWithAuthentication =  withAuthenticationHeader(
        http.baseUrl("https://api-ecomm.gatling.io")
            .acceptHeader("application/json")
            .userAgentHeader(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"));
//#http-protocol-builder-with-headers

//#scenario-1
// Define scenario 1 with a random traffic distribution
// Reference: https://docs.gatling.io/reference/script/core/scenario/#randomswitch
private val scn1: ScenarioBuilder = scenario("Scenario 1")
  .exitBlockOnFail(
    randomSwitch(
      70.0 -> group("fr")(
          homeAnonymous,
          pause(1, 15),
          authenticate,
          homeAuthenticated,
          pause(1, 15),
          addToCart,
          pause(1, 15),
          buy
      ),
      30.0 -> group("us")(
          homeAnonymous,
          pause(1, 15),
          authenticate,
          homeAuthenticated,
          pause(1, 15),
          addToCart,
          pause(1, 15),
          buy
      )
    )
  )
  .exitHereIfFailed
//#scenario-1

//#scenario-2
// Define scenario 2 with a uniform traffic distribution
// Reference: https://docs.gatling.io/reference/script/core/scenario/#uniformrandomswitch
private val scn2: ScenarioBuilder = scenario("Scenario 2")
  .exitBlockOnFail(
    uniformRandomSwitch(
      group("fr")(
          homeAnonymous,
          pause(1, 15),
          authenticate,
          homeAuthenticated,
          pause(1, 15),
          addToCart,
          pause(1, 15),
          buy
          ),
    group("us")(
          homeAnonymous,
          pause(1, 15),
          authenticate,
          homeAuthenticated,
          pause(1, 15),
          addToCart,
          pause(1, 15),
          buy
      )
    )
  )
  .exitHereIfFailed
//#scenario-2

//#injection-profile-switch
// Define different load injection profiles
// Reference: https://docs.gatling.io/reference/script/core/injection/
private def injectionProfile(scn: ScenarioBuilder): PopulationBuilder = {
  testType match {
    case "capacity" =>
      scn.inject(
        incrementUsersPerSec(1)
          .times(4)
          .eachLevelLasting(10)
          .separatedByRampsLasting(4)
          .startingFrom(10)
      )
    case "soak" =>
      scn.inject(constantUsersPerSec(1).during(180))
    case "stress" =>
      scn.inject(stressPeakUsers(200).during(20))
    case "breakpoint" =>
      scn.inject(rampUsers(300).during(120))
    case "ramp-hold" =>
      scn.inject(
        rampUsersPerSec(0).to(20).during(30),
        constantUsersPerSec(20).during(60)
      )
    case "smoke" =>
      scn.inject(atOnceUsers(1))
    case _ =>
      scn.inject(atOnceUsers(1))
  }
}
//#injection-profile-switch

//#assertions
// Define assertions for different test types
// Reference: https://docs.gatling.io/reference/script/core/assertions/
private def assertions: Seq[Assertion] = Seq(
        global.responseTime.percentile(90.0).lt(500),
        global.failedRequests.percent.lt(5.0)
      )

private def getAssertions(): Seq[Assertion] = {
  testType match {
    case "capacity" | "soak" | "stress" | "breakpoint" | "ramp-hold" =>
      assertions
    case "smoke" =>
      Seq(global.failedRequests.count.lt(1L))
    case _ => assertions
  }
}
//#assertions

//#setup-block
// Set up the simulation with scenarios, load profiles, and assertions
  setUp(
    injectionProfile(scn1),
    injectionProfile(scn2)
  ).assertions(
    getAssertions(): _*
  ).protocols(httpProtocolWithAuthentication)
//#setup-block

}

//#config
object Config {
  val testType: String = System.getProperty("type", "smoke")
  val targetEnv: String = System.getProperty("targetEnv", "DEV")
}
//#config

//#keys
object Keys {
  val ACCESS_TOKEN: String = "AccessToken";
}
//#keys

object KeysUsage {
val ACCESS_TOKEN = "AccessToken";

//#keys-usage
val login = http("Login")
    .post("/login")
    .asFormUrlEncoded
    .formParam("username", "#{username}")
    .formParam("password", "#{password}")
    .check(status.is(200))
    .check(jmesPath("accessToken").saveAs(ACCESS_TOKEN))
//#keys-usage
}

//#target-env-resolver
object TargetEnvResolver {
  // Record to store environment-specific information
  case class EnvInfo(pageUrl: String, baseUrl: String, usersFeederFile: String, productsFeederFile: String)

  // Resolve environment-specific configuration based on the target environment
  def resolveEnvironmentInfo(targetEnv: String): EnvInfo = targetEnv match {
    case "DEV" =>
      EnvInfo("https://ecomm.gatling.io", "https://api-ecomm.gatling.io", "data/users_dev.json", "data/products_dev.csv")
    case _ =>
      EnvInfo("https://ecomm.gatling.io", "https://api-ecomm.gatling.io", "data/users_dev.json", "data/products_dev.csv")
  }
}
//#target-env-resolver

}