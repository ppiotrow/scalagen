package scalagen.actor

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.WordSpecLike
import scalagen.message.{Evaluated, Eval}
import utils.StopSystemAfterAll
import utils.SampleActors.{TestEndOfAlgorithm, TestEvaluator, SampleGenome}

class EvaluatorActorTest extends TestKit(ActorSystem("EvaluatorTestActorSystem"))
with ImplicitSender
with WordSpecLike
with StopSystemAfterAll {

  "An EvaluatorActor " must {
    "respond with evaluated phenotype" in {
      val sampleGenotype = SampleGenome(Seq(1, 4, 12, 3))
      val endOfAlgorithm = TestActorRef[TestEndOfAlgorithm]
      val evaluator = TestActorRef(new TestEvaluator(endOfAlgorithm))
      val testPhenotype = TestActorRef(new Phenotype(sampleGenotype))
      evaluator ! Eval(testPhenotype, sampleGenotype)
      expectMsg(Evaluated(testPhenotype, 20))
    }
  }
}
