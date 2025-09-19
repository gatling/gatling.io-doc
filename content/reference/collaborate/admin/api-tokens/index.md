---
menutitle: API tokens
title: API tokens configuration
seotitle: Configure API Tokens in Gatling Enterprise Edition
description: Learn how to administrate API tokens to authenticate your requests to the Gatling Enterprise Edition public API.
lead: Create API Tokens to authenticate your requests to the Gatling Enterprise Edition public API.
date: 2021-03-10T13:47:16+00:00
aliases:
  - /reference/execute/cloud/admin/api-tokens/
---

API Tokens administration is accessible in the navigation bar.

## Managing API Tokens

{{< img src="tokens.png" alt="Tokens" >}}

To create an API token, click on the **Create** button.
Once the API token is created, make sure to copy the token, as you won't be able to retrieve it again later.

{{< img src="create-token.png" alt="Create token" >}}

There are 5 permissions available for API Tokens:

- The **None** permission, which doesn't allow any action. Typically useful to restrict global permissions on team-specific tokens.
- The **Read** permission, allows reading all data.
- The **Start** permission, allows starting simulations + Read permissions (typically useful in a CI plugin).
- The **Configure** permission, allows creating / uploading packages and creating simulations + Start permissions (typically useful in our build plugins).
- The **Administrate** permission, allows managing all organization resources.

You can set a permission globally or within a specific team only.

{{< alert tip >}}
**CI Plugins** need the **Start** permission
{{< /alert >}}
{{< alert tip >}}
**Build Plugins** need the **Configure** permission
{{< /alert >}}

You can edit the API Token permissions by clicking on the {{< icon pencil-alt >}} icon on the right part of the table. 
To regenerate a token, click on the {{< icon undo >}} icon.
