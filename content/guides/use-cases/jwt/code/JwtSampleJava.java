/*
 * Copyright 2011-2026 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//#imports
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
//#imports

class JwtSampleJava {

  static class HmacToken {
//#hmac-signer
// Read the shared secret from an environment variable instead of hard-coding it.
// HS256 requires a secret of at least 256 bits (32 bytes).
private static final JWSSigner SIGNER;

static {
  try {
    SIGNER = new MACSigner(System.getenv("JWT_SECRET"));
  } catch (KeyLengthException e) {
    throw new IllegalStateException("JWT_SECRET must be at least 32 bytes long", e);
  }
}
//#hmac-signer

//#generate-token
private static String generateToken(String subject) {
  Instant now = Instant.now();
  JWTClaimsSet claims = new JWTClaimsSet.Builder()
    .issuer("gatling")
    .audience("https://api.example.com")
    .subject(subject)
    .issueTime(Date.from(now))
    .expirationTime(Date.from(now.plus(Duration.ofMinutes(15))))
    // unique token ID, in case the server rejects replayed tokens
    .jwtID(UUID.randomUUID().toString())
    .build();

  SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
  try {
    jwt.sign(SIGNER);
  } catch (JOSEException e) {
    throw new IllegalStateException("Failed to sign JWT", e);
  }
  // compact form: header.payload.signature
  return jwt.serialize();
}
//#generate-token

//#use-token
HttpProtocolBuilder httpProtocol = http
  .baseUrl("https://api.example.com")
  .acceptHeader("application/json");

FeederBuilder<String> usersFeeder = csv("users.csv").circular();

ScenarioBuilder scn = scenario("JWT authentication")
  .feed(usersFeeder)
  // generate a token for the virtual user and store it in its Session
  .exec(session -> session.set("jwt", generateToken(session.getString("userId"))))
  .exec(
    http("Get profile")
      .get("/api/profile")
      .header("Authorization", "Bearer #{jwt}")
      .check(status().is(200)),
    http("Update profile")
      .put("/api/profile")
      .header("Authorization", "Bearer #{jwt}")
      .body(StringBody("{\"displayName\": \"#{userId}\"}"))
      .asJson()
      .check(status().is(200))
  );
//#use-token
  }

  static class RsaToken {
//#rsa-signer
// Read a PKCS#8 PEM-encoded private key ("-----BEGIN PRIVATE KEY-----") from an environment variable
private static final JWSSigner SIGNER = new RSASSASigner(loadPrivateKey(System.getenv("JWT_PRIVATE_KEY")));

private static PrivateKey loadPrivateKey(String pem) {
  String base64 = pem
    .replace("-----BEGIN PRIVATE KEY-----", "")
    .replace("-----END PRIVATE KEY-----", "")
    .replaceAll("\\s", "");
  try {
    return KeyFactory.getInstance("RSA")
      .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64)));
  } catch (GeneralSecurityException e) {
    throw new IllegalStateException("Invalid RSA private key", e);
  }
}

private static String generateToken(String subject) {
  Instant now = Instant.now();
  JWTClaimsSet claims = new JWTClaimsSet.Builder()
    .issuer("gatling")
    .audience("https://api.example.com")
    .subject(subject)
    .issueTime(Date.from(now))
    .expirationTime(Date.from(now.plus(Duration.ofMinutes(15))))
    .jwtID(UUID.randomUUID().toString())
    .build();

  // the key ID tells the server which public key to verify the signature with
  JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("gatling-key").build();
  SignedJWT jwt = new SignedJWT(header, claims);
  try {
    jwt.sign(SIGNER);
  } catch (JOSEException e) {
    throw new IllegalStateException("Failed to sign JWT", e);
  }
  return jwt.serialize();
}
//#rsa-signer
  }

  static class SignRequest {
    private static final JWSSigner SIGNER;

    static {
      try {
        SIGNER = new MACSigner(System.getenv("JWT_SECRET"));
      } catch (KeyLengthException e) {
        throw new IllegalStateException("JWT_SECRET must be at least 32 bytes long", e);
      }
    }

//#sign-request
HttpProtocolBuilder httpProtocol = http
  .baseUrl("https://api.example.com")
  // runs once Gatling has built the request, right before sending it
  .sign(request -> {
    byte[] body = request.getBody() != null ? request.getBody().getBytes() : new byte[0];
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
      .issuer("gatling")
      .issueTime(new Date())
      .jwtID(UUID.randomUUID().toString())
      // bind the signature to this exact request
      .claim("htm", request.getMethod().name())
      .claim("htu", request.getUri().toUrl())
      .claim("bsh", sha256(body))
      .build();
    request.getHeaders().set("X-Request-Signature", sign(claims));
    return request;
  });

private static String sha256(byte[] bytes) {
  try {
    return Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(bytes)).toString();
  } catch (NoSuchAlgorithmException e) {
    throw new IllegalStateException(e);
  }
}

private static String sign(JWTClaimsSet claims) {
  SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
  try {
    jwt.sign(SIGNER);
  } catch (JOSEException e) {
    throw new IllegalStateException("Failed to sign request", e);
  }
  return jwt.serialize();
}
//#sign-request
  }
}
