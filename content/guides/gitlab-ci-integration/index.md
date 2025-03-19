---
title: Integrate Gatling with Gitlab CI/CD
menutitle: Gitlab CI/CD integration
seotitle: How to integrate Gatling with Gitlab CI/CD
description: Set up automated load testing with Gitlab CI/CD
lead: Add performance testing to your Gitlab CI/CD pipeline with Gatling Enterprise.
badge:
  type: enterprise
  label: Enterprise
date: 2025-03-11T13:35:00+02:00

---

Performance testing is a critical component of modern software development. By integrating Gatling Enterprise with Gitlab CI/CD, you incorporate performance testing into your development workflow, ensuring your applications meet performance standards before reaching production.

## Prerequisites

- Gatling version `{{< var gatlingVersion >}}` or higher
- An account on Gatling Entreprise
- Clone [this code](https://github.com/gatling/devrel-projects) and cd into the `articles/gitlabintegration` folder.

## Test Creation

### Create the Scenario

Let's create a simple scenario that loads our [e-commerce demo website](https://ecomm.gatling.io/).

{{< include-code "ecommerce#ecommerce-example" ts java scala kt>}}

### Generate the user and assertions

We start with a single user to verify our simulation works correctly. Then we add assertions to validate our test results. In this example, we check that more than 90% of requests are successful.

{{< include-code "ecommerce#set-up" ts java scala kt>}}

## Gatling Enterprise

### Deploy to Gatling Enterprise

Now that we have our test ready, we need to deploy the package to Gatling Enterprise.

{{< alert info >}}
While this video demonstrates the JavaScript SDK specifically, the deployment process remains identical across all Gatling SDKs and CI/CD platforms we support.
{{< /alert >}}

{{< youtube jYPc6lwBAAQ >}}

### Create the Simulation

{{< alert info >}}
This step is applicable to all CI/CD platforms supported by Gatling Enterprise.
{{< /alert >}}

Next, we create a simulation in Gatling Enterprise using our deployed package.

{{< youtube 7Gb4BcHU0P8 >}}

### Copy Your Simulation ID

Make sure to copy the `simulation Id` by clicking the three dots menu next to your simulation name, as we use this ID in subsequent steps.

### Create an API Key

{{< alert info >}}
This step is applicable to all CI/CD platforms supported by Gatling Enterprise.
{{< /alert >}}

To enable Gitlab CI/CD to interact with Gatling Enterprise and trigger simulations, you need to generate an API key.

{{< youtube iP4_WM9wTNA >}}

With Gatling Enterprise configured, we proceed to setting up our Gitlab CI/CD pipeline.

## Gitlab CI/CD

### Set up the Secrets and Environment

To run simulations from Gitlab CI/CD, you first need to configure a secure API token. Follow these steps to set up the required variable:

1. Navigate to your GitLab repository
2. Access the `Settings` menu
3. Select `CI/CD` from the sidebar
4. Click on `Variables`
5. Press the `Add variable` button
6. Choose `Masked and hidden` for `Visibility`
7. Enter `GATLING_ENTERPRISE_API_TOKEN` as the `Key`
8. Paste your API token as the `Value`

If you need to generate a new API token, follow our guide [here]({{< ref "../reference/execute/cloud/admin/api-tokens" >}}).

### Configure the Pipeline

Let's set up a basic Gitlab CI/CD pipeline that runs your performance tests. The pipeline configuration triggers a simulation using the provided **`simulation Id`**.

Here's the pipeline configuration (.gitlab-ci.yml):

```yaml
stages:
  - load-test

run-gatling-enterprise:
  stage: load-test
  image:
    name: gatlingcorp/enterprise-runner:1
    entrypoint: ['']
  script:
    - gatlingEnterpriseStart
  variables:
    SIMULATION_ID: '00000000-0000-0000-0000-000000000000'
```

For advanced pipeline customization options, check our [Gitlab CI/CD documentation]({{< ref "../reference/integrations/ci-cd/gitlab-ci" >}}).

### Run the Pipeline

With everything configured, you can now trigger the pipeline from GitLab CI/CD. Provide your **`simulation Id`** when running the pipeline, and you are able to monitor the test execution both in GitLab and Gatling Enterprise.

{{< youtube N-QSlrufXps >}}

## Conclusion

This integration streamlines the testing process and enhances collaboration between development teams. With Gatling Enterprise and Gitlab CI/CD, you create a pipeline that continuously monitors your application's performance, making it easier to maintain high standards of application quality throughout the development lifecycle.
