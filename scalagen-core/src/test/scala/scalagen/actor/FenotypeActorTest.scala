package scalagen.actor

import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{WordSpecLike, ShouldMatchers, BeforeAndAfterAll}
import scalagen.actor.Fenotype
import scalagen.message.ReadGenom

class FenotypeActorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with ShouldMatchers with WordSpecLike with BeforeAndAfterAll {

  def this() = this(ActorSystem("FenotypeTestActorSystem"))

  val sampleGenotype = List[Int](1, 4, 12, 3)
  val fenotypeActorRef = system.actorOf(Props(classOf[Fenotype[List[Int]]], sampleGenotype))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "An FenotypeActor" must {
    "respond with its genotype" in {
      fenotypeActorRef ! ReadGenom
      expectMsg(sampleGenotype)
    }
  }
}
