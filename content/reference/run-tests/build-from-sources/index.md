---
title: Build from sources
seotitle: Gatling Enterprise Build from sources
description: Learn how to deploy your Gatling project on Gatling Enterprise by connecting a source repository.
date: 2024-03-10T14:29:04+00:00
aliases:
  - /reference/execute/cloud/user/build-from-sources/
---

# Introduction

Run simulations by simply plugging your git repository.

## Pre-requisites

- Private Repository: Build from Sources requires a configured [Private Repository]({{< ref "reference/deploy/private-locations/private-packages" >}}) to store build artifacts.
- Private Locations: Build from Sources is only compatible with [Private Locations]({{< ref "reference/deploy/private-locations/introduction" >}}). Ensure these are configured first.
- Control Plane image: Use `gatlingcorp/control-plane:latest-builder` instead of `gatlingcorp/control-plane:latest`.
- Allocate adequate CPU and memory resources according to your project's compilation needs.
- Git repository with a compatible Gatling plugin version configured

## Configuration

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

**Gatling Plugin Version**: The minimum compatible version of each build tool plugin that supports build from sources.

**Image Cache Path**: Ensure build tool caches persist across different upgrades by mounting a volume to the given path.

## Usage

### Git repositories

Navigate to Sources → Git repositories

Create a new repository by providing:
- Name
- Team (note: simulations built from this repository will inherit this team)
- URL (e.g: `git@github.com:gatling/gatling.git`)

{{< img src="build-from-sources-create-repository.png" alt="Create Git Repository" >}}

From repository actions, you can create new simulations.

### Creating simulations from sources

Go to **Simulations** → **Create Simulation** → Select **Build from sources**

{{< img src="build-from-sources-simulation-create.png" alt="Create Simulation from Git Repository" >}}

**Configure:**
- Source repository
- Branch (defaults to repository default branch)
- Working directory (defaults to repository root)
- Build tool of your project
- Simulation class
  - JVM projects: Enter the fully qualified name (example: `io.gatling.DemoSimulation`)
  - JavaScript projects: Use the simulation name (example: `demoSimulation` for `demoSimulation.gatling.js`)
- Configure private locations and other [simulation properties]({{< ref "reference/run-tests/simulations" >}})
- Save and launch your simulation

{{< img src="build-from-sources-simulation-configure.png" alt="Create Simulation from Git Repository" >}}
