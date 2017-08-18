package mojave

object TraversalExamples extends App {
  import mojave._
  import Examples._

  private val wrapper = Zoo(List(Giraffe("giraffe", 1), Pony("pony", 2), Insect()), 0)

  private val compositeLens: Traversal[Zoo, String] = (traversal[Zoo])
    .field[List[Animal]]("animals")
    .items
    .ifInstanceOf[AnimalWithName]
    .field[String]("name")

  println(compositeLens.modify(wrapper)(x => "great " + x))
}