---
title: Git repository
menutitle: Git repository
seotitle: Git repository in Gatling Enterprise Edition
description: Learn how to create and manage git repositories in Gatling Enterprise Edition.
lead: Git repository in Gatling Enterprise Edition.
date: 2025-07-30T10:29:36+00:00
---

## Manage Git repositories

To access your Git repositories, click on **Sources** in the navigation bar. You need the **Leader** role or higher to access this page.

The Git repositories tab inside the Sources view contains all the configured repositories with the

- name,
- repository URL,
- team,
- simulations (the number of simulations created from this repository).


## Create a new Git repository

To add a Git repository, click on the **Create** button above the Git repositories table. Add the following information:

- **Name**: The name that appears on the simulations general step.
- **Team**: Assign the repository to the team that will use it for running tests.
- **URL**: The URL of the Git repository.

Click **Save** to create the repository.

## Create simulations from a Git repository

From the Sources view, you can create simulations directly from a Git repository. Click the kebab menu (three dots) and select **Create simulation from repository**. This bypasses the Create simulation modal and opens the simulation creation page pre-filled with the repository information. For more information on creating simulations from a Git repository, refer to the [Create simulations from a git repository documentation]({{< ref "reference/run-tests/simulations/git-repository" >}}).

## Copy the repository ID

To copy the repository ID, click on the kebab menu (three dots) and select **Copy repository ID to clipboard**.

## Delete a Git repository from Gatling Enterprise Edition

To delete a Git repository, click on the kebab menu (three dots) and select **Delete**. Confirm the deletion in the pop-up dialog.
