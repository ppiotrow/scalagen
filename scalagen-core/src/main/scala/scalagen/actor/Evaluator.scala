package scalagen.actor

import akka.actor.Actor
import scalagen.message.{Best, Evaluated, Eval}
import scalagen.genome.Genome
import scalagen.population.PhenotypeValueComparator

/**
 * An actor that evaluates genome.
 * It should have implemented eval method
 */
abstract class Evaluator extends Actor with PhenotypeValueComparator {
  var bestResult: (Genome, Double) = (null, 0)

  def eval(genome: Genome): Double

  def receive = {
    case Eval(phenotype, genome) => {
      val phenotypeValue = eval(genome)
      sender ! Evaluated(phenotype, phenotypeValue)
      if (bestResult._1 != null) {
        if (isBetterValue(bestResult._2, phenotypeValue))
          updateBestResult(genome, phenotypeValue)
      } else
        updateBestResult(genome, phenotypeValue)
    }
  }

  private def updateBestResult(genome: Genome, value: Double) = {
    bestResult = (genome, value)
    sender ! Best(genome, value)
  }
}
