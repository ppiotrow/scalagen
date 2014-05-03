package utils

import org.scalatest.{Suite, BeforeAndAfterAll}
import akka.testkit.TestKit

trait StopSystemAfterAll extends BeforeAndAfterAll {

  this: TestKit with Suite =>
  override protected def afterAll() {
    super.afterAll()
    TestKit.shutdownActorSystem(system)
  }

}