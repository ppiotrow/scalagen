package scalagen.actor

import akka.actor._
import scalagen.genome.Genome
import scalagen.message.{Death, Kill, Phenotypes, GetPhenotypes}

/**
 * The parent of all phenotypes.
 */
abstract class Godfather(val deathItself: ActorRef,
                         val randomKiller: ActorRef) extends Actor {
  var phenotypes: Seq[ActorRef] = _

  override def receive = {
    case GetPhenotypes =>
      sender ! Phenotypes(phenotypes)
    case kill@Kill(phenotype) =>
      phenotypes = phenotypes diff Seq(phenotype)
      deathItself ! kill
    case Terminated(phenotype) =>
      randomKiller ! Death
  }

  def initialGenomes: Seq[Genome]

  def createPhenotype(genome: Genome): Phenotype

  phenotypes = initialGenomes.map(genome => {
    val child = context.actorOf(Props(createPhenotype(genome)))
    context.watch(child)
  })
}
