package scalagen.actor

import akka.actor.Actor
import scalagen.message.ReadGenom
import scalagen.genome.Genome

/**
 * An actor to store genome for calculations
 */
class Fenotype(genome: Genome) extends Actor {
  def receive = {
    case ReadGenom => sender ! genome
  }
}

