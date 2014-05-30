package scalagen.population

import scalagen.message.Evaluated
import akka.actor.ActorRef
import scala.util.Random
import scala.collection.immutable.TreeMap


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
  /**
   * Sorts phenotypes and selects 'howMany worsts
   * @param howMany How many phenotypes should be killed
   * @param phenotypes Evaluated phenotypes
   * @return phenotypes to be killed
   */
  override def selectToBeKilled(howMany: Int, phenotypes: Seq[Evaluated]) = {
    val sorted = phenotypes.sortWith((e1, e2) => isBetterValue(e1.value, e2.value))
    sorted.map(_.phenotype).take(howMany)
  }
}

trait RouletteWheelReproduction extends PopulationReproduction with PhenotypeValueComparator {
  /**
   * The better position in Seq of phenotypes after sorting, the bigger probability to be in couple.
   * @param howMany How many couples should be created
   * @param phenotypes Evaluated phenotypes
   * @return couples as parents collection
   */
  override def selectCouples(howMany: Int, phenotypes: Seq[Evaluated]) = {
    val sortedPhenotypes = phenotypes.sortWith((e1, e2) => isBetterValue(e1.value, e2.value)).map(_.phenotype)
    val (rouletteWheel, indexesSum) = sortedPhenotypes.zipWithIndex.reverse.foldLeft(TreeMap[Int, ActorRef](), 0) {
      (treeWithSum, elem) => (treeWithSum._1 + ((treeWithSum._2, elem._1)), elem._2 + treeWithSum._2)
    }
    def randomPhenotypeFromWheel = rouletteWheel.to(Random.nextInt(indexesSum + 1)).last._2
    if (rouletteWheel.size < 2) Nil
    else Seq.tabulate(howMany) { n =>
      val firstPhenotype = randomPhenotypeFromWheel
      var secondPhenotype = randomPhenotypeFromWheel
      while (firstPhenotype == secondPhenotype) {
        secondPhenotype = randomPhenotypeFromWheel
      }
      (firstPhenotype, secondPhenotype)
    }
  }
}
