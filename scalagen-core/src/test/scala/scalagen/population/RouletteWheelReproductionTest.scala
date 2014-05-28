package scalagen.population

import org.scalatest.{ShouldMatchers, WordSpecLike}
import scalagen.message.Evaluated
import utils.TestPhenotypeValueComparator
import akka.testkit.{TestKit, TestActorRef}
import scalagen.actor.Phenotype
import utils.SampleActors.SampleGenome
import akka.actor.ActorSystem

class RouletteWheelReproductionTest extends TestKit(ActorSystem("RankReproductionTestActorSystem"))
with ShouldMatchers
with WordSpecLike  {
  "A strategy" should {
    "select 3 randomly selected couples" in {
      val phenotype1 = TestActorRef(new Phenotype(SampleGenome(Seq(1))))
      val phenotype2 = TestActorRef(new Phenotype(SampleGenome(Seq(1,2,2))))
      val phenotype3 = TestActorRef(new Phenotype(SampleGenome(Seq(1,1))))
      val phenotype4 = TestActorRef(new Phenotype(SampleGenome(Seq(1,3,3,7))))
      val phenotype5 = TestActorRef(new Phenotype(SampleGenome(Seq(1,3,3,3 ,7))))
      val phenotypes = Seq(
        Evaluated(phenotype1, 5),
        Evaluated(phenotype2, 2),
        Evaluated(phenotype3, 10),
        Evaluated(phenotype4, 1),
        Evaluated(phenotype5, 1337)
      )
      val strategy = new RouletteWheelReproduction with TestPhenotypeValueComparator

      val couples = strategy.selectCouples(3, phenotypes)

      couples should have length 3
    }

    "select 0 randomly selected couples" in {
      val phenotype1 = TestActorRef(new Phenotype(SampleGenome(Seq(1))))
      val phenotype2 = TestActorRef(new Phenotype(SampleGenome(Seq(1,2,2))))
      val phenotype3 = TestActorRef(new Phenotype(SampleGenome(Seq(1,1))))
      val phenotypes = Seq(
        Evaluated(phenotype1, 5),
        Evaluated(phenotype2, 2),
        Evaluated(phenotype3, 10))
      val strategy = new RouletteWheelReproduction with TestPhenotypeValueComparator

      val couples = strategy.selectCouples(0, phenotypes)

      couples should have length 0
    }


    "select 0 couples from empty collection" in {

      val phenotypes = Seq[Evaluated]()
      val strategy = new RouletteWheelReproduction with TestPhenotypeValueComparator

      val couples = strategy.selectCouples(0, phenotypes)

      couples should have length 0
    }
  }

}
