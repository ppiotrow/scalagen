package scalagen.actor

import akka.testkit.{TestKit, ImplicitSender}
import akka.actor.{Props, ActorSystem}
import org.scalatest._
import utils.StopSystemAfterAll
import utils.SampleActors.{TestGodfather, TestRandomKiller}
import scalagen.message.{Phenotypes, Kill, GetPhenotypes}
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout

class RandomKillerActorIntegrationTest extends TestKit(ActorSystem("RandomKillerIntegrationTestActorSystem"))
with ImplicitSender
with WordSpecLike
with ShouldMatchers
with StopSystemAfterAll {

  "A RandomKill actor" must {
    "kill the last phenotype" in {
      implicit val timeout = Timeout(1 seconds)
      val deathItself = system.actorOf(Props[DeathItself])
      val randomKiller = system.actorOf(Props(new TestRandomKiller(0.1f)))
      val godfather = system.actorOf(Props(new TestGodfather(deathItself, randomKiller)))

      // Kill all the phenotypes except the last one
      val future = godfather ? GetPhenotypes
      val msg = Await.result(future.mapTo[Phenotypes], 1 second)
      msg.phenotypes.init.foreach(godfather ! Kill(_))

      // The last phenotype should be killed by a random killer
      awaitCond(Await.result((godfather ? GetPhenotypes).mapTo[Phenotypes], 1 second).phenotypes == Nil)
    }
  }
}
