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
      version := "0.2.0-SNAPSHOT",
      organization := "com.github.scalagen",
      libraryDependencies ++= Dependencies.core,
      scalacOptions ++= Seq("-feature"))
    .settings(Publishing.publishSettings: _*)

	lazy val example =  Project(
		id = "scalagen-examples",
		base = file("scalagen-examples"))
    .dependsOn(core)
    .settings(
        libraryDependencies++=Dependencies.examples
    )
}

object Dependencies {
  object Compile {
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.3.3"
    val jodaTime = "joda-time" % "joda-time" % "2.3"
    val jodaConvert = "org.joda" % "joda-convert" % "1.6"
  }

  object Test {
    val akkaTestkit ="com.typesafe.akka" %% "akka-testkit" % "2.3.3" % "test"
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

    licenses += "BSD-Style" -> url("http://www.opensource.org/licenses/bsd-license.php"),

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
          <url></url>
        </developer>
      </developers>
  )

}
