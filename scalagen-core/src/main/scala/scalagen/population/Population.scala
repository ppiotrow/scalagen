package scalagen.population

import scalagen.message.Evaluated
import akka.actor.ActorRef


trait PopulationReproduction {
  /**
   * Represents strategy on selection couples from evaluated phenotypes.
   *
   * @param howMany How many couples should be created
   * @param phenotypes Evaluated phenotypes
   * @return couples as parents collection
   */
  def selectCouples(howMany: Int, phenotypes: Seq[Evaluated]): Seq[(ActorRef, ActorRef)]
}

trait PopulationKilling {
  /**
   * Represents strategy on selection phenotypes to be killed.
   *
   * @param howMany How many phenotypes should be killed
   * @param phenotypes Evaluated phenotypes
   * @return phenotypes to be killed
   */
  def selectToBeKilled(howMany: Int, phenotypes: Seq[Evaluated]): Seq[ActorRef]
}

trait KillTheWorsts extends PopulationKilling with PhenotypeValueComparator {
  override def selectToBeKilled(howMany: Int, phenotypes: Seq[Evaluated]) = {
    val sorted = phenotypes.sortWith((e1, e2) => isBetterValue(e1.value, e2.value))
    sorted.map(_.phenotype).take(howMany)
  }
}
