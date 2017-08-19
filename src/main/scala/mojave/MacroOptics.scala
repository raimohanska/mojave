package mojave
import shapeless.Lens

import scala.language.experimental.macros
import scala.reflect.ClassTag
import scala.reflect.macros.blackbox.Context

case class MacroLens[A, B: ClassTag](lens: Lens[A, B]) {
  def field[C](field: String)(implicit evidence: ClassTag[B]): Lens[A, C] = macro OpticMacros.fieldLensMacro[A, B, C]
}

case class MacroTraversal[A, B: ClassTag](traversal: Traversal[A, B]) {
  def field[C](field: String)(implicit evidence: ClassTag[B]): Traversal[A, C] = macro OpticMacros.fieldTraversalMacro[A, B, C]
}

class OpticMacros(val c: Context) {
  import c.universe._

  private def objectFieldLensMacro[B: c.WeakTypeTag, C: c.WeakTypeTag](field: c.Expr[String], ev: c.Expr[ClassTag[B]]): c.Expr[Lens[B, C]] = {
    val methodname = field.tree.toString.replaceAll("\"", "") // TODO: this is not nice
    reify {
      implicit val e: ClassTag[B] = ev.splice
      new Lens[B, C] {
        override def get(s: B) = c.Expr[C](Select(Ident(TermName("s")), TermName(methodname))).splice

        override def set(s: B)(a: C) = CaseClassFieldAccessor.setField[B](s, field.splice, a)
      }
    }
  }

  def fieldLensMacro[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag](field: c.Expr[String])(evidence: c.Expr[ClassTag[B]]): c.Expr[Lens[A, C]] = {
    val thisLensExpr = c.Expr[Lens[A, B]](Select(c.prefix.tree, TermName("lens")))
    val objectFieldLensExpr: c.Expr[Lens[B, C]] = objectFieldLensMacro(field, evidence)

    reify {
      objectFieldLensExpr.splice.compose(thisLensExpr.splice)
    }
  }

  def fieldTraversalMacro[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag](field: c.Expr[String])(evidence: c.Expr[ClassTag[B]]): c.Expr[Traversal[A, C]] = {
    val thisLensExpr = c.Expr[Traversal[A, B]](Select(c.prefix.tree, TermName("traversal")))
    val objectFieldLensExpr: c.Expr[Lens[B, C]] = objectFieldLensMacro(field, evidence)

    reify {
      LensTraversal(objectFieldLensExpr.splice).compose(thisLensExpr.splice)
    }
  }
}