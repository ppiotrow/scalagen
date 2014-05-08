package scalagen.actor

import akka.actor.{PoisonPill, Actor}
import scalagen.message.Die

/**
 * Kills the specified [[Phenotype]].
 */
class DeathItself extends Actor {
  override def receive = {
    case Die(phenotype) =>
      phenotype ! PoisonPill
  }
}
