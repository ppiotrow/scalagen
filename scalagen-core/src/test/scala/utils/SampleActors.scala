package utils

import scalagen.genome.Genome
import scalagen.actor._
import akka.actor.ActorRef
import scala.concurrent.duration.FiniteDuration
import scalagen.message.Evaluated
import scalagen.population.PhenotypeValueComparator

object SampleActors {

  case class SampleGenome(chromosomes: Seq[Int]) extends Genome

  class TestEvaluator extends Evaluator {
    def eval(genome: Genome): Double = genome.asInstanceOf[SampleGenome].chromosomes.sum
  }

  /**
   * Does only recombination. Returns the recombined genome as result of mutation.
   */
  class TestRecombineProcreator(male: ActorRef, female: ActorRef) extends Procreator(male, female) {
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      SampleOperators.recombine(genomeA, genomeB)

    override def mutate(genome: Genome): Genome =
      genome
  }

  /**
   * Does only mutation. Returns the genome A as the result of recombination.
   */
  class TestMutateProcreator(male: ActorRef, female: ActorRef) extends Procreator(male, female){
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      genomeA

    override def mutate(genome: Genome): Genome =
      SampleOperators.mutate(genome)
  }

  /**
   * Does both recombination and mutation.
   */
  class TestRecombineAndMutateProcreator(male: ActorRef, female: ActorRef) extends Procreator(male, female){
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      SampleOperators.recombine(genomeA, genomeB)

    override def mutate(genome: Genome): Genome =
      SampleOperators.mutate(genome)
  }

  class TestGodfather(evaluator: ActorRef,
                      deathItself: ActorRef,
                      randomKiller: ActorRef,
                      controller: ActorRef,
                      endOfAlgorithm: ActorRef)
    extends Godfather(evaluator, deathItself, randomKiller, controller, endOfAlgorithm) {
    override def initialGenomes: Seq[Genome] =
      List.fill(9)(SampleGenome(Nil)) :+ SampleGenome(List(1337))

    override def phenotypeFactory(genome: Genome): Phenotype = new Phenotype(genome)

    override def procreatorFactory(male: ActorRef, female: ActorRef): Procreator = new TestRecombineAndMutateProcreator(male, female)
  }

  class TestRandomKiller(randomKillRatio: Float)
    extends RandomKiller(randomKillRatio) {
    override def selectToKill(phenotypes: Seq[Evaluated]): Option[ActorRef] =
      if (phenotypes.size > 0) Some(phenotypes.last.phenotype) else None
  }

  class TestEndOfAlgorithm extends EndOfAlgorithm with TestPhenotypeValueComparator {

    override def shouldStopCalculations(value: Double): Boolean = value > 10

  }

  class TestControllerActor extends Controller {
    override def selectToBeKilled(howMany: Int, phenotypes: Seq[Evaluated]): Seq[ActorRef] = {
      val sortedPhenotypes =phenotypes.sortBy(_.value)
      sortedPhenotypes.take(howMany).map(_.phenotype)
    }

    override def selectCouples(howMany: Int, phenotypes: Seq[Evaluated]): Seq[(ActorRef, ActorRef)] = {
      val sortedPhenotypes = phenotypes.sortBy(_.value).reverse.map {_.phenotype}
      val sampleCouples = for {
        male<-sortedPhenotypes
        female<-sortedPhenotypes
        if(male!=female)
      } yield (male, female)
      sampleCouples.take(howMany)
    }

    override def optimalPopulationSize: Int = 50

    override def maxToKillOrCreate: Int = 10
  }

}
