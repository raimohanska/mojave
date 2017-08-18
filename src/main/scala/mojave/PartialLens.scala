package mojave

import shapeless.Lens

import scala.reflect.ClassTag

case class PartialLens[A, B : ClassTag](wrapped: Lens[A, Option[B]]) {
  def andThen[C](second: Lens[B, Option[C]]): Lens[A, Option[C]] = PartialStep(wrapped, second)
  def modifyOpt(a: A)(fn: B => B) = wrapped.modify(a) { value => value.map(fn) }
  /**
    * Type-unsafe field accessor for case classes
    */
  def optField [C] (fieldName: String): Lens[A, Option[C]] = OptObjectFieldLens[B, C](fieldName) compose wrapped
}

private case class OptObjectFieldLens[ObjectType : ClassTag, FieldType](field: String) extends Lens[Option[ObjectType], Option[FieldType]]{
  private lazy val plainLens = TypeUnsafeObjectFieldLens[ObjectType, FieldType](field)
  override def get(s: Option[ObjectType]): Option[FieldType] = s.map(o => plainLens.get(o))
  override def set(s: Option[ObjectType])(a: Option[FieldType]): Option[ObjectType] = (s, a) match {
    case (Some(objectValue), Some(fieldValue)) => Some(plainLens.set(objectValue)(fieldValue))
    case (Some(objectValue), None) => throw new RuntimeException(s"Cannot set field $field of $objectValue to None")
    case (None, Some(fieldValue)) => throw new RuntimeException(s"Cannot set field $field of None to $fieldValue")
    case (None, None) => None
  }
}

private case class PartialStep[A, B, C](first: Lens[A, Option[B]], second: Lens[B, Option[C]]) extends Lens[A, Option[C]] {
  override def get(a: A): Option[C] = first.get(a).flatMap(second.get)
  override def set(a: A)(c: Option[C]): A = first.set(a)(first.get(a).map(b => second.set(b)(c)))
}