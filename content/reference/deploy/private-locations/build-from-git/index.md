---
title: Configure git repositories
seotitle: Gatling Enterprise Build from a Git repository
description: Learn how to deploy your Gatling project on Gatling Enterprise by connecting a source repository.
date: 2024-03-10T14:29:04+00:00
aliases:
  - /reference/execute/cloud/user/build-from-sources/
  - /reference/run-tests/build-from-sources/

---

# Introduction

Build from source allows you to build Gatling simulations directly from a source repository, such as GitHub, GitLab, or BitBucket without needing to package them first. This feature is particularly useful for teams that prefer to manage their Gatling projects in a source control system and want to streamline the process of running tests. Simulations are built and stored in your private network, using the Private Locations and Private Packages features of Gatling Enterprise. This approach eliminates the need for manual packaging and uploading of simulation files, enabling a more efficient workflow for performance testing.

{{< img src="generic-diagram.png" alt="Build from Git architecture diagram" >}}

The following sections detail how to configure the Control Plane for Build from a Git repository, including pre-requisites, git authentication, and build tool configurations. Once configured, you can [create simulations]({{< ref "reference/run-tests/simulations/git-repository" >}}) in the Gatling Enterprise UI.

## Pre-requisites

- Private Packages: Build from a Git repository requires a configured [private storage location]({{< ref "reference/deploy/private-locations/private-packages" >}}) to store build artifacts.
- Private Locations: Build from a Git repository is only compatible with [Private Locations]({{< ref "reference/deploy/private-locations/introduction" >}}). Ensure these are configured first.
- Control Plane image: Use the dedicated `gatlingcorp/control-plane:latest-builder` image. Note that this image is different from the standard Control Plane image.
- Allocate adequate CPU and memory resources according to your project's compilation needs.
- Git repository with a compatible Gatling plugin version configured.

## Architecture

The Build from a Git repository feature operates within the Gatling Enterprise architecture, leveraging the Control Plane to manage builds and simulations. The Control Plane interacts with your source repository to fetch simulation code, compiles it using the specified build tool, and stores the resulting artifacts in your private storage location. This process is triggered when you start a simulation in the Gatling Enterprise UI, allowing you to run tests directly from your source code without manual packaging.

After run completion, the test package is deleted from the private storage location, thus each run requires a fresh build from the source repository. This ensures that you always test the latest version of your simulations.

## Configuration

The following sections detail how to configure the Control Plane for Build from a Git repository.

### Git authentication

#### Cloning over SSH using an SSH key

##### SSH Configuration Mount

Set up your SSH key within the control plane configuration block:

```bash
control-plane {
  # ...
  builder {
    git.global.credentials.ssh {
      key-file = <keyFile>
      user-known-hosts-file = <userKnownHostsFile> # (optional – omit this line to disable strict host checking)
    }
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

###### How to Add Hosts to `user-known-hosts-file`

Use `ssh-keyscan` to populate the file mounted at `user-known-hosts-file`:

```bash
ssh-keyscan github.com gitlab.com >> ~/.ssh/known_hosts
```

#### Cloning over HTTPS using git credentials

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
  }
}
```

**Security Best Practices:**
* Use personal access tokens instead of passwords whenever possible.
* Limit token permissions to read-only access.
* Never include credentials in repository URLs within Gatling Enterprise.

### Build tools

| Build Tool | Gatling Plugin Version | Image Cache Path  |
|-----------:|------------------------|-------------------|
|  **Maven** | `4.16.3`               | `/app/.m2`        |
| **Gradle** | `3.13.5.4`             | `/app/.gradle`    |
|    **SBT** | `4.13.3`               | `/app/.sbt`       |
|    **NPM** | `3.13.501`             | `/app/.npm`       |

**Gatling Plugin Version**: The minimum compatible version of each build tool plugin that supports build from a Git repository.

**Image Cache Path**: Ensure build tool caches persist across different upgrades by mounting a volume to the given path.
