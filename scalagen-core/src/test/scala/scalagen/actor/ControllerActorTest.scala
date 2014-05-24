package scalagen.actor

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest._
import utils.StopSystemAfterAll
import utils.SampleActors.{SampleGenome, TestControllerActor}
import scalagen.message.{UpdatePopulation, Phenotypes, Evaluated}
import scalagen.message.UpdatePopulation
import scalagen.message.Evaluated
import utils.SampleActors.SampleGenome
import scalagen.message.Phenotypes

class ControllerActorTest extends TestKit(ActorSystem("ControllerTestActorSystem"))
with ImplicitSender
with WordSpecLike
with ShouldMatchers
with StopSystemAfterAll{
  "A Controller actor" must {
    " update a population" in {
      val sampleGenotype = Seq(1, 0, 1, 2)
      val evaluated = Seq.tabulate(5){
        n=>Evaluated(TestActorRef(new Phenotype(new SampleGenome(n+:sampleGenotype))), n+4)
      }.reverse
      val controller = TestActorRef(new TestControllerActor {
        override def optimalPopulationSize: Int = 5
        override def maxToKillOrCreate: Int = 2
      })
      controller ! Phenotypes(evaluated)
      val phenotypes = evaluated.map{_.phenotype}
      val expectedToBeKilled = Seq(phenotypes(4), phenotypes(3))
      val expectedCopules = (phenotypes(0), phenotypes(1))::(phenotypes(0), phenotypes(2))::Nil
      expectMsg(UpdatePopulation(expectedCopules, expectedToBeKilled))
    }
  }

"An calculation of population changes" must {
  "return max possible kills when population is too big" in {
    val currentPopulationSize = 50
    val optimalPopulationSize = 10
    val maxKillOrCreate = 10

    val (toKill, toCreate) = Controller.calculatePopulationChange(currentPopulationSize, optimalPopulationSize, maxKillOrCreate)

    toKill should be (10)
    toCreate should be (0)
  }

  "return max possible births when population is too small" in {
    val currentPopulationSize = 10
    val optimalPopulationSize = 50
    val maxKillOrCreate = 10

    val (toKill, toCreate) = Controller.calculatePopulationChange(currentPopulationSize, optimalPopulationSize, maxKillOrCreate)

    toKill should be (0)
    toCreate should be (10)
  }

  "return correct delta to decrease population size to optimal" in {
    val currentPopulationSize = 50
    val optimalPopulationSize = 45
    val maxKillOrCreate = 10

    val (toKill, toCreate) = Controller.calculatePopulationChange(currentPopulationSize, optimalPopulationSize, maxKillOrCreate)

    toKill should be (10)
    toCreate should be (5)
  }

  "return correct delta to increase population size to optimal" in {
    val currentPopulationSize = 45
    val optimalPopulationSize = 50
    val maxKillOrCreate = 10

    val (toKill, toCreate) = Controller.calculatePopulationChange(currentPopulationSize, optimalPopulationSize, maxKillOrCreate)

    toKill should be (5)
    toCreate should be (10)
  }

  "return maximum change of population possible to maintain optimal size" in {
    val currentPopulationSize = 50
    val optimalPopulationSize = 50
    val maxKillOrCreate = 10

    val (toKill, toCreate) = Controller.calculatePopulationChange(currentPopulationSize, optimalPopulationSize, maxKillOrCreate)

    toKill should be (10)
    toCreate should be (10)
  }
}

}
