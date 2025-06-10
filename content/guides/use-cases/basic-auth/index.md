---
title: Create a load test with basic authentication
menutitle: Basic authentication
seotitle: How to test applications with basic authentication with Gatling
description: Create a Gatling simulation that uses basic authentication.
lead: Add basic authentication using a csv feeder to your Gatling simulation.
aliases:
    - /guides/basic-auth
    - /guides/complex-use-cases/basic-auth/
---

## Prerequisites
- Gatling version `{{< var gatlingVersion >}}` or higher
- Basic understanding of your preferred programming language
- Basic understanding of HTTP authentication

## Understanding basic authentication
Basic authentication requires sending credentials (username and password) in the HTTP header. The credentials are encoded in base64 format in the following format:
 `Basic base64(username:password)`.

## Step 1: Create the credentials feeder

Feeders are data collections used in your load test. Learn about feeder types and usage in the [feeder reference documentation]({{< ref "/concepts/session/feeders" >}}). For this guide, use a `.csv` feeder file. 

1. Create a file named `credentials.csv` in the `resources` folder of your Gatling project.
2. Add the following data to the newly created `credentials.csv` file. 

```shell
# resources/credentials.csv
username,password
user1,pass123
user2,pass456
user3,pass789
```

## Step 2: Create the simulation

Create a simulation in the `src` folder of your Gatling project with the following code:

{{< include-code "basic-auth-example" java ts kt scala >}}

The code example includes annotations to help you understand how each component contributes to the overall simulation. 

## Use the code with your application

You must update the following URLs to target your application. The provided examples are not functioning basic authentication endpoints. Update the:

- `baseUrl`
- `GET` endpoint
- `POST` endpoint

## Best practices

1. Never commit real credentials to version control
2. Use different credentials for different virtual users to avoid rate limiting
3. Include proper error handling and assertions
4. Use meaningful pause times between requests
5. Monitor authentication failures during the test
6. Consider your target system's capacity when defining the ramp-up pattern

This example provides a foundation that you can customize based on your specific testing needs.
