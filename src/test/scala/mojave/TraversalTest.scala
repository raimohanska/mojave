package mojave

import org.scalatest.{FreeSpec, Matchers}

class TraversalTest extends FreeSpec with Matchers {
  val initial = Zoo(List(Giraffe("giraffe", 1), Pony("pony", 2), Insect(false)), 0)
  val animalsTraversal = traversal[Zoo].field[List[Animal]]("animals").items

  "Traversal" - {
    "list items" in {
      traversal[List[Int]].items.modify(List(1))(_ * 2) should equal(List(2))
    }
    "set items" in {
      traversal[Set[Int]].items.modify(Set(1))(_ * 2) should equal(Set(2))
    }
    "option items" in {
      traversal[Option[Int]].items.modify(Some(1))(_ * 2) should equal(Some(2))
    }

    "zoo example" in {
      val nameLens: Traversal[Zoo, String] = animalsTraversal
        .ifInstanceOf[AnimalWithName]
        .field[String]("name")

      nameLens.modify(initial)(name => "the " + name) should equal(Zoo(List(Giraffe("the giraffe", 1), Pony("the pony", 2), Insect(false)), 0))
    }

    "filtering" in {
      println(0)
      val flipped = animalsTraversal.ifInstanceOf[AnimalWithName].filter(_.name == "giraffe").field[String]("name").modify(initial){_ => "horse"}
      flipped should equal(Zoo(List(Giraffe("horse", 1), Pony("pony", 2), Insect(false)), 0))
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
