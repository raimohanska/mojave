package mojave

import shapeless.Lens

import scala.reflect.ClassTag

private case class FieldIfExistsLens[ObjectType : ClassTag, FieldType](field: String) extends Lens[ObjectType, Option[FieldType]]{
  override def get(s: ObjectType): Option[FieldType] = CaseClassFieldAccessor.hasField(s, field) match {
    case true => Some(TypeUnsafeObjectFieldLens[ObjectType, FieldType](field).get(s))
    case false => None
  }
  override def set(s: ObjectType)(a: Option[FieldType]): ObjectType = (a, CaseClassFieldAccessor.hasField(s, field)) match {
    case (Some(value), true) => TypeUnsafeObjectFieldLens[ObjectType, FieldType](field).set(s)(value)
    case (None, true) => throw new RuntimeException(s"Cannot set field $field to None")
    case (_, false) => s
  }
}
