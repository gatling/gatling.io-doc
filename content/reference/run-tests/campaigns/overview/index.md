---
title: Introduction to campaigns
menutitle: Introduction
seotitle: Campaigns in Gatling Enterprise Edition
description: Learn how campaigns help you structure tests around a clear performance purpose and track outcomes over time.
lead: Structure your tests around a clear and concrete purpose with valuable, trackable outcomes.
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## What is a campaign?

A campaign is a named group of tests organised around a concrete performance purpose meeting your needs.

Where a single test run tells you how a feature performs at a given moment, a campaign tells you whether your system is consistently meeting a defined standard over time. Each time a test belonging to a campaign completes a run, a **campaign score** is computed and added to the score trend, giving you an evolving picture of your system's performance health.

The score is broken down into three categories (**Methodology**, **Objectives**, and **Confidence**), each of which surfaces specific, actionable improvements. A low score always points to something concrete you can act on.

{{< img src="campaign-list.png" alt="Campaign list showing score all campaign and their scores" >}}

## Accessing campaigns

Click on **Campaigns** in the main menu to access the campaigns list. The list shows all campaigns available to you, along with their latest score.

Click on a campaign name or on **See details** to open its detail page, which shows the campaign score trend over time and the per-test score breakdown.

{{< img src="campaign-details.png" alt="Campaign details showing the campaign score and the per-test score breakdown" >}}