import akka.actor.{Props, ActorSystem, ActorRef}
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scalagen.actor._
import scalagen.genome.Genome
import scalagen.message.Evaluated
import scalagen.population.{MaximizeValue, RouletteWheelReproduction, KillTheWorsts}

object BackpackData {
  val weights = Vector(1, 1, 3, 2, 5, 7, 2, 8, 12, 5, 1 , 2)
  val profits = Vector(3, 2, 3, 1, 1, 3, 6, 2, 23, 6, 12, 12)
  val items =      Seq(0, 1, 2, 3, 4, 5, 6, 7, 8,  9, 10, 11)
  val backpackWeightLimit = 25
}

object BackpackExample extends App {
  val system = ActorSystem("backpackActorSystem")
  val death = system.actorOf(Props[DeathItself], "death-itself")
  val randomKiller = system.actorOf(Props[BackPackRandomKiller], "random-killer")
  val end = system.actorOf(Props[BackpackEndOfAlgorithm], "end-of-algorithm")
  val evaluator = system.actorOf(Props(new BackpackEvaluator(end)), "evaluator")
  val controller = system.actorOf(Props[BackpackControllerActor], "controller")

  system.actorOf(Props(new BackpackGodfather(evaluator, death, randomKiller, controller)), "godfather")
}

/**
 * A backpack is represented by permutation of items.
 * A backpack is created by taking items from @itemsOrder until limit exceeds.
 * Other items are not included in backpack.
 * So in other words the more to the left the item is the bigger chance is to be in backpack.
 */
case class BackpackGenome(itemsOrder: Seq[Int]) extends Genome

class BackpackEvaluator(endOfAlgorithm: ActorRef) extends Evaluator(endOfAlgorithm) with MaximizeValue {
  def eval(genome: Genome) = BackpackOperators.eval(genome)
}

class BackpackProcreator(male: ActorRef, female: ActorRef, mutationProbability: Double)
  extends Procreator(male, female, mutationProbability) {
  def recombine(genomeA: Genome, genomeB: Genome) = BackpackOperators.recombine(genomeA, genomeB)
  def mutate(genome: Genome) = BackpackOperators.mutate(genome)
}

class BackpackGodfather(evaluator: ActorRef,
                        deathItself: ActorRef,
                        randomKiller: ActorRef,
                        controller: ActorRef)
    extends Godfather(evaluator, deathItself, randomKiller, controller) {
  def procreatorFactory(male: ActorRef, female: ActorRef) =
    new BackpackProcreator(male, female, BackpackOperators.mutationProbability)
  def initialGenomes = BackpackOperators.genInitial
}

class BackpackControllerActor extends Controller with KillTheWorsts with RouletteWheelReproduction with MaximizeValue {
  def optimalPopulationSize = 30
  def maxToKillOrCreate = 20
}

class BackpackEndOfAlgorithm extends EndOfAlgorithm with MaximizeValue {
  override def shouldStopCalculations(value: Double) = value > 61
  override def onFinish = {
    super.onFinish
    context.system.shutdown()}
}

class BackPackRandomKiller extends RandomKiller(0.01f) {
  def selectToKill(phenotypes: Seq[Evaluated]): Option[ActorRef] = Option(phenotypes.head.phenotype)
}

object BackpackOperators {
  /**
   * Splits two genomes at random index.
   * Then it takes first part from first genome and
   * second part of second genome without items contained by first part from first genome
   * Between them there goes items not contained by mentioned parts.
   * Its somehow similar to PMX crossover.
   */
  def recombine(genomeA: Genome, genomeB: Genome): Genome = {
    val backPackGenomeA = genomeA.asInstanceOf[BackpackGenome]
    val backPackGenomeB = genomeB.asInstanceOf[BackpackGenome]
    val splitPoint = Random.nextInt(backPackGenomeA.itemsOrder.size)
    val (aLeft, aRight) = backPackGenomeA.itemsOrder.splitAt(splitPoint)
    val (bLeft, bRight) = backPackGenomeB.itemsOrder.splitAt(splitPoint)
    val result = Seq() ++
      aLeft ++
      bLeft.intersect(aRight) ++
      bRight.filterNot(aLeft.contains _)
    BackpackGenome(result)
  }

  /**
   * Swaps two items in items precedence to be packed list.
   */
  def mutate(genome: Genome): Genome = {
    val backPackGenome = genome.asInstanceOf[BackpackGenome]
    val itemsOrder = backPackGenome.itemsOrder
    val buffer = new ArrayBuffer[Int] ++= itemsOrder
    val index = Random.nextInt(itemsOrder.length)
    val index2 = Random.nextInt(itemsOrder.length)
    val tmp = buffer(index)
    buffer(index) = buffer(index2)
    buffer(index2) = tmp
    backPackGenome.copy(itemsOrder = buffer.toSeq)
  }

  /**
   * Iterates through items order permutation list, until weight exceeds backpack limit.
   * It sums profits for each item in not overloaded backpack.
   */
  def eval(genome: Genome): Double = {
    import BackpackData._
    var weightSum = 0
    var profitSum = 0
    genome.asInstanceOf[BackpackGenome].itemsOrder.takeWhile(
      n => {
        val newWeightSum = weightSum + weights(n)
        if (newWeightSum < backpackWeightLimit) {
          weightSum = newWeightSum
          profitSum+=profits(n)
          true
        }
        else false
      })
    profitSum
  }

  /**
   * Generates initial 20 permutations of backpacks items.
   */
  def genInitial = Seq.fill(20)(BackpackGenome(Random.shuffle(BackpackData.items)))

  /**
   * Probability that mutation occurs during procreation
   */
  val mutationProbability = 0.05d
}
