package scalagen.message

import akka.actor.ActorRef
import scalagen.genome.Genome

case object ReadGenom

case object GetPhenotypes

case object Death

case class GenomeReaded(genotype: Genome)

case class Eval(phenotype: ActorRef, genotype: Genome)

case class Evaluated(phenotype: ActorRef, value: Double)

case class Descendant(genome: Genome)

case class Kill(phenotype: ActorRef)

case class Phenotypes(phenotypes: Seq[Evaluated])

case class Best(genome: Genome, value: Double)

case class UpdatePopulation(couples: Seq[(ActorRef, ActorRef)], toBeKilled: Seq[ActorRef])
