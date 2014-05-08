package scalagen.example

import org.apache.commons.lang3.StringUtils

object Foo2 extends App {
  println(akka.actor.Actor.toString)
  println(StringUtils.isEmpty(""))
}
