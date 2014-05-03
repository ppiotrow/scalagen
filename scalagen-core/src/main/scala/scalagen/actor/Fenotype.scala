package scalagen.actor

import akka.actor.Actor
import scalagen.message.ReadGenom

/**
 * An actor to store genome for calculations
 */
class Fenotype[G](genome: G) extends Actor {
  def receive = {
    case ReadGenom => sender ! genome
  }
}

