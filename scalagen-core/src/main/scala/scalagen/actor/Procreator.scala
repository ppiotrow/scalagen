package scalagen.actor

import akka.actor.{ActorRef, Actor, FSM}
import scala.concurrent.duration.DurationInt
import scalagen.genome.Genome
import scalagen.message.{GenomeRead, ReadGenom, Descendant}

object Procreator {

  sealed trait State
  case object WaitingForGenomes extends State
  case object OneGenomeLeft extends State

  case class Data(firstGenome: Option[Genome])

}

abstract class Procreator(val male: ActorRef,
                          val female: ActorRef) extends Actor with FSM[Procreator.State, Procreator.Data] {

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

  override def preStart(): Unit= {
    male ! ReadGenom
    female ! ReadGenom
  }

  startWith(Procreator.WaitingForGenomes, Procreator.Data(None))

  when(Procreator.WaitingForGenomes, waitForGenomeTimeout) {
    case Event(GenomeRead(firstGenome), _) =>
      goto(Procreator.OneGenomeLeft) using Procreator.Data(Some(firstGenome))
    case Event(StateTimeout, _) =>
      stop
  }

  when(Procreator.OneGenomeLeft, waitForGenomeTimeout) {
    case Event(GenomeRead(secondGenome), _) =>
      context.parent ! Descendant(mutate(recombine(stateData.firstGenome.get, secondGenome)))
      stop
    case Event(StateTimeout, _) =>
      stop
  }
  initialize()
}
