import de.heikoseeberger.sbtheader.FileType
import sbt._
import sbt.io.ExtensionFilter
import sbt.Keys._
import _root_.io.gatling.build.license.ApacheV2License

kotlinVersion := "2.2.20"
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
Compile / javacOptions ++= Seq("--release", "17")
Test / javacOptions ++= Seq("-encoding", "utf8")
Test / javacOptions += "-Xlint:unchecked"
Test / unmanagedSourceDirectories ++= (baseDirectory.value / "content" ** "code").get

// Dependencies

val gatlingVersion = "3.14.4"
val gatlingGrpcVersion = "3.14.4"
val gatlingMqttVersion = "3.14.4"
val awsSdkVersion = "2.34.1"

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
  "org.apache.commons"     % "commons-lang3"   % "3.18.0",
  "commons-codec"          % "commons-codec"   % "1.19.0",
  "software.amazon.awssdk" % "secretsmanager"  % awsSdkVersion,
  "software.amazon.awssdk" % "s3"              % awsSdkVersion,
  "org.apache.activemq"    % "activemq-broker" % "6.1.7"
)
