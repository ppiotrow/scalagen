package utils

import akka.testkit.{TestProbe, TestKit}
import org.scalatest.Suite
import akka.actor.{Actor, Props, ActorSystem}

trait TestParentChildRelation {
  this: TestKit with Suite =>
  def mockParentWithProbe(childProps: Props)(implicit system: ActorSystem)={
    val proxy = TestProbe()
    system.actorOf(Props(new Actor {
      val child = context.actorOf(childProps)
      def receive = {
        case x if sender == child => proxy.ref forward x
        case x => child forward x
      }
    }))
    proxy
  }
}
