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
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.Date
import java.util.UUID
//#imports

class JwtSampleKotlin {

  object HmacToken {
//#hmac-signer
// Read the shared secret from an environment variable instead of hard-coding it.
// HS256 requires a secret of at least 256 bits (32 bytes).
private val signer: JWSSigner = MACSigner(
  requireNotNull(System.getenv("JWT_SECRET")) { "JWT_SECRET is not set" }
)
//#hmac-signer

//#generate-token
private fun generateToken(subject: String): String {
  val now = Instant.now()
  val claims = JWTClaimsSet.Builder()
    .issuer("gatling")
    .audience("https://api.example.com")
    .subject(subject)
    .issueTime(Date.from(now))
    .expirationTime(Date.from(now.plus(Duration.ofMinutes(15))))
    // unique token ID, in case the server rejects replayed tokens
    .jwtID(UUID.randomUUID().toString())
    .build()

  val jwt = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claims)
  jwt.sign(signer)
  // compact form: header.payload.signature
  return jwt.serialize()
}
//#generate-token

//#use-token
val httpProtocol = http
  .baseUrl("https://api.example.com")
  .acceptHeader("application/json")

val usersFeeder = csv("users.csv").circular()

val scn = scenario("JWT authentication")
  .feed(usersFeeder)
  // generate a token for the virtual user and store it in its Session
  .exec { session -> session.set("jwt", generateToken(session.getString("userId")!!)) }
  .exec(
    http("Get profile")
      .get("/api/profile")
      .header("Authorization", "Bearer #{jwt}")
      .check(status().`is`(200)),
    http("Update profile")
      .put("/api/profile")
      .header("Authorization", "Bearer #{jwt}")
      .body(StringBody("""{"displayName": "#{userId}"}"""))
      .asJson()
      .check(status().`is`(200))
  )
//#use-token
  }

  object RsaToken {
//#rsa-signer
// Read a PKCS#8 PEM-encoded private key ("-----BEGIN PRIVATE KEY-----") from an environment variable
private val signer: JWSSigner = RSASSASigner(
  loadPrivateKey(requireNotNull(System.getenv("JWT_PRIVATE_KEY")) { "JWT_PRIVATE_KEY is not set" })
)

private fun loadPrivateKey(pem: String): PrivateKey {
  val base64 = pem
    .replace("-----BEGIN PRIVATE KEY-----", "")
    .replace("-----END PRIVATE KEY-----", "")
    .replace(Regex("\\s"), "")
  return KeyFactory.getInstance("RSA")
    .generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64)))
}

private fun generateToken(subject: String): String {
  val now = Instant.now()
  val claims = JWTClaimsSet.Builder()
    .issuer("gatling")
    .audience("https://api.example.com")
    .subject(subject)
    .issueTime(Date.from(now))
    .expirationTime(Date.from(now.plus(Duration.ofMinutes(15))))
    .jwtID(UUID.randomUUID().toString())
    .build()

  // the key ID tells the server which public key to verify the signature with
  val header = JWSHeader.Builder(JWSAlgorithm.RS256).keyID("gatling-key").build()
  val jwt = SignedJWT(header, claims)
  jwt.sign(signer)
  return jwt.serialize()
}
//#rsa-signer
  }

  object SignRequest {
    private val signer: JWSSigner = MACSigner(
      requireNotNull(System.getenv("JWT_SECRET")) { "JWT_SECRET is not set" }
    )

//#sign-request
val httpProtocol = http
  .baseUrl("https://api.example.com")
  // runs once Gatling has built the request, right before sending it
  .sign { request ->
    val body = request.body?.bytes ?: ByteArray(0)
    val claims = JWTClaimsSet.Builder()
      .issuer("gatling")
      .issueTime(Date())
      .jwtID(UUID.randomUUID().toString())
      // bind the signature to this exact request
      .claim("htm", request.method.name())
      .claim("htu", request.uri.toUrl())
      .claim("bsh", sha256(body))
      .build()
    request.headers.set("X-Request-Signature", sign(claims))
    request
  }

private fun sha256(bytes: ByteArray): String =
  Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(bytes)).toString()

private fun sign(claims: JWTClaimsSet): String {
  val jwt = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claims)
  jwt.sign(signer)
  return jwt.serialize()
}
//#sign-request
  }
}
