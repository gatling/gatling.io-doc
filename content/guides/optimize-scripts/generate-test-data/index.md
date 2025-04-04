---
menutitle: Generate synthetic test data
title: Generate synthetic data for your load tests
seotitle: How to generate synthetic data for your Gatling load tests
description: Learn how to generate synthetic data for your Gatling load tests
lead: Learn how to generate synthetic data for your Gatling load tests
date: 2025-02-28T8:30:56+02:00
---

## Introduction

Realistic data is crucial in simulating real-world scenarios when building performance tests. Generating dynamic data can help make your Gatling tests more effective and closer to actual production conditions.

This article will explore how to generate fake data using the DataFaker library and integrate it into a Gatling simulation.

## DataFaker

### Why do we need to generate data?

Hardcoded or repetitive data in performance testing can lead to skewed results, as the system might not be fully tested for various inputs. By generating data dynamically, we can:

- Mimic real-world user interactions.
- Test for edge cases and diverse scenarios.
- Avoid caching issues that may arise with static data.

### What is DataFaker?

[DataFaker](https://www.datafaker.net/) is a library designed to generate realistic fake data for a variety of use cases. From names and addresses to emails and random numbers, DataFaker provides everything needed to populate your Gatling tests with dynamic data.

## Creating the simulation

### Setup

First, ensure you have Gatling installed and set up. If you haven't already, download it from the official documentation.

Now [clone this project](https://github.com/gatling/devrel-projects). Inside the `articles/datafaker` folder, you will find an API folder (our application) and a Gatling folder (our simulation).

### Explanation of project

The application we will test is a small application with one route ("hello") that takes the first and last name as arguments and returns "hello firstname lastname". This is a simple but good example to help to understand how to generate and send data.

We use the Gatling simulation to load test this application and see if it crashes under load. 🚀

### Creation of the feeder

To pass dynamic data in Gatling, we need to create a feeder. It can be a CSV, JSON, or other file format, or we can create a custom one. I will show you how to create a custom feeder with DataFaker.

1. Create the Faker Object: Start by creating an instance of the `Faker` class. This will allow us to generate fake data.

  ```java
  Faker Users = new Faker();
  ```

2. Get the Data: Use the Faker object to generate the required data. In this case, we use `Users.name().firstName()` and `Users.name().lastName()` to create fake first and last names.

```java
String firstName = Users.name().firstName();
String lastName = Users.name().lastName();
```
3. Build the Iterator: Combine the generated data into a Map and use a Stream to create an iterator that Gatling can use as a feeder.

  ```java
    Iterator<Map<String, Object>> feeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                String firstName = Users.name().firstName();
                String lastName = Users.name().lastName();
                return Map.of(
                        "firstname", firstName,
                        "lastname", lastName
                );
            }).iterator();
  ```

## Creation of the simulation

Now that we have our Iterator, we can use it as a feeder for our Gatling simulation. We create a scenario to test the hello endpoint and send the data from our custom feeder, to finish we check the status code and the return message of the API.

```java
ScenarioBuilder hello_user = scenario("Hello").exec(
        feed(feeder),
        http("Hello")
            .post("/hello")
                .body(StringBody("{\"firstname\": \"#{firstname}\", \"lastname\": \"#{lastname}\"}"))
                .asJson()
                .check(
                        status().is(201),
                        jsonPath("$.message").is(session -> "Hello "
                                + session.getString("firstname")
                                + " "
                                + session.getString("lastname"))
                )


    );
```
Now, we add the HttpProtocol, which specifies where Gatling will send the users; in our case, the URL of our application.

```java
HttpProtocolBuilder httpProtocol =
        http.baseUrl("http://127.0.0.1:5000")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
```

To finish, we create the injection profile, in our case, we will send 10 users at once:

```java
{
        setUp(
                hello_user.injectOpen(atOnceUsers(10))
        ).protocols(httpProtocol);
    }
```

## Launching the simulation

### Setup

To launch the API, open a terminal and run the followin commands from the API folder:

```console
pip3 install -r requirements.txt
python3 api.py
```

### Open-source version

To launch the load test, open a terminal and run the following command in the Gatling folder:

{{< platform-toggle >}}
Linux/MacOS: ./mvnw gatling:test
Windows: mvnw.cmd gatling:test
{{</ platform-toggle >}}

Once the simulation is complete, Gatling generates an HTML link in the terminal to access your report. Review metrics such as response times, successful and failed connections, and other indicators to identify potential issues with your service.

## Conclusion

Generating dynamic data with DataFaker allows you to build robust and realistic performance tests using Gatling. This article taught you how to create dynamic data that simulates real-world usage. 

This approach ensures dynamic data generation, reducing redundancy and providing a more accurate performance test environment. If you want to go deeper, check out Gatling Entreprise! You will get access to CI/CD integration, detailed reporting, private locations, and many more features to ensure your application performs reliably under load.
