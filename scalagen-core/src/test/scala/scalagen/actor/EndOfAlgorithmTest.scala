package scalagen.actor

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.{ShouldMatchers, WordSpecLike}
import utils.StopSystemAfterAll
import utils.SampleActors.{SampleGenome, TestEndOfAlgorithm}
import scalagen.genome.Genome
import scalagen.message.Best
import org.joda.time.DateTime

class EndOfAlgorithmTest extends TestKit(ActorSystem("EndOfAlgorithmActorSystem"))
with ImplicitSender
with WordSpecLike
with ShouldMatchers
with StopSystemAfterAll {
  "EndOfAlgorithm actor" must {
    "finish algorithm after getting satisfying result" in {
      var selectedBest: Option[(Genome, Double, DateTime)] = None
      val endOfAlgActor = TestActorRef(new TestEndOfAlgorithm {
        override def onFinish = {selectedBest = lastBestResult}
      })

      endOfAlgActor ! Best(SampleGenome(Seq(1,2,11)), 14)

      selectedBest should be ('defined)
      val selectedBestResult = selectedBest map {_._2}
      selectedBestResult should be (Some(14))
    }

    "not finish algorithm before getting satisfying result" in {
      var finishedAlgorithm = false
      val endOfAlgActor = TestActorRef(new TestEndOfAlgorithm {
        override def onFinish = {finishedAlgorithm = true}
      })

      endOfAlgActor ! Best(SampleGenome(Seq(1,2,3)), 6)
      endOfAlgActor ! Best(SampleGenome(Seq(1,2,5)), 9)

      finishedAlgorithm should be(false)
    }

    "return best result after timeout passed" in {
      var selectedBest: Option[(Genome, Double, DateTime)] = None
      import scala.concurrent.duration._
      val endOfAlgActor = TestActorRef(new TestEndOfAlgorithm {
        override val maxTimeBetweenBetweenResults: FiniteDuration = 400 milliseconds
        override def onFinish = {selectedBest = lastBestResult}
      })

      endOfAlgActor ! Best(SampleGenome(Seq(1,0,0)), 1)
      endOfAlgActor ! Best(SampleGenome(Seq(1,2,1)), 4)
      endOfAlgActor ! Best(SampleGenome(Seq(1,1,0)), 2)
      endOfAlgActor ! Best(SampleGenome(Seq(1,1,5)), 7) //<- its the best genome
      endOfAlgActor ! Best(SampleGenome(Seq(1,0,5)), 6)
      awaitCond(selectedBest != None, 1 second, 200 milliseconds)

      selectedBest should be ('defined)
      val selectedBestResult = selectedBest map {_._2}
      selectedBestResult should be (Some(7))
    }
  }
}
