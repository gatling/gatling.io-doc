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

## Load-tested application

In this guide, we will be implementing a script to load test an e-commerce platform: [https://ecomm.gatling.io](https://ecomm.gatling.io). We encourage you to experiment with the platform to get familiar with its available actions.

## Identify the relevant scenario(s) to your use case

The first step that we need to do before starting to write our script is identifying the relevant user journeys. Always remember that
the end goal is to simulate **real-world traffic** on your application, so **taking the time to determine the typical user journeys on your application is crucial.**

This can be done in several ways:

- Checking your Product Analytics tool (Amplitude, Firebase)
- Checking your APM tool (Dynatrace, Datadog)
- Asking the product-owner

For our e-commerce platform, we identified the following exact user journey:

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

The structure and components will become clearer as we progress through the tutorial.

### First, we define the endpoints

We need to define the individual requests that we need to call throughout the user journeys.

#### I. API Endpoints

We first define the API endpoints, i.e. the backend API calls and place them in a file under the `/endpoints` directory.\
Now let's take a closer look at the following definition of the `login` endpoint in the API endpoints file:

{{< include-code "login-endpoint" >}}

1. We use an http request action builder class to build a POST http request.
2. We use `.asFormUrlEncoded()`to set the content-type header.
3. We use `.formParam("username", "#{username}")` to set the form parameters of the POST request. More on `formParam` [here](https://docs.gatling.io/reference/script/protocols/http/request/#formparam).
   - We use the [Gatling Expression Language](https://docs.gatling.io/reference/script/core/session/el/) to retrieve the username's value from the virtual user's session. We will set this value later on in this guide using a [Feeder](https://docs.gatling.io/reference/script/core/session/feeders/).
4. We use `.check()` for the following:
   - Check that we receive a 200 status code in the response.
   - Retrieve the `accessToken` from the response body and **save it** to the user session under the name `AccessToken`.
   - More on Checks and `jmesPath` [here](https://docs.gatling.io/reference/script/core/checks/)

Let's add one more thing to our API endpoints file.

{{< include-code "with-authentication-headers-wrapper" >}}

The method above takes an `HttpProtocolBuilder` object and conditionally adds the `Authorization` header to requests:

- If the virtual user's session contains the `ACCESS_TOKEN` key, set `Authorization` header to corresponding value.
- Else, set `Authorization` to an empty string.

This method will be used later on in our Simulation class. It will eliminate the need to set the `Authorization` header individually for each request.

#### II. Web Endpoints

If the user journeys involve frontend calls that retrieve data (html, resources..etc) from the load-tested application server, then we need to define endpoints for these calls as well. Therefore we create another "web endpoints" file under the `/endpoints` directory.

Now let's take a look at the following definition of the `homePage` endpoint:

{{< include-code "homepage-endpoint" >}}

1. We define an http GET request for the `pageUrl`
2. We define a check to ensure we receive a response with status code corresponding to either 200 or 304.

### Next, we define the groups

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

### Next, let's create our Simulation class

We first define our http protocol builder

{{< include-code "http-protocol-builder" >}}

- We use the `withAuthenticationHeader` wrapper method (created [earlier](http://localhost:1313/tutorials/advanced/#i-api-endpoints)) to conditionally add `Authorization` header to the requests.
- We set `accept` and `user-agent` headers.

Now let's define our scenarios! We will define two scenarios that showcase different user behaviors.

- In our first scenario, we account for regional differences in user behavior commonly observed in e-commerce. To reflect this, we define two distinct user journeys based on the market: one for the French market and another for the US market:

  {{< include-code "scenario-1" >}}

  Let's take a closer look:

  - We wrap our scenario in an `exitBlockOnFail()` block to ensure that the virtual user exits the scenario whenever a request or check fails. This mimics real-world behavior, as users would be unable to proceed if they encounter blockers in the flow. Read more [here](https://docs.gatling.io/reference/script/core/scenario/#exitblockonfail).

  - We use `randomSwitch()` to distribute traffic between two flows based on predefined percentages: 70% for the French **(fr)** market and 30% for the US **(us)** market. - The `randomSwitch()` will assign virtual users to the two flows according to the defined probabilities in `percent()`. - Within each `percent()` block, we define the desired behavior. - More on `randomSwitch()` [here](https://docs.gatling.io/reference/script/core/scenario/#randomswitch).

- In a similar manner, we define our second scenario:

  {{< include-code "scenario-1" >}}

### Define injection profiles

We've defined our scenarios, i.e. the flows that the virtual user will go through. Now, we need to define how will these virtual users arrive into the load-tested application, i.e. **the injection profile**. Defining the injection profile is based on the type of load that you are looking to simulate on your application.
In our script, we define the following injection profiles according to the desired load test type:

- Capacity
- Soak
- Stress
- Breakpoint
- Ramp-hold
- Smoke

{{< include-code "injection-profile-switch" >}}

### Define assertions

Now, we need to define assertions—the benchmarks that determine whether the test is considered successful or failed.

{{< include-code "assertions" >}}

### Add the setUp block

Finally, we define the setup block. This configuration will execute both scenarios **simultaneously**. Based on the test type specified in the system properties, it will apply the corresponding injection profile and assertions.

{{< include-code "setup-block" >}}
