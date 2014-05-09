package scalagen.actor

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.WordSpecLike
import utils.StopSystemAfterAll
import scalagen.message.Die
import utils.SampleActors.SampleGenome

class DeathItselfActorTest extends TestKit(ActorSystem("DeathItselfTestActorSystem"))
with ImplicitSender
with WordSpecLike
with StopSystemAfterAll {

  "A DeathItself actor" must {
    "kill the specified phenotype" in {
      val phenotype = TestActorRef(new Phenotype(SampleGenome(Nil)))
      val deathItself = TestActorRef[DeathItself]

      watch(phenotype)
      deathItself ! Die(phenotype)

      expectTerminated(phenotype)
    }
  }

}
