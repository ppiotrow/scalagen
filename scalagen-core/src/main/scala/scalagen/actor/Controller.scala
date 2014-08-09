package scalagen.actor

import akka.actor.Actor
import scalagen.message.{Evaluated, UpdatePopulation, Phenotypes}
import scalagen.population.{PopulationReproduction, PopulationKilling}

object Controller {
  /**
   * Calculates how many phenotypes should be killed and created to maintain
   * optimal population size or get close to it and change as many phenotypes as possible.
   * Example 1:
   * optimalPopulationSize = 50; currentPopulationSize = 50; maxToKillOrCreate = 10
   * method should return:
   * (numberToKill = maxToKillOrCreate =10, numberToCreate = maxToKillOrCreate = 10)
   * Example 2:
   * optimalPopulationSize = 50; currentPopulationSize = 30; maxToKillOrCreate = 10
   * method should return
   * (numberToKill = 0, numberToCreate = maxToKillOrCreate = 10)
   * Example 3:
   * optimalPopulationSize = 50; currentPopulationSize = 45; maxToKillOrCreate = 10
   * method should return
   * (numberToKill = 5, numberToCreate = 10)
   **/
  def calculatePopulationChange(currentPopulationSize: Int, optimalPopulationSize: Int,
                                maxToKillOrCreate: Int): (Int, Int) = {
    def zeroIfLessThanZero(n: Int) = if(n<0) 0 else n

    val differenceFromOptimum = currentPopulationSize - optimalPopulationSize
    val numberToKill = if (differenceFromOptimum < 0)
      zeroIfLessThanZero(maxToKillOrCreate + differenceFromOptimum) else maxToKillOrCreate
    val numberToCreate = if (differenceFromOptimum > 0)
      zeroIfLessThanZero(maxToKillOrCreate - differenceFromOptimum) else maxToKillOrCreate
    (numberToKill, numberToCreate)
  }
}

abstract class Controller extends Actor with PopulationReproduction with PopulationKilling {

  def receive = {
    case Phenotypes(phenotypes) =>
      val (couples, toBeKilled) = updatePopulation(phenotypes)
      sender ! UpdatePopulation(couples, toBeKilled)
  }

  def optimalPopulationSize: Int

  def maxToKillOrCreate: Int

  def updatePopulation(currentPopulation: Seq[Evaluated]) = {
    val (numberToKill, numberToCreate) = calculatePopulationChange(currentPopulation.size)
    val toBeKilled = selectToBeKilled(numberToKill, currentPopulation)
    val alive = currentPopulation.filterNot { case Evaluated(ref, value) =>
      toBeKilled.contains(ref)
    }
    val couples = selectCouples(numberToCreate, alive)
    (couples, toBeKilled)
  }

  def calculatePopulationChange(currentPopulationSize: Int) =
    Controller.calculatePopulationChange(currentPopulationSize, optimalPopulationSize, maxToKillOrCreate)

}
