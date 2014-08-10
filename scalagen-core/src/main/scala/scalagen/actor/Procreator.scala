package scalagen.actor

import akka.actor.{ActorRef, Actor, FSM}
import scala.concurrent.duration.DurationInt
import scalagen.genome.Genome
import scalagen.message.{GenomeRead, ReadGenom, Descendant}
import scala.concurrent.forkjoin.ThreadLocalRandom.{current => Random}

object Procreator {

  sealed trait State

  case object WaitingForGenomes extends State

  case object OneGenomeLeft extends State

  case class Data(firstGenome: Option[Genome])

}

/**
 * An actor that creates a new descendant.
 * @param male male parent
 * @param female female parent
 * @param mutationProbability probability of a mutation; this value should be between 0.0 and 1.0, but other values
 *                            are also valid (for values greater or equal 1.0 the mutation will always occur on the
 *                            contrary for a negative value mutation will never occur)
 */
abstract class Procreator(val male: ActorRef,
                          val female: ActorRef,
                          val mutationProbability: Double) extends Actor with FSM[Procreator.State, Procreator.Data] {

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

  /** An timeout from creation or from receiving first genome after procreator stops waiting for genomes
    * and do not create new genome
    */
  val waitForGenomeTimeout = 3.seconds

  override def preStart(): Unit = {
    male ! ReadGenom
    female ! ReadGenom
  }

  startWith(Procreator.WaitingForGenomes, Procreator.Data(None))

  when(Procreator.WaitingForGenomes, waitForGenomeTimeout) {
    case Event(GenomeRead(firstGenome), _) =>
      goto(Procreator.OneGenomeLeft) using Procreator.Data(Some(firstGenome))
    case Event(StateTimeout, _) =>
      stop()
  }

  when(Procreator.OneGenomeLeft, waitForGenomeTimeout) {
    case Event(GenomeRead(secondGenome), _) =>
      val recombined = recombine(stateData.firstGenome.get, secondGenome)
      val descendant = if (Random.nextDouble() <= mutationProbability) mutate(recombined) else recombined
      context.parent ! Descendant(descendant)
      stop()
    case Event(StateTimeout, _) =>
      stop()
  }
  initialize()
}
