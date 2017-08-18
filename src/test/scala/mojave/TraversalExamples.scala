package mojave

object TraversalExamples extends App {
  import mojave._

  trait Animal
  trait AnimalWithName extends Animal {
    def name: String
  }
  case class Giraffe(name: String, age: Int) extends AnimalWithName
  case class Pony(name: String, age: Int) extends AnimalWithName
  case class Insect() extends Animal
  case class Zoo(animals: List[Animal], id: Int)


  private val zoo = Zoo(List(Giraffe("giraffe", 1), Pony("pony", 2), Insect()), 0)

  private val nameLens: Traversal[Zoo, String] = (traversal[Zoo])
    .field[List[Animal]]("animals")
    .items
    .ifInstanceOf[AnimalWithName]
    .field[String]("name")

  println(nameLens.modify(zoo)(x => "the " + x))
}