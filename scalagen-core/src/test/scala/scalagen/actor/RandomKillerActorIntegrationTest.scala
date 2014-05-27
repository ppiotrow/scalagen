package scalagen.actor

import akka.testkit.{TestProbe, TestActorRef, TestKit}
import akka.actor.{Props, ActorSystem}
import org.scalatest._
import utils.StopSystemAfterAll
import utils.SampleActors.{TestEndOfAlgorithm, TestEvaluator, TestGodfather, TestRandomKiller}
import scalagen.message.{UpdatePopulation, Phenotypes, GetPhenotypes}
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._

class RandomKillerActorIntegrationTest extends TestKit(ActorSystem("RandomKillerIntegrationTestActorSystem"))
with WordSpecLike
with ShouldMatchers
with StopSystemAfterAll {

  "A RandomKill actor" must {
    "kill the last phenotype" in {
      val deathItself = TestActorRef(Props[DeathItself])
      val randomKiller = TestActorRef(Props(new TestRandomKiller(0.1f)))
      val controller = new TestProbe(system)
      val endOfAlgorithm = TestActorRef[TestEndOfAlgorithm]
      val evaluator = TestActorRef(new TestEvaluator(endOfAlgorithm))
      val godfather = TestActorRef(Props(
        new TestGodfather(evaluator, deathItself, randomKiller, controller.ref)))
      // Controller receives population after evaluation.
      val phenotypes = controller.receiveOne(2.second).asInstanceOf[Phenotypes].phenotypes
      // Kill all the phenotypes except the last one
      godfather ! UpdatePopulation(Seq(), phenotypes.map(_.phenotype).init)

      //Wait until RandomKiller kill the very last one phenotype
      awaitCond(Await.result((godfather.ask(GetPhenotypes)(2.seconds)).mapTo[Phenotypes], 1.second).phenotypes == Nil,
        max=2.seconds,
      interval=1400.millis)
    }
  }
}
