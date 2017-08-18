package mojave

object Examples {
  trait Animal
  trait AnimalWithName extends Animal {
    def name: String
  }
  case class Giraffe(name: String, age: Int) extends AnimalWithName
  case class Pony(name: String, age: Int) extends AnimalWithName
  case class Insect() extends Animal
  case class Zoo(animals: List[Animal], id: Int)
}
