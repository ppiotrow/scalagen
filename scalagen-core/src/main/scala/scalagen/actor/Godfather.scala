package scalagen.actor

import akka.actor._
import scalagen.genome.Genome
import scalagen.message._
import scala.collection.immutable.{HashSet, HashMap}
import scalagen.message.UpdatePopulation
import scalagen.message.Best
import scalagen.message.Eval
import scalagen.message.Kill
import akka.actor.Terminated
import scalagen.message.Evaluated
import scalagen.message.Phenotypes
import scalagen.message.Descendant

/**
 * The parent of all phenotypes.
 */
abstract class Godfather(val evaluator: ActorRef,
                         val deathItself: ActorRef,
                         val randomKiller: ActorRef,
                         val controller: ActorRef) extends Actor {
  var phenotypes = new HashMap[ActorRef, Evaluated]()
  var phenotypesToEvaluate = new HashSet[ActorRef]()
  var phenotypeId: Long = 0
  var procreatorId: Long = 0

  override def receive = {
    case GetPhenotypes =>
      sender ! Phenotypes(phenotypes.values.toList)
    case UpdatePopulation(couples, toBeKilled) =>
      updatePopulation(couples, toBeKilled)
    case evaluated@Evaluated(_, _) =>
      updateEvaluatedData(evaluated)
      if (shouldInformController) informController()
    case Descendant(genome) =>
      startNewPhenotype(genome)
    case Terminated(phenotype) =>
      randomKiller ! Death
  }

  def initialGenomes: Seq[Genome]

  def phenotypeFactory(genome: Genome): Phenotype

  def procreatorFactory(male: ActorRef, female: ActorRef): Procreator

  def updatePopulation(couples: Seq[(ActorRef, ActorRef)], toBeKilled: Seq[ActorRef]) = {
    toBeKilled.foreach(killPhenotype(_))
    couples.foreach(procreate(_))
  }

  def procreate(parents: (ActorRef, ActorRef)): Unit = {
    val male = parents._1
    val female = parents._2
    context.actorOf(Props(procreatorFactory(male, female)), s"procreator$procreatorId")
    procreatorId += 1
  }

  def killPhenotype(phenotype: ActorRef): Unit = {
    phenotypes -= phenotype
    deathItself ! Kill(phenotype)
  }

  def updateEvaluatedData(evaluated: Evaluated): Unit = {
    phenotypesToEvaluate -= evaluated.phenotype
    phenotypes += ((evaluated.phenotype, evaluated))
  }

  override def preStart: Unit = {
    initialGenomes.map(startNewPhenotype _)
  }

  /**
   * Starts new actor representing phenotype.
   * Send request to evaluate it.
   */
  def startNewPhenotype(genome: Genome): Unit = {
    val newPhenotype = context.actorOf(Props(phenotypeFactory(genome)), s"phenotype$phenotypeId")
    phenotypeId += 1
    phenotypesToEvaluate += newPhenotype
    context.watch(newPhenotype)
    evaluator ! Eval(newPhenotype, genome)
  }

  def shouldInformController: Boolean = phenotypesToEvaluate.isEmpty

  def informController(): Unit = {
    controller ! Phenotypes(phenotypes.values.toSeq)
  }
}
