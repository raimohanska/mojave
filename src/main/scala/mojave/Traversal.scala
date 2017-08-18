package mojave

import shapeless.{Lens, MkFieldLens, Witness}

trait Traversal[S, A] {
  def modify(s: S)(f: A => A): S

  def compose[T](g: Traversal[T, S]) = {
    val self = this
    new Traversal[T, A] {
      override def modify(t: T)(f: (A) => A): T = g.modify(t) { s: S => self.modify(s)(f) }
    }
  }

  // Default implementation of toIterable implemented using modify
  def toIterable(s: S): Iterable[A] = {
    var list: List[A] = Nil
    modify(s) { item => list +:= item; item }
    list
  }

  def filter(predicate: A => Boolean) = {
    val self = this
    new Traversal[S, A] {
      override def modify(s: S)(f: (A) => A): S = {
        self.modify(s) { a: A => if (predicate(a)) { f(a) } else { a }}
      }
    }
  }

  def ifInstanceOf[SubTyep <: A](implicit mf: ClassManifest[SubTyep]): Traversal[S, SubTyep] = ClassSelectiveTraversal[A, SubTyep](mf.runtimeClass.asInstanceOf[Class[SubTyep]]).compose(this)
}

case class IdTraversal[A]() extends Traversal[A, A] {
  override def modify(s: A)(f: (A) => A): A = f(s)
  override def toIterable(s: A): Iterable[A] = List(s)
}

case class ListTraversal[A, B, C[B] <: Iterable[B]](traversal: Traversal[A, C[B]]) {
  def items: Traversal[A, B] = new Traversal[A, B] {
    def modify(s: A)(f: (B) => B): A = traversal.modify(s){ items: C[B] => items.map(f).asInstanceOf[C[B]] }
  }
}

case class OptionTraversal[A, B](traversal: Traversal[A, Option[B]]) {
  def items: Traversal[A, B] = new Traversal[A, B] {
    def modify(s: A)(f: (B) => B): A = traversal.modify(s){ items => items.map(f) }
  }
}

case class LensTraversal[A, B](lens: Lens[A, B]) extends Traversal[A, B] {
  override def toIterable(s: A): Iterable[B] = List(lens.get(s))
  def modify(s: A)(f: (B) => B): A = lens.modify(s)(f)
}

private case class ClassSelectiveTraversal[Tyep, SubTyep <: Tyep](subTypeClass: Class[SubTyep]) extends Traversal[Tyep, SubTyep] {
  override def modify(s: Tyep)(f: (SubTyep) => SubTyep): Tyep = if (subTypeClass.isInstance(s)) { f(s.asInstanceOf[SubTyep]) } else { s }
}
