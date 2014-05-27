package scalagen.actor

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.WordSpecLike
import scalagen.message.{Evaluated, Eval}
import utils.{StopSystemAfterAll}
import utils.SampleActors.{TestEvaluator, SampleGenome}

class EvaluatorActorTest extends TestKit(ActorSystem("EvaluatorTestActorSystem"))
with ImplicitSender
with WordSpecLike
with StopSystemAfterAll {

  "An EvaluatorActor " must {
    "respond with evaluated phenotype" in {
      val sampleGenotype = SampleGenome(Seq(1, 4, 12, 3))
      val evaluator = TestActorRef(new TestEvaluator)
      val testPhenotype = TestActorRef(new Phenotype(sampleGenotype))
      evaluator ! Eval(testPhenotype, sampleGenotype)
      expectMsg(Evaluated(testPhenotype, sampleGenotype, 20))
    }
  }
}
