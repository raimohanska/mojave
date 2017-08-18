package mojave

import org.scalatest.{FreeSpec, Matchers}

class TraversalTest extends FreeSpec with Matchers {
  val initial = Zoo(List(Giraffe("giraffe", 1), Pony("pony", 2), Insect(false)), 0)
  val animalsLens = traversal[Zoo].field[List[Animal]]("animals").items

  "Traversal" - {
    "zoo example" in {
      val nameLens: Traversal[Zoo, String] = animalsLens
        .ifInstanceOf[AnimalWithName]
        .field[String]("name")

      nameLens.modify(initial)(name => "the " + name) should equal(Zoo(List(Giraffe("the giraffe", 1), Pony("the pony", 2), Insect(false)), 0))
    }

    "filtering" in {
      println(0)
      val flipped = animalsLens.filter(_.isInstanceOf[Insect]).field[Boolean]("hasWings").modify(initial)(!_)
      flipped should equal(Zoo(List(Giraffe("giraffe", 1), Pony("pony", 2), Insect(true)), 0))
    }
  }

  trait Animal
  trait AnimalWithName extends Animal {
    def name: String
  }
  case class Giraffe(name: String, age: Int) extends AnimalWithName
  case class Pony(name: String, age: Int) extends AnimalWithName
  case class Insect(hasWings: Boolean) extends Animal
  case class Zoo(animals: List[Animal], id: Int)

}
