---
title: Organizations
seotitle: Organizations in Gatling Enterprise
description: Learn how to access your organization details in Gatling Enterprise.
date: 2021-08-05T13:13:30+00:00
aliases:
  - /reference/execute/cloud/user/organization/
---

To view [settings]({{< ref "#settings" >}}) about your organization, click on the **Organization settings** button or on the **Organization** menu item.

{{< img src="menu.png" alt="Organization settings button" caption="The organization settings button" >}}
{{< img src="menu-nav.png" alt="Organization menu" caption="The organization menu item" >}}

If you've been invited to multiple organizations, 
you can switch between them by clicking on the organization name in the top right corner.

{{< img src="multiple-organizations.png" alt="Multiple organizations" >}}

## Settings

{{< img src="profile.png" alt="Organization profile information" >}}

* **Avatar** - Composed by default from the two first characters of your **Organization name**.
* **Organization Name** - The display name for your organization.
* **Organization Slug** - Unique string name, in lowercase and spaced by dashes `-`.


{{< alert tip >}}
Click on the pen icon to edit the **Organization name**.
{{< /alert >}}

### Features management

{{< img src="features-management.png" alt="Features management" >}}

{{< alert info >}}
This section is only available to organizations using [private locations]({{< ref "../../deploy/private-locations/introduction" >}}).
{{< /alert >}}

#### Managed packages

Managed packages are **enabled** by default.

This setting allows you to control whether your organization's users can create and use managed packages. By disabling this feature, you enforce the exclusive use of [private packages]({{< ref "../../deploy/private-locations/private-packages" >}}) for all load tests.

When disabled, users in your organization will be prevented from:

* Creating, uploading or updating packages from the **Sources** view.
* Creating new simulations with a managed package.
* Launching any pre-existing simulation that is configured with a managed package.

Note: Any managed packages that were created before this feature was disabled will remain visible in the Sources view. Users can still delete these if necessary.

#### Managed locations

Managed locations are **enabled** by default.

This setting allows you to control whether your users can deploy load tests to our managed locations, including locations using a dedicated IP. By disabling this feature, you enforce the exclusive use of [private locations]({{< ref "../../deploy/private-locations/introduction" >}}) for all load tests.

When disabled, users in your organization will be prevented from:

* Creating new simulations with a managed location, or a location with a dedicated IP.
* Launching any pre-existing simulation configured with at least one managed/dedicated IP location.

## Users

Depending on your role, shows all users or administrators in your organization.

{{< img src="users.png" alt="Organization users" >}}

If you are an administrator, you can edit the roles of each user using the **Edit roles** button:

{{< img src="edit-roles.png" alt="Edit user roles" >}}

## Invitations

{{< alert warning >}}
This section is only available to [Administrators]({{< ref "../admin/users#permissions" >}}).
{{< /alert >}}

Create, see the pending invitations in your organization, and resend them if you need it by clicking the **Resend invitation** button:

{{< img src="invitations.png" alt="Organization invitations" >}}

## Teams

{{< alert warning >}}
This section is only available to [Administrators]({{< ref "../admin/users#permissions" >}}).
{{< /alert >}}

See the existing teams in your organization and their users using the **See members** button:

{{< img src="teams.png" alt="Organization teams" >}}

You can also see how many API tokens, simulations or packages belong to this team using the clickable badges in the **Relations column**:

{{< img src="relations.png" alt="Organization teams relations" >}}

## Usage

{{< alert warning >}}
This section is available to all [global roles]({{< ref "../admin/users#permissions" >}}) except Viewers.
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

## Billing

{{< alert warning >}}
This section is only available to [Administrators]({{< ref "../admin/users#permissions" >}}).
{{< /alert >}}

This page covers your billing and credit consumption settings and your subscribed plans history, if any.

{{< img src="billing.png" alt="Organization billing" >}}

### Billing settings

{{< alert warning >}}
This section is only available if you have subscribed to a paid plan.
{{< /alert >}}

You can click the **Customer portal** to update your billing settings and download your invoices:

{{< img src="billing-settings.png" alt="Organization billing settings" >}}

You will then be redirected to our Stripe portal:

{{< img src="billing-portal.png" alt="Organization billing portal" >}}

{{< alert info >}}
If you subscribed through the AWS marketplace, your billing information and invoices are available through AWS.
{{< /alert >}}

### Credits consumption

The amount of consumed and remaining credits are displayed for both paid and trial plans. For paid plans, the credits consumed are for your billing period:

{{< img src="paid-plan-credits.png" alt="Organization paid plan credits" caption="The credits consumption view for a paid plan" >}}

{{< img src="free-plan-credits.png" alt="Organization paid plan credits" caption="The credits consumption view for a free plan" >}}

{{< alert info >}}
For trial plans, the given credits are only available for 14 days.
{{< /alert >}}

### Extra credits

{{< alert warning >}}
This section is only available for payments made by Stripe.
{{< /alert >}}

When you approach the  credit limits of your plan:

{{< img src="credit_empty.png" alt="Empty credits" >}}

And you don't want to wait the next filling of credit, you can activate extra credits by clicking button **Edit spending limit**

{{< img src="credit_edit_limit.png" alt="Edit extra credits limit" >}}

And set new extra credit limit.

{{< img src="credit_extra_used.png" alt="Extra credits used" >}}

Now simulations -- you couldn't launch before -- can run consuming extra credits.


### Plans

Plans view history.

{{< img src="plans.png" alt="Organization plan" >}}

* **Status** - Current status of the payment plan: **Terminated**, **Active**, or **PaymentFailure**.
* **From** - Start date of the plan.
* **To** - End date of the plan, if there is one.
* **Credits** - Number of credits awarded each month by the plan.

### Offers

#### Payment via Stripe

This page shows all available offers for your organization. You can choose the number of credits for your offer. A credit represents a minute of usage of one Gatling load generator.

{{< img src="offers.png" alt="Available Offers" >}}

Click on the **Subscribe now** button in order to buy the desired offer via stripe. If you want to change your current offer, or buy the **Custom** one, please click on **Contact us**.


#### Payment via AWS Marketplace

After creating an Organization and an organization Admin user you can choose to subscribe to an offer via the [AWS marketplace](https://aws.amazon.com/marketplace/pp/prodview-6bhi2464rfmzq):  

{{<img src="aws_marketplace.png" alt="AWS marketplace offer" >}}

Select, among other options, the contract option:

{{<img src="aws_contract_option.png" alt="Contract option" >}}

And click on **Create contract**:

{{<img src="aws_create_contract.png" alt="Create contract" >}}

To finish setup, fill out the subscription form with current users and organization information:

{{<img src="aws_subscription_form.png" alt="setup subscription" >}}
