package mojave

import org.scalatest.{FreeSpec, Matchers}
import shapeless.Lens

class LensTest extends FreeSpec with Matchers {
  "Lens" - {
    val document = Html(Body("content"))

    "field access" in {
      val contentLens = lens[Html].field[Tag]("content")
      contentLens.get(document) should equal(Body("content"))
      contentLens.set(document)(Head("hello")) should equal(Html(Head("hello")))
    }

    "when field doesn't exist" in {
      assertThrows[NoSuchMethodException] {
        lens[Html].field[Tag]("content_").set(document)(Body("content"))
      }
      assertThrows[NoSuchMethodException] {
        lens[Html].field[Tag]("content_").get(document)
      }
    }

    "partial lens field access" in {
      val partialLens: Lens[Html, Option[String]] = lens[Html].field[Tag]("content").ifInstanceOf[Body].optField[String]("content")
      partialLens.get(document) should equal (Some("content"))
      val modified = partialLens.set(document)(Some("content2"))
      partialLens.get(modified) should equal (Some("content2"))
      assertThrows[RuntimeException](partialLens.set(document)(None))
      assertThrows[RuntimeException](partialLens.set(Html(Head("head")))(Some("content")))
    }
  }

  trait Tag
  case class Html(content: Tag)
  case class Body(content: String) extends Tag
  case class Head(content: String) extends Tag
}
