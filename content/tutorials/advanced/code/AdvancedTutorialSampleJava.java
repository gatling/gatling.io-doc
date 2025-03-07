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

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.util.List;

public class AdvancedTutorialSampleJava {
  public static final String pageUrl = "https://ecomm.gatling.io";
  public static final String ACCESS_TOKEN = "AccessToken";
  public static final String testType = "AccessToken";
  public static final int vu = 10;
  public static final int duration = 10;
  public static final int ramp_duration = 10;

  public static final ChainBuilder homeAnonymous =
      group("homeAnonymous")
          .on(exec(http("").get("/")));
  public static final ChainBuilder authenticate =
  group("homeAnonymous")
      .on(exec(http("").get("/")));
  public static final ChainBuilder homeAuthenticated =
  group("homeAnonymous")
      .on(exec(http("").get("/")));
  public static final ChainBuilder addToCart =
  group("homeAnonymous")
      .on(exec(http("").get("/")));
  public static final ChainBuilder buy =
  group("homeAnonymous")
      .on(exec(http("").get("/")));
  //#login-endpoint
  public class APIendpoints {
  public static final HttpRequestActionBuilder login =
      http("Login")
          .post("/login")
          .asFormUrlEncoded() // Short for header("Content-Type",
          // "application/x-www-form-urlencoded")
          .formParam("username", "#{username}")
          .formParam("password", "#{password}")
          .check(status().is(200))
          .check(jmesPath("accessToken").saveAs("AccessToken"));

  }
  //#login-endpoint

  //#with-authentication-headers-wrapper
  public static final HttpProtocolBuilder withAuthenticationHeader(
      HttpProtocolBuilder protocolBuilder) {
    return protocolBuilder.header(
        "Authorization",
        session -> session.contains("AccessToken") ? session.getString("AccessToken") : "");
  }
  //#with-authentication-headers-wrapper

  //#homepage-endpoint
  public class WebEndpoints {
    public static final String pageUrl = "https://ecomm.gatling.io";
    // Define the home page request with response status validation
    // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
    public static final HttpRequestActionBuilder homePage =
        http("HomePage")
            .get(pageUrl)
            .check(status().in(200, 304)); // Accept both OK (200) and Not Modified (304) statuses
  }
  //#homepage-endpoint

  public static class ScenarioGroupsWrapper {

    private static final int minPauseSec =
      Integer.getInteger("minPauseSec", 5); // Minimum pause between actions
    private static final int maxPauseSec =
      Integer.getInteger("maxPauseSec", 15); // Maximum pause between actions
    
    public static final HttpRequestActionBuilder login =
      http("Login")
          .post("/login");

    public static final HttpRequestActionBuilder loginPage =
      http("LoginPage").get(pageUrl + "/login").check(status().in(200, 304));

    //#authenticate-group
    public class ScenarioGroups{
      private static final FeederBuilder<Object> usersFeeder =
        jsonFile("data/users_dev.json").circular();
        // Define authentication process
      public static final ChainBuilder authenticate =
        group("authenticate")
          .on(loginPage, feed(usersFeeder), pause(5, 15), login);
      }
      //#authenticate-group
  
    }

