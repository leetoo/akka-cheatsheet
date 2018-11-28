name := "akka_recap"

version := "0.1"

scalaVersion := "2.12.7"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.18",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.18" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)