package mojave

object TraversalExamples extends App {
  import Optics._
  trait ThingieLike
  case class Thingie(x: String, y: Int) extends ThingieLike
  case class Stub() extends ThingieLike
  case class ThingieContainer(things: List[ThingieLike], id: Int)

  private val wrapper = ThingieContainer(List(Thingie("x", 1), Thingie("y", 2), Stub()), 0)

  private val compositeLens: Traversal[ThingieContainer, String] = (traversal[ThingieContainer])
    .field[List[ThingieLike]]("things")
    .items
    .ifInstanceOf[Thingie]
    .field[String]("x")

  println(compositeLens.modify(wrapper)(x => "hello " + x))
}

