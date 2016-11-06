import sbt.Keys._

lazy val platformSettings = Seq(
  scalaVersion := "2.11.8",
  resolvers ++= Seq("spray repo" at "http://repo.spray.io"),
  libraryDependencies ++= {
    val akkaVersion = "2.4.12"
    val sprayVersion = "1.3.4"
    Seq("com.typesafe.akka" %% "akka-actor" % akkaVersion
      , "io.spray" %% "spray-can" % sprayVersion
      , "io.spray" %% "spray-routing" % sprayVersion
      , "io.spray" %% "spray-testkit" % sprayVersion
      , "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.2"
      , "com.github.nscala-time" %% "nscala-time" % "2.14.0"
      , "org.scalatest" %% "scalatest" % "3.0.0" % "test"
    )
  },
  javaOptions ++= Seq("-Djava.net.preferIPv4Stack=true"),
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-Xlint", "-Ywarn-dead-code", "-encoding", "UTF-8")
)

lazy val projectSettings = Seq(
  name := "countdown service",
  organization := "com.microworkflow",
  version := "0.0.2-SNAPSHOT"
)

lazy val root = (project in file("."))
  .settings(platformSettings ++ projectSettings: _*)
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoOptions += BuildInfoOption.ToJson
  )
