---
menutitle: Teams
title: Teams administration
seotitle: Teams administration in Gatling Enterprise Edition
description: Learn how to administrate teams in Gatling Enterprise Edition.
lead: Administrate your organization's teams in Gatling Enterprise Edition.
date: 2021-03-10T13:47:03+00:00
aliases:
  - /reference/collaborate/admin/teams/
  - /reference/execute/cloud/admin/teams/
---

## Managing Teams

To access the Teams page, click on the "Teams" tab in the Organization page.

{{< img src="teams-page.png" alt="Teams" >}}

{{< alert info >}}
Displayed data are limited to the teams you belong to.
{{< /alert >}}
{{< alert info >}}
Creating, editing, or deleting teams requires administrator access. See [Teams administration]({{< ref "/reference/administration/teams" >}}) for details.
{{< /alert >}}

In the teams table, you can visualize for each team:
- The team name,
- The number of members (you can click on the link to see the list of members),
- The number of credits consumed for the period displayed below the title "Teams and Quota settings",
- The credit quota limit and it's edition (Administrator only),
- The associated API tokens, tests, packages, repositories (you can click on the pills to see the linked elements).

By using the menu at the end of each line you can:
- Copy the team id
- Edit the team name (Administrator only)
- Delete a team (Administrator only)

### Team creation

To create a new team, click on the **Create** button and enter a name for the new team.

{{< img src="create-new-team.png" alt="Creating a team" caption="Creating a team" >}}

### Change quota settings

{{< alert info >}}
You must have an annual plan and be an Organization Administrator to change quota setting.
{{< /alert >}}
You can switch from annual quota to monthly quota. If you do so, the team quota previously set will be reset.

### Team quota

{{< alert info >}}
You must be an Organization Administrator to set team quotas.
{{< /alert >}}

Usage quotas allow you to control credit consumption on a team by team basis. Set quotas are automatically carried over to your next billing period. To set a team usage quota:

1. Click the {{< icon edit >}} icon located in the **Quota** column of the table.
2. Enter a value less than your total billing period allotment.
3. Click **Save**.

You can edit the team quotas for the seleted period (annual or monthly).
Leave the input empty to set an unlimited credit consumption to a team.

{{< alert info >}}
Billing, plans, and offers are managed by administrators. See [Billing]({{< ref "/reference/administration/billing" >}}) for details.
{{< /alert >}}
