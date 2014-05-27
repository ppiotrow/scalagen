package scalagen.actor

import akka.actor.Actor
import scalagen.message.{Evaluated, Eval}
import scalagen.genome.Genome

/**
 * An actor that evaluates genome.
 * It should have implemented eval method
 */
abstract class Evaluator extends Actor {
  def eval(genome: Genome): Double

  def receive = {
    case Eval(phenotype, genome) => {
      val phenotypeValue = eval(genome)
      sender ! Evaluated(phenotype, genome, phenotypeValue)
    }
  }
}
