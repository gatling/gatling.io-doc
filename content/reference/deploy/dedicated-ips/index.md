---
menutitle: Dedicated IPs 
title: Dedicated IP addresses
seotitle: Use dedicated IP addresses in Gatling Enterprise
description: Request and use dedicated IP addresses for your load generator locations in Gatling Enterprise.
lead: Dedicated IP addresses for your Gatling Enterprise locations.
date: 2021-03-10T14:29:04+00:00
aliases:
  - /reference/execute/cloud/user/dedicated-ips/
---

{{< alert enterprise >}}
This feature is only available on Gatling Enterprise. To learn more, [explore our plans](https://gatling.io/pricing?utm_source=docs)
{{< /alert >}}

Dedicated IP addresses in Gatling Enterprise are static IPv4 addresses that can be provisioned in the region(s) of your choice. Once provisioned for your organization, these addresses can be assigned to your simulations, ensuring all load generation traffic originates from consistent, dedicated IPs. During test execution, Gatling Enterprise automatically allocates one of your unused dedicated IPs—provided it matches both the location and availability of your load generators—so you can maintain precise control over whitelisting, firewall configurations, and traffic analysis.

{{< img src="dedicated-ips.png" alt="Dedicated IPs" caption="Dedicated IPs" >}}

## Managing

To access the dedicated IP addresses section, click on **Dedicated IP Addresses** in the navigation bar.

{{< alert tip >}}
You can request dedicated IP addresses through [technical support](https://gatlingcorp.atlassian.net/servicedesk/customer/portal/8/group/12/create/91).

Please provide:

- Organization slug,
- Desired number of dedicated IP addresses per location([available locations]( {{< ref "reference/run-tests/simulations/test-as-code#locations-configuration" >}})),
- Contact Email,
- GitHub username.

A sales person will contact you.
{{< /alert >}}

The Dedicated IP addresses table shows your available dedicated IP addresses. Each one belongs to a specific location.

## Usage

### User Interface
To enable dedicated IP addresses, activate the `Use Dedicated IPs` option while [configuring simulation locations]({{< ref "simulations#step-2-locations-configuration" >}}).

When starting a simulation run with dedicated IP addresses:

- Gatling Enterprise checks whether you have enough available dedicated IP addresses to cover all configured locations.
- If enough IPs are available, they are reserved for the duration of the run.
- If insufficient IPs are available, the run cannot start.

### Config-as-code
You can also enable dedicated IP addresses programmatically through [config-as-code]({{< ref "configuration-as-code" >}}). Simply set the `useDedicatedIps` property to `true` in your [simulation configuration]({{< ref "/reference/run-tests/configuration-as-code/#simulation-config" >}}). This instructs Gatling Enterprise to automatically assign your dedicated IPs to all load generators for that simulation.
