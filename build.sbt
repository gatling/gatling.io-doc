import de.heikoseeberger.sbtheader.FileType
import sbt._
import sbt.io.ExtensionFilter
import sbt.Keys._
import _root_.io.gatling.build.license.ApacheV2License

kotlinVersion := "2.1.10"
scalaVersion := "2.13.16"

enablePlugins(GatlingAutomatedScalafmtPlugin)
scalafmtOnCompile := false

enablePlugins(GatlingCompilerSettingsPlugin)

enablePlugins(AutomateHeaderPlugin)
headerLicense := ApacheV2License
headerMappings ++= Map(
  FileType("kt") -> HeaderCommentStyle.cStyleBlockComment,
  FileType("ts") -> HeaderCommentStyle.cStyleBlockComment
)
headerSources / includeFilter := new ExtensionFilter("java", "scala", "kt", "ts")

Compile / javacOptions ++= Seq("-encoding", "utf8")
Test / javacOptions ++= Seq("-encoding", "utf8")
Test / javacOptions += "-Xlint:unchecked"
Test / unmanagedSourceDirectories ++= (baseDirectory.value / "content" ** "code").get

// Dependencies

val gatlingVersion = "3.13.5"
val gatlingGrpcVersion = "3.13.5"
val gatlingMqttVersion = "3.13.5"
val awsSdkVersion = "2.30.38"

libraryDependencies ++= Seq(
  // Gatling modules
  "io.gatling" % "gatling-core-java"  % gatlingVersion,
  "io.gatling" % "gatling-http-java"  % gatlingVersion,
  "io.gatling" % "gatling-jms-java"   % gatlingVersion,
  "io.gatling" % "gatling-jdbc-java"  % gatlingVersion,
  "io.gatling" % "gatling-redis-java" % gatlingVersion,
  // External Gatling modules
  "io.gatling" % "gatling-grpc-java" % gatlingGrpcVersion,
  "io.gatling" % "gatling-mqtt-java" % gatlingMqttVersion,
  // Other
  "org.apache.commons"     % "commons-lang3"   % "3.17.0",
  "commons-codec"          % "commons-codec"   % "1.18.0",
  "software.amazon.awssdk" % "secretsmanager"  % awsSdkVersion,
  "software.amazon.awssdk" % "s3"              % awsSdkVersion,
  ("org.apache.activemq"   % "activemq-broker" % "5.19.0" % Test)
    .exclude("jakarta.jms", "jakarta.jms-api")
)
