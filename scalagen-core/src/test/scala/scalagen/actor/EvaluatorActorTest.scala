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
    "respond with evaluated fenotype" in {
      val sampleGenotype = SampleGenome(Seq(1, 4, 12, 3))
      val evaluator = TestActorRef(new TestEvaluator)
      val testFenotype = TestActorRef(new Fenotype(sampleGenotype))
      evaluator ! Eval(testFenotype, sampleGenotype)
      expectMsg(Evaluated(testFenotype, 20))
    }
  }
}
