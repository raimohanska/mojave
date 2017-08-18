lazy val root = (project in file("."))
  .settings(
    name := "Mojave",
    scalaVersion := "2.12.3",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.2",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )
