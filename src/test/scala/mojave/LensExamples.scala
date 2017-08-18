package mojave

import shapeless.Lens

object LensExamples extends App {
  import mojave._

  trait Tag
  case class Html(content: Tag)
  case class Body(content: String) extends Tag
  case class Head(content: String) extends Tag

  private val wrapper = Html(Body("hello world"))

  private val compositeLens: Lens[Html, Option[String]] = (lens[Html])
    .field[Tag]("content")
    .ifInstanceOf[Body]
    .optField[String]("content")

  println(compositeLens.set(wrapper)(Some("hallo welt")))
}

