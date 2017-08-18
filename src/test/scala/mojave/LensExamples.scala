package mojave

import shapeless.Lens

object LensExamples extends App {
  import Optics._
  trait ThingieLike
  case class Thingie(x: String, y: Int) extends ThingieLike
  case class Stub() extends ThingieLike
  case class ThingieContainer(thing: ThingieLike, id: Int)

  private val wrapper = ThingieContainer(Thingie("x", 1), 0)

  private val compositeLens: Lens[ThingieContainer, Option[String]] = (lens[ThingieContainer])
    .field[ThingieLike]("thing")
    .ifInstanceOf[Thingie]
    .optField[String]("x")

  println(compositeLens.set(wrapper)(Some("y")))
}

