---
title: Configure git repositories
seotitle: Gatling Enterprise Edition Build from a Git repository
description: Learn how to deploy your Gatling project on Gatling Enterprise Edition by connecting a source repository.
date: 2024-03-10T14:29:04+00:00
aliases:
  - /reference/execute/cloud/user/build-from-sources/
  - /reference/run-tests/build-from-sources/

---

## Introduction

Build from a Git repository allows you to build Gatling simulations directly from a source repository, such as GitHub, GitLab, or BitBucket without needing to package them first. This feature is particularly useful for teams that prefer to manage their Gatling projects in a source control system and want to streamline the process of running tests. 

Simulations are built and stored in your private network, using the Private Locations and Private Packages features of Gatling Enterprise Edition. This approach eliminates the need for manual packaging and uploading of simulation files, enabling a more efficient workflow for performance testing.

{{< img src="generic-diagram.png" alt="Build from Git architecture diagram" >}}

The following sections detail how to configure the Control Plane for Build from a Git repository, including pre-requisites, git authentication, and build tool configurations. Once configured, you can [create simulations]({{< ref "reference/run-tests/simulations/git-repository" >}}) in the Gatling Enterprise Edition UI.

## Pre-requisites

### Control Plane Docker image

Use the dedicated `gatlingcorp/control-plane:latest-builder` image. 

{{< alert warning >}}
Note that this image is different from the regular Control Plane image, see the `latest-builder` tag.
{{< /alert >}}

### CPU and memory resources

Allocate adequate CPU and memory resources according to your project's compilation needs.
4 CPUs and 4Gb of memory should be a strict minimum.

### Private Locations

Build from a Git repository is only compatible with [Private Locations]({{< ref "reference/deploy/private-locations/introduction" >}}). Ensure these are configured first.

### Private Packages

Build from a Git repository requires a configured [private storage location]({{< ref "reference/deploy/private-locations/private-packages" >}}) to store ephemeral packages.
These will be automatically deleted once they are deployed on the load generators.

### Gatling build tool plugins versions

We require the following minimum versions:

| Build Tool | Gatling Plugin         | Minimum Version |
|-----------:|------------------------|-----------------|
|  **Maven** | `gatling-maven-plugin` | `4.16.3`        |
| **Gradle** | `io.gatling.gradle`    | `3.13.5.4`      |
|    **sbt** | `gatling-sbt`          | `4.13.3`        |
|    **npm** | `@gatling.io/cli`      | `3.13.501`      |

### Build tools local caches

{{< alert warning >}}
We strongly recommend that you mount **all** the following directories on a persisted volume so you don't have to re-download all the dependencies on each container reboot:

* `/app/.m2`
* `/app/.gradle`
* `/app/.sbt`
* `/app/.cache/coursier`
* `/app/.ivy2`
* `/app/.npm`

{{< /alert >}}

## Architecture

The Build from a Git repository feature operates within the Gatling Enterprise Edition architecture, leveraging the Control Plane to manage builds and simulations.\
The Control Plane interacts with your source repository to fetch simulation code, compiles it using the specified build tool, and stores the resulting artifacts in your private storage location.\
This process is triggered when you start a simulation in the Gatling Enterprise Edition UI, allowing you to run tests directly from your source code without manual packaging.

After run completion, the test package is deleted from the private storage location, thus each run requires a fresh build from the source repository.\
This ensures that you always test the latest version of your simulations.

## Git authentication

The following sections detail how to configure the Control Plane for Build from a Git repository.

### Cloning over SSH using an SSH key

When authenticating with Git over SSH, you can use an SSH key for secure access. This method provides a secure way to authenticate without exposing credentials.

#### SSH Configuration Mount

Set up your SSH key within the control plane configuration block:

```bash
control-plane {
  # ...
  builder {
    git.global.credentials.ssh {
      key-file = <keyFile>
      user-known-hosts-file = <userKnownHostsFile> # (optional – omit this line to disable strict host checking)
    }
    # Define the maximum number of builds the control plane can perform concurrently
    # max-concurrency = 1
  }
}
```

- Mount the SSH key file at the `git.global.credentials.ssh.key-file` path inside the container.  
  Ensure that `/app/.ssh/id_gatling` has permission **400**:
  - Owner: Read
  - Group: No access
  - Others: No access

- Make sure the mounted `.ssh` directory is owned by the Gatling user (UID **1001**):
```bash
chown -R 1001 /app/.ssh
```

- If `git.global.credentials.ssh.user-known-hosts-file` is set, mount a `known_hosts` file to the specified path.

#### How to Add Hosts to `user-known-hosts-file`

Use `ssh-keyscan` to populate the file mounted at `user-known-hosts-file`:

```bash
ssh-keyscan github.com gitlab.com >> ~/.ssh/known_hosts
```

### Cloning over HTTPS using git credentials

When authenticating with Git over HTTPS (via password or token), 
you can specify credentials in the Control Plane configuration file (e.g., control-plane.conf):

```bash
control-plane {
  # ...
  builder {
    git.global.credentials.https {
      username = <username> # (optional)
      password = <token>
    }
    # Define the maximum number of builds the control plane can perform concurrently
    # max-concurrency = 1
  }
}
```

{{< alert tip >}}
Security Best Practices:

* Use personal access tokens instead of passwords whenever possible.
* Limit token permissions to read-only access.
* Never include credentials in repository URLs within Gatling Enterprise Edition.

{{< /alert >}}
