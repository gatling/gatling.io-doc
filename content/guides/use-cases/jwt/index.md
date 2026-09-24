---
title: Authenticate with JWT
menutitle: JWT authentication
seotitle: How to generate JWT tokens and sign HTTP requests in Gatling load tests
description: Generate JSON Web Tokens (JWT) in a Gatling simulation to authenticate virtual users and sign HTTP requests, with Java, Kotlin, and Scala examples.
lead: Mint JWTs inside your simulation so every virtual user authenticates like a real client, without putting load on your identity provider.
date: 2026-09-24T00:00:00+00:00
---

This guide shows two common JWT use cases:

- Generate a JWT per virtual user and send it as a bearer token.
- Sign each request with a JWT that covers its method, URL, and body.

The examples use [Nimbus JOSE + JWT](https://connect2id.com/products/nimbus-jose-jwt), a widely used Java library that supports all standard JWT algorithms and doesn't pull in any other dependencies.

## Meet the prerequisites

Before you begin, make sure you have the following:

- Gatling version `{{< var gatlingVersion >}}` or higher, with the Java, Kotlin, or Scala SDK
- The signing key your system under test accepts: a shared secret for HMAC algorithms such as `HS256`, or a private key for RSA algorithms such as `RS256`
- Basic understanding of [feeders]({{< ref "/concepts/session/feeders" >}}) and the [Session]({{< ref "/concepts/session" >}})

## Understand how JWT authentication works

A JWT is a compact string made of three Base64URL-encoded parts separated by dots: `header.payload.signature`.

- The **header** declares the signing algorithm, for example `HS256`, and optionally a key ID.
- The **payload** holds the claims, such as the subject (`sub`), the issuer (`iss`), the audience (`aud`), and the expiration time (`exp`).
- The **signature** proves that the holder of the signing key issued the token and that nobody modified it.

In production, an identity provider usually issues the tokens. During a load test, fetching a token for every virtual user can overload the identity provider or trip its rate limits, and it isn't the system you want to measure. If you can access the signing key, generate the tokens directly in your simulation instead.

{{< alert warning >}}
Never commit signing keys to version control. The examples in this guide read them from environment variables.
{{< /alert >}}

## Add the JWT library to your project

Add the `com.nimbusds:nimbus-jose-jwt` dependency to your Gatling project. Check [Maven Central](https://central.sonatype.com/artifact/com.nimbusds/nimbus-jose-jwt) for the latest version.

{{< include-file >}}
Maven: includes/add-dependency.maven.md
Gradle: includes/add-dependency.gradle.md
sbt: includes/add-dependency.sbt.md
{{< /include-file  >}}


The examples in this guide use the following imports:

{{< include-code "imports" java kt scala >}}

## Generate a JWT token

Generating a token takes three steps: create a signer from your key, build and sign the claims, and send the result in the `Authorization` header.

### Create a signer

A signer holds the key and computes signatures. Signers are thread-safe, so create a single instance and share it across all virtual users.

The following example creates an HMAC signer from a shared secret stored in the `JWT_SECRET` environment variable:

{{< include-code "hmac-signer" java kt scala >}}

### Build and sign the token

Build the claims your system under test expects, then sign them. Adjust the issuer, the audience, and any custom claims to match what your server validates.

{{< include-code "generate-token" java kt scala >}}

### Send the token in the Authorization header

Create a `users.csv` file in the `resources` folder of your Gatling project to give each virtual user its own identity:

```csv
userId
user1
user2
user3
```

The following scenario generates a token for each virtual user, stores it in the Session under the `jwt` key, and sends it as a bearer token with the [Gatling Expression Language]({{< ref "/concepts/session/el" >}}):

{{< include-code "use-token" java kt scala >}}

{{< alert tip >}}
The token expires 15 minutes after it's generated. If your virtual users run longer than that, regenerate the token before it expires, for example at the start of each loop iteration.
{{< /alert >}}

### Sign with an RSA private key

If your server verifies tokens with a public key, sign them with the matching private key and an asymmetric algorithm such as `RS256`. The following example loads a PKCS#8 PEM-encoded private key from the `JWT_PRIVATE_KEY` environment variable:

{{< include-code "rsa-signer" java kt scala >}}

If your key is in the PKCS#1 format (`-----BEGIN RSA PRIVATE KEY-----`), convert it to PKCS#8 first:

```console
openssl pkcs8 -topk8 -nocrypt -in private-key.pem -out private-key-pkcs8.pem
```

## Sign each request

Some APIs require a signature for each request instead of, or in addition to, a bearer token. The signature covers the request's content, so the server can reject any request that was modified or replayed.

Use the [`sign` method]({{< ref "/reference/script/http/protocol#sign" >}}) to compute the signature. Gatling calls your function once it has built the request, right before sending it, so the function has access to the final method, URL, headers, and body.

The following example reuses the HMAC signer from the previous section. For each request, it builds a JWT that contains the HTTP method (`htm`), the target URL (`htu`), and a SHA-256 hash of the body (`bsh`), then sends it in the `X-Request-Signature` header:

{{< include-code "sign-request" java kt scala >}}

The claim names and the header name depend on your API's contract. Adjust them to match what your server verifies.

{{< alert tip >}}
Call `sign` on the protocol to sign all requests, or on an [individual request]({{< ref "/reference/script/http/request#sign" >}}) to sign only that one. If the signature depends on the virtual user, for example a per-user key, use the variant that also gives you access to the Session.
{{< /alert >}}

## Next steps

- Learn more about the [HTTP protocol configuration]({{< ref "/reference/script/http/protocol" >}}).
- Load user identities with [feeders]({{< ref "/concepts/session/feeders" >}}).
- Create a load test with [basic authentication]({{< ref "/guides/use-cases/basic-auth" >}}).
