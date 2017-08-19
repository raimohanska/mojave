package mojave

import shapeless.Lens

import scala.reflect.ClassTag

case class UnsafeLens[A, B : ClassTag](lens: Lens[A, B]) {
  /**
    * Field by name, where the non-existence of accessor in object is handled: get(None)=None
    */
  def fieldIfExists[C](field: String): Lens[A, Option[C]] = FieldIfExistsLens[B, C](field).compose(lens)

  /**
    * Partial lens that shows only instances of given class as Some(_) and other objects as None
    */
  def ifInstanceOf[SubTyep <: B](implicit mf: ClassManifest[SubTyep]): Lens[A, Option[SubTyep]] = ClassSelectiveLens[B, SubTyep](mf.runtimeClass.asInstanceOf[Class[SubTyep]]).compose(lens)
}

private case class ClassSelectiveLens[Tyep, SubTyep <: Tyep](subTypeClass: Class[SubTyep]) extends Lens[Tyep, Option[SubTyep]] {
  override def get(s: Tyep): Option[SubTyep] = if (subTypeClass.isInstance(s)) { Some(s.asInstanceOf[SubTyep]) } else { None }
  override def set(s: Tyep)(a: Option[SubTyep]): Tyep = a.getOrElse(s)
}
