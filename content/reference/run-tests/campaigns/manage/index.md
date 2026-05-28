---
title: Create and manage campaigns
menutitle: Manage campaigns
seotitle: Create and manage campaigns in Gatling Enterprise Edition
description: Learn how to create campaigns, link tests, and manage campaign settings in Gatling Enterprise Edition.
lead: Create campaigns, link your tests, and manage them over time.
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Create a campaign

To create a campaign, click **Create a campaign** from the Campaigns list.

When creating a campaign, provide:

- A **title** that reflects the performance purpose the campaign is tracking.
- The **team** that will own the campaing
- An optional **description** with additional context.

Each campaign belongs to a **team** and this assignment cannot be changed after creation. The team determines which tests are eligible to be linked to the campaign: only tests that belong to the same team can be added.

{{< img src="campaign-create.png" alt="Campaign creation modal" >}}

## Link tests to a campaign

Tests are linked to a campaign from the campaign detail page. Click **Manage tests** to open the test managelent modal, which lists all tests belonging to the campaign's team.

{{< img src="campaign-test-management.png" alt="Campaign test management modal" >}}

Select the tests you want to include and confirm. The campaign will start tracking scores for those tests from their next completed run.

Unselect the tests you want to remove from the campaing and confirm. The campaign will start tracking scores for the remaining tests from their next completed run.

{{< alert info >}}
A test can belong to at most one campaign. If a test is already part of another campaign, it will be displayed as disabled.
{{< /alert >}}

You can update the list of linked tests at any time. Adding a test starts tracking it from its next run; removing a test stops tracking it but does not affect historical scores already recorded.

## Edit a campaign

To edit a campaign's title or description, open the campaign detail page and click **Edit**. The team assignment cannot be changed.

## Delete a campaign

To delete a campaign, open the campaign detail page and click **Delete**. Deleting a campaign removes it and all its recorded scores **permanently**. The tests themselves are not affected.
