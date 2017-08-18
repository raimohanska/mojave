import shapeless.Lens

import scala.reflect.ClassTag

package object mojave {
  def lens[A] = shapeless.lens[A]
  def traversal[A] = IdTraversal[A]()

  implicit def toPartial[A, B : ClassTag](l: Lens[A, Option[B]]) = PartialLens(l)
  implicit def toUnsafeLens[A, B : ClassTag](l: Lens[A, B]) = UnsafeLens(l)
  implicit def toTraversableLens[A, B : ClassTag](l: Lens[A, B]) = TraversableLens(l)
  implicit def toUnsafeTraversal[A, B: ClassTag](t: Traversal[A, B]) = UnsafeTraversal(t)
  implicit def toListTraversal[A, B, C[B] <: Iterable[B]](t: Traversal[A, C[B]]) = ListTraversal(t)
  implicit def toOptionTraversal[A, B](t: Traversal[A, Option[B]]) = OptionTraversal(t)
}
