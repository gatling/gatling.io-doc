---
menutitle: Slack
title: Slack notifications configuration
seotitle: Configure Gatling run notifications for Slack
description: Get notified about your Gatling simulations in Slack.
lead: Get notified about your simulations with Slack.
date: 2021-11-07T14:29:04+00:00
aliases: 
  - /reference/execute/cloud/admin/notifications/
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Introduction

You can configure Gatling Enterprise Edition to send notifications about your simulation runs results directly with Slack. 

Notifications will be sent as soon as a simulation run ends, and will display:
- A summary of your simulation info
- The run result (Successful, Assertions failure, etc.)
- Assertions results, if you configured any in your simulation

{{< img src="notifications-slack-example-1.png" alt="Slack example 1" >}}

{{< alert info >}}
Configuring Notifications is only available to Administrators. 
{{< /alert >}}

## Preparation

{{< include-file >}}
Slack: includes/preparation.slack.md
{{< /include-file >}}

## Configuration

Navigate to your organization configuration page, and to the **Notifications** tab.

From there, you will be able to activate and configure notifications for your communication tools:

{{< img src="notifications-configuration-1.png" alt="Notifications Tab 1" >}}

{{< img src="notifications-configuration-2.png" alt="Notifications Tab 2" >}}

Click on the toggle to activate notifications.

Paste your webhook URL in the text field, then you can test and save it:

{{< img src="notifications-configuration-slack-1.png" alt="Notifications Slack 1" >}}

- The **Test** button sends a hello world message to your webhook before saving it.
- **Save** persists your configuration. The next simulation runs will send a notification at the end.

To deactivate notifications, click on the toggle, and confirm your choice.

{{< alert warning >}}
A note about data security:
- Your webhook URL is ciphered before being stored in our database.
- Deactivating notifications deletes the webhook URL from configuration, we do not keep it in our database.
{{< /alert >}}
