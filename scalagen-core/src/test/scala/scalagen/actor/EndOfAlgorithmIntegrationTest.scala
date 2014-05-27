package scalagen.actor

import akka.actor.{Props, ActorSystem}
import org.scalatest._
import utils.StopSystemAfterAll
import utils.SampleActors._
import akka.testkit.TestKit
import scala.concurrent.duration._
import scalagen.genome.Genome
import org.joda.time.DateTime

class EndOfAlgorithmIntegrationTest extends TestKit(ActorSystem("EndOfAlgorithmIntegrationTestActorSystem"))
with WordSpecLike
with ShouldMatchers
with StopSystemAfterAll {

  "A EndOfAlgorithm actor" must {
    "receive the best genome" in {
      val deathItself = system.actorOf(Props[DeathItself], "death-itself")
      val randomKiller = system.actorOf(Props(new TestRandomKiller(0.1f)), "random-killer")
      val controller = system.actorOf(Props(new TestControllerActor {
        override def optimalPopulationSize: Int = 50

        override def maxToKillOrCreate: Int = 10
      }), "controller")
      var bestResult: Option[(Genome, Double, DateTime)] = None
      val expectedValue = 8
      val endOfAlgorithm = system.actorOf(Props(new TestEndOfAlgorithm {
        override val maxTimeBetweenImprovement = 1.second

        override def onFinish() = bestResult = lastBestResult

        override def shouldStopCalculations(value: Double): Boolean = value >= expectedValue
      }), "end-of-algorithm")
      val evaluator = system.actorOf(Props(new TestEvaluator(endOfAlgorithm)), "evaluator")

      watch(endOfAlgorithm)
      system.actorOf(Props(
        new TestGodfather(evaluator, deathItself, randomKiller, controller) {
          // It should be easy to find genome greater or equal 8 with 7 and 5 as starting values
          override def initialGenomes = {
            Seq(SampleGenome(Seq(7)), SampleGenome(Seq(5)))
          }
        }), "godfather")

      // Wait 4 seconds and check the best result. It should be greater or equal 'expectedValue'
      expectTerminated(endOfAlgorithm, 4.seconds)
      bestResult.get._1.asInstanceOf[SampleGenome].chromosomes.sum should be >= expectedValue
    }
  }
}
