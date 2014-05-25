package scalagen.actor

import akka.actor.Actor
import scalagen.message.{GenomeReaded, ReadGenom}
import scalagen.genome.Genome

/**
 * An actor to store genome for calculations
 */
class Phenotype(genome: Genome) extends Actor {
  def receive = {
    case ReadGenom => sender ! GenomeReaded(genome)
  }
}

