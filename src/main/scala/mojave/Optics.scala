package mojave

import shapeless.Lens

import scala.reflect.ClassTag

object Optics {
  def lens[A] = shapeless.lens[A]
  def traversal[A] = IdTraversal[A]()

  implicit def toPartial[A, B : ClassTag](l: Lens[A, Option[B]]) = PartialLens(l)
  implicit def toUnsafeLens[A, B : ClassTag](l: Lens[A, B]) = UnsafeLens(l)
  implicit def toTraversableLens[A, B : ClassTag](l: Lens[A, B]) = TraversableLens(l)
  implicit def toUnsafeTraversal[A, B: ClassTag](t: Traversal[A, B]) = UnsafeTraversal(t)
  implicit def toListTraversal[A, B](t: Traversal[A, List[B]]) = ListTraversal(t)
}

case class TraversableLens[A, B](lens: Lens[A, B]) {
  def toTraversal[A, B] = LensTraversal(lens)
}