---
title: AI Trends Analysis
description: Get an AI-generated analysis of your test trends across the last 10 runs.
lead: AI Trends Analysis reads your last 10 runs and produces a structured report covering health findings, cross-run insights, and concrete recommendations so you can spot what's stable, what's drifting, and what to act on before the next run.
date: 2026-06-19T12:00:00+00:00
---

## Overview
 
The Trends page shows metric charts across your last runs: response times, error rates, and more. It's useful for spotting patterns over time, but making sense of what's normal vs. what needs attention still takes experience.
 
**AI Trends Analysis** adds an AI-generated report on top of those charts. Click the button, and get a structured reading of what's stable, what's drifting, and what stands out across your last 10 runs.
 
## How to use it
 
1. Open a test and select **Trends** in the left panel.
2. Click on the **Analyze with AI** button in the **AI trends analysis** block.
3. The report appears above the trends charts.
4. Once generated, the report persists. If new runs are added, the report is marked as outdated and **Analyze with AI** becomes available again to regenerate it.

{{<  alert info >}}
AI features can be disabled organization-wide by an administrator. If you don't see the **Analyze with AI** button, contact your organization admin.
{{< /alert >}}

{{< img src="ai-trends-analysis.png" alt="Run trends page with the AI trends analysis button" >}}


## What you get
 
The report is structured around three blocks:
 
**Health Findings** opens with a one-sentence summary of the most significant health signal across the window, followed by observations about runs that behave differently from the rest: error spikes, tail latency anomalies, or outliers worth investigating.
 
**Insights** opens with a one-sentence interpretation of what the findings mean, followed by a more detailed read of the patterns: whether an anomaly is isolated or recurring, whether a spike looks like a defect burst or a structural issue.
 
**Recommendations** opens with a one-sentence action focus, followed by 2 to 4 concrete steps to apply before the next run  assertions to add, metrics to watch, or configuration to keep stable.
 
The report also includes a **verdict** (`Stable`, `SomeIssues`, or `Degrading`) and a **confidence level** (`Low`, `Medium`, or `High`), reflecting both the overall trends health and how much signal was available to reason from.

{{< img src="ai-trends-analysis-details.png" alt="Example of generated AI trends analysis" >}}
 
## Notes
 
- The analysis always covers the **last 10 valid runs** of the test, regardless of which run you're currently viewing.
- The report reasons exclusively about runs in that window, it does not compare against other tests or historical baselines outside the window.
- Reports are persisted. The **Analyze with AI** button is only available again once new runs have been added since the last analysis.

{{<  alert info >}}
AI-generated insights are provided for informational purposes only. Always verify results before making decisions.
{{< /alert >}}