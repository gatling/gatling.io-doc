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
import java.security.{ KeyFactory, MessageDigest, PrivateKey }
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.util.{ Base64, Date, UUID }

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import com.nimbusds.jose.{ JWSAlgorithm, JWSHeader, JWSSigner }
import com.nimbusds.jose.crypto.{ MACSigner, RSASSASigner }
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.{ JWTClaimsSet, SignedJWT }
//#imports

class JwtSampleScala {

  object HmacToken {
//#hmac-signer
// Read the shared secret from an environment variable instead of hard-coding it.
// HS256 requires a secret of at least 256 bits (32 bytes).
private val signer: JWSSigner = new MACSigner(
  sys.env.getOrElse("JWT_SECRET", throw new IllegalStateException("JWT_SECRET is not set"))
)
//#hmac-signer

//#generate-token
private def generateToken(subject: String): String = {
  val now = Instant.now()
  val claims = new JWTClaimsSet.Builder()
    .issuer("gatling")
    .audience("https://api.example.com")
    .subject(subject)
    .issueTime(Date.from(now))
    .expirationTime(Date.from(now.plusSeconds(15.minutes.toSeconds)))
    // unique token ID, in case the server rejects replayed tokens
    .jwtID(UUID.randomUUID().toString)
    .build()

  val jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims)
  jwt.sign(signer)
  // compact form: header.payload.signature
  jwt.serialize()
}
//#generate-token

//#use-token
val httpProtocol = http
  .baseUrl("https://api.example.com")
  .acceptHeader("application/json")

val usersFeeder = csv("users.csv").circular

val scn = scenario("JWT authentication")
  .feed(usersFeeder)
  // generate a token for the virtual user and store it in its Session
  .exec(session => session.set("jwt", generateToken(session("userId").as[String])))
  .exec(
    http("Get profile")
      .get("/api/profile")
      .header("Authorization", "Bearer #{jwt}")
      .check(status.is(200)),
    http("Update profile")
      .put("/api/profile")
      .header("Authorization", "Bearer #{jwt}")
      .body(StringBody("""{"displayName": "#{userId}"}"""))
      .asJson
      .check(status.is(200))
  )
//#use-token
  }

  object RsaToken {
//#rsa-signer
// Read a PKCS#8 PEM-encoded private key ("-----BEGIN PRIVATE KEY-----") from an environment variable
private val signer: JWSSigner = new RSASSASigner(
  loadPrivateKey(sys.env.getOrElse("JWT_PRIVATE_KEY", throw new IllegalStateException("JWT_PRIVATE_KEY is not set")))
)

private def loadPrivateKey(pem: String): PrivateKey = {
  val base64 = pem
    .replace("-----BEGIN PRIVATE KEY-----", "")
    .replace("-----END PRIVATE KEY-----", "")
    .replaceAll("\\s", "")
  KeyFactory
    .getInstance("RSA")
    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder.decode(base64)))
}

private def generateToken(subject: String): String = {
  val now = Instant.now()
  val claims = new JWTClaimsSet.Builder()
    .issuer("gatling")
    .audience("https://api.example.com")
    .subject(subject)
    .issueTime(Date.from(now))
    .expirationTime(Date.from(now.plusSeconds(15.minutes.toSeconds)))
    .jwtID(UUID.randomUUID().toString)
    .build()

  // the key ID tells the server which public key to verify the signature with
  val header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("gatling-key").build()
  val jwt = new SignedJWT(header, claims)
  jwt.sign(signer)
  jwt.serialize()
}
//#rsa-signer
  }

  object SignRequest {
    private val signer: JWSSigner = new MACSigner(
      sys.env.getOrElse("JWT_SECRET", throw new IllegalStateException("JWT_SECRET is not set"))
    )

//#sign-request
val httpProtocol = http
  .baseUrl("https://api.example.com")
  // runs once Gatling has built the request, right before sending it
  .sign { (request, _) =>
    val body = Option(request.getBody).map(_.getBytes).getOrElse(Array.emptyByteArray)
    val claims = new JWTClaimsSet.Builder()
      .issuer("gatling")
      .issueTime(new Date())
      .jwtID(UUID.randomUUID().toString)
      // bind the signature to this exact request
      .claim("htm", request.getMethod.name)
      .claim("htu", request.getUri.toUrl)
      .claim("bsh", sha256(body))
      .build()
    request.getHeaders.set("X-Request-Signature", sign(claims))
    request
  }

private def sha256(bytes: Array[Byte]): String =
  Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(bytes)).toString

private def sign(claims: JWTClaimsSet): String = {
  val jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims)
  jwt.sign(signer)
  jwt.serialize()
}
//#sign-request
  }
}
