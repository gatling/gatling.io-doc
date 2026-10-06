import sbt.*
import sbt.io.ExtensionFilter
import sbt.Keys.*
import sbtheader.FileType
import _root_.io.gatling.build.license.ApacheV2License
import org.jetbrains.sbt.kotlin.Keys.*

private val gatlingVersion = "3.16.0"
private val gatlingGrpcVersion = "3.16.0"
private val gatlingMqttVersion = "3.16.0"
private val gatlingGraphqlVersion = "3.16.0.4"
private val awsSdkVersion = "2.55.10"

lazy val root = rootProject
  .enablePlugins(
    GatlingCompilerSettingsPlugin,
    KotlinPlugin,
    GatlingAutomatedScalafmtPlugin,
    AutomateHeaderPlugin
  )
  .settings(
    Test / unmanagedSourceDirectories ++= (baseDirectory.value / "content" ** "code").get(),
    // Scala
    scalaVersion := "2.13.18",
    scalafmtOnCompile := false,
    // Java
    Compile / javacOptions ++= Seq("-encoding", "utf8", "--release", "17"),
    Test / javacOptions ++= Seq("-encoding", "utf8", "-Xlint:unchecked"),
    // Kotlin
    kotlinVersion := "2.4.20",
    kotlincJvmTarget := "11",
    // Headers
    headerLicense := ApacheV2License,
    headerMappings ++= Map(
      FileType("kt") -> HeaderCommentStyle.cStyleBlockComment,
      FileType("ts") -> HeaderCommentStyle.cStyleBlockComment
    ),
    headerSources / includeFilter := new ExtensionFilter("java", "scala", "kt", "ts"),
    // Dependencies
    libraryDependencies ++= Seq(
      // Gatling modules
      "io.gatling" % "gatling-core-java"  % gatlingVersion,
      "io.gatling" % "gatling-http-java"  % gatlingVersion,
      "io.gatling" % "gatling-jms-java"   % gatlingVersion,
      "io.gatling" % "gatling-jdbc-java"  % gatlingVersion,
      "io.gatling" % "gatling-redis-java" % gatlingVersion,
      // External Gatling modules
      "io.gatling" % "gatling-grpc-java"    % gatlingGrpcVersion,
      "io.gatling" % "gatling-mqtt-java"    % gatlingMqttVersion,
      "io.gatling" % "gatling-graphql-java" % gatlingGraphqlVersion,
      // Other
      "org.apache.commons"     % "commons-lang3"   % "3.21.0",
      "commons-codec"          % "commons-codec"   % "1.22.1",
      "software.amazon.awssdk" % "secretsmanager"  % awsSdkVersion,
      "software.amazon.awssdk" % "s3"              % awsSdkVersion,
      "org.apache.activemq"    % "activemq-broker" % "6.3.2",
      "com.nimbusds"           % "nimbus-jose-jwt" % "10.10"
    )
  )
