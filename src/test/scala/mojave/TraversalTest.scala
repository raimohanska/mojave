package mojave

import org.scalatest.{FreeSpec, Matchers}

class TraversalTest extends FreeSpec with Matchers {
  val initial = Zoo(List(Giraffe("giraffe", 1), Pony("pony", 2), Insect(false)), 0)
  val animalsTraversal = traversal[Zoo].field[List[Animal]]("animals").items

  "Traversal" - {
    "Collection / Option access" - {
      "list items" in {
        traversal[List[Int]].items.modify(List(1))(_ * 2) should equal(List(2))
      }
      "set items" in {
        traversal[Set[Int]].items.modify(Set(1))(_ * 2) should equal(Set(2))
      }
      "option items" in {
        traversal[Option[Int]].items.modify(Some(1))(_ * 2) should equal(Some(2))
      }
    }

    "Traversat.set" in {
      traversal[List[Int]].items.set(List(1))(0) should equal(List(0))
    }

    "Compose" in {
      val id = traversal[Int]
      val items = traversal[List[Int]].items

      id.compose(items)
    }

    "Concat" in {
      val a: Traversal[TwoLists, Int] = traversal[TwoLists].field[List[Int]]("a").items
      val b: Traversal[TwoLists, Int] = traversal[TwoLists].field[List[Int]]("b").items

      val both = a ++ b

      both.modify(TwoLists(List(1), List(2)))(_ + 1) should equal(TwoLists(List(2), List(3)))
    }

    "Zoo example" - {
      val nameLens: Traversal[Zoo, String] = animalsTraversal
        .ifInstanceOf[AnimalWithName]
        .field[String]("name")
      "modify" in {
        nameLens.modify(initial)(name => "the " + name) should equal(Zoo(List(Giraffe("the giraffe", 1), Pony("the pony", 2), Insect(false)), 0))
      }
      "toIterable" in {
        nameLens.toIterable(initial).toList should equal(List("giraffe", "pony"))
      }
    }

    "Filtering" in {
      val flipped = animalsTraversal
        .ifInstanceOf[AnimalWithName]
        .filter(_.name == "giraffe")
        .field[String]("name")
        .modify(initial){_ => "horse"}
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
  case class TwoLists(a: List[Int], b: List[Int])
}
