import sbt._

object ScalagenBuild extends Build {
	
	lazy val root = Project(
		id = "root",
		base = file("."),
		aggregate = Seq(core, example)
	)
	
	lazy val core = Project(
		id = "scalagen-core",
		base = file("scalagen-core")
	)

	lazy val example =  Project(
		id = "scalagen-examples",
		base = file("scalagen-examples"),
		dependencies = Seq(core)
	)
}
