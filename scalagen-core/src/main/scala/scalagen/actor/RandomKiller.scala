package scalagen.actor

import akka.actor.{ActorLogging, ActorRef, Actor}
import scalagen.message.{Phenotypes, GetPhenotypes, Death, Kill}

object RandomKiller {
  def randomKillRatioMatches(deaths: Long,
                             randomKills: Long,
                             randomKillRatio: Float) = {
    ((randomKills + 1) / Math.max(deaths + 1, 1f)) <= randomKillRatio
  }
}

/**
 * An actor that kills randomly selected [[Phenotype]].
 * @param randomKillRatio the random kill to death ratio (must be lower than 1)
 */
abstract class RandomKiller(val randomKillRatio: Float) extends Actor {
  require(randomKillRatio < 1f)

  /**
   * Defines random killing strategy. It should choose zero or one phenotype to kill.
   * @param phenotypes all living phenotypes
   * @return the phenotype to kill
   */
  def randomKill(phenotypes: Seq[ActorRef]): Option[ActorRef]

  var deaths: Long = 0
  var randomKills: Long = 0

  override def receive = {
    case Death =>
      deaths = deaths + 1
      if (RandomKiller.randomKillRatioMatches(deaths, randomKills, randomKillRatio)) {
        randomKills = randomKills + 1
        sender ! GetPhenotypes
      }
      // Handle overflow - just reset the random kills. Use BigInt in the future?
      if (deaths == 0) randomKills = 0
    case Phenotypes(phenotypes) =>
      randomKill(phenotypes).foreach {
        sender ! Kill(_)
      }
  }
}
