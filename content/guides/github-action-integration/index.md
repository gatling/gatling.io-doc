---
title: Integrate Gatling with Github Actions
menutitle: GitHub Actions integration
seotitle: How to test integrate Gatling with Github Actions
description: Learn how to integrate Gatling with GitHub Actions for continuous performance testing
lead: Set up automated performance testing in your CI/CD pipeline using Gatling and GitHub Actions
date: 2025-03-11T13:35:00+02:00

---

At Gatling, we strongly believe  the shift-left approach is fundamental to ensuring application quality. This methodology, which involves integrating testing as early as possible in the development cycle, is particularly crucial for performance testing. Detecting and resolving performance issues before production deployment helps prevent critical and costly situations.

In this guide, we will demonstrate how to integrate performance testing into your CI/CD pipeline using Gatling and GitHub Actions. This approach will enable you to automate your performance tests and ensure application reliability.



## Prerequisites
- Gatling version `{{< var gatlingVersion >}}` or higher
- An account on Gatling Entreprise
- Clone [this code](https://github.com/gatling/devrel-projects) and cd into the **`articles/githubintegration`** folder.

## Test Creation

### Scenario

In this tutorial, we will create a small scenario using our JS SDK. Our users will load the home page of our [e-commerce website demo](https://ecomm.gatling.io/).

{{< include-code "ecommerce#ecommerce-example" ts java scala kt>}}

### Generate the user and assertions

In our example, we will generate 1 users coming at once to see if our simulation is working. After that we define our assertions, these allow us to assert the maximum or minimum response time, the percentage of successful requests, and more. In our case, we will only check if we have more than 90% successful requests.

{{< include-code "ecommerce#set-up" ts java scala kt>}}

## Gatling Enterprise

### Deploy to Gatling Enterprise

Now that we have our test locally, we need to deploy the package to Gatling Enterprise to create a simulation.


{{< alert info >}}
This video showcases the JavaScript SDK, but the same process applies to all SDKs that Gatling provides.
{{< /alert >}}


<div style="position: relative; width: 100%; padding-bottom: 56.25%; height: 0;">
    <video style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;" controls>
        <source src="video/create_a_package.mp4" type="video/mp4">
    </video>
</div>

### Create the simulation

Now that our package is on Gatling Enterprise, we need to create a simulation.

<div style="position: relative; width: 100%; padding-bottom: 56.25%; height: 0;">
    <video style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;" controls>
        <source src="video/create_a_simulation.mp4" type="video/mp4">
    </video>
</div>


Don't forget to copy your simulationId (click on the three dots to the right of your simulation name); we will need it later.

### Create an API key

Now, for GitHub to communicate with Gatling Enterprise and launch the simulation, you need to create an API key:

<div style="position: relative; width: 100%; padding-bottom: 56.25%; height: 0;">
    <video style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;" controls>
        <source src="video/create_api_token.mp4" type="video/mp4">
    </video>
</div>

Now that everything is set up on the Gatling Enterprise side, it's time to create our CI script on GitHub.

## Github Actions

### Setup the secrets and env

To successfully launch our workflow, we need to begin by configuring a secret in our GitHub repository. This secret is essential for securely managing access to external services and APIs.

To add the secret, navigate to your GitHub repository and go to the "Settings" tab. In the left sidebar, click "Secrets and variables" and then select "Actions". Click on the "New repository secret" button. You will need to add a new secret named **`GATLING_ENTERPRISE_API_TOKEN`**. The value of this secret should be your API token. If you don't have an API token yet, you can learn how to create one by following the instructions in this [page]({{< ref "../reference/execute/cloud/admin/api-tokens" >}})

### Understand the Workflow

Now, let's create a simple workflow. This workflow, when launched, will prompt for a **`simulation_id`** and run the specified simulation.

Below is the configuration for the workflow:

{{< include-code "workflow-example" yml>}}

If you wish to customize this workflow further, refer to our [GitHub documentation]({{< ref "../reference/integrations/ci-cd/github-actions" >}}) for more details and options.

### Launch the workflow

Now that our requirements are set up, you can go to GitHub → Actions and launch our workflow with the simulation as an input. After that, you will have a link to check the results on Gatling Enterprise, and see logs in the workflow.

<div style="position: relative; width: 100%; padding-bottom: 56.25%; height: 0;">
    <video style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;" controls>
        <source src="video/launch_the_workflow.mp4" type="video/mp4">
    </video>
</div>

## Conclusion

Integrating Gatling with GitHub Actions offers a solution for automating performance testing. By adopting this shift-left approach, you can identify and address performance issues early in the development cycle, ensuring higher application quality and reliability. 

This integration streamlines the testing process and enhances efficiency between development teams. With Gatling Enterprise and GitHub Actions, you can create a workflow that monitors your application's performance continuously.


