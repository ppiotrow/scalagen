package scalagen.example

import org.apache.commons.lang3.StringUtils

object Foo2 extends App {
	println(scalagen.core.Data.goo)
  println(akka.actor.Actor.toString)
  println(StringUtils.isEmpty(""))
}
