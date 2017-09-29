![mojave](mojave.png)

# Mojave

[Shapeless](https://github.com/milessabin/shapeless) lenses for the brave. Boilerplate-light partially type-unsafe optics for those who know what they're doing.

Works with scala 2.12

## Type-unsafe lenses

Mojave takes [Shapeless](https://github.com/milessabin/shapeless) lenses and adds a partially type-unsafe way for accessing
case class fields. This is useful in cases where you have a polymorphic data model where you have multiple case classes
implementing the same trait.

By partial type-unsafety I mean that read access to fields is type safe (it's implemented using macros) but
write access is unsafe because it relies on the assumption that there is a corresponding `copy` method in the
implementing case class, to be called using reflection. Anyway, it works as long as all of your fields are 
defined as regular case class fields.

```scala
  import mojave._

  trait Tag
  case class Html(content: Tag)
  case class Body(content: String) extends Tag
  case class Head(content: String) extends Tag

  private val wrapper = Html(Body("hello world"))

  private val compositeLens: Lens[Html, Option[String]] = (lens[Html])
    .field[Tag]("content")
    .ifInstanceOf[Body]
    .optField[String]("content")

  println(compositeLens.set(wrapper)(Some("hallo welt")))
```

## Traversals

In addition, Mojave introduces `Traversals` that allow you to focus on multiple items (whereas lenses always focus on a single item).
With traversals you can change multiple items within an arbitrary data structure.

```scala
  import mojave._

  trait Animal
  trait AnimalWithName extends Animal {
    def name: String
  }
  case class Giraffe(name: String, age: Int) extends AnimalWithName
  case class Pony(name: String, age: Int) extends AnimalWithName
  case class Insect() extends Animal
  case class Zoo(animals: List[Animal], id: Int)


  private val zoo = Zoo(List(Giraffe("giraffe", 1), Pony("pony", 2), Insect()), 0)

  private val nameLens: Traversal[Zoo, String] = (traversal[Zoo])
    .field[List[Animal]]("animals")
    .items
    .ifInstanceOf[AnimalWithName]
    .field[String]("name")

  println(nameLens.modify(zoo)(x => "the " + x))
```

## SBT

```
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies += "com.github.raimohanska" % "mojave" % "0.5"
```

## Maven

```
...
    <dependency>
      <groupId>com.github.raimohanska</groupId>
      <artifactId>mojave</artifactId>
      <version>0.5</version>
    </dependency>
...

  <repositories>
    <repository>
      <id>central</id>
      <url>http://repo1.maven.org/maven2</url>
    </repository>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>
```

