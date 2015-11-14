name := "countdown service"

organization := "com.microworkflow"

scalaVersion := "2.11.7"

version := "0.0.1-SNAPSHOT"

resolvers ++= Seq("spray repo" at "http://repo.spray.io")

libraryDependencies ++= {
 val akkaVersion = "2.4.0"
 val sprayVersion = "1.3.3"
 Seq( "com.typesafe.akka" %% "akka-actor" % akkaVersion
 	, "io.spray" %% "spray-can" % sprayVersion
 	, "io.spray" %% "spray-routing" % sprayVersion
 	, "io.spray" %% "spray-testkit" % sprayVersion
 	, "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.3"
 	, "org.scalatest" %% "scalatest" % "2.2.4" % "test"
 )
 }

javaOptions ++= Seq("-Djava.net.preferIPv4Stack=true")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-Xlint", "-Ywarn-dead-code", "-encoding", "UTF-8")

enablePlugins(JavaAppPackaging)