    public class AdvancedSimulation extends Simulation {
      //#http-protocol-builder-simple
      static final HttpProtocolBuilder httpProtocol =
          http.baseUrl("https://api-ecomm.gatling.io")
              .acceptHeader("application/json")
              .userAgentHeader(
                  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0");
      //#http-protocol-builder-simple


      //#http-protocol-builder-with-headers
      static final HttpProtocolBuilder httpProtocolWithAuthentication =
      withAuthenticationHeader(
          http.baseUrl("https://api-ecomm.gatling.io")
              .acceptHeader("application/json")
              .userAgentHeader(
                  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"));
      //#http-protocol-builder-with-headers

  //#scenario-1
  // Define scenario 1 with a random traffic distribution
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#randomswitch
  static final ScenarioBuilder scn1 =
    scenario("Scenario 1")
        .exitBlockOnFail()
        .on(
            randomSwitch()
                .on(
                    percent(70)
                        .then(
                            group("fr")
                                .on(
                                    homeAnonymous,
                                    pause(1, 15),
                                    authenticate,
                                    homeAuthenticated,
                                    pause(1, 15),
                                    addToCart,
                                    pause(1, 15),
                                    buy)),
                    percent(30)
                        .then(
                            group("us")
                                .on(
                                    homeAnonymous,
                                    pause(1, 15),
                                    authenticate,
                                    homeAuthenticated,
                                    pause(1, 15),
                                    addToCart,
                                    pause(1, 15),
                                    buy))))
        .exitHereIfFailed();
    //#scenario-1
    
    //#scenario-2
    // Define scenario 2 with a uniform traffic distribution
    // Reference: https://docs.gatling.io/reference/script/core/scenario/#uniformrandomswitch
    static final ScenarioBuilder scn2 =
    scenario("Scenario 2")
        .exitBlockOnFail()
        .on(
            uniformRandomSwitch()
                .on(
                    group("fr")
                        .on(
                            homeAnonymous,
                            pause(1, 15),
                            authenticate,
                            homeAuthenticated,
                            pause(1, 15),
                            addToCart,
                            pause(1, 15),
                            buy),
                    group("us")
                        .on(
                            homeAnonymous,
                            pause(1, 15),
                            authenticate,
                            homeAuthenticated,
                            pause(1, 15),
                            addToCart,
                            pause(1, 15),
                            buy)))
        .exitHereIfFailed();
    //#scenario-2

    //#injection-profile-switch
    // Define different load injection profiles
    // Reference: https://docs.gatling.io/reference/script/core/injection/
    static final PopulationBuilder injectionProfile(ScenarioBuilder scn) {
      return switch (testType) {
        case "capacity" ->
            scn.injectOpen(
                incrementUsersPerSec(1)
                    .times(4)
                    .eachLevelLasting(10)
                    .separatedByRampsLasting(4)
                    .startingFrom(10));
        case "soak" -> scn.injectOpen(constantUsersPerSec(1).during(180));
        case "stress" -> scn.injectOpen(stressPeakUsers(200).during(20));
        case "breakpoint" -> scn.injectOpen(rampUsers(300).during(120));
        case "ramp-hold" ->
            scn.injectOpen(
                rampUsersPerSec(0).to(20).during(30),
                constantUsersPerSec(20).during(60));
        case "smoke" -> scn.injectOpen(atOnceUsers(1));
        default -> scn.injectOpen(atOnceUsers(1));
      };
    }
    //#injection-profile-switch

    //#assertions
    // Define assertions for different test types
    // Reference: https://docs.gatling.io/reference/script/core/assertions/
    static final List<Assertion> assertions =
      List.of(
          global().responseTime().percentile(90.0).lt(500),
          global().failedRequests().percent().lt(5.0));

  static final List<Assertion> getAssertions() {
    return switch (testType) {
      case "capacity", "soak", "stress", "breakpoint", "ramp-hold" -> assertions;
      case "smoke" -> List.of(global().failedRequests().count().lt(1L));
      default -> assertions;
    };
  }
    //#assertions

    //#setup-block
    // Set up the simulation with scenarios, load profiles, and assertions
    {
      setUp(injectionProfile(scn1), injectionProfile(scn2))
          .assertions(getAssertions())
          .protocols(httpProtocolWithAuthentication);
    }
    //#setup-block



    }

  //#config
  public class Config {
    public static final String testType = System.getProperty("testType", "smoke"); // Test type (default: smoke)
    public static final String targetEnv = System.getProperty("targetEnv", "DEV");
  }
  //#config

  //#keys
  public class Keys {
    public static final String ACCESS_TOKEN = "AccessToken";
  }
  //#keys

  
  public static class KeysUsage {
    public static final String ACCESS_TOKEN = "AccessToken";
    //#keys-usage
    public static final HttpRequestActionBuilder login =
    http("Login")
        .post("/login")
        .asFormUrlEncoded() // Short for header("Content-Type",
        // "application/x-www-form-urlencoded")
        .formParam("username", "#{username}")
        .formParam("password", "#{password}")
        .check(status().is(200))
        .check(jmesPath("accessToken").saveAs(ACCESS_TOKEN));
    //#keys-usage
  }

  //#target-env-resolver
  public class TargetEnvResolver {

    // Record to store environment-specific information
    public record EnvInfo(
        String pageUrl, String baseUrl, String usersFeederFile, String productsFeederFile) {}
  
    // Resolve environment-specific configuration based on the target environment
    public static EnvInfo resolveEnvironmentInfo(String targetEnv) {
      return switch (targetEnv) {
        case "DEV" ->
            new EnvInfo(
                "https://ecomm.gatling.io",
                "https://api-ecomm.gatling.io",
                "data/users_dev.json",
                "data/products_dev.csv");
        default ->
            new EnvInfo(
                "https://ecomm.gatling.io",
                "https://api-ecomm.gatling.io",
                "data/users_dev.json",
                "data/products_dev.csv");
      };
    }
  }
  //#target-env-resolver

}
