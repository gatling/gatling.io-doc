---
menutitle: Writing realistic tests
title: Writing realistic Gatling tests
description: How to transform a raw recorded test into a dynamic and meaningful load test. Introduce feeders, dynamic parameters, loops, functions and multiple scenarios.
lead: Isolate process, configure virtual users, use feeders, checks and looping
date: 2021-04-20T18:30:56+02:00
---

In this tutorial, we assume that you have already gone through the
introductory guides and that you have a basic understanding of how a simulation works.\
We will build a realistic load test for a relevant real-world scenario and introduce more advanced concepts and
[Domain Specific Language](https://en.wikipedia.org/wiki/Domain-specific_language) constructs.

{{< alert tip >}}
The files for this tutorial can be found
[on GitHub](https://github.com/karimatwa/ecommerce-demo-gatling-tests/tree/main).
{{< /alert >}}

{{< alert tip >}}
It is strongly recommended to review the introductory guides first, as this tutorial introduces more advanced concepts:

- [Create a simulation with Java](https://docs.gatling.io/tutorials/scripting-intro/)
- [Create a simulation with JavaScript](https://docs.gatling.io/tutorials/scripting-intro-js/)
- [Introduction to the Recorder](https://docs.gatling.io/tutorials/recorder/)

Additionally, it is important to have a basic understanding of a virtual user's session. Kindly consult the [Session documentation](https://docs.gatling.io/reference/script/core/session/), particularly the **Feeders** and **Expression Language** sections.
{{< /alert >}}

## Test application

In this guide, we will be implementing a script to load test the following application: [https://ecomm.gatling.io](https://ecomm.gatling.io). This is a sample e-commerce website where you can browse products, add to cart, checkout..etc. We encourage you to experiment with the platform to get familiar with its available actions. You may also open the network tab for further insights.

## Identify relevant scenario(s)

The first step that we need to do before starting to write our script is identifying the relevant user journeys. Always remember that
the end goal is to simulate **real-world traffic**, so **taking the time to determine the typical user journeys on your application is crucial.**

This can be done in several ways:

- Checking your Product Analytics tool (Amplitude, Firebase)
- Checking your APM tool (Dynatrace, Datadog)
- Asking the product-owner

For our e-commerce platform, we identified the following user journey:

1. User lands on the homepage
2. User logs in
3. User lands again on the homepage (as an authenticated user)
4. User adds a product to cart
5. User buys (checkout)

## Writing the script

### Project structure

```console
.
├── .gatling/
└── src/
    ├── java/
        ├── example/
            ├── endpoints/
                ├── APIendpoints.java
                └── WebEndpoints.java
            ├── groups/
                ├── ScenarioGroups.java
            ├── utils/
                ├── Config.java
                └── Keys.java
                └── TargetEnvResolver.java
            ├── resources/
                ├── bodies/
                ├── data/
            ├── AdvancedSimulation.java
```

Let's break it down:

- **`endpoints/`**: Contains files responsible for defining individual requests, which we reference throughout our scenarios.
  - **API endpoints file**: Defines and manages API requests (backend calls) to our application.
  - **Web endpoints file**: Defines and manages all frontend calls to our application.
- **`groups/`**: Contains files responsible for defining **groups**, which are collections of endpoints.
  - **Scenario groups file**: Defines and manages groups.
- **`utils/`**: Contains utility files designed to simplify and streamline processes.
  - **Config file**: Handles the retrieval of all necessary system properties and environment variables.
  - **Keys file**: Contains predefined constants that serve as a single source of truth for virtual user session variable names. More on sessions [here](https://docs.gatling.io/reference/script/core/session/).
  - **Target env resolver**: Responsible for resolving the targetEnv system property to the appropriate configuration.
- **`resources/`**: Contains feeder files and request bodies that we reference throughout our script.
  - **`bodies/`**: Contains the request bodies. For more information on referencing request bodies, see [here](https://docs.gatling.io/reference/script/protocols/http/request/#request-body).
  - **`data/`**: Contains the feeder files.
- **Advanced simulation file**: The main Gatling simulation file where we define the scenarios, http protocol, injection profile and assertions.

### Endpoints

We need to define the individual requests that we need to call throughout the user journeys.

#### I. API Endpoints

We first define the API endpoints, i.e. the backend API calls and place them in a file under the `/endpoints` directory.\
Now let's take a closer look at the following definition of the `login` endpoint in the API endpoints file:

<a id="login-endpoint-snippet"></a>
{{< include-code "login-endpoint" >}}

1. We use an http request action builder class to build a POST http request.
2. We use `.asFormUrlEncoded()`to set the content-type header to `application/x-www-form-urlencoded`.
3. We use `.formParam("username", "#{username}")` to set the form parameters of the POST request. More on `formParam` [here](https://docs.gatling.io/reference/script/protocols/http/request/#formparam).
   - We use the [Gatling Expression Language](https://docs.gatling.io/reference/script/core/session/el/) to retrieve the username's value from the virtual user's session. We will set this value later on in this guide using a [Feeder](https://docs.gatling.io/reference/script/core/session/feeders/).
4. We use `.check()` for the following:
   - **Validate** that we receive a 200 status code in the response. More on validating [here](https://docs.gatling.io/reference/script/core/checks/#validating).
   - **Extract** the `accessToken` from the response body and **save** it to the user session under the name `AccessToken`. Further information on extracting can be found [here](https://docs.gatling.io/reference/script/core/checks/#extracting), and on saving [here](https://docs.gatling.io/reference/script/core/checks/#saving).
   - More on `jmesPath` [here](https://docs.gatling.io/reference/script/core/checks/#jmespath).

#### II. Web Endpoints

If the user journeys involve frontend calls that retrieve data (html, resources..etc) from the load-tested application server, then we need to define endpoints for these calls as well. Therefore we create another "web endpoints" file under the `/endpoints` directory.

Now let's take a look at the following definition of the `homePage` endpoint:

{{< include-code "homepage-endpoint" >}}

1. We define an http GET request for the `pageUrl`
2. We define a check to ensure we receive a response with status code corresponding to either 200 or 304.

### Groups

Groups serve as a collection of multiple http requests, providing a clear logical separation for different parts of the user journey. Defining groups enables us to filter by them in the Gatling Enterprise reports, providing a more precise analysis of various processes within the load-tested application.

We will define the groups in a file under the `groups/` directory.

Let's take a look at the following `authenticate` group definition:

{{< include-code "authenticate-group" >}}

1. We define a group under the name `authenticate`.
2. This group will encompass the following two requests in the user journey: a `GET` request to retrieve the login page html and a `POST` request to the login endpoint.
3. We use a feeder that injects dynamic data into our simulation. Here is how it works:

   - We first create a json file `users_dev.json` in the directory `/resources/data`.

     ```json
     [
       {
         "username": "admin",
         "password": "gatling"
       },
       {
         "username": "admin1",
         "password": "gatling1"
       }
     ]
     ```

   - We define `usersFeeder` that loads the json file using `jsonFile()` with the `circular()` strategy. More on feeder strategies [here](https://docs.gatling.io/reference/script/core/session/feeders/#strategies).
   - We call the `feed(usersFeeder)` in the `authenticate` ChainBuilder to pass dynamic `username` and `password` values to the `login` endpoint that we defined [earlier](http://localhost:1313/tutorials/advanced/#i-api-endpoints).

4. We also include a `pause(5, 15)` before the login step. This instructs the virtual user to pause for a random duration between 5 and 15 seconds. The randomness helps simulate human-like variations in navigation, such as filling out forms. Pauses are a crucial component of replicating real-world behavior, and it's important to ensure they are placed appropriately throughout the scenario.

### Scenarios

Now let's define our scenarios! We will define two scenarios that showcase different user behaviors.

- In our first scenario, we account for regional differences in user behavior commonly observed in e-commerce. To reflect this, we define two distinct user journeys based on the market: one for the French market and another for the US market:

  {{< include-code "scenario-1" >}}

  Let's take a closer look:

  - We wrap our scenario in an `exitBlockOnFail()` block to ensure that the virtual user exits the scenario whenever a request or check fails. This mimics real-world behavior, as users would be unable to proceed if they encounter blockers in the flow. Read more [here](https://docs.gatling.io/reference/script/core/scenario/#exitblockonfail).

  - We use `randomSwitch()` to distribute traffic between two flows based on predefined percentages: 70% for the French **(fr)** market and 30% for the US **(us)** market. - The `randomSwitch()` will assign virtual users to the two flows according to the defined probabilities in `percent()`. - Within each `percent()` block, we define the desired behavior. - More on `randomSwitch()` [here](https://docs.gatling.io/reference/script/core/scenario/#randomswitch).

- In a similar manner, we define our second scenario:

  {{< include-code "scenario-2" >}}

### HTTP protocol

Now, let's define our http protocol builder.

{{< include-code "http-protocol-builder-simple" >}}

- We set the base url, `accept` and `user-agent` headers.

For the `Authorization` header, we will have to set it per each API request that requires authentication, a bit of a headache no? To address this, we can use the following wrapper method:

{{< include-code "with-authentication-headers-wrapper" >}}

The method above takes an `HttpProtocolBuilder` object and conditionally adds the `Authorization` header to the requests:

- If the virtual user's session contains the `AccessToken` key, set `Authorization` header to corresponding value.
- Else, set `Authorization` to an empty string.

This will eliminate the need to set the `Authorization` header individually for each request. Now we can define our http protocol builder like the following:

{{< include-code "http-protocol-builder-with-headers" >}}

### Injection profiles

We've defined our scenarios, i.e. the flows that the virtual user will go through. Now, we need to define how will these virtual users arrive into the load-tested application—**the injection profile**. \
You should take the time to properly determine how the real-world users arrive to your application and accordingly, decide on the type of load that you will simulate on your application. For instance, are you looking to simulate load for a **Black Friday**? Are you looking to determine the maximum number of users your application can sustain? Deciding on what you are trying to simulate is necessary before defining the injection profile and starting your tests.

In our script, we define the following injection profiles according to the desired load test type:

- **Capacity**: Generally used to determine the maximum number of virtual users your application can sustain. Users arrival rate gets incremented over multiple levels and we analyze the metrics (response time, error ratio..etc) at each level according to our benchmarks.
- **Soak**: Generally used to monitor the application performance with a fixed load over a long period of time. Useful for checking memory leaks and database degradation over time.
- **Stress**: Generally used to push the application over its limits. We monitor how the system behaves under extreme load, does it recover properly from failures, does it autoscale as required?
- **Breakpoint**: Users arrival rate increases linearly. Useful in checking the "hard limit" at which the system starts to break. The key difference between a capacity test and a breakpoint test is that the latter typically reveals a "hard limit", whereas a capacity test provides an estimate of the maximum number of users at which the system can maintain stable performance."
- **Ramp-hold**: Useful for simulating constant peak traffic. Users arrival rate increases up to a certain rate the stays at this rate for a period of time. Simulates real-world behavior of a Black Friday for example where number of users stays at peak for a long period of time.
- **Smoke**: Test with one virtual user. Used to ensure that the scenario works and does not break.

{{< alert tip >}}

- The injection profiles mentioned above are for **open** workload models, meaning the number of concurrent users is **NOT** capped (unlike some ticketing websites for example). For closed models or more information on open vs closed workload models, see [here](https://docs.gatling.io/reference/script/core/injection/#open-vs-closed-workload-models).
- Injection profiles can be defined according to your specific needs. The profiles provided are commonly used for the mentioned use cases, but they are not set in stone. Be sure to choose the injection profile that best fits your use case.
  {{< /alert >}}

<a id="injection-profile-snippet"></a>
{{< include-code "injection-profile-switch" >}}

For more information on defining injection profiles using the Gatling DSL, refer to this [section](https://docs.gatling.io/reference/script/core/injection/#open-model).

### Define assertions

Now, we need to define assertions—the benchmarks that determine whether the test is considered successful or failed.

{{< include-code "assertions" >}}

### Add setUp block

Finally, we define the setup block. This configuration will execute both scenarios **simultaneously**. Based on the test type specified in the system properties, it will apply the corresponding injection profile and assertions.

{{< include-code "setup-block" >}}

There also is the possibility to execute scenarios sequentially. For more information, please refer to this [section](https://docs.gatling.io/reference/script/core/injection/#sequential-scenarios).

### Utility files

One last step would be adding some utility files to have a better organisation of the codebase. In the `/utils` directory, we add the following files:

#### Config file

Responsible for defining and retrieving all the necessary system properties and environment variables. Let's take a look at the following system properties:

{{< include-code "config" >}}

- We define the `testType` system property that we use later on in the switch case of the `injectionProfile` [method](http://localhost:1313/tutorials/advanced/#injection-profile-snippet).
- We define the `targetEnv` system property to specify the target application environment for the load simulation.

We leverage system properties and environment variables to create dynamic tests that require minimal code changes.

#### Keys file

Here, we define the session variable keys. Let's take a look at the following key definition:

{{< include-code "keys" >}}

Now for the login endpoint, instead of doing `.saveAs("AccessToken")` [here](http://localhost:1313/tutorials/advanced/#login-endpoint-snippet), we can do the following:

{{< include-code "keys-usage" >}}

#### Target env resolver file

You may need different configurations depending on the target application environment. Here, we define `resolveEnvironmentInfo(String targetEnv)`, which takes the target environment as input and returns the corresponding configuration.:

- `pageUrl`: Frontend base URL.
- `baseUrl`: Backend base URL.
- `usersFeederFile`: The feeder file to use for user credentials.
- `productsFeederFile`: The feeder file to use for products.

{{< include-code "target-env-resolver" >}}
