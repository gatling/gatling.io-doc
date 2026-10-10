---
menutitle: Daily Alerts
title: Daily Alerts email
seotitle: Get a daily email about Gatling tests that need your attention
description: Receive a daily email listing the tests of your teams whose last run ended without success the day before.
lead: Find out each day which tests in your teams ended without success the day before, without opening Gatling.
date: 2026-10-02T00:00:00+00:00
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise Edition. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Understand how Daily Alerts works

Daily Alerts is an email that Gatling Enterprise Edition sends automatically to team administrators. It lists the tests in their teams that need attention: tests whose last run ended the previous day without success.

If no test needs attention, Gatling doesn't send an email.

## Check who receives Daily Alerts

You receive Daily Alerts if you have the **Administrator** role on at least **one team**. Organization administrators who don't administer a team don't receive Daily Alerts. The email lists the tests of the teams you administer. To learn more about roles, see [Manage users and permissions]({{< ref "/reference/administration/users#managing-users" >}}).

{{< alert info >}}
Gatling doesn't support SSO group mapping for Daily Alerts yet.
{{< /alert >}}

## Check when Gatling sends Daily Alerts

Gatling sends Daily Alerts every day. Each email covers the previous day, from 00:00 to 24:00 UTC.

The email lists a test only if its **last run** ended the previous day without success. If a test failed that day but its last run is successful when Gatling sends the email, the test isn't listed.

## See an example email

The email shows the total number of tests that need your attention in the teams you administer, and lists up to five of the most recent tests with issues. Select **Open test** to go to the run, or **See all tests with issues** to open the Tests page filtered on the **Unsuccessful** status.

{{< img src="notifications-daily-alerts-example.png" alt="Daily Alerts email with the day covered, a counter of tests that need your attention, the five most recent tests with issues with their run status, team, run label and trigger, and a See all tests with issues button" >}}
