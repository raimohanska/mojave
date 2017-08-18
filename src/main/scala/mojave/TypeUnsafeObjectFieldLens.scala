package mojave

import shapeless.Lens

import scala.reflect.ClassTag

private case class TypeUnsafeObjectFieldLens[ObjectType : ClassTag, FieldType](fieldName: String) extends Lens[ObjectType, FieldType]{
  override def get(s: ObjectType): FieldType = CaseClassFieldAccessor.getField(s, fieldName).asInstanceOf[FieldType]
  override def set(s: ObjectType)(a: FieldType): ObjectType = CaseClassFieldAccessor.setField(s, fieldName, a)
}
