package scalagen.actor

import akka.actor.Actor
import scalagen.message.ReadGenom

class Fenotype[G](genome: G) extends Actor {
  def receive = {
    case ReadGenom => sender ! genome
  }
}

