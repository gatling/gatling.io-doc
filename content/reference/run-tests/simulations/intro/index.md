---
title: Introduction to simulations in Gatling Enterprise
seotitle: Run tests on Gatling Enterprise
description: Learn how to run tests on Gatling Enterprise.
lead: Run tests on Gatling Enterprise.
date: 2025-07-30T10:29:36+00:00
---

## Managing Simulations {#managing-simulations}

To access the Simulations section, click on **Simulations** in the navigation bar.

The Simulations view contains all the simulations configured by your organization and the results of their last run. From the Simulations view, you can:

- [Create a new simulation]({{< ref "#create-simulation" >}}).
- [Access existing simulations]({{< ref "#existing-simulations" >}}).
- [Access the last run of a simulation]({{< ref "#last-run-simulation" >}}).
- [Start a new run of a simulation]({{< ref "#start-run-simulation" >}}).
- [Edit a simulation]({{< ref "#edit-simulation" >}}).
- [Duplicate a simulation]({{< ref "#duplicate-simulation" >}}).
- [Copy a simulation ID]({{< ref "#copy-simulation-id" >}}).
- [Delete a simulation]({{< ref "#delete-simulation" >}}).
- [Set the default load generator parameters]({{< ref "#set-default-load-generator-parameters" >}}).

You can also sort and filter the simulations by:
- name, 
- sources, 
- team, and 
- last run.

{{< img src="simulations-intro.png" alt="Simulations intro" >}}

{{< alert info >}}
If your organization or team has no simulations, you are prompted to create your first test with either the no-code test builder or by uploading a test-as-code simulation. 
{{< /alert >}}

### Create a simulation {#create-simulation}

To create a new simulation, click on the **Create a simulation** button in the Simulations view. This opens the simulation creation modal, where you can choose from three options:

- [**No-code**]({{< ref "no-code.md" >}}): Create a simulation without writing any code.
- [**Test-as-code**]({{< ref "test-as-code.md" >}}): Create a simulation from a packaged test.
- [**Build from a Git repository**]({{< ref "git-repository.md" >}}): Create a simulation from a git repository.

Each option has its own dedicated documentation page that explains how to configure the simulation. You can access these pages by clicking on the links above or from the table of contents on the left side of this page.

{{< img src="simulation-creation-modal.png" alt="Simulation creation modal" >}}

### Access existing simulations {#existing-simulations}

To access an existing simulation, click on its name in the **NAME** column in the Simulations view. This will take you to the simulation details page, where you can view its configuration, results, and other relevant information.

### Access the last run of a simulation {#last-run-simulation}

To access the last run of a simulation from the simulations table, click on the **LAST RUN** link, which is shaded depending on the run outcome (e.g. green for successful runs). This takes you to the last run results, where you can view the performance metrics and other relevant information.

### Start a new run of a simulation {#start-run-simulation}

To start a new run of a simulation, click on the **Start Simulation** button in the **START** column in the Simulations table. This opens a confirmation modal, where you can launch the new run.

### Edit a simulation {#edit-simulation}

To edit a simulation, click on the kebab menu at the far right side of the Simulations table. Click  the **Edit simulation** button to edit the simulation configuration. This opens the simulation creation modal, where you can modify the simulation's name, package, and other parameters.

### Duplicate a simulation {#duplicate-simulation}

To duplicate a simulation, click on the kebab menu at the far right side of the Simulations table. Click the **Duplicate simulation** button to create a copy of the simulation. This opens the simulation creation modal, pre-filled with the duplicated simulation's parameters. You can then modify the parameters as needed and save the new simulation.

### Copy a simulation ID {#copy-simulation-id}

To copy a simulation ID, click on the kebab menu at the far right side of the Simulations table. Click the **Copy simulation ID** button to copy the simulation's unique identifier to your clipboard. This is useful for referencing the simulation in API calls or other contexts.

### Delete a simulation {#delete-simulation}

To delete a simulation, click on the kebab menu at the far right side of the Simulations table. Click the **Delete simulation** button to remove the simulation from the list. A confirmation modal will appear to confirm the deletion. Once confirmed, the simulation will be permanently removed.

### Default load generator parameters {#set-default-load-generator-parameters}

Default load generator parameters contain every Java system property or JS parameter and environment variable used in your simulations by default.
Editing these properties propagates to all simulations. You can access the form by clicking the button in the top right corner of the Simulations view.

{{< img src="default-load-generator-properties.png" alt="Properties" >}}

If you want specific properties for a simulation, you can ignore the default properties by unchecking the `Default properties` box when creating or editing the simulation:

{{< img src="override-load-generator-properties.png" alt="Override" >}} 
