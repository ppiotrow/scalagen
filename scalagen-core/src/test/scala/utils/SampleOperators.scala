package utils

import scalagen.genome.Genome
import utils.SampleActors.SampleGenome
import scalagen.population.PhenotypeValueComparator

object SampleOperators {

  def mutate(genome: Genome): SampleGenome = {
    val chromosomes = genome.asInstanceOf[SampleGenome].chromosomes
    new SampleGenome(chromosomes.init :+ (chromosomes.last + 1))
  }

  def recombine(genomeA: Genome, genomeB: Genome): Genome = {
    val chromosomesA = genomeA.asInstanceOf[SampleGenome].chromosomes
    val chromosomesB = genomeB.asInstanceOf[SampleGenome].chromosomes
    val half = chromosomesA.size / 2
    new SampleGenome(chromosomesA.take(half) ++ chromosomesB.drop(half))
  }

}

trait TestPhenotypeValueComparator extends PhenotypeValueComparator {
  override def isBetterValue(oldVal: Double, newVal: Double): Boolean = oldVal < newVal
}
