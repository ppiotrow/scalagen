package scalagen.actor

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.WordSpecLike
import utils.StopSystemAfterAll
import utils.SampleActors.{TestRecombineAndMutateProcreator, TestMutateProcreator, TestRecombineProcreator, SampleGenome}
import scalagen.message.{Descendant, Reproduce}

class ProcreatorActorTest extends TestKit(ActorSystem("ProcreatorTestActorSystem"))
with ImplicitSender
with WordSpecLike
with StopSystemAfterAll {

  "A Procreator actor" must {
    "recombine the genome" in {
      val sampleGenotypeA = SampleGenome(Seq(1, 3, 3, 7, 1))
      val sampleGenotypeB = SampleGenome(Seq(9, 8, 7, 6, 5))
      val procreatorActorRef = TestActorRef[TestRecombineProcreator]

      procreatorActorRef ! Reproduce(sampleGenotypeA, sampleGenotypeB)

      val expectedGenome = SampleGenome(Seq(1, 3, 7, 6, 5))
      expectMsg(Descendant(expectedGenome))
    }

    "mutate the genome" in {
      val sampleGenotypeA = SampleGenome(Seq(1, 3, 3, 7, 1))
      val sampleGenotypeB = SampleGenome(Nil)
      val procreatorActorRef = TestActorRef[TestMutateProcreator]

      procreatorActorRef ! Reproduce(sampleGenotypeA, sampleGenotypeB)

      val expectedGenome = SampleGenome(Seq(1, 3, 3, 7, 2))
      expectMsg(Descendant(expectedGenome))
    }

    "recombine and mutate the genome" in {
      val sampleGenotypeA = SampleGenome(Seq(1, 3, 3, 7, 1))
      val sampleGenotypeB = SampleGenome(Seq(9, 8, 7, 6, 5))
      val procreatorActorRef = TestActorRef[TestRecombineAndMutateProcreator]

      procreatorActorRef ! Reproduce(sampleGenotypeA, sampleGenotypeB)

      val expectedGenome = SampleGenome(Seq(1, 3, 7, 6, 6))
      expectMsg(Descendant(expectedGenome))
    }
  }

}
