---
title: Integrate Gatling with Github Actions
menutitle: GitHub Actions integration
seotitle: How to integrate Gatling with Github Actions
description: Learn how to integrate Gatling with GitHub Actions for continuous performance testing
lead: Set up automated performance testing in your CI/CD pipeline using Gatling and GitHub Actions
badge:
  type: enterprise
  label: Enterprise
date: 2025-03-11T13:35:00+02:00

---

At Gatling, we strongly believe the shift-left approach is fundamental to ensuring application quality. This methodology, which involves integrating testing as early as possible in the development cycle, is particularly crucial for performance testing. Detecting and resolving performance issues before production deployment helps prevent critical and costly situations.

In this guide, we demonstrate how to integrate performance testing into your CI/CD pipeline using Gatling and GitHub Actions. This approach enables you to automate your performance tests and ensure application reliability.

## Prerequisites

- Gatling version `{{< var gatlingVersion >}}` or higher
- An account on Gatling Entreprise
- Clone [this code](https://github.com/gatling/devrel-projects) and cd into the `articles/githubintegration` folder.

## Test Creation

### Create the Scenario

In this tutorial, we create a small scenario using our JS SDK. Our users will load the home page of our [e-commerce website demo](https://ecomm.gatling.io/).

{{< include-code "ecommerce#ecommerce-example" ts java scala kt>}}

### Generate the user and assertions

In our example, we generate 1 user coming at once to see if our simulation is working. After that, we define our assertions, which allows us to assert the maximum or minimum response time, the percentage of successful requests, and more. In our case, we only check if we have more than 90% successful requests.

{{< include-code "ecommerce#set-up" ts java scala kt>}}

## Gatling Enterprise

### Deploy to Gatling Enterprise

Now that we have our test locally, we need to deploy the package to Gatling Enterprise to create a simulation.

{{< alert info >}}
This video showcases the JavaScript SDK, but the same process applies to all SDKs that Gatling provides.
{{< /alert >}}

{{< youtube jYPc6lwBAAQ >}}

### Create the Simulation

Now that our package is on Gatling Enterprise, we need to create a simulation.

{{< youtube 7Gb4BcHU0P8 >}}

### Copy Your Simulation ID

Don't forget to copy your `simulation Id` (click on the three dots to the right of your simulation name); we will need it later.

### Create an API Key

Now, for GitHub to communicate with Gatling Enterprise and launch the simulation, you need to create an API key.

{{< youtube iP4_WM9wTNA >}}

Now that everything is set up on the Gatling Enterprise side, it's time to create our CI script on GitHub.

## GitHub Actions

### Set up the Secrets and Environment

To successfully launch our workflow, we begin by configuring a secret in our GitHub repository. This secret is essential for securely managing access to external services and APIs. To add the secret, follow these steps:

1. Navigate to your GitHub repository
2. Go to the "Settings" tab
3. In the left sidebar, click "Secrets and variables"
4. Select "Actions"
5. Click on the "New repository secret" button
6. Add a new secret named `GATLING_ENTERPRISE_API_TOKEN`
7. Set the value of this secret to your API token

If you don't have an API token yet, you can learn how to create one by following the instructions on this [page]({{< ref "/reference/execute/cloud/admin/api-tokens" >}})

### Understand the Workflow

Now, let's create a simple workflow. This workflow, when launched, prompts for a **`simulation Id`** and runs the specified simulation.

Below is the configuration for the workflow:

{{< include-code workflow yml>}}

If you wish to customize this workflow further, refer to our [GitHub documentation]({{< ref "/reference/integrations/ci-cd/github-actions" >}}) for more details and options.

### Launch the Workflow

Now that our requirements are set up, go to GitHub → Actions and launch our workflow with the  **`simulation Id`** as an input. After that, you will have a link to check the results on Gatling Enterprise and see logs in the workflow.

{{< youtube Ocv66gH5ZjM >}}

## Conclusion

Integrating Gatling with GitHub Actions offers a solution for automating performance testing. By adopting this shift-left approach, you can identify and address performance issues early in the development cycle, ensuring higher application quality and reliability.

This integration streamlines the testing process and enhances efficiency between development teams. With Gatling Enterprise and GitHub Actions, you can create a workflow that monitors your application's performance
