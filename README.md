scalagen
========

Important! project is still under development.

Scala actors toolkit to solve problems in scalable way using genetic algorithms

## Requirements
* SBT >= 0.13

## Compilation

Type *sbt* in project directory. Then type *projects*. You can see project list containing:
* *root
* scalagen-core
* scalagen-examples

Root is your current project and its parent project form core and examples. Type *compile* to compile both of them.

You can switch to projects using command *project {project-name}*. After you switch you can type *run* to run current project

## User manual
To use library developer has to implement few things:
* custom genotype class,
* crossover operator,
* mutation operator,
* objective function, 
* initial population generator.

Crossover operator should take two custom genotype objects as argument and return new genotype object. 

Mutation operator should take one genotype and return it's mutation.

Objective function take genotype as an argument. Its role is to evaluate its value. The bigger the value is the better phenotype it is.

Initial population generator should return collection of genotypes to start evolution with. The better they are the faster good results may be obtained.

User can also manipulate alghoritm's parameters. And these with defaults are: TODO
* *scalagen.population.size.optimal* = 50,
* *scalagen.mutation.probability* = 0.01,
* *scalagen.phenotype.maxLifeTime* = 100,
* *scalagen.death.probability* = 0.01.

## Actors
Library uses a few types of actors to respect principle of single responsibility. These actor are:
* Phenotype, it contains genotype,
* EndAlgorithmActor, it makes decision when to stop algorithm and all the actors. It knows few of best genotypes with their born timestamps,
* Controller, it tries to keep the correct number of Phenotypes by killing and procreation order. It should realize some sellection strategy,
* Evaluator, it knows the objective function so it can evaluate Phenotype value,
* RandomKiller, kills random phenotype TODO,
* Procreator, knows mutation and crossover operators so can create new genotype from parents,
* DeathItself, kills specified Phenotype.


