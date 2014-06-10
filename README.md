scalagen
========

Scala actors toolkit to solve problems in generic and scalable way using genetic algorithms

## User manual
*The fastest way to understand library is to take a look at BackpackExample*

To use library developer has to implement few things:
* custom **genome** class - it extends trait Genome,
* Procreator.recombine - **crossover operator** takes two custom genotype objects as argument and return new genotype object,
* Procreator.mutate - **mutation operator** takes one genotype and return its mutation,
* Evaluator.eval - **objective function** takes genotype as an argument and return its value,
* Godfather.initialGenomes - **initial population generator** should return collection of genotypes to start evolution with (
the better they are the faster good results may be obtained).

User can also manipulate algorithm's parameters. And these are:

* Controller.optimalPopulationSize - the **size of population** that Controller will try to reach and maintain,
* Controller.maxToKillOrCreate - the maximum number of phenotypes that controller will try to kill or create
to be as close to optimalPopulationSize as possible and to replace as many phenotypes as possible,
(it is clearly described in Controller.calculatePopulationChange code doc),
* Procreator.mutationProbability - **probability of mutation** in newly created genome,
* RandomKiller.randomKillRatio - determines the ratio of the number of random deaths to deaths,
* EndOfAlgorithm.maxTimeBetweenImprovement - timeout to stop the algorithm since last best result(default 30 seconds).

## Actors
Library uses a few types of actors to model genetic algorithm. These actors are:
* Phenotype, it contains genotype,
* Godfather, holds references to population of Phenotypes.
* Evaluator, it implements the objective function so it can evaluate Phenotype value,
* Controller, it tries to keep the correct population size but also to replace effectively old phenotypes with new ones, 
* EndOfAlgorithm, it makes decision when to stop algorithm. The Evaluator notify it every time when it finds the new best phenotype,
* Procreator, implements mutation and crossover operators so it can create new genotype from parents,
* DeathItself, kills specified Phenotype,
* RandomKiller, kills random phenotype from time to time.

## Objective function
The objective function value can be minimized or maximized. This behavior is determined by two implementations from scalagen.population package:
* MaximizeValue,
* MinimizeValue.
This traits can be mixed into implementations of Controller, EndOfAlgorithm and Evaluator actors.

## Population updates
Controller chooses some number of phenotypes to be killed. From the rest of the population it selects pairs. 
They will procreate and have a child.

In package scalagen.population there are two traits with some example implementations:
* PopulationKilling, represents strategy of selecting phenotypes to be killed,
* PopulationReproduction, represents strategy of selecting parents pairs.

## Compilation

Type *sbt* in project directory. Then type *projects*. You can see project list containing:
* *root
* scalagen-core
* scalagen-examples

Root is your current project and it is parent project for core and examples. Type *compile* to compile both of them.

You can switch to projects using command *project {project-name}*.
In scalagen-core you can perform tests using *test* command.
In scalagen-example you can type *run* to run examples.

## Contribution
If you would like to contribute to scalagen, your help is very welcome. You may
* work on some existing issue,
* make up your own improvement,
* write some example using library.

Download
--------

SBT
```scala
libraryDependencies += "com.github.scalagen" % "scalagen_2.10" % "0.1.0"
```

Gradle
```groovy
compile 'com.github.scalagen:scalagen_2.10:0.1.0'
```

Maven
```xml
<groupId>com.github.scalagen</groupId>
<artifactId>scalagen_2.10</artifactId>
<version>0.1.0</version>
```

License
--------

    Copyright 2014 Przemek Piotrowski
    Copyright 2014 Tomasz Rozbicki

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
