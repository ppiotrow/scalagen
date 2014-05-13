import sbt._
import sbt.Keys._

object ScalagenBuild extends Build {

  scalaVersion := "2.10.4"

	lazy val root = Project(
		id = "root",
		base = file("."),
		aggregate = Seq(core, example)
	)

	lazy val core = Project(
		id = "scalagen-core",
		base = file("scalagen-core"))
    .settings(
        name := "scalagen",
        version := "0.1.0-SNAPSHOT",
        libraryDependencies++=Dependencies.core,
        scalacOptions ++= Seq("-feature"))

	lazy val example =  Project(
		id = "scalagen-examples",
		base = file("scalagen-examples"))
    .dependsOn(core)
    .settings(
        libraryDependencies++=Dependencies.examples
    )


  object Dependencies {
    object Compile {
      val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.3.2"
      val apacheCommons = "org.apache.commons" % "commons-lang3" % "3.0"
      val jodaTime = "joda-time" % "joda-time" % "2.3"
      val jodaConvert = "org.joda" % "joda-convert" % "1.6"
    }

    object Test {
      val akkaTestkit ="com.typesafe.akka" %% "akka-testkit" % "2.3.2" % "test"
      val scalaTest = "org.scalatest" %% "scalatest" % "2.1.4" % "test"
      val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
    }

    import Compile._

    val core = Seq(akkaActor, jodaTime, jodaConvert, Test.akkaTestkit, Test.scalaTest, Test.scalaCheck)
    val examples = Seq(apacheCommons)

  }
}
