package mojave

import scala.reflect.ClassTag

case class UnsafeTraversal[A, B : ClassTag](traversal: Traversal[A, B]) {
  /**
    * Type-unsafe field accessor for case classes
    */
  def field [C](fieldName: String): Traversal[A, C] = {
    LensTraversal(TypeUnsafeObjectFieldLens[B, C](fieldName)).compose(traversal)
  }
}
