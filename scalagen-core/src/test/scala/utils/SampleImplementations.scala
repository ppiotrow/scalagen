package utils

import scalagen.genome.Genome
import scalagen.actor.Evaluator

object SampleImplementations {

  case class SampleGenome(chromosomes: Seq[Int]) extends Genome

  class TestEvaluator extends Evaluator {
    def eval(genome: Genome): Double = genome.asInstanceOf[SampleGenome].chromosomes.sum
  }

}
