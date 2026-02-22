---
title: Organizations
seotitle: Organizations in Gatling Enterprise Edition
description: Learn how to view your organization details in Gatling Enterprise Edition.
date: 2021-08-05T13:13:30+00:00
---

To view details about your organization, click on the **Organization settings** button or on the **Organization** menu item.

{{< img src="menu.png" alt="Organization settings button" caption="The organization settings button" >}}
{{< img src="menu-nav.png" alt="Organization menu" caption="The organization menu item" >}}

If you've been invited to multiple organizations,
you can switch between them by clicking on the organization name in the top right corner.

{{< img src="multiple-organizations.png" alt="Multiple organizations" >}}

{{< alert info >}}
Organization name, slug, avatar, and feature toggles are managed by administrators. See [Organization settings]({{< ref "/reference/administration/organization-settings" >}}) for details.
If you need changes, contact your organization's administrator.
{{< /alert >}}

## Users

Depending on your role, shows all users or administrators in your organization.

{{< img src="users.png" alt="Organization users" >}}

If you are an administrator, you can edit the roles of each user using the **Edit roles** button:

{{< img src="edit-roles.png" alt="Edit user roles" >}}

{{< alert info >}}
Inviting new users is restricted to administrators. See [Invitations]({{< ref "/reference/administration/invitations" >}}) for details.
{{< /alert >}}

{{< alert info >}}
For detailed information about user roles and permissions, see [User management]({{< ref "/reference/administration/users" >}}).
{{< /alert >}}

## Teams

See and manage the teams you belong to in your organization. Display their details including **members**, **credit consumption** and **quotas**.

For more details on viewing teams and setting quotas, see the [Teams]({{< ref "/reference/user-guide/teams" >}}) page.

{{< alert info >}}
Creating, editing, deleting teams and setting quota requires Organization level administrator access. See [Teams administration]({{< ref "/reference/administration/teams" >}}) for details.
{{< /alert >}}

## Usage

{{< alert warning >}}
This section is available to all [global roles]({{< ref "/reference/administration/users#permissions" >}}) except Viewers.
{{< /alert >}}

The usage dashboard allows you to monitor credit consumption daily, monthly, and by team.

Data displayed are downloadable as CSV file.
