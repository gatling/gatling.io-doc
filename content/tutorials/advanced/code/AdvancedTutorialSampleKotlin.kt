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
 import io.gatling.javaapi.http.HttpDsl.*
 import io.gatling.javaapi.http.*

 import java.util.Optional
 
 class AdvancedTutorialSampleKotlin {

//#project-structure
/*
.
├── .gatling/
└── src/
    ├── gatling/
        ├── kotlin/
            ├── example/
                ├── endpoints/
                    ├── APIendpoints.kt
                    └── WebEndpoints.kt
                ├── groups/
                    ├── ScenarioGroups.kt
                ├── utils/
                    ├── Config.kt
                    └── Keys.kt
                    └── TargetEnvResolver.kt
                ├── AdvancedSimulation.kt
        ├── resources/
            ├── bodies/
            ├── data/
*/
//#project-structure
 

//#login-endpoint
// Define login request
val login: HttpRequestActionBuilder = http("Login")
    .post("/login")
    .asFormUrlEncoded()
    .formParam("username", "#{username}")
    .formParam("password", "#{password}")
    .check(status().`is`(200))
    .check(jmesPath("accessToken").saveAs("AccessToken"))
//#login-endpoint

//#homepage-endpoint
// Define the home page request with response status validation
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
val homePage: HttpRequestActionBuilder = http("HomePage")
    .get("https://ecomm.gatling.io")
    .check(status().`in`(200, 304)) // Accept both OK (200) and Not Modified (304) statuses
//#homepage-endpoint

object ScenarioGroupsWrapper {

  val login = http("Login").post("/login")
  val loginPage = http("LoginPage").get("/login")

//#authenticate-group
// Define a feeder for user data
// Reference: https://docs.gatling.io/reference/script/core/feeder/
private val usersFeeder = jsonFile("data/users_dev.json").circular()
// Define authentication process
val authenticate: ChainBuilder = group("authenticate")
    .on(loginPage, feed(usersFeeder), pause(5, 15), login)
//#authenticate-group

}

class AdvancedSimulation : Simulation() {

  val homeAnonymous: ChainBuilder = group("homeAnonymous")
  .on(exec(http("Get").get("/")))

  val authenticate: ChainBuilder = group("homeAnonymous")
  .on(exec(http("Get").get("/")))

  val homeAuthenticated: ChainBuilder = group("homeAnonymous")
  .on(exec(http("Get").get("/")))

  val addToCart: ChainBuilder = group("homeAnonymous")
  .on(exec(http("Get").get("/")))

  val buy: ChainBuilder = group("homeAnonymous")
  .on(exec(http("Get").get("/")))

  val testType: String = "smoke"

//#with-authentication-headers-wrapper
// Add authentication header if an access token exists in the session
fun withAuthenticationHeader(protocolBuilder: HttpProtocolBuilder): HttpProtocolBuilder {
  return protocolBuilder.header("Authorization") { session ->
      Optional.ofNullable(session.getString("AccessToken")).orElse("")
  }
}
//#with-authentication-headers-wrapper

//#http-protocol-builder-simple
val httpProtocol = http.baseUrl("https://api-ecomm.gatling.io")
  .acceptHeader("application/json")
  .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
  )
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
val scn1: ScenarioBuilder = scenario("Scenario 1")
    .exitBlockOnFail()
    .on(
        randomSwitch()
            .on(
                percent(70.0)
                    .then(
                        group("fr")
                            .on(
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
                percent(30.0)
                    .then(
                        group("us")
                            .on(
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
    .exitHereIfFailed()
//#scenario-1

//#scenario-2
// Define scenario 2 with a uniform traffic distribution
// Reference: https://docs.gatling.io/reference/script/core/scenario/#uniformrandomswitch
val scn2: ScenarioBuilder = scenario("Scenario 2")
    .exitBlockOnFail()
    .on(
        uniformRandomSwitch()
            .on(
                group("fr")
                    .on(
                        homeAnonymous,
                        pause(5, 15),
                        authenticate,
                        homeAuthenticated,
                        pause(5, 15),
                        addToCart,
                        pause(5, 15),
                        buy
                    ),
                group("us")
                    .on(
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
    .exitHereIfFailed()
//#scenario-2

//#injection-profile-switch
// Define different load injection profiles
// Reference: https://docs.gatling.io/reference/script/core/injection/
private fun injectionProfile(scn: ScenarioBuilder): PopulationBuilder {
  return when (testType) {
      "capacity" -> scn.injectOpen(
          incrementUsersPerSec(1.0)
              .times(4)
              .eachLevelLasting(10)
              .separatedByRampsLasting(4)
              .startingFrom(10.0)
      )
      "soak" -> scn.injectOpen(constantUsersPerSec(1.0).during(100))
      "stress" -> scn.injectOpen(stressPeakUsers(200).during(20))
      "breakpoint" -> scn.injectOpen(rampUsers(300).during(120))
      "ramp-hold" -> scn.injectOpen(
          rampUsersPerSec(0.0).to(20.0).during(30),
          constantUsersPerSec(20.0).during(60)
      )
      "smoke" -> scn.injectOpen(atOnceUsers(1))
      else -> scn.injectOpen(atOnceUsers(1))
  }
}
//#injection-profile-switch

//#assertions
// Define assertions for different test types
// Reference: https://docs.gatling.io/reference/script/core/assertions/
private val assertions: List<Assertion> = listOf(
    global().responseTime().percentile(90.0).lt(500),
    global().failedRequests().percent().lt(5.0)
)

private fun getAssertions(): List<Assertion> {
    return when (testType) {
        "capacity", "soak", "stress", "breakpoint", "ramp-hold" -> assertions
        "smoke" -> listOf(global().failedRequests().count().lt(1L))
        else -> assertions
    }
}
//#assertions

//#setup-block
// Set up the simulation with scenarios, load profiles, and assertions
init {
  setUp(
      injectionProfile(scn1), injectionProfile(scn2)
  )
      .assertions(getAssertions())
      .protocols(httpProtocolWithAuthentication)
}
//#setup-block

}

//#config
object Config {
  val testType: String = System.getProperty("testType", "smoke")
  val targetEnv: String = System.getProperty("targetEnv", "DEV")
}
//#config

companion object {
//#keys
const val ACCESS_TOKEN = "AccessToken"
//#keys
}

object KeysUsageWrapper {
//#keys-usage
val login: HttpRequestActionBuilder = http("Login")
    .post("/login")
    .asFormUrlEncoded()
    .formParam("username", "#{username}")
    .formParam("password", "#{password}")
    .check(status().`is`(200))
    .check(jmesPath("accessToken").saveAs(ACCESS_TOKEN))
//#keys-usage
}

//#target-env-resolver
// Data class to store environment-specific information
data class EnvInfo(
    val pageUrl: String,
    val baseUrl: String,
    val usersFeederFile: String,
    val productsFeederFile: String
)

// Object to resolve environment-specific configuration based on the target environment
object TargetEnvResolver {
    // Resolve environment-specific configuration based on the target environment
    fun resolveEnvironmentInfo(targetEnv: String): EnvInfo {
        return when (targetEnv) {
            "DEV" -> EnvInfo(
                pageUrl = "https://ecomm.gatling.io",
                baseUrl = "https://api-ecomm.gatling.io",
                usersFeederFile = "data/users_dev.json",
                productsFeederFile = "data/products_dev.csv"
            )
            else -> EnvInfo(
                pageUrl = "https://ecomm.gatling.io",
                baseUrl = "https://api-ecomm.gatling.io",
                usersFeederFile = "data/users_dev.json",
                productsFeederFile = "data/products_dev.csv"
            )
        }
    }
}
//#target-env-resolver

}