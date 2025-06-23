---
title: Run trends
seotitle: Use run trends to understand your recent app performance
description: Using trends and comparison to analyze tests with Gatling Enterprise
lead: Get a high-level overview of your recent simulation results
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

## Introduction

The run trends page provides a high-level overview of your recent runs performance, allowing you to quickly assess if your application performance is improving or regressing.  

## Browse run trends

Your recent runs are displayed in 3 bar graphs, with up to 10 runs in total. Only successfully executed runs (noted by green check marks in the left-side menu) are included in the graphs. The runs are labeled by their run number on the x-axis and can be matched to the run titles in the left-side run menu. The 3 displayed metrics are: 

- Response status 
- Response time percentiles
- Throughput

The following sections contain details for each metric to assist you in understanding your run trends.  

### Response status

The response status is divided between OK and KO, which are grouped as success and error response codes respectively. Customization options for the response status graph include: 

- Toggle between the response count or percent basis, using the **Show values/Show %** toggle located in the top right corner of the graph.
- Filter to display only OK responses, only KO responses, or both by clicking on the legend titles **Responses OK** or **Responses KO** beneath the graph.  

### Response Time Percentiles

Response time percentiles are reported for the following groupings: 

- p50
- p90
- p95
- p99
- p99.9
- p99.99
- maximum

You can customize the display by filtering the displayed percentiles. To remove or add a response time percentile to the graph click on the name in the graph legend.  

### Throughput 

Throughput is the average responses per second over the entire run. Filter to display only OK responses, only KO responses, or both by clicking on the legend titles **Responses OK** or **Responses KO** beneath the graph.  

## Manage simulation runs

### Access detailed reports for a specific run

The detailed reports for each run are accessible by clicking on the run in the left-side menu. 

### Delete a run 

Sometimes you might want to delete a simulation run. For example, if the build or deployment step fails and there is no useful data in the run report. To delete a run from the run trends page: 

1. Select the run by clicking on the run title in the left-side menu. 
2. Click the dropdown menu on the right side of the run summary page, noted by three vertical dots. 
3. Select **Delete run** from the menu.
4. Click **Confirm** in the confirmation modal. 

### Edit the run title

Each run is labeled with a sequential run number by default. Optional run titles can be added to the display to improve the readability of the run trends. To edit the run title from the run trends page: 

1. Select a run from the left-side menu.
2. Click the edit {{< icon edit >}} icon next to the run title (it displays **Untitled run** if you have not previously named the run).
3. Enter a run title in the displayed text box. 
4. Click **Save** to save the run title. 
5. Click **Trends** in the left-side menu to return to the run trends page. 
