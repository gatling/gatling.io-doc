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

See the existing teams in your organization and their users using the **See members** button.

{{< alert info >}}
Creating, editing, or deleting teams requires administrator access. See [Teams administration]({{< ref "/reference/administration/teams" >}}) for details.
{{< /alert >}}

For more details on viewing teams, see the [Teams]({{< ref "/reference/user-guide/teams" >}}) page.

## Usage

{{< alert warning >}}
This section is available to all [global roles]({{< ref "/reference/administration/users#permissions" >}}) except Viewers.
{{< /alert >}}

The usage dashboard allows you to monitor and manage credit consumption during your current billing period. The primary functions are:

- monitor global consumption
- monitor usage by team
- set credit quotas by team
- monitor usage by simulation

### Set a team usage quota

Usage quotas allow you to control credit consumption on a team by team basis. Set quotas are automatically carried over to your next billing period. To set a team usage quota:

1. Click the {{< icon edit >}} icon located on the right side of the **Credit consumption by team** table.
2. Enter a value less than your total billing period allotment.
3. Click **Save**.

{{< img src="usage.png" alt="Organization usage" >}}

{{< alert info >}}
Billing, plans, and offers are managed by administrators. See [Billing]({{< ref "/reference/administration/billing" >}}) for details.
{{< /alert >}}
