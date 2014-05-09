package scalagen.actor

import akka.actor.{PoisonPill, Actor}
import scalagen.message.Die

/**
 * An actor that kills the specified [[Phenotype]].
 */
class DeathItself extends Actor {
  override def receive = {
    case Die(phenotype) =>
      phenotype ! PoisonPill
  }
}
