---
title: Billing
seotitle: Billing in Gatling Enterprise Edition
description: Learn how to manage billing, plans, and offers in Gatling Enterprise Edition.
lead: Manage your organization's billing settings, credit consumption, and subscription plans.
date: 2021-08-05T13:13:30+00:00
---

{{< alert warning >}}
This section is only available to [Administrators]({{< ref "/reference/administration/users/#permissions" >}}).
{{< /alert >}}

This page covers your billing and credit consumption settings and your subscribed plans history, if any.

{{< img src="billing.png" alt="Organization billing" >}}

## Billing settings

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

## Credits consumption

The amount of consumed and remaining credits are displayed for both paid and trial plans. For paid plans, the credits consumed are for your billing period:

{{< img src="paid-plan-credits.png" alt="Organization paid plan credits" caption="The credits consumption view for a paid plan" >}}

{{< img src="free-plan-credits.png" alt="Organization paid plan credits" caption="The credits consumption view for a free plan" >}}

{{< alert info >}}
For trial plans, the given credits are only available for 14 days.
{{< /alert >}}

## Extra credits

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


## Plans

Plans view history.

{{< img src="plans.png" alt="Organization plan" >}}

* **Status** - Current status of the payment plan: **Terminated**, **Active**, or **PaymentFailure**.
* **From** - Start date of the plan.
* **To** - End date of the plan, if there is one.
* **Credits** - Number of credits awarded each month by the plan.

## Offers

### Payment via Stripe

This page shows all available offers for your organization. You can choose the number of credits for your offer. A credit represents a minute of usage of one Gatling load generator.

{{< img src="offers.png" alt="Available Offers" >}}

Click on the **Subscribe now** button in order to buy the desired offer via stripe. If you want to change your current offer, or buy the **Custom** one, please click on **Contact us**.


### Payment via AWS Marketplace

After creating an Organization and an organization Admin user you can choose to subscribe to an offer via the [AWS marketplace](https://aws.amazon.com/marketplace/pp/prodview-6bhi2464rfmzq):

{{<img src="aws_marketplace.png" alt="AWS marketplace offer" >}}

Select, among other options, the contract option:

{{<img src="aws_contract_option.png" alt="Contract option" >}}

And click on **Create contract**:

{{<img src="aws_create_contract.png" alt="Create contract" >}}

To finish setup, fill out the subscription form with current users and organization information:

{{<img src="aws_subscription_form.png" alt="setup subscription" >}}
