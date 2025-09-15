---
title: Installation with Kubernetes
seotitle: Kubernetes install for Gatling Enterprise Edition Self-Hosted
description: Learn how to install Gatling Enterprise Edition with Kubernetes.
lead: Install Gatling Enterprise Edition and Cassandra easily with Kubernetes.
date: 2021-03-26T17:37:11+01:00
---

If you're using Kubernetes-based injector pools, it is recommended to run Gatling Enterprise Edition itself inside Kubernetes too:

* Less configuration is required than when connecting to a cluster from the outside
* It is not necessary to open firewall rules so that Gatling Enterprise Edition can contact injectors

We currently support the `linux/amd64` and `linux/arm64` platforms and publish our official images for both (Docker will automatically select the correct image variant).

## Getting Gatling Enterprise Edition's Docker image

Gatling Enterprise Edition's image is hosted as a private image on [Docker Hub](https://hub.docker.com/r/gatlingcorp/frontline).

Please contact our support and provide us with your Docker Hub username so we can grant you access.

## Getting Gatling Enterprise Edition's injectors Docker image

Gatling Enterprise Edition's injector image is publicly accessible on [Docker Hub](https://hub.docker.com/r/gatlingcorp/frontline-injector).

## Setup Cassandra

This manifest setups a single-node Cassandra cluster, along with a service to expose it

{{< include-code "kubernetes-cassandra.yml" yaml >}}

## Setup Gatling Enterprise Edition

### Setup RBAC (optional)

If your cluster has RBAC enabled, this manifest configures the necessary permissions for Gatling Enterprise Edition:

{{< include-code "kubernetes-frontline.yml" yaml >}}

### Setup Docker Hub credentials as a secret (Optional)

If you're not mirroring Gatling Enterprise Edition's image in your private registry, you'll need to setup your Docker credentials as a secret to pull Gatling Enterprise Edition's image:

```shell
kubectl create secret docker-registry docker-hub-credentials \
  --docker-server=<your-registry-server> \
  --docker-username=<your-name> \
  --docker-password=<your-pword> \
  --docker-email=<your-email>
```

### Setup Gatling Enterprise Edition

This manifest sets up Gatling Enterprise Edition, pre configured with your license key and admin credentials.
You can then expose Gatling Enterprise Edition using LoadBalancer/NodePort services, Ingress, etc...

Gatling Enterprise Edition requires two persistent volumes:
- `frontline-conf` contains your Gatling Enterprise Edition configuration, license key included.
- `ssh-keys` contains all your SSH private keys, possibly used for cloning git repositories and deploying tests on remote virtual machines.

In the example below, `configMap` and `hostPath` are used, but you can use `secret` or setup [persistent volumes](https://kubernetes.io/docs/concepts/storage/persistent-volumes/).

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: frontline-conf
  namespace: frontline
data:
  frontline.conf: |
    frontline.licenseKey = <YOUR FRONTLINE LICENSE KEY>
    frontline.security.superAdminPassword = <YOUR SUPER ADMIN PASSWORD>
    frontline.security.secretKey = <YOUR ENCRYPTION SECRET KEY>
    frontline.cassandra.basic.contact-points = ["cassandra.frontline.svc.cluster.local:9042"]
  logback.xml: |
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
      <contextListener class="ch.qos.logback.classic.jul.LevelChangePropagator">
        <resetJUL>true</resetJUL>
      </contextListener>
      <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
          <pattern>%d [%-5level] %logger{15} - %msg%n%rEx</pattern>
        </encoder>
        <immediateFlush>false</immediateFlush>
      </appender>
      <logger name="io.gatling.frontline" level="INFO"/>
      <root level="WARN">
        <appender-ref ref="CONSOLE"/>
      </root>
    </configuration>
---
apiVersion: v1
kind: Service
metadata:
  name: frontline
  namespace: frontline
spec:
  ports:
    - name: http
      port: 10542
  selector:
    app: frontline
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontline
  namespace: frontline
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: frontline
  template:
    metadata:
      labels:
        app: frontline
    spec:
      # Required unless you mirror Gatling Enterprise Edition in your private registry
      imagePullSecrets:
        - name: docker-hub-credentials
      serviceAccountName: frontline-sa
      containers:
        - name: frontline
          imagePullPolicy: Never
          image: gatlingcorp/frontline:{{< var selfHostedVersion >}}
          resources:
            requests:
              cpu: 2
              memory: 4Gi
          ports:
            - containerPort: 10542
          volumeMounts:
            - mountPath: /opt/frontline/conf
              name: frontline-conf
            - mountPath: /opt/frontline/keys
              name: ssh-keys
      volumes:
        - name: frontline-conf
          configMap:
            name: frontline-conf
        - name: ssh-keys
          # Prefer PersistentVolumeClaims for durability
          hostPath:
            path: <local storage path for SSH keys>
```

{{< alert tip >}}
Depending on your needs, you may need to configure additional volumes on the Gatling Enterprise Edition container (SSL certificate if HTTPS is configured, or keystore/truststore for LDAP support)
{{< /alert >}}
