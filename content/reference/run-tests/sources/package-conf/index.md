---
title: Package Configuration
seotitle: Configure packages in Gatling Enterprise
description: Learn how to configure and upload a package to Gatling Enterprise.
lead: Configure and upload your package to Gatling Enterprise.
date: 2021-03-10T14:29:36+00:00
aliases:
- artifact_conf
- /reference/execute/cloud/user/package-conf/
---

## Manage packages

To access your packages, click on **Sources** in the navigation bar. You need the **Leader** role or higher to access this page.

The Packages tab inside the Sources view contains all the packages you have configured with the
- name, 
- format, 
- team, 
- filename of the uploaded package if not empty, and
- the date of the last upload.

The Gatling version of the package is displayed in a badge next to the filename.

{{< img src="package-table.png" alt="Packages table" >}}

## Create a package

To add a package, click on the **Create** button above the Packages table.

{{< img src="package-create.png" alt="Package creation" >}}

- **Name**: the name that will appear on the simulations general step.
- **Team**: select the team who has access to the package.
- **Simulation packaged with Gatling Enterprise plugin**: *optional (see below)*. The generated package file to upload.

Depending on the language used for your simulation, the package created is assigned to the `JVM` (Java, Kotlin, Scala) or the `JS` (Javascript, Typescript) type.
This type cannot be updated. For example, a `JS` package cannot replace a `JVM` package: you have to create a new package.

## Upload packages

### Option 1: Manual Upload

In order to fill the package with your bundled simulation, click on the **Browse files** button or drag and drop your file directly on the dashed-bordered area.

{{< alert info >}}
In order to package your simulation, refer to the [Package Generation documentation]({{< ref "package-gen" >}}).
{{< / alert >}}

Upon successful file upload, you should see your file:

{{< img src="package-filled.png" alt="Package upload filled" >}}

### Option 2: API Upload

You can also upload packages programmatically with our REST API.

You'll need:
* an [API token]({{< ref "/reference/collaborate/admin/api-tokens" >}}) with at least the `Packages` permission
* the Package's ID, which can be copied from the WebUI.

You can then upload your package, eg with `curl`:

```
curl -X PUT --upload-file <PACKAGE_LOCAL_PATH> \
  "https://<DOMAIN>/api/public/artifacts/<PACKAGE_ID>/content?filename=<PACKAGE_FILE_NAME>" \
  -H "Authorization:<API_TOKEN>"
```

### Option 3: Plugin configuration

Maven, Gradle, sbt and JavaScript/TypeScript plugins offer commands to automatically deploy and manage your packages and simulations.

{{< alert info >}}
Check the [Maven]({{< ref "/integrations/build-tools/maven-plugin/#deploying-on-gatling-enterprise" >}}), 
[Gradle]({{< ref "/integrations/build-tools/gradle-plugin/#deploying-on-gatling-enterprise" >}}), 
[sbt]({{< ref "/integrations/build-tools/sbt-plugin/#deploying-on-gatling-enterprise" >}}), and
[JavaScript/TypeScript]({{< ref "/integrations/build-tools/js-cli/#deploying-on-gatling-enterprise" >}}) 
integrations for more information.
{{< / alert >}}

## Use a package in a simulation

You can configure which package to use for a simulation in the simulation's **General** step.

{{< img src="package-simulation-step.png" alt="Package upload filled" >}}
