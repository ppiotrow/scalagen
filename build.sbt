name := "scalagen"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.4"

// Compile
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.2"
)

// Test
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.3.2" % "test",
  "org.scalatest" %% "scalatest" % "2.1.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
)
