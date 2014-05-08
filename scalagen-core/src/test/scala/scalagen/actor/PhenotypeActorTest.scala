package scalagen.actor

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.WordSpecLike
import scalagen.message.ReadGenom
import utils.StopSystemAfterAll
import utils.SampleActors.SampleGenome

class PhenotypeActorTest extends TestKit(ActorSystem("PhenotypeTestActorSystem"))
with ImplicitSender
with WordSpecLike
with StopSystemAfterAll {

  "A Phenotype actor" must {
    "respond with its genotype" in {
      val sampleGenotype = SampleGenome(Seq(1, 4, 12, 3))
      val phenotypeActorRef = TestActorRef(new Phenotype(sampleGenotype))
      phenotypeActorRef ! ReadGenom
      expectMsg(sampleGenotype)
    }
  }
}
