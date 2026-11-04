---
title: Trust a custom CA on Kubernetes load generators
menutitle: Custom CA certificates
seotitle: Trust a custom certificate authority on Kubernetes and OpenShift load generators in Gatling Enterprise Edition
description: Make Kubernetes and OpenShift load generators trust your enterprise certificate authority with a ConfigMap, without building a custom image.
lead: Make your Kubernetes and OpenShift load generators trust your enterprise certificate authority, without building a custom image.
date: 2026-11-04T00:00:00+00:00
---

Your load generators may need to reach HTTPS endpoints whose certificates are signed by your enterprise certificate authority (CA),
for example a [private package repository]({{< ref "reference/deploy/private-locations/private-packages" >}}) exposed over HTTPS.
This guide explains how to make them trust that CA with configuration only: you keep using the certified images.

It applies to Kubernetes and OpenShift. Commands are given for both `kubectl` and `oc`.

## Which clients need to trust your CA

A load generator makes HTTPS calls from two places:

* **The startup script (curl).** It downloads the simulation package, for example from your private package repository, and talks to Gatling Enterprise.
  It needs a **PEM bundle**.
* **The JVM running your simulation.** It needs a **PKCS12 truststore**, but only if your simulations use clients that validate certificates with the JVM default truststore,
  such as Kafka, JDBC, JMS, gRPC, MQTT or a `java.net.http.HttpClient` in your own code, or if you configured a truststore in `gatling.conf`.
  The Gatling HTTP client doesn't validate certificates by default (see [TLS]({{< ref "reference/script/http/tls#configuring-keystore-and-truststore" >}})),
  so a simulation that only uses it doesn't need this truststore.

Each file replaces the default list of trusted CAs, so it must contain **the default public CAs plus your enterprise CA**.

## Build the trust files

Extract the default trust files from the load generator image. The following commands use the `gatlingcorp/classic-openjdk` image;
adapt the image name and paths if you use another image.

```console
docker create --name lg gatlingcorp/classic-openjdk:latest
docker cp lg:/etc/ssl/certs/ca-certificates.crt ./system-ca.crt
docker cp -L lg:/usr/lib/jvm/zulu/lib/security/cacerts ./cacerts
docker rm lg
```

Build the PEM bundle for curl, where `enterprise-ca.pem` is your enterprise CA certificate:

```console
cat system-ca.crt enterprise-ca.pem > ca-bundle.pem
```

If you need the JVM truststore, build it as a PKCS12 file without password:

```console
keytool -J-Dkeystore.pkcs12.certProtectionAlgorithm=NONE -J-Dkeystore.pkcs12.macAlgorithm=NONE \
  -importkeystore -srckeystore cacerts -srcstorepass changeit \
  -destkeystore truststore.p12 -deststoretype PKCS12 -deststorepass changeit
keytool -J-Dkeystore.pkcs12.certProtectionAlgorithm=NONE -J-Dkeystore.pkcs12.macAlgorithm=NONE \
  -importcert -noprompt -alias enterprise-ca -file enterprise-ca.pem \
  -keystore truststore.p12 -storetype PKCS12 -storepass changeit
```

`keytool` requires a store password, but the `-J-D...` options store the certificates unencrypted and without integrity check:
`changeit` is never used, and the JVM can read the truststore without password.

Importing your root CA is enough, as long as your servers send the full certificate chain.
If you have several root CAs, repeat the second command with a different alias for each.

## Store the trust files in the cluster

Create a ConfigMap in the namespace where the load generators run.
Leave out `truststore.p12` if you don't need the JVM truststore.

{{< code-toggle key="k8s-cli" >}}
Kubernetes: kubectl create configmap gatling-truststores --from-file=ca-bundle.pem --from-file=truststore.p12 -n <namespace>
OpenShift: oc create configmap gatling-truststores --from-file=ca-bundle.pem --from-file=truststore.p12 -n <namespace>
{{</ code-toggle >}}

If your policies require a Secret, use `create secret generic` instead, and `secret.secretName` instead of `configMap.name` in the job definition below.

## Configure the location

{{< alert warning >}}
The `truststore` setting belongs to the `proxy` block for historical reasons, but it doesn't require a proxy:
the file is passed to curl with `--cacert` whether or not `proxy.http` is set.
The way to configure custom certificates may change in a future version, if we find a better way to handle them.
{{< /alert >}}

The trust files must be mounted in the load generator container, which requires a custom job definition (the `job` field).
The control plane adds its own volumes to the container of your job definition and keeps the ones you define.

Add the following settings to your Kubernetes location in the [control plane configuration file]({{< ref "reference/deploy/private-locations/kubernetes/configuration#control-plane-configuration-file" >}}):

```bash
locations = [
  {
    id = "prl_kubernetes"
    type = "kubernetes"
    # ...
    enterprise-cloud {
      proxy {
        # No proxy URL needed: this file is passed to curl as --cacert
        truststore.path = "/etc/gatling/trust/ca-bundle.pem"
      }
    }
    # Only if you need the JVM truststore
    system-properties {
      "javax.net.ssl.trustStore" = "/etc/gatling/trust/truststore.p12"
      "javax.net.ssl.trustStoreType" = "PKCS12"
    }
    job {
      apiVersion = "batch/v1"
      kind = "Job"
      spec.template.spec {
        volumes = [{ name = "gatling-trust", configMap.name = "gatling-truststores" }]
        containers = [{
          volumeMounts = [{ name = "gatling-trust", mountPath = "/etc/gatling/trust", readOnly = true }]
        }]
      }
    }
  }
]
```

## Limitations

* **Snapshot of the public CAs.** The trust files freeze the default CA list at the time you build them, and don't follow later changes in the image.
  Certified images (`type = certified`) are updated transparently, so you won't be notified when their CA list changes.
  Rebuild the trust files regularly, when your enterprise CA rotates, and whenever load generators fail to connect to public HTTPS endpoints.
* **Custom job definition required.** The basic Kubernetes location configuration can't mount extra volumes.
* **Truststore password.** If you build the JVM truststore with a password (without the `-J-D...` flags), you must also set the
  `javax.net.ssl.trustStorePassword` system property. Without it, the JVM trusts no certificate at all.
