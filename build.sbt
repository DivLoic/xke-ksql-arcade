name := "xke-ksql-arcade"

version := "0.1"

scalaVersion := "2.12.5"

val ksLightbendVersion = "0.2.1"
val akkaKafkaVersion = "0.20"
val catsVersion = "1.0.0-MF"
val checkVersion = "1.14.0"
val logbackVersion = "1.2.3"
val pureVersion = "0.9.1"
val json4sVersion = "3.5.3"


scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-Xlint",
  "-feature",
  "-Xfatal-warnings",
  "-Ypartial-unification"
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.json4s" %% "json4s-native" % json4sVersion,
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "org.scalacheck" %% "scalacheck" % checkVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.github.pureconfig" %% "pureconfig" % pureVersion,
  "com.lightbend" %% "kafka-streams-scala" % ksLightbendVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % akkaKafkaVersion)
