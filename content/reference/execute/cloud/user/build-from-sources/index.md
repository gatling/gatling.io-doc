---
title: Build from sources
seotitle: Gatling Enterprise Cloud Build from sources
description: Learn how to deploy your Gatling project on Gatling Enterprise Cloud by connecting a source repository.
date: 2024-03-10T14:29:04+00:00
---

# Introduction

Run simulations by simply plugging your git repository.

## Pre-requisites

- Private Repository: Build from Sources requires a configured [Private Repository]({{< ref "reference/install/cloud/private-locations/private-packages" >}}) to store build artifacts.
- Private Locations: Build from Sources is only compatible with [Private Locations]({{< ref "reference/install/cloud/private-locations/introduction" >}}). Ensure these are configured first.
- Control Plane image: Use `gatlingcorp/control-plane:latest-builder` instead of `gatlingcorp/control-plane:latest`.
- Allocate adequate CPU and memory resources according to your project's compilation needs.

## Configuration

### Git authentication

#### Cloning over SSH using an SSH key

**Mount SSH Configuration**

- Provide an SSH configuration mounted at `/app/.ssh` into the container.
- Ensure the mounted folder is owned by the Gatling user (UID 1001): `chown -R 1001 /app/.ssh`.

**Update SSH Config:**

Edit `/app/.ssh/config` to specify your identity file. For example (GitHub):

```
Host github.com
IdentityFile /app/.ssh/id_gatling
StrictHostKeyChecking no
```

**Set Key Permissions:**

Ensure `/app/.ssh/id_gatling` has permissions **644**:
- Owner: Read & Write
- Group: Read
- Others: Read

**Host Verification (Optional):**

If you set `StrictHostKeyChecking yes`, manually add hosts keys to `/app/.ssh/known_hosts`.

#### Cloning over HTTPS using git credentials

When using HTTPS Git authentication (with passwords or tokens):

**Configure Git Credential Storage:**  
Run this command on your builder to enable secure credential storage:

```bash
git config --global credential.helper store
```

The credentials file (`~/.git-credentials` on the host) is mounted at `/app/.git-credentials` into the container.

**Store Approved Credentials:**
Use Git's credential approval mechanism (never hardcode credentials in URLs):

```bash
git credential approve <<EOF
url=https://github.com/your-org/your-repo.git
username=your_username
password=your_token_or_password
EOF
```

**Security Best Practices:**
- Always use personal access tokens instead of passwords.
- Never include credentials in repository URLs within Gatling Enterprise.
- Review [Git credentials documentation](https://git-scm.com/docs/gitcredentials) for advanced configurations.

### Build cache

To ensure build tool caches persist across different builder upgrades, mount a volume for the following directories:
- Maven cache: `/app/.m2`
- Gradle cache: `/app/.gradle`
- SBT cache: `/app/.sbt`
- NPM cache: `/app/.npm`

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
- Configure private locations and other [simulation properties]({{< ref "reference/execute/cloud/user/simulations" >}})
- Save and launch your simulation

{{< img src="build-from-sources-simulation-configure.png" alt="Create Simulation from Git Repository" >}}
