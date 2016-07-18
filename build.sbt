name := "WorkDayLength"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

lazy val http4sVersion = "0.13.2"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.1.0",
  "ch.qos.logback"             %  "logback-classic" % "1.1.7",

  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,

  "io.spray" %% "spray-json" % "1.3.2",

  "com.typesafe" % "config" % "1.3.0"
)


    