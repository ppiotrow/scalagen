package scalagen.population

import scalagen.message.Evaluated
import akka.actor.ActorRef


trait PopulationKilling {
  /**
   * Represents strategy on selection copules from evaluated phenotypes.
   *
   * @param howMany How many copules should be created
   * @param phenotypes Evaluated phenotypes
   * @return copules as parents collection
   */
  def selectCopules(howMany: Int, phenotypes: Seq[Evaluated]): Seq[(ActorRef, ActorRef)]
}

trait PopulationReproduction {
  /**
   * Represents strategy on selection phenotypes to be killed.
   *
   * @param howMany How many phenotypes should be killed
   * @param phenotypes Evaluated phenotypes
   * @return phenotypes to be killed
   */
  def selectToBeKilled(howMany: Int, phenotypes: Seq[Evaluated]): Seq[ActorRef]
}
