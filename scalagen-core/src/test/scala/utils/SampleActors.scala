package utils

import scalagen.genome.Genome
import scalagen.actor._
import akka.actor.ActorRef

object SampleActors {

  case class SampleGenome(chromosomes: Seq[Int]) extends Genome

  class TestEvaluator extends Evaluator {
    def eval(genome: Genome): Double = genome.asInstanceOf[SampleGenome].chromosomes.sum
  }

  /**
   * Does only recombination. Returns the recombined genome as result of mutation.
   */
  class TestRecombineProcreator extends Procreator {
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      SampleOperators.recombine(genomeA, genomeB)

    override def mutate(genome: Genome): Genome =
      genome
  }

  /**
   * Does only mutation. Returns the genome A as the result of recombination.
   */
  class TestMutateProcreator extends Procreator {
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      genomeA

    override def mutate(genome: Genome): Genome =
      SampleOperators.mutate(genome)
  }

  /**
   * Does both recombination and mutation.
   */
  class TestRecombineAndMutateProcreator extends Procreator {
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      SampleOperators.recombine(genomeA, genomeB)

    override def mutate(genome: Genome): Genome =
      SampleOperators.mutate(genome)
  }

  class TestGodfather(deathItself: ActorRef, randomKiller: ActorRef)
    extends Godfather(deathItself, randomKiller) {
    override def initialGenomes: Seq[Genome] =
      List.fill(9)(SampleGenome(Nil)) :+ SampleGenome(List(1337))

    override def createPhenotype(genome: Genome): Phenotype =
      new Phenotype(genome)
  }

  class TestRandomKiller(randomKillRatio: Float)
    extends RandomKiller(randomKillRatio) {
    override def randomKill(phenotypes: Seq[ActorRef]): Option[ActorRef] =
      if (phenotypes.size > 0) Some(phenotypes.last) else None
  }

}
