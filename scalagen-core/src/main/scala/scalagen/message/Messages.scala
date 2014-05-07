package scalagen.message

import akka.actor.ActorRef
import scalagen.genome.Genome

case object ReadGenom

case class Eval(fenotype: ActorRef, genotype: Genome)

case class Evaluated(fenotype: ActorRef, value: Double)

case class Reproduce(genomeA: Genome, genomeB: Genome)

case class Descendant(genome: Genome)
