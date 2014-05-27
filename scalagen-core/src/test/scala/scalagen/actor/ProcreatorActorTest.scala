package scalagen.actor

import akka.testkit.{TestKit, ImplicitSender, TestProbe, TestActorRef, TestFSMRef}
import akka.actor.{Props, ActorSystem}
import org.scalatest.WordSpecLike
import utils.{TestParentChildRelation, StopSystemAfterAll}
import utils.SampleActors.{TestRecombineAndMutateProcreator, TestMutateProcreator, TestRecombineProcreator, SampleGenome}
import scalagen.message.Descendant
import scala.concurrent.duration.DurationInt
import scalagen.actor.Procreator.{WaitingForGenomes, OneGenomeLeft}

class ProcreatorActorTest extends TestKit(ActorSystem("ProcreatorTestActorSystem"))
with ImplicitSender
with WordSpecLike
with StopSystemAfterAll
with TestParentChildRelation {

  "A Procreator actor" must {
    "recombine the genome" in {
      val maleGenotype = SampleGenome(Seq(1, 3, 3, 7, 1))
      val femaleGenotype = SampleGenome(Seq(9, 8, 7, 6, 5))

      val male = TestActorRef(new Phenotype(maleGenotype))
      val female = TestActorRef(new Phenotype(femaleGenotype))
      val proxy = mockParentWithProbe(Props(new TestRecombineProcreator(male, female)))

      val expectedGenome = SampleGenome(Seq(1, 3, 7, 6, 5))
      proxy.expectMsg(Descendant(expectedGenome))
    }

    "mutate the genome" in {
      val maleGenotype = SampleGenome(Seq(1, 3, 3, 7, 1))
      val femaleGenotype = SampleGenome(Nil)

      val male = TestActorRef(new Phenotype(maleGenotype))
      val female = TestActorRef(new Phenotype(femaleGenotype))
      val proxy = mockParentWithProbe(Props(new TestMutateProcreator(male, female)))

      val expectedGenome = SampleGenome(Seq(1, 3, 3, 7, 2))
      proxy.expectMsg(Descendant(expectedGenome))
    }

    "recombine and mutate the genome" in {
      val maleGenotype = SampleGenome(Seq(1, 3, 3, 7, 1))
      val femaleGenotype = SampleGenome(Seq(9, 8, 7, 6, 5))

      val male = TestActorRef(new Phenotype(maleGenotype))
      val female = TestActorRef(new Phenotype(femaleGenotype))
      val proxy = mockParentWithProbe(Props(new TestRecombineAndMutateProcreator(male, female)))

      val expectedGenome = SampleGenome(Seq(1, 3, 7, 6, 6))
      proxy.expectMsg(Descendant(expectedGenome))
    }

    "shutdown after timeout if no genomes received" in {
      val mockPhenotype = new TestProbe(system).ref
      val procreator = TestFSMRef(new TestRecombineProcreator(mockPhenotype, mockPhenotype))

      watch(procreator)

      awaitCond(procreator.stateName == WaitingForGenomes, 2.seconds)
      expectTerminated(procreator, 4.seconds)
    }

    "shutdown after timeout after first genome received" in {
      val mockPhenotype = new TestProbe(system).ref
      val maleGenotype = SampleGenome(Seq(1, 3, 3, 7, 1))
      val male = TestActorRef(new Phenotype(maleGenotype))
      val procreator = TestFSMRef(new TestRecombineProcreator(male, mockPhenotype))

      watch(procreator)

      awaitCond(procreator.stateName == OneGenomeLeft, 2.seconds)
      expectTerminated(procreator, 4.seconds)
    }

  }
}
