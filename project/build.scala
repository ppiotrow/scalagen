import sbt._
import sbt.Keys._

object ScalagenBuild extends Build {

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
      scalaVersion := "2.11.2",
      version := "0.4.0",
      organization := "com.github.scalagen",
      libraryDependencies ++= Dependencies.core,
      scalacOptions ++= Seq("-feature"))
    .settings(Publishing.publishSettings: _*)

	lazy val example =  Project(
		id = "scalagen-examples",
		base = file("scalagen-examples"))
    .dependsOn(core)
    .settings(
      scalaVersion := "2.11.2",
      libraryDependencies++=Dependencies.examples
    )
}

object Dependencies {
  object Compile {
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.3.4"
    val jodaTime = "joda-time" % "joda-time" % "2.3"
    val jodaConvert = "org.joda" % "joda-convert" % "1.6"
  }

  object Test {
    val akkaTestkit ="com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test"
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.0-M1" % "test"
    val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
  }

  import Compile._

  val core = Seq(akkaActor, jodaTime, jodaConvert, Test.akkaTestkit, Test.scalaTest, Test.scalaCheck)
  val examples = Seq()

}

object Publishing {


  def publishSettings: Seq[Setting[_]] = Seq(

    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    pomIncludeRepository := { x => false },

    homepage := Some(url("http://github.com/ppiotrow/scalagen")),

    licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"),

    scmInfo := Some(ScmInfo(url("http://github.com/ppiotrow/scalagen"), "scm:git@github.com:ppiotrow/scalagen.git")),

    pomExtra :=
      <developers>
        <developer>
          <id>ppiotrow</id>
          <name>Przemek Piotrowski</name>
          <url>pl.linkedin.com/in/ppiotrow</url>
        </developer>
        <developer>
          <id>tomrozb</id>
          <name>Tomasz Rozbicki</name>
          <url>linkedin.com/in/tomaszrozbicki</url>
        </developer>
      </developers>
  )

}
