package scalagen.actor

import akka.actor.{PoisonPill, Actor}
import scalagen.message.Kill

/**
 * Kills the specified [[Fenotype]].
 */
class DeathItself extends Actor {
  override def receive = {
    case Kill(phenotype) =>
      phenotype ! PoisonPill
  }
}
