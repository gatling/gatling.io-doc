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

Build from a Git repository allows you to build Gatling simulations directly from a source repository, such as GitHub, GitLab, or BitBucket without needing to package them first. This feature is particularly useful for teams that prefer to manage their Gatling projects in a source control system and want to streamline the process of running tests. 

Simulations are built and stored in your private network, using the Private Locations and Private Packages features of Gatling Enterprise. This approach eliminates the need for manual packaging and uploading of simulation files, enabling a more efficient workflow for performance testing.

{{< img src="generic-diagram.png" alt="Build from Git architecture diagram" >}}

The following sections detail how to configure the Control Plane for Build from a Git repository, including pre-requisites, git authentication, and build tool configurations. Once configured, you can [create simulations]({{< ref "reference/run-tests/simulations/git-repository" >}}) in the Gatling Enterprise UI.

## Pre-requisites

- **Private Packages**: Build from a Git repository requires a configured [private storage location]({{< ref "reference/deploy/private-locations/private-packages" >}}) to store build artifacts.
- **Private Locations**: Build from a Git repository is only compatible with [Private Locations]({{< ref "reference/deploy/private-locations/introduction" >}}). Ensure these are configured first.
- **Control Plane image**: Use the dedicated `gatlingcorp/control-plane:latest-builder` image. Note that this image is different from the standard Control Plane image.
- **CPU and memory resources**: Allocate adequate CPU and memory resources according to your project's compilation needs.
- **Git repository**: Git repository with a compatible Gatling plugin version configured.

## Architecture

The Build from a Git repository feature operates within the Gatling Enterprise architecture, leveraging the Control Plane to manage builds and simulations. The Control Plane interacts with your source repository to fetch simulation code, compiles it using the specified build tool, and stores the resulting artifacts in your private storage location. This process is triggered when you start a simulation in the Gatling Enterprise UI, allowing you to run tests directly from your source code without manual packaging.

After run completion, the test package is deleted from the private storage location, thus each run requires a fresh build from the source repository. This ensures that you always test the latest version of your simulations.



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
  }
}
```

**Security Best Practices:**
* Use personal access tokens instead of passwords whenever possible.
* Limit token permissions to read-only access.
* Never include credentials in repository URLs within Gatling Enterprise.

## Build tools for Git repositories

| Build Tool | Gatling Plugin Version | Image Cache Path | Tuning         |
| ---------: | ---------------------- | ---------------- | -------------- |
|  **Maven** | `4.16.3`               | `/app/.m2`       | `MAVEN_OPTS`   |
| **Gradle** | `3.13.5.4`             | `/app/.gradle`   | `GRADLE_OPTS`  |
|    **sbt** | `4.13.3`               | `/app/.sbt`      | `SBT_OPTS`     |
|    **npm** | `3.13.501`             | `/app/.npm`      | `NODE_OPTIONS` |

**Gatling Plugin Version**: The minimum compatible version of each build tool plugin that supports build from a Git repository.

**Image Cache Path**: Ensure build tool caches persist across different upgrades by mounting a volume to the given path.

**Tuning**: These environment variables can be in particular used to cap the memory used by each build process, so that your container doesn't crash with OOM when running multiple concurrent builds.
The memory of your container is used by:

- the Control Plane process
- the concurrent build processes

It's your responsibility to provide enough resources to your container and properly distribute the memory so that your container can handle your configured maximum number of concurrent build.

### Configuring Build Tool Memory Settings

Build tools (Maven, Gradle, and sbt) can be configured to use a specific maximum RAM quantity through their respective environment variables. You can set these environment variables through the Control Plane environment variables, and they will be transitively passed to the build tools during compilation.

#### Memory Configuration Examples

**Maven**: Use the `MAVEN_OPTS` environment variable to define JVM memory settings with values like `-Xms512m -Xmx2048m`.

```bash
# Example: Set Maven to use up to 2GB of RAM
MAVEN_OPTS="-Xms512m -Xmx2048m"
```

**Gradle**: Gradle configurations can be applied using the `GRADLE_OPTS` environment variable.

```bash
# Example: Set Gradle to use up to 2GB of RAM
GRADLE_OPTS="-Xms512m -Xmx2048m"
```

**sbt**: Use the `SBT_OPTS` environment variable to configure sbt memory settings.

```bash
# Example: Set sbt to use up to 2GB of RAM
SBT_OPTS="-Xms512m -Xmx2048m"
```

**npm**: Use the `NODE_OPTIONS` environment variable to configure npm memory settings.

```bash
# Example: Set npm to use up to 2GB of RAM
NODE_OPTIONS="-Xms512m -Xmx2048m"
```

#### Setting Environment Variables in Control Plane

When you define these build tool environment variables in the Control Plane container configuration, they are automatically passed through to the respective build tools during the build process. This transitive behavior ensures that memory settings are consistently applied without requiring additional configuration at the build tool level.

To configure these environment variables in your Control Plane deployment:

1. Set the appropriate environment variables (`MAVEN_OPTS`, `GRADLE_OPTS`, `SBT_OPTS` or `NODE_OPTIONS`) in your Control Plane container configuration
2. The Control Plane will automatically pass these variables to the build tools when compiling your Gatling simulations
3. Adjust the memory values based on your project's compilation requirements and available system resources

**Note**: Ensure that the Control Plane container has sufficient memory allocated to accommodate the maximum memory settings configured for your build tools.
