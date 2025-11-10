---
menuTitle: Gatling Studio
title: Gatling Studio
description: Create a Recording, transform it into a Scenario, and export a fully functional Gatling project in seconds from scratch thanks to Gatling Studio.
lead: Create a Gatling project in seconds from scratch thanks to Gatling Studio.
---

## Introduction

The Gatling Studio allows you to record a user journey through your browser and turn it into a Gatling-ready Scenario.

Gatling Studio is a native application installed on your system and running locally.

In this documentation, we present how Gatling Studio works and explain how to create a [Recording](#recordings), transform it into a [Scenario](#scenarios), and [export](#export-as-gatling-project) a fully functional Gatling project in seconds from scratch.


{{< alert info >}}
**Requirements**

- You must have a **Gatling Enterprise Edition account** to log in. Sign up for a free trial if you don’t already have an account.
- Last version of Gatling Studio
- **Chromium-based web Browser** installed on your system (Chrome, Edge, …)
{{< /alert >}}


## Installation

Download the installation file corresponding to your system. Gatling Studio is available for macOS, Windows, and Linux operating systems. You will find the latest version of Gatling Studio on the [Github releases page](https://github.com/gatling/gatling-studio/releases/latest)

Once downloaded, open the file and install Gatling Studio on your system.

## Log in to Gatling Studio

When you open the application, the first page allows you to log in with a Gatling Enterprise Edition account.

{{< img src="studio-login.png" alt="Log in page of Gatling Studio" >}}

- With the button “Log in with Gatling Enterprise Edition account”, you will be redirected to the Gatling Enterprise Edition login page. Enter your credentials as usual.
- With the button “Organization specific log in”, you will be able to reach your custom organization access, if you have one, with your professional email. Then you will be able to enter your credentials as usual.
- If you don’t already have a Gatling Enterprise Edition account, you can use the "Create an account" button to sign up.

Immediately after logging in, Gatling Enterprise Edition will prompt you to Grant Access to certain properties. Click on yes to allow Gatling Studio to link your account properly.

You can now close the browser window and go back to Gatling Studio.

## Workspaces

Initially, your need to set a workspace.

A workspace is a folder on your computer where we’ll store your recordings (HAR file), scenarios, and exported projects.

Choose an existing folder or create one.

Later, you will be able to create multiple workspaces and switch between them from the main menu on the left. You can also open the workspace management modal to create, delete, or select a workspace.

{{< alert info >}}
Deleting a workspace **won't delete the folder and contained files** (recordings, scenarios) on your system. It will remove it from the Gatling Studio interface. You can re-add the folder to your workspace anytime.
{{< /alert >}}

## Recordings

The landing page is the Recordings page.

Gatling Studio will record all requests when browsing a website.

### Create a new recording

By clicking on “Record a journey”, you will start the recording process.

On your first recording, Gatling Studio will ask you first to set your browser configuration.

{{< img src="studio-browserconf.png" alt="Browser configuration for Gatling Studio recording" >}}

You can select:
- A web browser in the list of automatically detected browsers
- If your favorite browser is not in the list, you may provide a path to a Chromium-based web browser installed on your computer.

Edit this choice anytime in the [Settings](#settings).

Once the setting is saved, enter the URL you want to explore.

By providing a valid HTTPS URL and hitting “start the recording”, Gatling Studio will open a web browser on the provided URL. You can browse by following the desired user journey.
When you are satisfied with your navigation, you can close the browser. It will end the recording, and Gatling Studio will allow you to modify the default name of the recording.

Save to add the recording to the list. Recordings are saved as a HAR file in your workspace. You can open the workspace recording folder in your system file explorer by clicking on the folder icon next to “Recordings”.

### Import an existing HAR file

You can import an existing HAR with the "Import a HAR" button. We cannot guarantee the proper functioning of HARs not recorded by Gatling Studio.

### Visualize the recording details

Select a recording in the list and visualize registered requests.

{{< alert info >}}
For performance concerns, we only display the first 200 requests. You can display all the requests, but it may cause some performance issues.
{{< /alert >}}

Clicking on a specific request will display its details. You will be able to see the header, body, or raw file of both the request and the response.

{{< img src="studio-recording-details.png" alt="Gatling Studio recording details" >}}

In the recording header, you can:

- Edit the recording name (by clicking on the pencil icon next to the name). This will change the HAR file name saved in your workspace. The file name is the title of the recording.
- Create a Gatling scenario from the recording. See the [Scenario](#scenarios) section of this documentation.
- Open the recording folder of your current workspace with the recording selected
- Delete the recording. The HAR file will be permanently deleted.

{{< alert warning >}}
Deleting a recording **will permanently remove the HAR file from your system**.
{{< /alert >}}


## Scenarios

Scenarios are the first steps toward Gatling Tests. A scenario is a user journey used to test your solution.

### Create a new scenario

{{< alert info >}}
You need a recording to create a Scenario.
{{< /alert >}}

To create a scenario, you can:
- Go to the Scenario page, click on "Create a scenario" and select a recording in the list
- Go on a specific recording and click on "Create a scenario from recording" in the header

Then, the scenario filter will show up.

{{< img src="studio-scenario-filter.png" alt="Recording filtering for a Gatling Studio scenario creation" >}}

You can:
- Change the recording used
- Select/unselect specific domains
- Add/remove the static resources
- Have a preview of filtered requests that will be used in the scenario

{{< alert tip >}}
Including static resources is not recommended for load testing
{{< /alert >}}

When you have selected only the desired request, click on save to visualize the scenario.

You can edit your filters by clicking on "Edit" in a Scenario.

### Visualize a scenario

Gatling Studio will automatically regroup all filtered requests with a timestamp below 100ms and add pauses between request groups based on the recording timing.

The whole scenario is displayed as it will be used in the Gatling Test.

{{< img src="studio-scenario-details.png" alt="Gatling Studio scenario details" >}}

In the scenario header, you can:

- Edit the recording filter as explained above.
- Export a project. See the [Export as Gatling Project](#export-as-gatling-project) section of this documentation.
- Delete the scenario. The scenario file will be permanently deleted.

{{< alert warning >}}
Deleting a scenario **will permanently remove the file from your system**.
{{< /alert >}}

### Export as Gatling Project

The "Export project" button allows you to download a functional Gatling Project filled with your Scenario.

{{< alert info >}}
For now, the project export is only available in **Java/Maven**. 
{{< /alert >}}

You can then use your local environment to run your Gatling Test locally, edit parameters, and package it to upload to Gatling Enterprise Edition.

{{< alert tip >}}
For more information on how to run a test, refer to the [Installation Guide](https://docs.gatling.io/tutorials/test-as-code/java-jvm/installation-guide/) part of this documentation.
{{< /alert >}}

## Settings

### Browser settings

This page allows you to edit your browser configuration for recording.

You can select a web browser in the list of detected ones.

If your browser is not in the list, you may manually provide a path to a Chromium-based web browser installed on your computer.


## Feedbacks

We are very interested in our community feedback.

This is a first version; we have a very long-term vision, and many features will be released gradually.

Feel free to share your feedback, any problems you encounter, or feature requests on the [GitHub issue sections](https://github.com/gatling/gatling-studio/issues). We'd love to discuss them with you to build a better testing experience together.