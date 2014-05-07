package scalagen.actor

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.WordSpecLike
import scalagen.message.ReadGenom
import utils.StopSystemAfterAll
import utils.SampleActors.SampleGenome

class FenotypeActorTest extends TestKit(ActorSystem("FenotypeTestActorSystem"))
with ImplicitSender
with WordSpecLike
with StopSystemAfterAll {

  "An FenotypeActor" must {
    "respond with its genotype" in {
      val sampleGenotype = SampleGenome(Seq(1, 4, 12, 3))
      val fenotypeActorRef = TestActorRef(new Fenotype(sampleGenotype))
      fenotypeActorRef ! ReadGenom
      expectMsg(sampleGenotype)
    }
  }
}
