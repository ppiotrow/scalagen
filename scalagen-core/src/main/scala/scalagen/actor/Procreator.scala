package scalagen.actor

import akka.actor.Actor
import scalagen.genome.Genome
import scalagen.message.{Descendant, Reproduce}

abstract class Procreator extends Actor {

  /**
   * The crossover step of a reproduction process.
   * @param genomeA genome of the parent A
   * @param genomeB genome of the parent B
   * @return offspring of recombination
   */
  def recombine(genomeA: Genome, genomeB: Genome): Genome

  /**
   * The mutation step of a reproduction process.
   * @param genome an offspring after the crossover step
   * @return genome which is result of crossover and mutation
   */
  def mutate(genome: Genome): Genome

  def receive = {
    case Reproduce(genomeA, genomeB) =>
      sender ! Descendant(mutate(recombine(genomeA, genomeB)))
  }

}
