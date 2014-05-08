package scalagen.message

import akka.actor.ActorRef
import scalagen.genome.Genome

case object ReadGenom

case class Eval(phenotype: ActorRef, genotype: Genome)

case class Evaluated(phenotype: ActorRef, value: Double)

case class Reproduce(genomeA: Genome, genomeB: Genome)

case class Descendant(genome: Genome)

case class Kill(phenotype: ActorRef)
