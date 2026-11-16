---
menutitle: Repositories
title: Repositories configuration
seotitle: Configure repositories in Gatling Enterprise Self-Hosted
description: Configure repositories so that Gatling Enterprise Self-Hosted can fetch your Gatling simulations.
lead: Configure repositories so that Gatling Enterprise Self-Hosted can fetch your Gatling simulations.
date: 2021-03-25T18:25:43+01:00
---

{{< alert tip >}}
Before creating a repository, you need to make sure your Gatling simulations are adjusted to Gatling Enterprise. Check the [corresponding section]({{< ref "package-gen" >}}).
{{< /alert >}}

To access the repositories administration, click on **Admin** in the navigation bar, and choose **Repositories**.

There are 2 types of repository: the ones where you download and compile the sources, and the others where you download an already-compiled project.

{{< img src="repositories.png" alt="Repositories table" >}}

To create a repository, click on the **Create** button.
You can edit the repository by clicking on the {{< icon pencil-alt >}} icon and delete them using the checkboxes on the table's right part.

## General

{{< img src="create-repository.png" alt="Repository creation" >}}

- **Name**: the name that will appear on the repositories table.
- **Team**: set if the repository is global or owned by a team
- **Repository Type**: the desired type of your repository

## Downloading from sources

Choose *Build from sources* as repository type if you wish that Gatling Enterprise fetch and compile the sources of your Gatling simulation. In this page, you'll configure how to fetch the sources.

{{< img src="create-repository-sources.png" alt="Sources Repository creation" >}}

There are 3 different ways to retrieve your sources:

- **Clone a Git repository**: If you want to clone a git repository. You'll need to fill in the URL of the targeted repository, and the targeted git branch or tag (which can be overridden in the simulation configuration). If you're using ssh authentication, you can also fill in a previously added [private key]({{< ref "../admin/private-keys" >}}) scoped on repository with **Git SSH key**. If you're using HTTPS authentication, you can setup an username and password.
- **Use a project on Gatling Enterprise's filesystem**: Use a project located on Gatling Enterprise's filesystem, fill in the path to project repository.
- **Check out from Source Code Control System**: Useful if you're using a code control system other than Git, or if you need a really specific Git command.

## Downloading from a binary repository

Choose **Download from a binary repository** if you already compiled your project and pushed to a binary repository.

{{< img src="create-repository-binary.png" alt="Binary Repository creation" >}}

We currently support 5 different providers: JFrog Artifactory, Sonatype Nexus 2 & 3, GitLab Package Registry, AWS S3.

If you're using an Artifactory, Nexus or GitLab repository, you'll need to fill in the following fields:

- **Repository URL**: the URL of the targeted repository
- **Authentication settings**: the key for the jar you want to download
- **Username**: the username of the user with sufficient permissions
- **API Key** (Artifactory), **Password** (Nexus) or **Access Token** (GitLab): the secret of this user, for example an [API key for the current user](https://www.jfrog.com/confluence/display/RTF/Updating+Your+Profile#UpdatingYourProfile-APIKey) on Artifactory
- **Use proxy**: connect to the repository through the HTTP proxy defined in the `http.proxy` section of the [Gatling Enterprise configuration]({{< ref "/install/server/manual#configure-gatling-enterprise" >}}). If the configuration defines no proxy, Gatling Enterprise connects directly.

{{< alert warning >}}
Make sure to follow the Repository URL pattern as follow:

- JFrog Artifactory: `http[s]://<host>/<repository>`
- Sonatype Nexus 2: `http[s]://<host>/nexus/content/repositories/<repository>`
- Sonatype Nexus 3: `http[s]://<host>/repository/<repository>`
- GitLab Package Registry, project level: `http[s]://<host>/api/v4/projects/<project ID or URL-encoded path>/packages/maven`
- GitLab Package Registry, group level: `http[s]://<host>/api/v4/groups/<group ID or URL-encoded path>/-/packages/maven`
{{< /alert >}}

### Finding your repository URL

#### Artifactory

Navigate to **Application**, then **Artifactory** and sublevel **Artifact**:

{{< img src="artifactory-repository-url.png" alt="Finding the URL of your Artifactory repository" >}}

#### Sonatype Nexus

On Sonatype Nexus 2, it's available directly on the main page. Here with the **Releases** repository:

{{< img src="nexus2-repository-url.png" alt="Finding the URL of your Nexus 2 repository" >}}

Same for Sonatype Nexus 3:

{{< img src="nexus3-repository-url.png" alt="Finding the URL of your Nexus 3 repository" >}}

By default, it will be the **maven-releases** repository you are looking for.

#### GitLab Package Registry

Use the Maven endpoint of the project or group where you publish your package. GitLab displays the project or group ID on its main page. You can also use its URL-encoded path, for example `my-group%2Fmy-project` for `my-group/my-project`.

Authenticate with a token that can read the package registry, such as a personal access token with your GitLab username, or a deploy token with its username. See the [GitLab documentation](https://docs.gitlab.com/user/packages/maven_repository/#authenticate-to-the-package-registry) for the supported tokens and their required scopes.

#### AWS S3

If you're using an AWS S3 bucket, you'll need to fill in the following fields:

{{< img src="create-repository-s3.png" alt="AWS S3" >}}

- **Profile name**: choose a profile described in `~/.aws/credentials`, or select **Use environment or system variables** to use the permissions granted to the EC2 Gatling Enterprise instance
- **Region**: the region where you created your bucket
- **Bucket name**: the bucket name

Before saving, we advise you to check the connection to the repository by clicking on the **Check Connection** button.

{{< alert tip >}}
Profile name requires the following permissions to be able to download from the AWS S3 repository:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "s3:GetObject"
      ],
      "Effect": "Allow",
      "Resource": "arn:aws:s3:::DOC-EXAMPLE-BUCKET/*"
    }
  ]
}
```
{{< /alert >}}
