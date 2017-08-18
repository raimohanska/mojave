package mojave

import shapeless.Lens

case class TraversableLens[A, B](lens: Lens[A, B]) {
  def toTraversal[A, B] = LensTraversal(lens)
}
