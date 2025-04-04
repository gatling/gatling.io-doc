---
title: Understand your first Gatling open-source test report 
menutitle: Understand OSS reports
seotitle: Learn how to read and understand a Gatling report
description: Learn how to read and understand a Gatling report
date: 2025-04-03T04:30:56+02:00
---

## Introduction

Gatling is a powerful open-source load testing tool designed to measure and analyze the performance of web applications. One of its standout features is the detailed and comprehensive static HTML report it generates after a test run. This report provides valuable insights into the performance and behavior of the application under load. If you're new to Gatling, this guide will help you understand the important reporting features and how to make the most of them. 

And if you find yourself needing more advanced features, be sure to check out Gatling Enterprise!

Let's dive into the key sections of the Gatling report and explore what each one offers:

## Understand the Global tab

This section gives you a quick summary of the entire test run and is broken down into the following catagories:

- Ranges
- Stats
- Active Users
- Requests per second
- Responses per second

### Ranges

Ranges provides a high-lvel overview of your test results, including the response time raanges and the number of requests. There are also important information about the Gatling version and run information stored in this part of the report. 

#### Assertions

- **Assertion Results**: If assertions are defined in the test, their results will be displayed here. Assertions help in validating that the test meets specific performance criteria.

### Stats


### Active users


### Requests per second


### Responses per second




- **Response Time Percentiles**: This section breaks down the response times into percentiles, such as the 50th (median), 75th, 95th, and 99th percentiles. Understanding these percentiles helps you see how consistent your response times are and identify any outliers.

- **Response Time Graph**: A visual representation of response times over the test duration. This graph can help you spot trends or spikes in response times, making it easier to pinpoint issues.

### Requests

- **Requests Overview**: Get detailed information about each request made during the test, including the number of successful and failed requests.

- **Request Details**: For each request, you can see metrics like the average response time, minimum and maximum response times, and the standard deviation. This helps you identify which specific requests are performing poorly.

### Errors

- **Error Summary**: A list of all errors encountered during the test, along with the number of occurrences for each error type.

- **Error Details**: Detailed information about each error, including the request that failed and the error message. This is crucial for diagnosing issues and understanding their impact on the test results.

### Active Users

- **User Load Graph**: This graph shows the number of active users over time. It helps you understand how the load was distributed throughout the test and correlate it with performance metrics.

### Response Time vs. Time

- **Response Time Graph**: This graph plots the response time against the time of the test. It helps you identify if there are any trends or patterns in response times as the test progresses.

### Throughput

- **Requests per Second**: This section shows the number of requests processed per second. It helps you understand the capacity of your application and identify any bottlenecks.

### Detailed Request Statistics

- **Individual Request Metrics**: For each request, detailed statistics are provided, including the count, mean response time, standard deviation, and percentiles. This helps you pinpoint which requests are contributing to performance issues.

## Understand the Details tab

The details tab provides request and group-specific charts. In Gatling, you can organize your test scenarios into [groups]({{< ref "/reference/script/core/scenario/#groups" >}}) to better structure and analyze your load tests. The Groups metric provides insights into how these groups performed during the test. Remember that groups can be nested! The provided charts include:

- Group duration/Response time ranges
- Group duration/Response time distribution
- Group duration/Response time percentiles over time
- Group cumulated response time distribution
- Number of requests per second for a specific request
- Number of responses per second for a specific request
- Response time against global throughput

To change which information is plotted, select the group or request of interest in the left-side hierarchal menu. 

Understanding the Groups metric allows you to gain a more granular view of your application's performance and make targeted improvements based on how different parts of your test scenarios are executed.

## Tips for Effective Report Analysis

- **Compare Metrics**: Compare the metrics from different test runs to identify trends and improvements.

- **Correlate Data**: Correlate the data from different sections of the report to get a holistic view of the application's performance.

- **Identify Bottlenecks**: Use the report to identify bottlenecks and areas for optimization.

- **Regular Monitoring**: Regularly monitor the performance metrics to ensure the application meets the desired performance standards.

By understanding these key features of the Gatling static HTML report, you can effectively analyze the performance of your applications and make data-driven decisions to improve their load handling capabilities. Happy testing!
