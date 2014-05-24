package scalagen.actor

import akka.actor.{Cancellable, Actor}
import scalagen.message.Best
import scalagen.genome.Genome
import scala.concurrent.duration._
import org.joda.time.DateTime

/**
 * An actor to determine, when the algorithm should ends.
 * It receives evaluated candidates for being the best genome.
 * When it gets genome with note satisfying isGoodEnough method it stops algorithm.
 * It also has timer to indicate when the last time it got improved result.
 */
abstract class EndOfAlgorithm extends Actor {
  var lastBestResult: Option[(Genome, Double, DateTime)] = None
  var timeout: Cancellable = _
  val maxTimeBetweenImprovement: FiniteDuration = 30.seconds

  override def preStart(): Unit = setTimeout()

  def receive = {
    case Best(genome, value) => processBestGenomeCandidate(genome, value)
    case ResultTimeoutPassed => finishAlgorithm()
  }

  /**
   * An function to determine if received value is better than current.
   * Example:
   * override def isBetterValue(currentValue: Double, newValue: Double) = currentValue < newValue
   */
  def isBetterValue(currentValue: Double, newValue: Double): Boolean

  /**
   * An function to determine if calculation should be stopped.
   * It can check if value reached some threshold or
   * if too much time passed after calculation started or
   * after last better result was received.
   */
  def shouldStopCalculations(value: Double): Boolean

  /**
   * An handler called when algorithm stopped.
   * It can be used to send somewhere message with best genome obtained.
   */
  def onFinish(): Unit = {
    lastBestResult match {
      case None => println("No best result found")
      case Some((genome, value, _)) => println(s"Best result is $value for genome ${genome.toString}")
      case _ => ()
    }
  }

  private def processBestGenomeCandidate(newGenome: Genome, newValue: Double): Unit = {

    lastBestResult match {
      case Some((_, oldValue, _)) if isBetterValue(oldValue, newValue) => processBetterGenome()
      case None => processBetterGenome()
      case _ => ()
    }

    def processBetterGenome(): Unit = {
      resetTimeout()
      lastBestResult = Some((newGenome, newValue, DateTime.now()))
      if (shouldStopCalculations(newValue)) {
        finishAlgorithm()
      }
    }
  }

  private def resetTimeout() = {
    timeout.cancel()
    setTimeout()
  }

  private def setTimeout() = {
    timeout = context.system.scheduler.scheduleOnce(
      maxTimeBetweenImprovement,
      self,
      ResultTimeoutPassed)(context.system.dispatcher)
  }

  private def finishAlgorithm() = {
    timeout.cancel()
    onFinish()
    context.stop(self)
  }

  case object ResultTimeoutPassed
}
