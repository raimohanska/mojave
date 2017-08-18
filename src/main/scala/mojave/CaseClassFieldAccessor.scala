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
    val members = decl.members.map(method => transformMethod(method, paramName, paramValue, instanceMirror)).filter {
      case _: Empty => false
      case _ => true
    }.toArray.reverse

    val copyMethod = decl.decl(TermName("copy")).asMethod
    val copyMethodInstance = instanceMirror.reflectMethod(copyMethod)

    copyMethodInstance(members: _*).asInstanceOf[R]
  }

  private def transformMethod(method: Symbol, paramName: String, paramValue: Any, instanceMirror: InstanceMirror) = {
    val term = method.asTerm
    if (term.isAccessor) {
      if (term.name.toString == paramName) {
        paramValue
      } else instanceMirror.reflectField(term).get
    } else new Empty
  }
}
