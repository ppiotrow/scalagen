package scalagen.population

import org.scalatest.{ShouldMatchers, WordSpecLike}
import scalagen.message.Evaluated
import utils.TestPhenotypeValueComparator
import akka.testkit.{TestKit, TestActorRef}
import scalagen.actor.Phenotype
import utils.SampleActors.SampleGenome
import akka.actor.ActorSystem

class KillTheWorstsTest extends TestKit(ActorSystem("KillTheWorstsTestActorSystem"))
with ShouldMatchers
with WordSpecLike  {
  "A strategy" should {
    "select 3 worst phenotypes to kill" in {
      val mockRef = TestActorRef(new Phenotype(SampleGenome(Seq(1))))
      val mockRef2 = TestActorRef(new Phenotype(SampleGenome(Seq(1,2,2))))
      val mockRef3 = TestActorRef(new Phenotype(SampleGenome(Seq(1,1))))
      val mockBetter = TestActorRef(new Phenotype(SampleGenome(Seq(1,3,3,7))))
      val phenotypes = Seq(
        Evaluated(mockRef, 5),
        Evaluated(mockRef2, 2),
        Evaluated(mockBetter, 10),
        Evaluated(mockRef3, 1),
        Evaluated(mockBetter, 1337)
      )
      val strategy = new KillTheWorsts with TestPhenotypeValueComparator

      val toBeKilled = strategy.selectToBeKilled(3, phenotypes)

      toBeKilled should have length 3
      toBeKilled should contain (phenotypes(0).phenotype)
      toBeKilled should contain (phenotypes(1).phenotype)
      toBeKilled should contain (phenotypes(3).phenotype)
    }

    "select 0 worst phenotypes to kill from non empty collection" in {
      val mockRef = TestActorRef(new Phenotype(SampleGenome(Seq(1))))
      val mockRef2 = TestActorRef(new Phenotype(SampleGenome(Seq(1,2,2))))
      val mockRef3 = TestActorRef(new Phenotype(SampleGenome(Seq(1,1))))
      val mockBetter = TestActorRef(new Phenotype(SampleGenome(Seq(1,3,3,7))))
      val phenotypes = Seq(
        Evaluated(mockRef, 5),
        Evaluated(mockRef2, 2),
        Evaluated(mockBetter, 10),
        Evaluated(mockRef3, 1),
        Evaluated(mockBetter, 1337)
      )
      val strategy = new KillTheWorsts with TestPhenotypeValueComparator

      val toBeKilled = strategy.selectToBeKilled(0, phenotypes)

      toBeKilled should have length 0
    }

    "select 0 worst phenotypes to kill from empty collection" in {

      val phenotypes = Seq[Evaluated]()
      val strategy = new KillTheWorsts with TestPhenotypeValueComparator

      val toBeKilled = strategy.selectToBeKilled(0, phenotypes)

      toBeKilled should have length 0
    }
  }

}
