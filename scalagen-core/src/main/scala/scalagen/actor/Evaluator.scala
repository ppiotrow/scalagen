package scalagen.actor

import akka.actor.{ActorRef, Actor}
import scalagen.message.{Best, Evaluated, Eval}
import scalagen.genome.Genome
import scalagen.population.PhenotypeValueComparator

/**
 * An actor that evaluates genome.
 * It should have implemented eval method
 */
abstract class Evaluator(val endOfAlgorithm: ActorRef) extends Actor with PhenotypeValueComparator {
  var bestResult: Option[Double] = None

  def eval(genome: Genome): Double

  def receive = {
    case Eval(phenotype, genome) =>
      val phenotypeValue = eval(genome)
      sender ! Evaluated(phenotype, phenotypeValue)
      bestResult match {
        case Some(result) =>
          if (isBetterValue(result, phenotypeValue))
            updateBestResult(genome, phenotypeValue)
        case None =>
          updateBestResult(genome, phenotypeValue)
      }
  }

  private def updateBestResult(genome: Genome, value: Double) = {
    bestResult = Some(value)
    endOfAlgorithm ! Best(genome, value)
  }
}
