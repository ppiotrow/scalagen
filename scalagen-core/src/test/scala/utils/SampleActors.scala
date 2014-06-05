package utils

import scalagen.genome.Genome
import scalagen.actor._
import akka.actor.ActorRef
import scalagen.message.Evaluated
import scalagen.population.MaximizeValue

object SampleActors {

  case class SampleGenome(chromosomes: Seq[Int]) extends Genome

  class TestEvaluator(endOfAlgorithm: ActorRef) extends Evaluator(endOfAlgorithm) with MaximizeValue {
    def eval(genome: Genome): Double = genome.asInstanceOf[SampleGenome].chromosomes.sum
  }

  /**
   * Does only recombination. Returns the recombined genome as result of mutation.
   */
  class TestRecombineProcreator(male: ActorRef, female: ActorRef, mutationProbability: Double)
    extends Procreator(male, female, mutationProbability) {
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      SampleOperators.recombine(genomeA, genomeB)

    override def mutate(genome: Genome): Genome =
      genome
  }

  /**
   * Does only mutation. Returns the genome A as the result of recombination.
   */
  class TestMutateProcreator(male: ActorRef, female: ActorRef, mutationProbability: Double)
    extends Procreator(male, female, mutationProbability){
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      genomeA

    override def mutate(genome: Genome): Genome =
      SampleOperators.mutate(genome)
  }

  /**
   * Does both recombination and mutation.
   */
  class TestRecombineAndMutateProcreator(male: ActorRef, female: ActorRef, mutationProbability: Double)
    extends Procreator(male, female, mutationProbability){
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      SampleOperators.recombine(genomeA, genomeB)

    override def mutate(genome: Genome): Genome =
      SampleOperators.mutate(genome)
  }

  class TestGodfather(evaluator: ActorRef,
                      deathItself: ActorRef,
                      randomKiller: ActorRef,
                      controller: ActorRef,
                      mutationProbability: Double)
    extends Godfather(evaluator, deathItself, randomKiller, controller) {
    override def initialGenomes: Seq[Genome] =
      List.fill(9)(SampleGenome(Nil)) :+ SampleGenome(List(1337))

    override def procreatorFactory(male: ActorRef, female: ActorRef): Procreator =
      new TestRecombineAndMutateProcreator(male, female, mutationProbability)
  }

  class TestRandomKiller(randomKillRatio: Float)
    extends RandomKiller(randomKillRatio) {
    override def selectToKill(phenotypes: Seq[Evaluated]): Option[ActorRef] =
      if (phenotypes.size > 0) Some(phenotypes.last.phenotype) else None
  }

  class TestEndOfAlgorithm extends EndOfAlgorithm with MaximizeValue {
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
