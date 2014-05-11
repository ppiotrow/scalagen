package scalagen.actor

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.{ShouldMatchers, WordSpecLike}
import utils.StopSystemAfterAll
import utils.SampleActors.{SampleGenome, TestRandomKiller}
import scalagen.message.{Phenotypes, GetPhenotypes, Death, Kill}

class RandomKillerActorTest extends TestKit(ActorSystem("RandomKillTestActorSystem"))
with ImplicitSender
with WordSpecLike
with ShouldMatchers
with StopSystemAfterAll {

  "A RandomKill actor" must {
    "get phenotypes when the kill to death ratio equals 10%" in {
      val randomKiller = TestActorRef(new TestRandomKiller(0.1f))

      (1 to 9).foreach(_ => randomKiller ! Death)

      expectMsg(GetPhenotypes)
    }

    "kill a phenotype when received a list of phenotypes" in {
      val randomKiller = TestActorRef(new TestRandomKiller(0.1f))
      val firstPhenotype = TestActorRef(new Phenotype(SampleGenome(Seq(1337))))
      val secondPhenotype = TestActorRef(new Phenotype(SampleGenome(Seq(7331))))

      randomKiller ! Phenotypes(Seq(firstPhenotype, secondPhenotype))

      expectMsg(Kill(secondPhenotype))
    }
  }

  "A randomKillRatioMatches" must {
    "match with 10% random kill ratio and 9 deaths" in {
      RandomKiller.randomKillRatioMatches(9, 0, 0.1f) should be(true)
    }

    "match with 1% random kill ratio and 99 deaths" in {
      RandomKiller.randomKillRatioMatches(99, 0, 0.01f) should be(true)
    }

    "match with 2% random kill ratio, 99 deaths and 1 random kill" in {
      RandomKiller.randomKillRatioMatches(99, 1, 0.02f) should be(true)
    }

    "match with 0.1% random kill ratio, 1999 deaths and 1 random kills" in {
      RandomKiller.randomKillRatioMatches(1999, 1, 0.001f) should be(true)
    }

    "not match with 1% random kill ratio, 99 deaths and 1 random kill" in {
      RandomKiller.randomKillRatioMatches(99, 1, 0.01f) should be(false)
    }

    "not match with 0% random kill ratio" in {
      RandomKiller.randomKillRatioMatches(Int.MaxValue, 0, 0f) should be(false)
    }
  }
}
