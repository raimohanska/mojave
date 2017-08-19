import shapeless.Lens

import scala.reflect.ClassTag

package object mojave {
  def lens[A] = shapeless.lens[A]
  def traversal[A] = IdTraversal[A]()

  implicit def toPartial[A, B : ClassTag](l: Lens[A, Option[B]]) = PartialLens(l)
  implicit def toTraversableLens[A, B : ClassTag](l: Lens[A, B]) = TraversableLens(l)
  implicit def toListTraversal[A, B, C[B] <: Iterable[B]](t: Traversal[A, C[B]]) = ListTraversal(t)
  implicit def toOptionTraversal[A, B](t: Traversal[A, Option[B]]) = OptionTraversal(t)

  implicit def toMacroLens[A, B: ClassTag](lens: Lens[A, B]): MacroLens[A, B] = MacroLens(lens)
  implicit def toMacroTraversal[A, B: ClassTag](traversal: Traversal[A, B]): MacroTraversal[A, B] = MacroTraversal(traversal)

  implicit def toUnsafeLens[A, B : ClassTag](l: Lens[A, B]) = UnsafeLens(l)

}
