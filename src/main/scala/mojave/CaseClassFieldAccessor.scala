package mojave

protected[mojave] object CaseClassFieldAccessor {
  import reflect._
  import scala.reflect.runtime._
  import scala.reflect.runtime.universe._

  private class Empty

  private lazy val mirror = universe.runtimeMirror(getClass.getClassLoader)

  def hasField(obj: Any, paramName: String) = obj.getClass.getMethod(paramName) != null

  def getField(obj: Any, paramName: String): Any = {
    obj.getClass.getMethod(paramName).invoke(obj)
  }

  def setField[R : ClassTag](obj: R, paramName: String, paramValue: Any): R = {
    val instanceMirror = mirror.reflect(obj)
    val decl = instanceMirror.symbol.asType.toType
    var found = false

    val parametersForCopyMethod = decl.members.filter(_.asTerm.isAccessor).map { method: Symbol =>
      if (method.asTerm.name.toString == paramName) {
        found = true
        paramValue
      } else {
        instanceMirror.reflectField(method.asTerm).get
      }
    }.toArray.reverse

    if (!found) {
      throw new NoSuchMethodException(s"Method $paramName not found in $obj")
    }

    val copyMethod = decl.decl(TermName("copy")).asMethod
    val copyMethodInstance = instanceMirror.reflectMethod(copyMethod)

    copyMethodInstance(parametersForCopyMethod: _*).asInstanceOf[R]
  }
}
