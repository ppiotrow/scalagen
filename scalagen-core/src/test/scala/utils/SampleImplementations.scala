package utils

import scalagen.genome.Genome
import scalagen.actor.{Procreator, Evaluator}

object SampleImplementations {

  sample =>

  case class SampleGenome(chromosomes: Seq[Int]) extends Genome

  class TestEvaluator extends Evaluator {
    def eval(genome: Genome): Double = genome.asInstanceOf[SampleGenome].chromosomes.sum
  }

  /**
   * Does only recombination. Returns the recombined genome as result of mutation.
   */
  class TestRecombineProcreator extends Procreator {
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      sample.recombine(genomeA, genomeB)

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
      sample.mutate(genome)
  }

  /**
   * Does both recombination and mutation.
   */
  class TestRecombineAndMutateProcreator extends Procreator {
    override def recombine(genomeA: Genome, genomeB: Genome): Genome =
      sample.recombine(genomeA, genomeB)

    override def mutate(genome: Genome): Genome =
      sample.mutate(genome)
  }

  private def mutate(genome: Genome): SampleGenome = {
    val chromosomes = genome.asInstanceOf[SampleGenome].chromosomes
    new SampleGenome(chromosomes.init :+ (chromosomes.last + 1))
  }

  private def recombine(genomeA: Genome, genomeB: Genome): Genome = {
    val chromosomesA = genomeA.asInstanceOf[SampleGenome].chromosomes
    val chromosomesB = genomeB.asInstanceOf[SampleGenome].chromosomes
    val half = chromosomesA.size / 2
    new SampleGenome(chromosomesA.take(half) ++ chromosomesB.drop(half))
  }

}
