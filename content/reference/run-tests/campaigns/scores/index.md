---
title: Campaign scores
menutitle: Scores
seotitle: Understanding campaign scores in Gatling Enterprise Edition
description: Learn how campaign scores are computed and what each metric means.
lead: Understand how campaign scores are computed and what each metric tells you to act on.
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## How scores are computed

A campaign score is computed at the end of every run belonging to a test in the campaign. All tests in the campaign are evaluated together, not just the one that just ran.

The overall campaign score (0–100) is the average of three category scores: **Methodology**, **Objectives**, and **Confidence**. Each category score is itself the average of that category's scores across all tests in the campaign.

{{< alert info >}}
When a campaign is first created, tests that have not yet completed a run will score 0 on all execution-based metrics. This is expected: those metrics require run data that does not exist yet. Scores will improve naturally as tests accumulate runs, and the overall campaign score will stabilise once all tests have run at least once.
{{< /alert >}}

## Score categories and metrics

Each category groups related metrics that point to a specific type of improvement. The breakdown is visible per test in the campaign detail page.

{{< img src="campaign-test-scores.png" alt="Per-test score breakdown showing Methodology, Objectives, and Confidence metrics" >}}

### Methodology

Methodology measures how well each test is configured to produce meaningful, stable results.

| Metric | What it checks | Weight in the category score |
|---|---|---|
| **Test name** | A custom name has to be manually assigned to the test. A meaningful name makes the campaign easier to understand and maintain. | 20% |
| **Load test type** | A load test type has to be explicitly defined in the test parameters. This ensures the test's intent is clear and its results are reproducible. | 20% |
| **Objective set** | At least one objective (SLO or assertion) has to be configured for the test. Without an objective, test outcomes have no defined success criteria. | 60% |

### Objectives

Objectives measures whether each test is actually meeting the goals you have defined.

| Metric | What it checks | Weight in the category score |
|---|---|---|
| **Execution valid** | The last run should complete successfully. A failed or aborted run produces no usable results. | 20% |
| **Objectives pass** | The last run should meet all configured objectives (SLOs or assertions). | 80% |

### Confidence

Confidence measures how reliable and stable each test is over time.

| Metric | What it checks | Weight in the category score |
|---|---|---|
| **Last 3 runs stability** | Weighted average of the Objective scores from the last 3 runs. A high confidence score means your results are consistent, not one-off. | Current run accounts for 50% and the two previous runs accounts for 25% each. |

## Acting on scores

Every metric is actionable. A low score on any metric points directly at what to improve:

- Low **Methodology** score: assign a meaningful name to the test, define its load test type, and configure an objective.
- Low **Objectives** score: investigate why the last run failed or did not meet its objectives.
- Low **Confidence** score: look for instability across the last few runs. Flaky infrastructure, environment drift, or inconsistent load patterns are common causes.

{{< img src="campaign-test-score-advices.png" alt="Advice on test score metrics" >}}


## Campaign scores trends

The trend chart shows the evolution of the campaign score over time, with one data point captured per day at midnight UTC.

{{< img src="campaign-score-trends.png" alt="Campaign score trend over time" >}}
