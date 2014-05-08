package scalagen.actor

import akka.actor.{PoisonPill, Actor}
import scalagen.message.Kill

/**
 * An actor that kills the specified [[Phenotype]].
 */
class DeathItself extends Actor {
  override def receive = {
    case Kill(phenotype) =>
      phenotype ! PoisonPill
  }
}
