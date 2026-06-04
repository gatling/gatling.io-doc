---
title: Introduction to tests in Gatling Enterprise Edition
seotitle: Run tests on Gatling Enterprise Edition
description: Learn how to run tests on Gatling Enterprise Edition.
lead: Run tests on Gatling Enterprise Edition.
date: 2025-07-30T10:29:36+00:00
---

## Managing Tests {#managing-tests}

To access the Tests section, click on **Tests** in the navigation bar.

The Tests view contains all the tests configured by your organization and the results of their last run. From the Tests view, you can:

- [Create a new test]({{< ref "#create-test" >}}).
- [Access existing tests]({{< ref "#existing-tests" >}}).
- [Access the last run of a test]({{< ref "#last-run-test" >}}).
- [Start a new run of a test]({{< ref "#start-run-test" >}}).
- [Edit a test]({{< ref "#edit-test" >}}).
- [Duplicate a test]({{< ref "#duplicate-test" >}}).
- [Copy a test ID]({{< ref "#copy-test-id" >}}).
- [Delete a test]({{< ref "#delete-test" >}}).
- [Set the default load generator parameters]({{< ref "#set-default-load-generator-parameters" >}}).

You can also sort and filter the tests by:
- name, 
- sources, 
- team, and 
- last run.

{{< img src="tests-intro.png" alt="Tests intro" >}}

{{< alert info >}}
If your organization or team has no tests, you are prompted to create your first test with either the no-code test builder or by uploading a test-as-code test. 
{{< /alert >}}

### Create a test {#create-test}

To create a new test, click on the **Create a test** button in the Tests view. This opens the test creation modal, where you can choose from three options:

- [**No-code**]({{< ref "no-code.md" >}}): Create a test without writing any code.
- [**Test-as-code**]({{< ref "test-as-code.md" >}}): Create a test from a packaged test.
- [**Build from a Git repository**]({{< ref "git-repository.md" >}}): Create a test from a git repository.

Each option has its own dedicated documentation page that explains how to configure the test. You can access these pages by clicking on the links above or from the table of contents on the left side of this page.

{{< img src="test-creation-modal.png" alt="Test creation modal" >}}

### Access existing tests {#existing-tests}

To access an existing test, click on its name in the **NAME** column in the Tests view. This will take you to the test details page, where you can view its configuration, results, and other relevant information.

### Access the last run of a test {#last-run-test}

To access the last run of a test from the tests table, click on the **LAST RUN** link, which is shaded depending on the run outcome (e.g. green for successful runs). This takes you to the last run results, where you can view the performance metrics and other relevant information.

### Start a new run of a test {#start-run-test}

To start a new run of a test, click on the **Start Test** button in the **START** column in the Tests table. This opens a confirmation modal, where you can launch the new run.

### Edit a test {#edit-test}

To edit a test, click on the kebab menu at the far right side of the Tests table. Click  the **Edit test** button to edit the test configuration. This opens the test creation modal, where you can modify the test's name, package, and other parameters.

### Duplicate a test {#duplicate-test}

To duplicate a test, click on the kebab menu at the far right side of the Tests table. Click the **Duplicate test** button to create a copy of the test. This opens the test creation modal, pre-filled with the duplicated test's parameters. You can then modify the parameters as needed and save the new test.

### Copy a test ID {#copy-test-id}

To copy a test ID, click on the kebab menu at the far right side of the Tests table. Click the **Copy test ID** button to copy the test's unique identifier to your clipboard. This is useful for referencing the test in API calls or other contexts.

### Delete a test {#delete-test}

To delete a test, click on the kebab menu at the far right side of the Tests table. Click the **Delete test** button to remove the test from the list. A confirmation modal will appear to confirm the deletion. Once confirmed, the test will be permanently removed.

### Default load generator parameters {#set-default-load-generator-parameters}

Default load generator parameters contain every Java system property or JS parameter and environment variable used in your tests by default.
Editing these properties propagates to all tests. You can access the form by clicking the button in the top right corner of the Tests view.

{{< img src="default-load-generator-properties.png" alt="Properties" >}}

If you want specific properties for a test, you can ignore the default properties by unchecking the `Default properties` box when creating or editing the test:

{{< img src="override-load-generator-properties.png" alt="Override" >}} 
